package com.example.fruits.utils;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class CooldownManager {
    private final Map<String, Long> cooldowns = new HashMap<>();
    
    public void setCooldown(Player player, String key, int seconds, String name) {
        cooldowns.put(player.getUniqueId() + "_" + key, System.currentTimeMillis() + (seconds * 1000L));
    }
    
    public boolean checkCooldown(Player player, String key) {
        String k = player.getUniqueId() + "_" + key;
        if(!cooldowns.containsKey(k)) return true;
        if(cooldowns.get(k) <= System.currentTimeMillis()) {
            cooldowns.remove(k);
            return true;
        }
        long remaining = (cooldowns.get(k) - System.currentTimeMillis()) / 1000;
        player.sendMessage("§c⏳ Cooldown: §e" + remaining + "s");
        return false;
    }
    
    public long getRemaining(Player player, String key) {
        String k = player.getUniqueId() + "_" + key;
        if(!cooldowns.containsKey(k)) return 0;
        return Math.max(0, (cooldowns.get(k) - System.currentTimeMillis()) / 1000);
    }
}
