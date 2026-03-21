package com.example.fruits;

import com.example.fruits.listeners.PlayerInteractListener;
import com.example.fruits.listeners.JoinListener;
import com.example.fruits.registry.FruitRegistry;
import com.example.fruits.manager.CooldownManager;
import com.example.fruits.manager.SpinManager;
import com.example.fruits.manager.ConfigManager;
import com.example.fruits.commands.RewardCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class FruitsPlugin extends JavaPlugin {
    private static FruitsPlugin instance;
    private FruitRegistry fruitRegistry;
    private CooldownManager cooldownManager;
    private SpinManager spinManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        fruitRegistry = new FruitRegistry();
        cooldownManager = new CooldownManager();
        configManager = new ConfigManager(this);
        spinManager = new SpinManager();
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        
        // Register commands
        getCommand("freward").setExecutor(new RewardCommand());
        
        getLogger().info("=========================================");
        getLogger().info("§a✓ Fruits Plugin Enabled!");
        getLogger().info("§e✓ 10 Magical Fruits Loaded");
        getLogger().info("§e✓ Join Reward: " + (configManager.isRewardEnabled() ? "ENABLED" : "DISABLED"));
        getLogger().info("=========================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("§c✗ Fruits Plugin Disabled");
    }

    public static FruitsPlugin getInstance() { return instance; }
    public FruitRegistry getFruitRegistry() { return fruitRegistry; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public SpinManager getSpinManager() { return spinManager; }
    public ConfigManager getConfigManager() { return configManager; }
}
