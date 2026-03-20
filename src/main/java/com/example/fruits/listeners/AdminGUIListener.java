package com.example.fruits.listeners;  // ← YAHI SAHI PACKAGE HAI

import com.example.fruits.gui.AdminGUI;
import com.example.fruits.models.Fruit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class AdminGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof AdminGUI.GUIHolder)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (event.getCurrentItem() == null) return;
        
        Player player = (Player) event.getWhoClicked();
        
        if (event.getSlot() == 17) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            player.performCommand("fruitadmin spin " + player.getName());
            return;
        }
        
        String fruitId = Fruit.getFruitId(event.getCurrentItem());
        if (fruitId != null) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
            player.performCommand("fruitadmin give " + player.getName() + " " + fruitId);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof AdminGUI.GUIHolder) {
            event.setCancelled(true);
        }
    }
}
