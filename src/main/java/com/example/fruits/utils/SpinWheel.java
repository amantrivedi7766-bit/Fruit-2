package com.example.fruits.utils;

import com.example.fruits.FruitsPlugin;
import org.bukkit.entity.Player;

public class SpinWheel {
    
    private static CinematicSpinWheel activeSpin = null;
    
    public static void startCinematicSpin(FruitsPlugin plugin, Player player, int spins) {
        if(activeSpin != null) {
            player.sendMessage("§cA spin is already in progress!");
            return;
        }
        
        CinematicSpinWheel spin = new CinematicSpinWheel(plugin, player);
        spin.startSpin();
        activeSpin = spin;
        
        // Auto clear after spin
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                activeSpin = null;
            }
        }.runTaskLater(plugin, 400L); // 20 seconds
    }
    
    public static void stopSpin(Player player) {
        // Implementation if needed
    }
    
    public static CinematicSpinWheel getActiveSpin(Player player) {
        return activeSpin;
    }
    
    public static int getRewardCount() {
        return 10; // Number of fruits
    }
}
