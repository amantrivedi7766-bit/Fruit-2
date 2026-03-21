package com.example.fruits.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerFruitData {
    private final UUID playerId;
    private String currentFruit;
    private long lastUsed;
    private int usedCount;
    private Map<String, Integer> abilityUsage = new HashMap<>();
    
    public PlayerFruitData(UUID playerId, String fruitId) {
        this.playerId = playerId;
        this.currentFruit = fruitId;
        this.lastUsed = System.currentTimeMillis();
        this.usedCount = 0;
    }
    
    public UUID getPlayerId() { return playerId; }
    
    public String getCurrentFruit() { return currentFruit; }
    public void setCurrentFruit(String fruitId) { this.currentFruit = fruitId; }
    
    public long getLastUsed() { return lastUsed; }
    public void setLastUsed(long time) { this.lastUsed = time; }
    public void updateLastUsed() { this.lastUsed = System.currentTimeMillis(); }
    
    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int count) { this.usedCount = count; }
    public void incrementUsed() { this.usedCount++; }
    
    // ==================== ALIAS METHODS FOR COMPATIBILITY ====================
    
    public String getFruit() {
        return currentFruit;
    }
    
    public void setFruit(String fruitId) {
        this.currentFruit = fruitId;
    }
    
    public Map<String, Integer> getAbilityUsage() {
        return abilityUsage;
    }
    
    public int getUsedAbilities() {
        return usedCount;
    }
    
    public void incrementUsedAbilities() {
        this.usedCount++;
    }
    
    public void recordAbilityUse(String abilityId) {
        abilityUsage.put(abilityId, abilityUsage.getOrDefault(abilityId, 0) + 1);
        usedCount++;
    }
    
    public int getAbilityUseCount(String abilityId) {
        return abilityUsage.getOrDefault(abilityId, 0);
    }
    
    public void resetStats() {
        usedCount = 0;
        abilityUsage.clear();
    }
}
