package com.example.fruits.utils;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import com.example.fruits.FruitsPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<String, Long> cooldowns = new HashMap<>();
    private final Map<UUID, String> activeBar = new HashMap<>();

    public void setCooldown(Player player, String abilityKey, int seconds) {
        String key = player.getUniqueId() + "_" + abilityKey;
        cooldowns.put(key, System.currentTimeMillis() + (seconds * 1000L));
        
        // Show cooldown bar
        showCooldownBar(player, abilityKey, seconds);
    }

    public boolean checkCooldown(Player player, String abilityKey) {
        String key = player.getUniqueId() + "_" + abilityKey;
        if(!cooldowns.containsKey(key)) return true;
        
        long remaining = cooldowns.get(key) - System.currentTimeMillis();
        if(remaining <= 0) {
            cooldowns.remove(key);
            return true;
        }
        
        // Show remaining time
        int seconds = (int) (remaining / 1000);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
            TextComponent.fromLegacyText("§c⏳ Cooldown: §e" + seconds + "s"));
        
        return false;
    }

    public long getRemaining(Player player, String abilityKey) {
        String key = player.getUniqueId() + "_" + abilityKey;
        if(!cooldowns.containsKey(key)) return 0;
        long remaining = cooldowns.get(key) - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    private void showCooldownBar(Player player, String abilityKey, int totalSeconds) {
        activeBar.put(player.getUniqueId(), abilityKey);
        
        Bukkit.getScheduler().runTaskTimer(FruitsPlugin.getInstance(), task -> {
            if(!activeBar.containsKey(player.getUniqueId())) {
                task.cancel();
                return;
            }
            
            long remaining = getRemaining(player, abilityKey);
            if(remaining <= 0) {
                activeBar.remove(player.getUniqueId());
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                    TextComponent.fromLegacyText("§a✅ Ability ready!"));
                task.cancel();
                return;
            }
            
            // Create progress bar
            int percent = (int) ((remaining * 100) / totalSeconds);
            int bars = 20 - (percent / 5);
            String bar = "§a" + "█".repeat(Math.max(0, 20 - bars)) + 
                        "§7" + "█".repeat(Math.max(0, bars));
            
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                TextComponent.fromLegacyText("§e⏳ " + bar + " §c" + remaining + "s"));
                
        }, 0L, 10L); // Update every 0.5 seconds
    }
}
