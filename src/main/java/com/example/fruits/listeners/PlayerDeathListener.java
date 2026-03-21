package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // Check if player has a fruit
        String fruitId = FruitsPlugin.getInstance().getPlayerManager().getPlayerFruit(player);
        
        if(fruitId != null) {
            // Optional: Drop fruit on death?
            // event.getDrops().add(fruit.createItem());
            
            // Clear player data on death
            FruitsPlugin.getInstance().getPlayerManager().removeActivePlayer(player);
        }
    }
}
