package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.Ability;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if(event.getItem() == null) return;
        String fruitId = Fruit.getFruitId(event.getItem());
        if(fruitId == null) return;
        
        Fruit fruit = FruitsPlugin.getInstance().getFruitRegistry().getFruit(fruitId);
        if(fruit == null) return;
        
        Action action = event.getAction();
        
        // TEMPORARILY DISABLED - No abilities yet
        if((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);
            player.sendMessage("§e🍎 " + fruit.getName() + " §7- Abilities coming soon!");
        }
    }
    
    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        
        if(event.getHand() != EquipmentSlot.HAND) return;
        
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item == null) return;
        
        String fruitId = Fruit.getFruitId(item);
        if(fruitId == null) return;
        
        Fruit fruit = FruitsPlugin.getInstance().getFruitRegistry().getFruit(fruitId);
        if(fruit == null) return;
        
        event.setCancelled(true);
        player.sendMessage("§e🍎 " + fruit.getName() + " §7- Abilities coming soon!");
    }
}
