package com.example.fruits;

import com.example.fruits.managers.*;
import com.example.fruits.commands.FruitCommand;
import com.example.fruits.models.Fruit;
import com.example.fruits.registry.FruitRegistry;
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
        
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        
        // Register commands
        FruitCommand fruitCommand = new FruitCommand(this);
        Objects.requireNonNull(getCommand("fruit")).setExecutor(fruitCommand);
        Objects.requireNonNull(getCommand("fruit")).setTabCompleter(fruitCommand);
        
        getLogger().info("=========================================");
        getLogger().info("FruitsPlugin v3.0 enabled!");
        getLogger().info("Loaded " + fruitRegistry.getAllFruits().size() + " fruits");
        getLogger().info("=========================================");
    }
    
    @Override
    public void onDisable() {
        // Save all player data
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerManager.savePlayerStats(player);
        }
        saveFirstJoinData();
        
        getLogger().info("FruitsPlugin disabled!");
    }
    
    private void loadFirstJoinData() {
        if (configManager.getData().contains("first-join-players")) {
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
        List<String> uuids = new ArrayList<>();
        for (UUID uuid : firstJoinPlayers) {
            uuids.add(uuid.toString());
        }
        configManager.getData().set("first-join-players", uuids);
        configManager.saveDataConfig();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Add to active players
        playerManager.addActivePlayer(player);
        playerManager.loadPlayerStats(player);
        
        // Check first join
        boolean isFirstJoin = !firstJoinPlayers.contains(uuid);
        
        if (isFirstJoin) {
            firstJoinPlayers.add(uuid);
            saveFirstJoinData();
            
            // Give join fruit if enabled
            if (configManager.isJoinFruitEnabled()) {
                Fruit fruit = fruitRegistry.getFruit(configManager.getJoinFruitId());
                if (fruit != null) {
                    player.getInventory().addItem(fruit.createItemStack(configManager.getJoinFruitAmount()));
                    player.sendMessage("§a🎁 Welcome! You received §6" + 
                        configManager.getJoinFruitAmount() + "x " + fruit.getName() + "§a as a first-join gift!");
                }
            }
        }
        
        // Auto give for all joins
        if (configManager.isAutoGiveEnabled()) {
            Fruit fruit = fruitRegistry.getFruit(configManager.getAutoGiveFruitId());
            if (fruit != null) {
                player.getInventory().addItem(fruit.createItemStack(configManager.getAutoGiveAmount()));
                player.sendMessage("§a🎁 Auto-gift: You received §6" + 
                    configManager.getAutoGiveAmount() + "x " + fruit.getName() + "§a!");
            }
        }
        
        // Start join protection
        gracePeriodManager.startProtectionOnJoin(player);
        
        if (configManager.isDebugMode()) {
            getLogger().info(player.getName() + " joined. First join: " + isFirstJoin);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playerManager.savePlayerStats(player);
        playerManager.removeActivePlayer(player);
        playerManager.clearPlayerFruitCache(player);
    }
    
    // ==================== RESET METHODS ====================
    
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
    
    public int resetAllFirstJoin() {
        int count = firstJoinPlayers.size();
        firstJoinPlayers.clear();
        saveFirstJoinData();
        return count;
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
    
    public boolean isAutoGiveEnabled() { return configManager.isAutoGiveEnabled(); }
    public String getAutoGiveFruit() { return configManager.getAutoGiveFruitId(); }
    public int getAutoGiveAmount() { return configManager.getAutoGiveAmount(); }
    public boolean isJoinFruitEnabled() { return configManager.isJoinFruitEnabled(); }
    public String getJoinFruit() { return configManager.getJoinFruitId(); }
    public int getJoinFruitAmount() { return configManager.getJoinFruitAmount(); }
    public String getJoinFruitName() {
        Fruit fruit = fruitRegistry.getFruit(configManager.getJoinFruitId());
        return fruit != null ? fruit.getName() : configManager.getJoinFruitId();
    }
    
    // ==================== SETTER METHODS ====================
    
    public void setAutoGive(boolean enabled, String fruitId, int amount) {
        configManager.setAutoGiveEnabled(enabled);
        if (fruitId != null) {
            configManager.setAutoGiveFruitId(fruitId);
            configManager.setAutoGiveAmount(amount);
        }
    }
    
    public boolean toggleAutoGive() {
        configManager.setAutoGiveEnabled(!configManager.isAutoGiveEnabled());
        return configManager.isAutoGiveEnabled();
    }
    
    public void setJoinFruit(String fruitId, int amount) {
        configManager.setJoinFruitId(fruitId);
        configManager.setJoinFruitAmount(amount);
    }
    
    public boolean toggleJoinFruit() {
        configManager.setJoinFruitEnabled(!configManager.isJoinFruitEnabled());
        return configManager.isJoinFruitEnabled();
    }
    
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        configManager.reload();
    }
}
