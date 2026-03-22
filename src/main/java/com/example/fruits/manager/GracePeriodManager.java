package com.example.fruits.managers;

import com.example.fruits.FruitsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GracePeriodManager {
    
    private final FruitsPlugin plugin;
    private final Map<UUID, GracePeriod> activePeriods = new ConcurrentHashMap<>();
    private final Map<UUID, List<Runnable>> pendingActions = new HashMap<>();
    private BukkitTask globalGraceTask;
    private boolean globalGraceActive = false;
    
    public GracePeriodManager(FruitsPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Start a grace period for a specific player
     */
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
    
    /**
     * End grace period for a specific player
     */
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
    
    /**
     * Cancel grace period for a specific player
     */
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
    
    /**
     * Check if player is in grace period
     */
    public boolean isInGracePeriod(Player player) {
        return activePeriods.containsKey(player.getUniqueId());
    }
    
    /**
     * Get remaining time in seconds for a player's grace period
     */
    public int getRemainingTime(Player player) {
        GracePeriod period = activePeriods.get(player.getUniqueId());
        if (period == null) return 0;
        
        long elapsed = System.currentTimeMillis() - period.startTime;
        long remaining = (period.durationSeconds * 1000L) - elapsed;
        return remaining > 0 ? (int) (remaining / 1000) : 0;
    }
    
    /**
     * Get reason for player's grace period
     */
    public String getGracePeriodReason(Player player) {
        GracePeriod period = activePeriods.get(player.getUniqueId());
        return period != null ? period.reason : null;
    }
    
    /**
     * Add a pending action to execute after grace period ends
     */
    public void addPendingAction(Player player, Runnable action) {
        pendingActions.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(action);
    }
    
    /**
     * Start protection on player join (10 seconds)
     */
    public void startProtectionOnJoin(Player player) {
        startGracePeriod(player, 10, "Join Protection");
    }
    
    /**
     * Start protection after player death (5 seconds)
     */
    public void startProtectionAfterDeath(Player player) {
        startGracePeriod(player, 5, "Death Protection");
    }
    
    /**
     * Start PvP protection for a player
     */
    public void startPvPProtection(Player player, int seconds) {
        startGracePeriod(player, seconds, "PvP Protection");
    }
    
    // ==================== GLOBAL GRACE PERIOD METHODS ====================
    
    /**
     * Start a global grace period for all online players
     */
    public void startGlobalGrace(int durationSeconds) {
        if (globalGraceActive) {
            plugin.getLogger().warning("Global grace period already active!");
            return;
        }
        
        globalGraceActive = true;
        
        // Apply to all online players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            startGracePeriod(player, durationSeconds, "Global Grace Period");
        }
        
        // Auto-end after duration
        globalGraceTask = new BukkitRunnable() {
            @Override
            public void run() {
                endGlobalGrace();
            }
        }.runTaskLater(plugin, durationSeconds * 20L);
        
        plugin.getServer().broadcastMessage("§6§l⚠ GLOBAL GRACE PERIOD STARTED! ⚠");
        plugin.getServer().broadcastMessage("§7All players are protected for " + durationSeconds + " seconds!");
    }
    
    /**
     * End the global grace period
     */
    public void endGlobalGrace() {
        if (!globalGraceActive) return;
        
        globalGraceActive = false;
        if (globalGraceTask != null) {
            globalGraceTask.cancel();
            globalGraceTask = null;
        }
        
        // End grace for all players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            endGracePeriod(player);
        }
        
        plugin.getServer().broadcastMessage("§a✓ Global grace period ended!");
    }
    
    /**
     * Check if global grace period is active
     */
    public boolean isGlobalGraceActive() {
        return globalGraceActive;
    }
    
    /**
     * Get remaining time for global grace period (approximate)
     */
    public int getGlobalGraceRemaining() {
        if (!globalGraceActive || globalGraceTask == null) return 0;
        // This is approximate - actual remaining would need tracking
        return 0;
    }
    
    /**
     * Remove all grace periods
     */
    public void removeAllGracePeriods() {
        for (UUID uuid : new ArrayList<>(activePeriods.keySet())) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null) {
                endGracePeriod(player);
            } else {
                activePeriods.remove(uuid);
            }
        }
        
        if (globalGraceActive) {
            endGlobalGrace();
        }
    }
    
    /**
     * Get all active grace periods
     */
    public Map<UUID, GracePeriod> getAllActivePeriods() {
        return new HashMap<>(activePeriods);
    }
    
    /**
     * Check if a player can take damage (not in grace period)
     */
    public boolean canTakeDamage(Player player) {
        return !isInGracePeriod(player);
    }
    
    /**
     * Check if a player can be attacked
     */
    public boolean canBeAttacked(Player attacker, Player target) {
        // If either player is in grace period, attack is prevented
        if (isInGracePeriod(attacker) || isInGracePeriod(target)) {
            return false;
        }
        return true;
    }
    
    // ==================== INNER CLASS ====================
    
    private static class GracePeriod {
        final Player player;
        final int durationSeconds;
        final String reason;
        final long startTime;
        BukkitTask task;
        
        GracePeriod(Player player, int durationSeconds, String reason) {
            this.player = player;
            this.durationSeconds = durationSeconds;
            this.reason = reason;
            this.startTime = System.currentTimeMillis();
        }
    }
}
