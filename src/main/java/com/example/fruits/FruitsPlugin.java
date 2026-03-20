package com.example.fruits;

import com.example.fruits.commands.FruitAdminCommand;
import com.example.fruits.commands.FruitCommand;
import com.example.fruits.gui.AdminGUIListener;
import com.example.fruits.listeners.PlayerEatListener;
import com.example.fruits.listeners.PlayerInteractListener;
import com.example.fruits.models.PlayerFruitData;
import com.example.fruits.registry.FruitRegistry;
import com.example.fruits.utils.CooldownManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FruitsPlugin extends JavaPlugin {
    private static FruitsPlugin instance;
    private FruitRegistry fruitRegistry;
    private CooldownManager cooldownManager;
    private final Map<UUID, PlayerFruitData> activePlayers = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        
        fruitRegistry = new FruitRegistry();
        cooldownManager = new CooldownManager();

        // Register commands
        getCommand("fruit").setExecutor(new FruitCommand());
        getCommand("fruitadmin").setExecutor(new FruitAdminCommand());

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerEatListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new AdminGUIListener(), this);

        getLogger().info("✅ FruitsPlugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("❌ FruitsPlugin disabled.");
    }

    public static FruitsPlugin getInstance() { return instance; }
    public FruitRegistry getFruitRegistry() { return fruitRegistry; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
    public Map<UUID, PlayerFruitData> getActivePlayers() { return activePlayers; }
}
