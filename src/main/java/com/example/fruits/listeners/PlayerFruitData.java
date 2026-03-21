package com.example.fruits.models;

import java.util.UUID;

public class PlayerFruitData {
    private final UUID playerId;
    private String currentFruit;
    private long lastUsed;
    
    public PlayerFruitData(UUID playerId, String fruitId) {
        this.playerId = playerId;
        this.currentFruit = fruitId;
        this.lastUsed = System.currentTimeMillis();
    }
    
    public UUID getPlayerId() { return playerId; }
    public String getCurrentFruit() { return currentFruit; }
    public long getLastUsed() { return lastUsed; }
    
    public void setCurrentFruit(String fruitId) { this.currentFruit = fruitId; }
    public void setLastUsed(long time) { this.lastUsed = time; }
    public void updateLastUsed() { this.lastUsed = System.currentTimeMillis(); }
}
