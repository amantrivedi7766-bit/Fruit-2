package com.example.fruits.managers;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.utils.CinematicSpinWheel;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpinManager {
    
    private final FruitsPlugin plugin;
    private final Map<UUID, Boolean> spinningPlayers = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> dailySpins = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastSpinTime = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitRunnable> activeSpins = new ConcurrentHashMap<>();
    
    public SpinManager(FruitsPlugin plugin) {
        this.plugin = plugin;
        startDailyResetTask();
    }
    
    /**
     * Start spin with default 1 spin
     */
    public void startSpin(Player player) {
        startSpin(player, 1);
    }
    
    /**
     * Start spin with specified number of spins
     */
    public void startSpin(Player player, int spins) {
        UUID uuid = player.getUniqueId();
        
        // Check if already spinning
        if (spinningPlayers.getOrDefault(uuid, false)) {
            player.sendMessage("§cYou are already spinning!");
            return;
        }
        
        // Check daily limit
        int maxSpins = plugin.getConfigManager().getMaxSpinsPerDay();
        int spinsToday = dailySpins.getOrDefault(uuid, 0);
        
        if (spinsToday >= maxSpins && maxSpins > 0) {
            player.sendMessage("§cYou have reached your daily spin limit (" + maxSpins + " spins)!");
            return;
        }
        
        // Check cooldown
        long lastSpin = lastSpinTime.getOrDefault(uuid, 0L);
        long cooldown = plugin.getConfigManager().getSpinCooldown() * 1000L;
        long remaining = (lastSpin + cooldown) - System.currentTimeMillis();
        
        if (remaining > 0 && cooldown > 0) {
            long seconds = remaining / 1000;
            player.sendMessage("§cSpin on cooldown! §7" + seconds + " seconds remaining");
            return;
        }
        
        // Mark as spinning
        spinningPlayers.put(uuid, true);
        
        // Start cinematic spin
        player.sendMessage("§a✨ Starting cinematic spin! Get ready! ✨");
        CinematicSpinWheel spinWheel = new CinematicSpinWheel(plugin, player);
        spinWheel.startSpin();
        
        // Update stats
        dailySpins.put(uuid, spinsToday + spins);
        lastSpinTime.put(uuid, System.currentTimeMillis());
        
        // Auto-unmark after spin duration
        BukkitRunnable unmarkTask = new BukkitRunnable() {
            @Override
            public void run() {
                spinningPlayers.put(uuid, false);
                activeSpins.remove(uuid);
            }
        };
        unmarkTask.runTaskLater(plugin, 400L); // 20 seconds
        activeSpins.put(uuid, unmarkTask);
        
        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info(player.getName() + " started spin (" + spins + " spins)");
        }
    }
    
    /**
     * Stop spin for a specific player
     */
    public void stopSpin(Player player) {
        UUID uuid = player.getUniqueId();
        spinningPlayers.put(uuid, false);
        
        BukkitRunnable task = activeSpins.remove(uuid);
        if (task != null) {
            task.cancel();
        }
        
        player.sendMessage("§cYour spin has been stopped!");
        
        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info(player.getName() + "'s spin was stopped");
        }
    }
    
    /**
     * Stop all active spins
     */
    public void stopAllSpins() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            stopSpin(player);
        }
        spinningPlayers.clear();
        activeSpins.clear();
        plugin.getLogger().info("All spins stopped!");
    }
    
    /**
     * Check if player is currently spinning
     */
    public boolean isSpinning(Player player) {
        return spinningPlayers.getOrDefault(player.getUniqueId(), false);
    }
    
    /**
     * Get daily spin count for a player
     */
    public int getDailySpins(Player player) {
        return dailySpins.getOrDefault(player.getUniqueId(), 0);
    }
    
    /**
     * Reset daily spins for a specific player
     */
    public void resetDailySpins(Player player) {
        dailySpins.put(player.getUniqueId(), 0);
        player.sendMessage("§aYour daily spins have been reset!");
    }
    
    /**
     * Reset daily spins for all players
     */
    public void resetAllDailySpins() {
        dailySpins.clear();
        plugin.getLogger().info("All daily spins reset!");
        
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage("§aDaily spins have been reset!");
        }
    }
    
    /**
     * Start daily reset task (runs every 24 hours)
     */
    private void startDailyResetTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                resetAllDailySpins();
                plugin.getLogger().info("Daily spin limits reset!");
            }
        }.runTaskTimer(plugin, 86400L, 86400L); // 24 hours (20 ticks * 60 * 60 * 24)
    }
    
    /**
     * Get remaining cooldown time in seconds
     */
    public long getSpinCooldownRemaining(Player player) {
        long lastSpin = lastSpinTime.getOrDefault(player.getUniqueId(), 0L);
        long cooldown = plugin.getConfigManager().getSpinCooldown() * 1000L;
        long remaining = (lastSpin + cooldown) - System.currentTimeMillis();
        return remaining > 0 ? remaining / 1000 : 0;
    }
    
    /**
     * Get formatted remaining cooldown time
     */
    public String getFormattedCooldownRemaining(Player player) {
        long seconds = getSpinCooldownRemaining(player);
        if (seconds <= 0) return "§aReady!";
        
        if (seconds < 60) {
            return "§e" + seconds + "s";
        } else {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return "§e" + minutes + "m " + remainingSeconds + "s";
        }
    }
    
    /**
     * Clear all spin data for a player (for reset purposes)
     */
    public void clearPlayerSpinData(Player player) {
        UUID uuid = player.getUniqueId();
        spinningPlayers.remove(uuid);
        dailySpins.remove(uuid);
        lastSpinTime.remove(uuid);
        
        BukkitRunnable task = activeSpins.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }
    
    /**
     * Get all players currently spinning
     */
    public Set<Player> getSpinningPlayers() {
        Set<Player> spinning = new HashSet<>();
        for (Map.Entry<UUID, Boolean> entry : spinningPlayers.entrySet()) {
            if (entry.getValue()) {
                Player player = plugin.getServer().getPlayer(entry.getKey());
                if (player != null) {
                    spinning.add(player);
                }
            }
        }
        return spinning;
    }
    
    /**
     * Get all daily spin counts
     */
    public Map<UUID, Integer> getAllDailySpins() {
        return new HashMap<>(dailySpins);
    }
}
