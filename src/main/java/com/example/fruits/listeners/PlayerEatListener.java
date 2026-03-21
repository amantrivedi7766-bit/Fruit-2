package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEatListener implements Listener {
    
    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        String fruitId = Fruit.getFruitId(item);
        if(fruitId == null) return;
        
        Fruit fruit = FruitsPlugin.getInstance().getFruitRegistry().getFruit(fruitId);
        if(fruit == null) return;
        
        // Cancel eating - fruit is for abilities, not for eating
        event.setCancelled(true);
        
        // Add to active players if not already
        if(!FruitsPlugin.getInstance().isActivePlayer(player)) {
            FruitsPlugin.getInstance().addActivePlayer(player);
        }
        
        // Store fruit data
        FruitsPlugin.getInstance().getPlayerManager().setPlayerFruit(player, fruitId);
        
        player.sendMessage("§e🍎 " + fruit.getName() + " §7- Right-click to use abilities!");
    }
}
