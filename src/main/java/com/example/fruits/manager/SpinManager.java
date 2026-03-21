package com.example.fruits.managers;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.utils.CinematicSpinWheel;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SpinManager {
    
    private final FruitsPlugin plugin;
    private final Map<UUID, Boolean> spinningPlayers = new HashMap<>();
    private final Map<UUID, Integer> dailySpins = new HashMap<>();
    private final Map<UUID, Long> lastSpinTime = new HashMap<>();
    
    public SpinManager(FruitsPlugin plugin) {
        this.plugin = plugin;
        startDailyResetTask();
    }
    
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
            player.sendMessage("§cSpin on cooldown! §7" + (remaining / 1000) + " seconds remaining");
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
        new BukkitRunnable() {
            @Override
            public void run() {
                spinningPlayers.put(uuid, false);
            }
        }.runTaskLater(plugin, 400L);
    }
    
    public void stopSpin(Player player) {
        UUID uuid = player.getUniqueId();
        spinningPlayers.put(uuid, false);
        CinematicSpinWheel.stopSpin(player);
        player.sendMessage("§cYour spin has been stopped!");
    }
    
    public void stopAllSpins() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            stopSpin(player);
        }
        spinningPlayers.clear();
        plugin.getLogger().info("All spins stopped!");
    }
    
    public boolean isSpinning(Player player) {
        return spinningPlayers.getOrDefault(player.getUniqueId(), false);
    }
    
    public int getDailySpins(Player player) {
        return dailySpins.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void resetDailySpins(Player player) {
        dailySpins.put(player.getUniqueId(), 0);
    }
    
    public void resetAllDailySpins() {
        dailySpins.clear();
        plugin.getLogger().info("All daily spins reset!");
    }
    
    private void startDailyResetTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                resetAllDailySpins();
                plugin.getLogger().info("Daily spin limits reset!");
            }
        }.runTaskTimer(plugin, 86400L, 86400L);
    }
    
    public long getSpinCooldownRemaining(Player player) {
        long lastSpin = lastSpinTime.getOrDefault(player.getUniqueId(), 0L);
        long cooldown = plugin.getConfigManager().getSpinCooldown() * 1000L;
        long remaining = (lastSpin + cooldown) - System.currentTimeMillis();
        return remaining > 0 ? remaining / 1000 : 0;
    }
    
    public Map<UUID, Integer> getAllDailySpins() {
        return new HashMap<>(dailySpins);
    }
}
