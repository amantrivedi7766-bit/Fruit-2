package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Add to active players
        FruitsPlugin.getInstance().addActivePlayer(player);
        
        // Check if reward system is enabled
        if(!FruitsPlugin.getInstance().getConfigManager().isRewardEnabled()) {
            return;
        }
        
        // Check if player already got reward
        if(player.hasPlayedBefore()) {
            return;
        }
        
        // Start spin animation for new player
        FruitsPlugin.getInstance().getSpinManager().startSpin(player);
    }
}
