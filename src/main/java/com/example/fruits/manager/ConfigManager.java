package com.example.fruits.managers;

import com.example.fruits.FruitsPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    
    private final FruitsPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration messagesConfig;
    private FileConfiguration dataConfig;
    
    private File configFile;
    private File messagesFile;
    private File dataFile;
    
    // Config values
    private boolean autoGiveEnabled;
    private String autoGiveFruitId;
    private int autoGiveAmount;
    private boolean joinFruitEnabled;
    private String joinFruitId;
    private int joinFruitAmount;
    private int spinCooldown;
    private int stealCooldown;
    private int maxSpinsPerDay;
    private boolean debugMode;
    private boolean rewardEnabled;
    
    public ConfigManager(FruitsPlugin plugin) {
        this.plugin = plugin;
        loadAllConfigs();
    }
    
    private void loadAllConfigs() {
        loadMainConfig();
        loadMessagesConfig();
        loadDataConfig();
    }
    
    private void loadMainConfig() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
        
        // Load values
        autoGiveEnabled = config.getBoolean("auto-give.enabled", false);
        autoGiveFruitId = config.getString("auto-give.fruit-id", "nature_fruit");
        autoGiveAmount = config.getInt("auto-give.amount", 1);
        
        joinFruitEnabled = config.getBoolean("join-fruit.enabled", true);
        joinFruitId = config.getString("join-fruit.id", "nature_fruit");
        joinFruitAmount = config.getInt("join-fruit.amount", 1);
        
        spinCooldown = config.getInt("spin-wheel.cooldown", 60);
        stealCooldown = config.getInt("steal.cooldown", 60);
        maxSpinsPerDay = config.getInt("spin-wheel.max-spins", 10);
        debugMode = config.getBoolean("settings.debug", false);
    }
    
    private void loadMessagesConfig() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try {
                messagesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create messages.yml: " + e.getMessage());
            }
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    private void loadDataConfig() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Could not create data.yml: " + e.getMessage());
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        // Load reward enabled
        rewardEnabled = dataConfig.getBoolean("reward-enabled", true);
    }
    
    public void saveMainConfig() {
        config.set("auto-give.enabled", autoGiveEnabled);
        config.set("auto-give.fruit-id", autoGiveFruitId);
        config.set("auto-give.amount", autoGiveAmount);
        config.set("join-fruit.enabled", joinFruitEnabled);
        config.set("join-fruit.id", joinFruitId);
        config.set("join-fruit.amount", joinFruitAmount);
        config.set("spin-wheel.cooldown", spinCooldown);
        config.set("steal.cooldown", stealCooldown);
        config.set("spin-wheel.max-spins", maxSpinsPerDay);
        config.set("settings.debug", debugMode);
        
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config.yml: " + e.getMessage());
        }
    }
    
    public void saveDataConfig() {
        dataConfig.set("reward-enabled", rewardEnabled);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml: " + e.getMessage());
        }
    }
    
    public void reload() {
        loadAllConfigs();
        plugin.getLogger().info("Configurations reloaded!");
    }
    
    // ==================== GETTERS ====================
    
    public boolean isAutoGiveEnabled() { return autoGiveEnabled; }
    public String getAutoGiveFruitId() { return autoGiveFruitId; }
    public int getAutoGiveAmount() { return autoGiveAmount; }
    
    public boolean isJoinFruitEnabled() { return joinFruitEnabled; }
    public String getJoinFruitId() { return joinFruitId; }
    public int getJoinFruitAmount() { return joinFruitAmount; }
    
    public int getSpinCooldown() { return spinCooldown; }
    public int getStealCooldown() { return stealCooldown; }
    public int getMaxSpinsPerDay() { return maxSpinsPerDay; }
    public boolean isDebugMode() { return debugMode; }
    
    public boolean isRewardEnabled() { return rewardEnabled; }
    
    public FileConfiguration getMessages() { return messagesConfig; }
    public FileConfiguration getData() { return dataConfig; }
    
    // ==================== SETTERS ====================
    
    public void setAutoGiveEnabled(boolean enabled) {
        this.autoGiveEnabled = enabled;
        saveMainConfig();
    }
    
    public void setAutoGiveFruitId(String fruitId) {
        this.autoGiveFruitId = fruitId;
        saveMainConfig();
    }
    
    public void setAutoGiveAmount(int amount) {
        this.autoGiveAmount = amount;
        saveMainConfig();
    }
    
    public void setJoinFruitEnabled(boolean enabled) {
        this.joinFruitEnabled = enabled;
        saveMainConfig();
    }
    
    public void setJoinFruitId(String fruitId) {
        this.joinFruitId = fruitId;
        saveMainConfig();
    }
    
    public void setJoinFruitAmount(int amount) {
        this.joinFruitAmount = amount;
        saveMainConfig();
    }
    
    public void setRewardEnabled(boolean enabled) {
        this.rewardEnabled = enabled;
        saveDataConfig();
    }
    
    // ==================== DATA MANAGEMENT ====================
    
    public int getPlayerSpinCount(String playerName) {
        return dataConfig.getInt("spins." + playerName, 0);
    }
    
    public void setPlayerSpinCount(String playerName, int count) {
        dataConfig.set("spins." + playerName, count);
        saveDataConfig();
    }
    
    public void incrementPlayerSpinCount(String playerName) {
        int current = getPlayerSpinCount(playerName);
        setPlayerSpinCount(playerName, current + 1);
    }
    
    public void resetPlayerSpinCount(String playerName) {
        setPlayerSpinCount(playerName, 0);
    }
    
    public Map<String, Object> getPlayerData(String playerName) {
        Map<String, Object> data = new HashMap<>();
        if (dataConfig.contains("players." + playerName)) {
            data = dataConfig.getConfigurationSection("players." + playerName).getValues(false);
        }
        return data;
    }
    
    public void setPlayerData(String playerName, String key, Object value) {
        dataConfig.set("players." + playerName + "." + key, value);
        saveDataConfig();
    }
    
    public String getMessage(String key) {
        return messagesConfig.getString(key, "§cMessage not found: " + key);
    }
    
    public String getMessage(String key, String defaultValue) {
        return messagesConfig.getString(key, defaultValue);
    }
}
