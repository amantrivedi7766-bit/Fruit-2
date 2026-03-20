package com.example.fruits.utils;

import com.example.fruits.FruitsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GracePeriodManager {
    private final Map<UUID, BossBar> activeBars = new HashMap<>();
    private final Map<UUID, Integer> remainingTime = new HashMap<>();
    private boolean globalGraceActive = false;
    private BossBar globalBar = null;

    public void startGracePeriod(Player player, int seconds) {
        UUID uuid = player.getUniqueId();
        
        BossBar bar = Bukkit.createBossBar(
            getFormattedTime(seconds),
            BarColor.RED,
            BarStyle.SEGMENTED_12
        );
        bar.addPlayer(player);
        bar.setProgress(1.0);
        activeBars.put(uuid, bar);
        remainingTime.put(uuid, seconds);
        
        player.sendMessage(FruitsPlugin.getInstance().getConfig().getString("messages.grace_start")
            .replace("{time}", String.valueOf(seconds))
            .replace('&', '§'));
        
        startTimer(player, seconds);
    }
    
    public void startGlobalGrace(int seconds) {
        globalGraceActive = true;
        globalBar = Bukkit.createBossBar(
            getFormattedTime(seconds),
            BarColor.RED,
            BarStyle.SEGMENTED_12
        );
        
        for(Player p : Bukkit.getOnlinePlayers()) {
            globalBar.addPlayer(p);
        }
        
        Bukkit.broadcastMessage(FruitsPlugin.getInstance().getConfig().getString("messages.grace_start")
            .replace("{time}", String.valueOf(seconds))
            .replace('&', '§'));
        
        new BukkitRunnable() {
            int timeLeft = seconds;
            @Override
            public void run() {
                if(timeLeft <= 0) {
                    globalGraceActive = false;
                    if(globalBar != null) globalBar.removeAll();
                    Bukkit.broadcastMessage(FruitsPlugin.getInstance().getConfig().getString("messages.grace_end").replace('&', '§'));
                    this.cancel();
                    return;
                }
                
                double progress = timeLeft / (double) seconds;
                globalBar.setProgress(progress);
                globalBar.setTitle(getFormattedTime(timeLeft));
                timeLeft--;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 20L);
    }
    
    private void startTimer(Player player, int totalSeconds) {
        new BukkitRunnable() {
            int timeLeft = totalSeconds;
            @Override
            public void run() {
                if(!player.isOnline() || timeLeft <= 0) {
                    BossBar bar = activeBars.remove(player.getUniqueId());
                    if(bar != null) bar.removeAll();
                    remainingTime.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }
                
                BossBar bar = activeBars.get(player.getUniqueId());
                if(bar != null) {
                    double progress = timeLeft / (double) totalSeconds;
                    bar.setProgress(progress);
                    bar.setTitle(getFormattedTime(timeLeft));
                }
                timeLeft--;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 20L);
    }
    
    private String getFormattedTime(int seconds) {
        return FruitsPlugin.getInstance().getConfig().getString("grace_period.bossbar_title")
            .replace("{time}", String.valueOf(seconds))
            .replace('&', '§');
    }
    
    public boolean isGraceActive(Player player) {
        return activeBars.containsKey(player.getUniqueId()) || globalGraceActive;
    }
    
    public void endGracePeriod(Player player) {
        BossBar bar = activeBars.remove(player.getUniqueId());
        if(bar != null) bar.removeAll();
        remainingTime.remove(player.getUniqueId());
    }
}
