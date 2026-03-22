package com.example.fruits.utils;

import org.bukkit.entity.Player;

import java.util.*;

public class CooldownManager {
    
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    
    public boolean hasCooldown(Player player, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if(playerCooldowns == null) return false;
        
        Long expiry = playerCooldowns.get(key);
        if(expiry == null) return false;
        
        return System.currentTimeMillis() < expiry;
    }
    
    public long getRemaining(Player player, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if(playerCooldowns == null) return 0;
        
        Long expiry = playerCooldowns.get(key);
        if(expiry == null) return 0;
        
        long remaining = (expiry - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }
    
    public void setCooldown(Player player, String key, int seconds, String abilityName) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(key, System.currentTimeMillis() + (seconds * 1000L));
    }
    
    public Map<String, Long> getPlayerCooldowns(Player player) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if(playerCooldowns == null) return new HashMap<>();
        
        Map<String, Long> active = new HashMap<>();
        long now = System.currentTimeMillis();
        for(Map.Entry<String, Long> entry : playerCooldowns.entrySet()) {
            if(entry.getValue() > now) {
                active.put(entry.getKey(), entry.getValue());
            }
        }
        return active;
    }
    
    public void clearCooldown(Player player, String key) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if(playerCooldowns != null) {
            playerCooldowns.remove(key);
        }
    }
    
    public void clearAllCooldowns(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
