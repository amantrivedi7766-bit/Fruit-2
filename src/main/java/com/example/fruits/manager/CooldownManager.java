package com.example.fruits.utils;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {
    
    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();
    
    /**
     * Check if a player has an active cooldown for a specific key
     */
    public boolean hasCooldown(Player player, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return false;
        
        Long expiry = playerCooldowns.get(key);
        if (expiry == null) return false;
        
        return System.currentTimeMillis() < expiry;
    }
    
    /**
     * Check cooldown and return boolean (alias for hasCooldown)
     */
    public boolean checkCooldown(Player player, String key) {
        return hasCooldown(player, key);
    }
    
    /**
     * Get remaining cooldown time in seconds
     */
    public long getRemaining(Player player, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return 0;
        
        Long expiry = playerCooldowns.get(key);
        if (expiry == null) return 0;
        
        long remaining = (expiry - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }
    
    /**
     * Set a cooldown for a player
     */
    public void setCooldown(Player player, String key, int seconds, String abilityName) {
        Map<String, Long> playerCooldowns = cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        playerCooldowns.put(key, System.currentTimeMillis() + (seconds * 1000L));
    }
    
    /**
     * Get all active cooldowns for a player
     */
    public Map<String, Long> getPlayerCooldowns(Player player) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return new HashMap<>();
        
        Map<String, Long> active = new HashMap<>();
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : playerCooldowns.entrySet()) {
            if (entry.getValue() > now) {
                active.put(entry.getKey(), entry.getValue());
            }
        }
        return active;
    }
    
    /**
     * Clear a specific cooldown for a player
     */
    public void clearCooldown(Player player, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns != null) {
            playerCooldowns.remove(key);
        }
    }
    
    /**
     * Clear all cooldowns for a player
     */
    public void clearAllCooldowns(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
    
    /**
     * Clear all cooldowns for all players
     */
    public void clearAllCooldowns() {
        cooldowns.clear();
    }
    
    /**
     * Get formatted cooldown time string
     */
    public String getFormattedRemaining(Player player, String key) {
        long seconds = getRemaining(player, key);
        if (seconds <= 0) return "§aReady!";
        
        if (seconds < 60) {
            return "§e" + seconds + "s";
        } else {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return "§e" + minutes + "m " + remainingSeconds + "s";
        }
    }
}
