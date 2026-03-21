package com.example.fruits.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;

public class GracePeriodManager {
    private final Map<UUID, Long> gracePeriods = new HashMap<>();
    private final int DEFAULT_GRACE_SECONDS = 30;
    private boolean globalGraceActive = false;
    private long globalGraceExpiry = 0;

    // ==================== PLAYER GRACE PERIOD ====================
    
    public void setGracePeriod(Player player, int seconds) {
        long expiry = System.currentTimeMillis() + (seconds * 1000L);
        gracePeriods.put(player.getUniqueId(), expiry);
        player.sendMessage("§e🛡️ Grace period activated for " + seconds + " seconds!");
    }
    
    public void startGracePeriod(Player player, int seconds) {
        setGracePeriod(player, seconds);
    }
    
    public boolean hasGracePeriod(Player player) {
        if(!gracePeriods.containsKey(player.getUniqueId())) {
            return false;
        }
        
        long expiry = gracePeriods.get(player.getUniqueId());
        if(System.currentTimeMillis() > expiry) {
            gracePeriods.remove(player.getUniqueId());
            return false;
        }
        
        return true;
    }
    
    public boolean isGraceActive(Player player) {
        return hasGracePeriod(player);
    }
    
    public long getRemainingTime(Player player) {
        if(!gracePeriods.containsKey(player.getUniqueId())) {
            return 0;
        }
        
        long remaining = gracePeriods.get(player.getUniqueId()) - System.currentTimeMillis();
        return Math.max(0, remaining);
    }
    
    public void removeGracePeriod(Player player) {
        gracePeriods.remove(player.getUniqueId());
        player.sendMessage("§c🛡️ Grace period removed!");
    }
    
    // ==================== GLOBAL GRACE PERIOD ====================
    
    public void startGlobalGrace(int seconds) {
        globalGraceActive = true;
        globalGraceExpiry = System.currentTimeMillis() + (seconds * 1000L);
        
        // Broadcast to all players
        Bukkit.broadcastMessage("§6§l=================================");
        Bukkit.broadcastMessage("§e🛡️ GLOBAL GRACE PERIOD ACTIVATED!");
        Bukkit.broadcastMessage("§7All players are protected for §e" + seconds + " §7seconds!");
        Bukkit.broadcastMessage("§6§l=================================");
        
        // Schedule end
        Bukkit.getScheduler().runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), () -> {
            endGlobalGrace();
        }, seconds * 20L);
    }
    
    public void endGlobalGrace() {
        globalGraceActive = false;
        globalGraceExpiry = 0;
        
        Bukkit.broadcastMessage("§6§l=================================");
        Bukkit.broadcastMessage("§c🛡️ GLOBAL GRACE PERIOD ENDED!");
        Bukkit.broadcastMessage("§7Players can now attack each other!");
        Bukkit.broadcastMessage("§6§l=================================");
    }
    
    public boolean isGlobalGraceActive() {
        if(!globalGraceActive) {
            return false;
        }
        
        if(System.currentTimeMillis() > globalGraceExpiry) {
            globalGraceActive = false;
            return false;
        }
        
        return true;
    }
    
    public long getGlobalGraceRemaining() {
        if(!globalGraceActive) {
            return 0;
        }
        return Math.max(0, globalGraceExpiry - System.currentTimeMillis());
    }
    
    public void cancelGlobalGrace() {
        globalGraceActive = false;
        globalGraceExpiry = 0;
        Bukkit.broadcastMessage("§c🛡️ Global grace period cancelled!");
    }
    
    // ==================== UTILITY ====================
    
    public void clearAll() {
        gracePeriods.clear();
        globalGraceActive = false;
        globalGraceExpiry = 0;
    }
}
