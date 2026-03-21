package com.example.fruits.manager;

import org.bukkit.entity.Player;
import java.util.*;

public class GracePeriodManager {
    private final Map<UUID, Long> gracePeriods = new HashMap<>();
    private final int DEFAULT_GRACE_SECONDS = 30;
    
    public void setGracePeriod(Player player, int seconds) {
        long expiry = System.currentTimeMillis() + (seconds * 1000L);
        gracePeriods.put(player.getUniqueId(), expiry);
        player.sendMessage("§e🛡️ Grace period activated for " + seconds + " seconds!");
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
    
    public void clearAll() {
        gracePeriods.clear();
    }
}
