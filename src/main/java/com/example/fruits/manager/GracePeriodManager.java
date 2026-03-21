package com.example.fruits.manager;

import org.bukkit.Bukkit;
package com.example.fruits.managers;

import com.example.fruits.FruitsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GracePeriodManager {
    
    private final FruitsPlugin plugin;
    private final Map<UUID, GracePeriod> activePeriods = new ConcurrentHashMap<>();
    private final Map<UUID, List<Runnable>> pendingActions = new HashMap<>();
    
    public GracePeriodManager(FruitsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void startGracePeriod(Player player, int durationSeconds, String reason) {
        UUID uuid = player.getUniqueId();
        
        // Cancel existing period if any
        if (activePeriods.containsKey(uuid)) {
            cancelGracePeriod(player);
        }
        
        GracePeriod period = new GracePeriod(player, durationSeconds, reason);
        activePeriods.put(uuid, period);
        
        player.sendMessage("§e⚠ Grace period started: " + reason);
        player.sendMessage("§7You have " + durationSeconds + " seconds of protection!");
        
        // Auto-end after duration
        period.task = new BukkitRunnable() {
            @Override
            public void run() {
                endGracePeriod(player);
            }
        }.runTaskLater(plugin, durationSeconds * 20L);
    }
    
    public void endGracePeriod(Player player) {
        UUID uuid = player.getUniqueId();
        GracePeriod period = activePeriods.remove(uuid);
        
        if (period != null) {
            if (period.task != null) {
                period.task.cancel();
            }
            
            player.sendMessage("§a✓ Grace period ended!");
            
            // Execute pending actions
            List<Runnable> actions = pendingActions.remove(uuid);
            if (actions != null) {
                for (Runnable action : actions) {
                    action.run();
                }
            }
        }
    }
    
    public void cancelGracePeriod(Player player) {
        UUID uuid = player.getUniqueId();
        GracePeriod period = activePeriods.remove(uuid);
        
        if (period != null) {
            if (period.task != null) {
                period.task.cancel();
            }
            player.sendMessage("§c✗ Grace period cancelled!");
        }
    }
    
    public boolean isInGracePeriod(Player player) {
        return activePeriods.containsKey(player.getUniqueId());
    }
    
    public int getRemainingTime(Player player) {
        GracePeriod period = activePeriods.get(player.getUniqueId());
        if (period == null) return 0;
        
        long elapsed = System.currentTimeMillis() - period.startTime;
        long remaining = (period.durationSeconds * 1000L) - elapsed;
        return remaining > 0 ? (int) (remaining / 1000) : 0;
    }
    
    public String getGracePeriodReason(Player player) {
        GracePeriod period = activePeriods.get(player.getUniqueId());
        return period != null ? period.reason : null;
    }
    
    public void addPendingAction(Player player, Runnable action) {
        pendingActions.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(action);
    }
    
    public void startProtectionOnJoin(Player player) {
        // Give 10 seconds of protection on join
        startGracePeriod(player, 10, "Join Protection");
    }
    
    public void startProtectionAfterDeath(Player player) {
        // Give 5 seconds of protection after respawn
        startGracePeriod(player, 5, "Death Protection");
    }
    
    public void startPvPProtection(Player player, int seconds) {
        startGracePeriod(player, seconds, "PvP Protection");
    }
    
    public void removeAllGracePeriods() {
        for (UUID uuid : new ArrayList<>(activePeriods.keySet())) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                endGracePeriod(player);
            } else {
                activePeriods.remove(uuid);
            }
        }
    }
    
    public Map<UUID, GracePeriod> getAllActivePeriods() {
        return new HashMap<>(activePeriods);
    }
    
    private static class GracePeriod {
        final Player player;
        final int durationSeconds;
        final String reason;
        final long startTime;
        BukkitRunnable task;
        
        GracePeriod(Player player, int durationSeconds, String reason) {
            this.player = player;
            this.durationSeconds = durationSeconds;
            this.reason = reason;
            this.startTime = System.currentTimeMillis();
        }
    }
}
