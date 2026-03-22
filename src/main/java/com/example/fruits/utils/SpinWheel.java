package com.example.fruits.utils;

import com.example.fruits.FruitsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SpinWheel {
    
    private static final Map<UUID, CinematicSpinWheel> activeSpins = new HashMap<>();
    
    public static void startCinematicSpin(FruitsPlugin plugin, Player player, int spins) {
        UUID uuid = player.getUniqueId();
        
        if(activeSpins.containsKey(uuid)) {
            player.sendMessage("§cYou already have an active spin!");
            return;
        }
        
        CinematicSpinWheel spin = new CinematicSpinWheel(plugin, player);
        spin.startSpin();
        activeSpins.put(uuid, spin);
        
        // Auto clear after spin
        new BukkitRunnable() {
            @Override
            public void run() {
                activeSpins.remove(uuid);
            }
        }.runTaskLater(plugin, 400L); // 20 seconds
    }
    
    public static void stopSpin(Player player) {
        UUID uuid = player.getUniqueId();
        CinematicSpinWheel spin = activeSpins.remove(uuid);
        if(spin != null) {
            // Call stop method if available
            player.sendMessage("§cYour spin has been stopped!");
        }
    }
    
    public static void stopAllSpins() {
        for (UUID uuid : new ArrayList<>(activeSpins.keySet())) {
            Player player = FruitsPlugin.getInstance().getServer().getPlayer(uuid);
            if (player != null) {
                stopSpin(player);
            }
        }
        activeSpins.clear();
        FruitsPlugin.getInstance().getLogger().info("All spins stopped!");
    }
    
    public static CinematicSpinWheel getActiveSpin(Player player) {
        return activeSpins.get(player.getUniqueId());
    }
    
    public static boolean isSpinning(Player player) {
        return activeSpins.containsKey(player.getUniqueId());
    }
    
    public static int getRewardCount() {
        return 10; // Number of fruits
    }
    
    public static Map<UUID, CinematicSpinWheel> getAllActiveSpins() {
        return new HashMap<>(activeSpins);
    }
}
