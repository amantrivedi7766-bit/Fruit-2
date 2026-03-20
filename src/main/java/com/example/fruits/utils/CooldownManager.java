package com.example.fruits.utils;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import com.example.fruits.FruitsPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<String, Long> cooldowns = new HashMap<>();
    private final Map<UUID, BossBar> activeBars = new HashMap<>();

    public void setCooldown(Player player, String abilityKey, int seconds, String abilityName) {  // ✅ 4 parameters
        String key = player.getUniqueId() + "_" + abilityKey;
        cooldowns.put(key, System.currentTimeMillis() + (seconds * 1000L));
        
        BossBar bar = Bukkit.createBossBar(
            "§6⏳ " + abilityName + " §c" + seconds + "s",
            BarColor.RED,
            BarStyle.SEGMENTED_12
        );
        bar.addPlayer(player);
        bar.setProgress(1.0);
        activeBars.put(player.getUniqueId(), bar);
        
        startCooldownTimer(player, abilityKey, seconds, bar);
    }

    private void startCooldownTimer(Player player, String abilityKey, int totalSeconds, BossBar bar) {
        Bukkit.getScheduler().runTaskTimer(FruitsPlugin.getInstance(), task -> {
            if(!player.isOnline()) {
                bar.removeAll();
                activeBars.remove(player.getUniqueId());
                task.cancel();
                return;
            }
            
            long remaining = getRemaining(player, abilityKey);
            
            if(remaining <= 0) {
                bar.setTitle("§a✅ Ready!");
                bar.setColor(BarColor.GREEN);
                bar.setProgress(0);
                
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                    TextComponent.fromLegacyText("§a⚡ Ability ready!"));
                
                Bukkit.getScheduler().runTaskLater(FruitsPlugin.getInstance(), () -> {
                    bar.removeAll();
                    activeBars.remove(player.getUniqueId());
                }, 40L);
                
                task.cancel();
                return;
            }
            
            double progress = remaining / (double) totalSeconds;
            bar.setProgress(progress);
            
            String barText = createProgressBar(remaining, totalSeconds);
            bar.setTitle("§6⏳ " + barText + " §c" + remaining + "s");
            
            if(remaining > totalSeconds * 0.66) {
                bar.setColor(BarColor.RED);
            } else if(remaining > totalSeconds * 0.33) {
                bar.setColor(BarColor.YELLOW);
            } else {
                bar.setColor(BarColor.GREEN);
            }
            
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
                TextComponent.fromLegacyText("§c⏳ " + barText + " §e" + remaining + "s"));
                
        }, 0L, 10L);
    }

    private String createProgressBar(long remaining, int total) {
        int percent = (int) ((remaining * 100) / total);
        int bars = 20 - (percent / 5);
        
        StringBuilder bar = new StringBuilder();
        bar.append("§a" + "█".repeat(Math.max(0, 20 - bars)));
        bar.append("§7" + "█".repeat(Math.max(0, bars)));
        
        return bar.toString();
    }

    public boolean checkCooldown(Player player, String abilityKey) {
        String key = player.getUniqueId() + "_" + abilityKey;
        if(!cooldowns.containsKey(key)) return true;
        
        long remaining = cooldowns.get(key) - System.currentTimeMillis();
        if(remaining <= 0) {
            cooldowns.remove(key);
            return true;
        }
        
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
}
