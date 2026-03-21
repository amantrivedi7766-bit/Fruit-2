package com.example.fruits;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class FruitsPlugin extends JavaPlugin implements Listener {
    
    private static FruitsPlugin instance;
    private FruitRegistry fruitRegistry;
    private CooldownManager cooldownManager;
    
    // First join tracking
    private File firstJoinFile;
    private FileConfiguration firstJoinConfig;
    private Set<UUID> firstJoinPlayers = new HashSet<>();
    
    // Auto give settings
    private boolean autoGiveEnabled = false;
    private String autoGiveFruitId = "nature_dye";
    private int autoGiveAmount = 1;
    
    // Join fruit settings
    private boolean joinFruitEnabled = true;
    private String joinFruitId = "nature_dye";
    private int joinFruitAmount = 1;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize registries
        fruitRegistry = new FruitRegistry();
        cooldownManager = new CooldownManager();
        
        // Load first join data
        loadFirstJoinData();
        
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        
        // Load config
        loadConfig();
        
        getLogger().info("FruitsPlugin enabled!");
    }
    
    @Override
    public void onDisable() {
        saveFirstJoinData();
        getLogger().info("FruitsPlugin disabled!");
    }
    
    private void loadFirstJoinData() {
        firstJoinFile = new File(getDataFolder(), "firstjoin.yml");
        if(!firstJoinFile.exists()) {
            saveResource("firstjoin.yml", false);
        }
        firstJoinConfig = YamlConfiguration.loadConfiguration(firstJoinFile);
        
        // Load all players who have joined before
        if(firstJoinConfig.contains("players")) {
            List<String> uuids = firstJoinConfig.getStringList("players");
            for(String uuid : uuids) {
                try {
                    firstJoinPlayers.add(UUID.fromString(uuid));
                } catch(Exception e) {
                    getLogger().warning("Invalid UUID in firstjoin.yml: " + uuid);
                }
            }
        }
        
        getLogger().info("Loaded " + firstJoinPlayers.size() + " players who have joined before");
    }
    
    private void saveFirstJoinData() {
        List<String> uuids = new ArrayList<>();
        for(UUID uuid : firstJoinPlayers) {
            uuids.add(uuid.toString());
        }
        firstJoinConfig.set("players", uuids);
        try {
            firstJoinConfig.save(firstJoinFile);
        } catch(IOException e) {
            getLogger().severe("Could not save firstjoin.yml: " + e.getMessage());
        }
    }
    
    private void loadConfig() {
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        
        autoGiveEnabled = config.getBoolean("auto-give.enabled", false);
        autoGiveFruitId = config.getString("auto-give.fruit-id", "nature_dye");
        autoGiveAmount = config.getInt("auto-give.amount", 1);
        
        joinFruitEnabled = config.getBoolean("join-fruit.enabled", true);
        joinFruitId = config.getString("join-fruit.id", "nature_dye");
        joinFruitAmount = config.getInt("join-fruit.amount", 1);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        // Check if first join
        boolean isFirstJoin = !firstJoinPlayers.contains(uuid);
        
        if(isFirstJoin) {
            // Mark as joined
            firstJoinPlayers.add(uuid);
            saveFirstJoinData();
            
            // Give join fruit if enabled
            if(joinFruitEnabled) {
                Fruit fruit = fruitRegistry.getFruit(joinFruitId);
                if(fruit != null) {
                    player.getInventory().addItem(fruit.createItemStack(joinFruitAmount));
                    player.sendMessage("§a🎁 Welcome! You received §6" + joinFruitAmount + "x " + fruit.getName() + "§a as a first-join gift!");
                } else {
                    getLogger().warning("Join fruit not found: " + joinFruitId);
                }
            }
        }
        
        // Auto give for all joins
        if(autoGiveEnabled) {
            Fruit fruit = fruitRegistry.getFruit(autoGiveFruitId);
            if(fruit != null) {
                player.getInventory().addItem(fruit.createItemStack(autoGiveAmount));
                player.sendMessage("§a🎁 Auto-gift: You received §6" + autoGiveAmount + "x " + fruit.getName() + "§a!");
            }
        }
    }
    
    // ==================== RESET METHODS ====================
    
    /**
     * Reset first join data for a specific player
     */
    public boolean resetPlayerFirstJoin(UUID uuid) {
        if(firstJoinPlayers.contains(uuid)) {
            firstJoinPlayers.remove(uuid);
            saveFirstJoinData();
            return true;
        }
        return false;
    }
    
    /**
     * Reset first join data for a specific player by name
     */
    public boolean resetPlayerFirstJoin(String playerName) {
        Player player = getServer().getPlayer(playerName);
        if(player != null) {
            return resetPlayerFirstJoin(player.getUniqueId());
        }
        
        // Try to find by offline player
        @SuppressWarnings("deprecation")
        org.bukkit.OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(playerName);
        if(offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
            return resetPlayerFirstJoin(offlinePlayer.getUniqueId());
        }
        
        return false;
    }
    
    /**
     * Reset first join data for ALL players
     */
    public int resetAllFirstJoin() {
        int count = firstJoinPlayers.size();
        firstJoinPlayers.clear();
        saveFirstJoinData();
        return count;
    }
    
    /**
     * Check if a player has joined before
     */
    public boolean hasJoinedBefore(UUID uuid) {
        return firstJoinPlayers.contains(uuid);
    }
    
    /**
     * Get all players who have joined before
     */
    public Set<UUID> getFirstJoinPlayers() {
        return Collections.unmodifiableSet(firstJoinPlayers);
    }
    
    // ==================== GETTERS & SETTERS ====================
    
    public static FruitsPlugin getInstance() { return instance; }
    public FruitRegistry getFruitRegistry() { return fruitRegistry; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    
    public boolean isAutoGiveEnabled() { return autoGiveEnabled; }
    public String getAutoGiveFruit() { return autoGiveFruitId; }
    public int getAutoGiveAmount() { return autoGiveAmount; }
    
    public void setAutoGive(boolean enabled, String fruitId, int amount) {
        this.autoGiveEnabled = enabled;
        if(fruitId != null) {
            this.autoGiveFruitId = fruitId;
            this.autoGiveAmount = amount;
        }
        getConfig().set("auto-give.enabled", enabled);
        getConfig().set("auto-give.fruit-id", autoGiveFruitId);
        getConfig().set("auto-give.amount", autoGiveAmount);
        saveConfig();
    }
    
    public boolean isJoinFruitEnabled() { return joinFruitEnabled; }
    public String getJoinFruit() { return joinFruitId; }
    public int getJoinFruitAmount() { return joinFruitAmount; }
    public String getJoinFruitName() {
        Fruit fruit = fruitRegistry.getFruit(joinFruitId);
        return fruit != null ? fruit.getName() : joinFruitId;
    }
    
    public void setJoinFruit(String fruitId, int amount) {
        this.joinFruitId = fruitId;
        this.joinFruitAmount = amount;
        getConfig().set("join-fruit.id", fruitId);
        getConfig().set("join-fruit.amount", amount);
        saveConfig();
    }
    
    public boolean toggleJoinFruit() {
        joinFruitEnabled = !joinFruitEnabled;
        getConfig().set("join-fruit.enabled", joinFruitEnabled);
        saveConfig();
        return joinFruitEnabled;
    }
    
    @Override
    public void reloadConfig() {
        super.reloadConfig();
        loadConfig();
    }
}
