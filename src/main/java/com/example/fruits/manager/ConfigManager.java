package com.example.fruits.manager;

import com.example.fruits.FruitsPlugin;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final FruitsPlugin plugin;
    private boolean rewardEnabled;
    
    public ConfigManager(FruitsPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    private void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        rewardEnabled = config.getBoolean("reward-on-join", true);
    }
    
    public void saveConfig() {
        plugin.getConfig().set("reward-on-join", rewardEnabled);
        plugin.saveConfig();
    }
    
    public boolean isRewardEnabled() {
        return rewardEnabled;
    }
    
    public void setRewardEnabled(boolean enabled) {
        this.rewardEnabled = enabled;
        saveConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }
}
