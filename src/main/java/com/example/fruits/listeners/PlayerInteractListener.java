package com.example.fruits.listeners;

import com.example.fruits.models.Fruit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(!event.getAction().name().contains("RIGHT_CLICK")) return;
        
        Player player = event.getPlayer();
        if(event.getItem() == null) return;
        
        String fruitId = Fruit.getFruitId(event.getItem());
        if(fruitId == null) return;
        
        event.setCancelled(true);
        player.sendMessage("§eEat the fruit first to use its powers!");
    }
}
