package com.example.fruits.utils;

import com.example.fruits.FruitsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.Map;

public class CooldownManager {
    private final Map<String, Long> cooldowns = new HashMap<>();
    
    public void setCooldown(Player player, String key, int seconds, String name) {
        String fullKey = player.getUniqueId() + "_" + key;
        cooldowns.put(fullKey, System.currentTimeMillis() + (seconds * 1000L));
        
        // Show XP bar cooldown with timer
        player.setExp(1.0f);
        player.setLevel(seconds);
        
        new BukkitRunnable() {
            int timeLeft = seconds;
            @Override
            public void run() {
                if(timeLeft <= 0) {
                    player.setExp(0);
                    player.setLevel(0);
                    player.sendTitle("§a✅ " + name + " Ready!", "", 5, 20, 5);
                    this.cancel();
                    return;
                }
                
                float progress = (float) timeLeft / seconds;
                player.setExp(progress);
                player.setLevel(timeLeft);
                
                // Send action bar with timer
                player.sendActionBar("§c⏳ " + name + " §7| §e" + timeLeft + "s");
                
                timeLeft--;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 20L);
    }
    
    public boolean checkCooldown(Player player, String key) {
        String fullKey = player.getUniqueId() + "_" + key;
        if(!cooldowns.containsKey(fullKey)) return true;
        
        long remaining = cooldowns.get(fullKey) - System.currentTimeMillis();
        if(remaining <= 0) {
            cooldowns.remove(fullKey);
            return true;
        }
        
        int seconds = (int) (remaining / 1000);
        player.sendActionBar("§c⏳ Cooldown: §e" + seconds + "s");
        return false;
    }
    
    public long getRemaining(Player player, String key) {
        String fullKey = player.getUniqueId() + "_" + key;
        if(!cooldowns.containsKey(fullKey)) return 0;
        return Math.max(0, (cooldowns.get(fullKey) - System.currentTimeMillis()) / 1000);
    }
}
