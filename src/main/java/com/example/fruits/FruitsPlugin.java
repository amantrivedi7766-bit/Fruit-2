package com.example.fruits;

import com.example.fruits.managers.*;
import com.example.fruits.commands.FruitCommand;
import com.example.fruits.models.Fruit;
import com.example.fruits.registry.FruitRegistry;
import com.example.fruits.utils.CinematicSpinWheel;
import com.example.fruits.utils.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class FruitsPlugin extends JavaPlugin implements Listener {
    
    private static FruitsPlugin instance;
    
    // Managers
    private FruitRegistry fruitRegistry;
    private CooldownManager cooldownManager;
    private ConfigManager configManager;
    private SpinManager spinManager;
    private PlayerManager playerManager;
    private GracePeriodManager gracePeriodManager;
    
    // First join tracking
    private Set<UUID> firstJoinPlayers = new HashSet<>();
    
    // First join spin settings
    private boolean firstJoinSpinEnabled = true;
    private int firstJoinSpinCount = 1;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        fruitRegistry = new FruitRegistry();
        cooldownManager = new CooldownManager();
        configManager = new ConfigManager(this);
        spinManager = new SpinManager(this);
        playerManager = new PlayerManager(this);
        gracePeriodManager = new GracePeriodManager(this);
        
        // Load first join data
        loadFirstJoinData();
        loadSettings();
        
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        
        // Register commands
        FruitCommand fruitCommand = new FruitCommand(this);
        Objects.requireNonNull(getCommand("fruit")).setExecutor(fruitCommand);
        Objects.requireNonNull(getCommand("fruit")).setTabCompleter(fruitCommand);
        
        getLogger().info("=========================================");
        getLogger().info("FruitsPlugin v3.0 enabled!");
        getLogger().info("Loaded " + fruitRegistry.getAllFruits().size() + " fruits");
        getLogger().info("First Join Spin: " + (firstJoinSpinEnabled ? "ENABLED" : "DISABLED"));
        getLogger().info("=========================================");
    }
    
    @Override
    public void onDisable() {
        // Save all player data
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerManager != null) {
                playerManager.savePlayerStats(player);
            }
        }
        saveFirstJoinData();
        saveSettings();
        
        getLogger().info("FruitsPlugin disabled!");
    }
    
    private void loadFirstJoinData() {
        if (configManager != null && configManager.getData().contains("first-join-players")) {
            List<String> uuids = configManager.getData().getStringList("first-join-players");
            for (String uuid : uuids) {
                try {
                    firstJoinPlayers.add(UUID.fromString(uuid));
                } catch (Exception e) {
                    getLogger().warning("Invalid UUID in data: " + uuid);
                }
            }
        }
        getLogger().info("Loaded " + firstJoinPlayers.size() + " players who have joined before");
    }
    
    private void saveFirstJoinData() {
        if (configManager == null) return;
        List<String> uuids = new ArrayList<>();
        for (UUID uuid : firstJoinPlayers) {
            uuids.add(uuid.toString());
        }
        configManager.getData().set("first-join-players", uuids);
        configManager.saveDataConfig();
    }
    
    private void loadSettings() {
        if (configManager == null) return;
        firstJoinSpinEnabled = configManager.getData().getBoolean("first-join-spin.enabled", true);
        firstJoinSpinCount = configManager.getData().getInt("first-join-spin.count", 1);
    }
    
    private void saveSettings() {
        if (configManager == null) return;
        configManager.getData().set("first-join-spin.enabled", firstJoinSpinEnabled);
        configManager.getData().set("first-join-spin.count", firstJoinSpinCount);
        configManager.saveDataConfig();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Add to active players
        if (playerManager != null) {
            playerManager.addActivePlayer(player);
            playerManager.loadPlayerStats(player);
        }
        
        // Check first join
        boolean isFirstJoin = !firstJoinPlayers.contains(uuid);
        
        if (isFirstJoin) {
            firstJoinPlayers.add(uuid);
            saveFirstJoinData();
            
            // Give join fruit if enabled
            if (configManager != null && configManager.isJoinFruitEnabled()) {
                Fruit fruit = fruitRegistry.getFruit(configManager.getJoinFruitId());
                if (fruit != null) {
                    player.getInventory().addItem(fruit.createItemStack(configManager.getJoinFruitAmount()));
                    player.sendMessage("§a🎁 Welcome! You received §6" + 
                        configManager.getJoinFruitAmount() + "x " + fruit.getName() + "§a as a first-join gift!");
                }
            }
            
            // ========== FIRST JOIN AUTO SPIN ==========
            if (firstJoinSpinEnabled) {
                player.sendMessage("");
                player.sendMessage("§6§l═══════════════════════════════════");
                player.sendMessage("§a§l✨ WELCOME TO THE SERVER! ✨");
                player.sendMessage("§eYou get a FREE SPIN as a welcome gift!");
                player.sendMessage("§7Watch the magic happen...");
                player.sendMessage("§6§l═══════════════════════════════════");
                
                // Schedule spin after 2 seconds (so player can see the message)
                Bukkit.getScheduler().runTaskLater(this, new Runnable() {
                    @Override
                    public void run() {
                        if (player.isOnline()) {
                            player.sendMessage("§a✨ Starting your welcome spin! ✨");
                            CinematicSpinWheel spinWheel = new CinematicSpinWheel(FruitsPlugin.this, player);
                            spinWheel.startSpin();
                            
                            // Add to spin stats
                            if (spinManager != null) {
                                spinManager.incrementTotalSpins(player);
                            }
                        }
                    }
                }, 40L); // 2 seconds delay
            }
        }
        
        // Auto give for all joins
        if (configManager != null && configManager.isAutoGiveEnabled()) {
            Fruit fruit = fruitRegistry.getFruit(configManager.getAutoGiveFruitId());
            if (fruit != null) {
                player.getInventory().addItem(fruit.createItemStack(configManager.getAutoGiveAmount()));
                player.sendMessage("§a🎁 Auto-gift: You received §6" + 
                    configManager.getAutoGiveAmount() + "x " + fruit.getName() + "§a!");
            }
        }
        
        // Start join protection
        if (gracePeriodManager != null) {
            gracePeriodManager.startProtectionOnJoin(player);
        }
        
        if (configManager != null && configManager.isDebugMode()) {
            getLogger().info(player.getName() + " joined. First join: " + isFirstJoin);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (playerManager != null) {
            playerManager.savePlayerStats(player);
            playerManager.removeActivePlayer(player);
            playerManager.clearPlayerFruitCache(player);
        }
    }
    
    // ==================== RESET METHODS ====================
    
    /**
     * Reset player's first join data (they will get join fruit again)
     */
    public boolean resetPlayerFirstJoin(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return resetPlayerFirstJoin(player.getUniqueId());
        }
        @SuppressWarnings("deprecation")
        org.bukkit.OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
        if (offline != null && offline.hasPlayedBefore()) {
            return resetPlayerFirstJoin(offline.getUniqueId());
        }
        return false;
    }
    
    public boolean resetPlayerFirstJoin(UUID uuid) {
        if (firstJoinPlayers.contains(uuid)) {
            firstJoinPlayers.remove(uuid);
            saveFirstJoinData();
            return true;
        }
        return false;
    }
    
    /**
     * Reset ALL players' first join data
     */
    public int resetAllFirstJoin() {
        int count = firstJoinPlayers.size();
        firstJoinPlayers.clear();
        saveFirstJoinData();
        getLogger().info("Reset first join data for " + count + " players");
        return count;
    }
    
    /**
     * Reset ALL player data (first join, stats, cooldowns, etc.)
     */
    public int resetAllPlayerData() {
        int count = firstJoinPlayers.size();
        
        // Clear first join data
        firstJoinPlayers.clear();
        saveFirstJoinData();
        
        // Clear all player stats
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (playerManager != null) {
                playerManager.clearPlayerStats(player);
            }
            if (spinManager != null) {
                spinManager.clearPlayerSpinData(player);
            }
            if (cooldownManager != null) {
                cooldownManager.clearAllCooldowns(player);
            }
        }
        
        // Clear all stored data in config
        if (configManager != null) {
            configManager.clearAllPlayerData();
        }
        
        getLogger().info("Reset all player data for " + count + " players");
        return count;
    }
    
    /**
     * Reset player's complete data (first join, stats, cooldowns)
     */
    public boolean resetPlayerCompleteData(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        UUID uuid = null;
        
        if (player != null) {
            uuid = player.getUniqueId();
        } else {
            @SuppressWarnings("deprecation")
            org.bukkit.OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
            if (offline != null && offline.hasPlayedBefore()) {
                uuid = offline.getUniqueId();
            }
        }
        
        if (uuid != null) {
            // Reset first join
            boolean firstJoinReset = resetPlayerFirstJoin(uuid);
            
            // Reset stats
            if (player != null) {
                if (playerManager != null) {
                    playerManager.clearPlayerStats(player);
                }
                if (spinManager != null) {
                    spinManager.clearPlayerSpinData(player);
                }
                if (cooldownManager != null) {
                    cooldownManager.clearAllCooldowns(player);
                }
            }
            
            // Clear stored data in config
            if (configManager != null) {
                configManager.setPlayerData(playerName, "stats", null);
                configManager.setPlayerSpinCount(playerName, 0);
            }
            
            return true;
        }
        
        return false;
    }
    
    // ==================== FIRST JOIN SPIN SETTINGS ====================
    
    public boolean isFirstJoinSpinEnabled() {
        return firstJoinSpinEnabled;
    }
    
    public void setFirstJoinSpinEnabled(boolean enabled) {
        this.firstJoinSpinEnabled = enabled;
        saveSettings();
    }
    
    public int getFirstJoinSpinCount() {
        return firstJoinSpinCount;
    }
    
    public void setFirstJoinSpinCount(int count) {
        this.firstJoinSpinCount = count;
        saveSettings();
    }
    
    public boolean toggleFirstJoinSpin() {
        firstJoinSpinEnabled = !firstJoinSpinEnabled;
        saveSettings();
        return firstJoinSpinEnabled;
    }
    
    // ==================== GETTERS ====================
    
    public static FruitsPlugin getInstance() { return instance; }
    public FruitRegistry getFruitRegistry() { return fruitRegistry; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public SpinManager getSpinManager() { return spinManager; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public GracePeriodManager getGracePeriodManager() { return gracePeriodManager; }
    
    public Set<Player> getActivePlayers() {
        return new HashSet<>(Bukkit.getOnlinePlayers());
    }
    
    public boolean isAutoGiveEnabled() { 
        return configManager != null && configManager.isAutoGiveEnabled(); 
    }
    
    public String getAutoGiveFruit() { 
        return configManager != null ? configManager.getAutoGiveFruitId() : "nature_fruit"; 
    }
    
    public int getAutoGiveAmount() { 
        return configManager != null ? configManager.getAutoGiveAmount() : 1; 
    }
    
    public boolean isJoinFruitEnabled() { 
        return configManager != null && configManager.isJoinFruitEnabled(); 
    }
    
    public String getJoinFruit() { 
        return configManager != null ? configManager.getJoinFruitId() : "nature_fruit"; 
    }
    
    public int getJoinFruitAmount() { 
        return configManager != null ? configManager.getJoinFruitAmount() : 1; 
    }
    
    public String getJoinFruitName() {
        if (configManager == null) return "Nature Fruit";
        Fruit fruit = fruitRegistry.getFruit(configManager.getJoinFruitId());
        return fruit != null ? fruit.getName() : configManager.getJoinFruitId();
    }
    
    // ==================== SETTER METHODS ====================
    
    public void setAutoGive(boolean enabled, String fruitId, int amount) {
        if (configManager == null) return;
        configManager.setAutoGiveEnabled(enabled);
        if (fruitId != null) {
            configManager.setAutoGiveFruitId(fruitId);
            configManager.setAutoGiveAmount(amount);
        }
    }
    
    public boolean toggleAutoGive() {
        if (configManager == null) return false;
        configManager.setAutoGiveEnabled(!configManager.isAutoGiveEnabled());
        return configManager.isAutoGiveEnabled();
    }
    
    public void setJoinFruit(String fruitId, int amount) {
        if (configManager == null) return;
        configManager.setJoinFruitId(fruitId);
        configManager.setJoinFruitAmount(amount);
    }
    
    public boolean toggleJoinFruit() {
        if (configManager == null) return false;
        configManager.setJoinFruitEnabled(!configManager.isJoinFruitEnabled());
        return configManager.isJoinFruitEnabled();
    }
    
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        if (configManager != null) {
            configManager.reload();
        }
        loadSettings();
    }
}
