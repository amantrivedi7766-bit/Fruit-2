package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.PlayerFruitData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // Check if player has an active fruit
        PlayerFruitData data = FruitsPlugin.getInstance().getActivePlayers().get(player.getUniqueId());
        if(data == null || data.getFruit() == null) return;
        
        // Check if grace period is active
        if(FruitsPlugin.getInstance().getGracePeriodManager().isGraceActive(player)) {
            // Remove fruit on death during grace period
            FruitsPlugin.getInstance().getActivePlayers().remove(player.getUniqueId());
            player.sendMessage(FruitsPlugin.getInstance().getConfig().getString("messages.power_lost").replace('&', '§'));
            
            // Give back fruit? No, they lost it!
        } else {
            // Keep fruit if death loss is disabled
            if(!FruitsPlugin.getInstance().getConfig().getBoolean("death.lose_power", true)) {
                player.sendMessage(FruitsPlugin.getInstance().getConfig().getString("messages.power_kept").replace('&', '§'));
            } else {
                // Remove fruit on death
                FruitsPlugin.getInstance().getActivePlayers().remove(player.getUniqueId());
                player.sendMessage(FruitsPlugin.getInstance().getConfig().getString("messages.power_lost").replace('&', '§'));
            }
        }
    }
}
