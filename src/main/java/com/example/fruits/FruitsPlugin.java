package com.example.fruits;

import com.example.fruits.listeners.PlayerInteractListener;
import com.example.fruits.listeners.JoinListener;
import com.example.fruits.listeners.ThiefGUIListener;
import com.example.fruits.listeners.PlayerDamageListener;
import com.example.fruits.registry.FruitRegistry;
import com.example.fruits.manager.CooldownManager;
import com.example.fruits.manager.SpinManager;
import com.example.fruits.manager.ConfigManager;
import com.example.fruits.manager.PlayerManager;
import com.example.fruits.manager.GracePeriodManager;
import com.example.fruits.commands.RewardCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FruitsPlugin extends JavaPlugin {
    private static FruitsPlugin instance;
    private FruitRegistry fruitRegistry;
    private CooldownManager cooldownManager;
    private SpinManager spinManager;
    private ConfigManager configManager;
    private PlayerManager playerManager;
    private GracePeriodManager gracePeriodManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize all managers
        fruitRegistry = new FruitRegistry();
        cooldownManager = new CooldownManager();
        configManager = new ConfigManager(this);
        spinManager = new SpinManager();
        playerManager = new PlayerManager();
        gracePeriodManager = new GracePeriodManager();
        
        // ==================== REGISTER ALL LISTENERS ====================
        // Main listeners
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        
        // Thief ability GUI listener
        getServer().getPluginManager().registerEvents(new ThiefGUIListener(), this);
        
        // Damage listener for Golden Aegis shield
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);
        
        // ==================== REGISTER COMMANDS ====================
        if(getCommand("freward") != null) {
            getCommand("freward").setExecutor(new RewardCommand());
        }
        
        // ==================== PLUGIN STARTUP MESSAGE ====================
        getLogger().info("=========================================");
        getLogger().info("§a✓ Fruits Plugin Enabled!");
        getLogger().info("§e✓ Magical Fruits Loaded:");
        getLogger().info("§e  - Vine Weaver (Nature)");
        getLogger().info("§e  - Shadowweaver (Thief)");
        getLogger().info("§e  - Golden Aegis (Throne)");
        getLogger().info("§e  - 8+ More Fruits Coming Soon!");
        getLogger().info("§e✓ Join Reward: " + (configManager.isRewardEnabled() ? "ENABLED" : "DISABLED"));
        getLogger().info("=========================================");
    }

    @Override
    public void onDisable() {
        // Cleanup any running tasks
        getLogger().info("§c✗ Fruits Plugin Disabled");
    }

    // ==================== GETTERS ====================
    
    public static FruitsPlugin getInstance() { return instance; }
    public FruitRegistry getFruitRegistry() { return fruitRegistry; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public SpinManager getSpinManager() { return spinManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public GracePeriodManager getGracePeriodManager() { return gracePeriodManager; }
    
    // ==================== PLAYER MANAGEMENT METHODS ====================
    
    public List<Player> getActivePlayers() {
        return playerManager.getActivePlayers();
    }
    
    public void addActivePlayer(Player player) {
        playerManager.addActivePlayer(player);
    }
    
    public void removeActivePlayer(Player player) {
        playerManager.removeActivePlayer(player);
    }
    
    public boolean isActivePlayer(Player player) {
        return playerManager.isActivePlayer(player);
    }
    
    public Map<UUID, Map<String, Object>> getActivePlayersMap() {
        return playerManager.getPlayerDataMap();
    }
}
