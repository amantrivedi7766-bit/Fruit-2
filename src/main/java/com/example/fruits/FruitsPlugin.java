package com.example.fruits;

import com.example.fruits.listeners.PlayerInteractListener;
import com.example.fruits.registry.FruitRegistry;
import com.example.fruits.manager.CooldownManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FruitsPlugin extends JavaPlugin {
    private static FruitsPlugin instance;
    private FruitRegistry fruitRegistry;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;
        fruitRegistry = new FruitRegistry();
        cooldownManager = new CooldownManager();
        
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        
        // Withdraw command REMOVED
        
        getLogger().info("=========================================");
        getLogger().info("§a✓ Fruits Plugin Enabled!");
        getLogger().info("§e✓ 10 Magical Fruits Loaded");
        getLogger().info("=========================================");
    }

    @Override
    public void onDisable() {
        getLogger().info("§c✗ Fruits Plugin Disabled");
    }

    public static FruitsPlugin getInstance() { return instance; }
    public FruitRegistry getFruitRegistry() { return fruitRegistry; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
}
