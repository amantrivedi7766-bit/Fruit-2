package com.example.fruits.listeners;

import com.example.fruits.gui.AdminGUI;
import com.example.fruits.models.Fruit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class AdminGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Check if it's our GUI
        if (!(event.getInventory().getHolder() instanceof AdminGUI.GUIHolder)) {
            return;
        }
        
        // Cancel all clicks in our GUI
        event.setCancelled(true);
        
        // Check if clicked item is null
        if (event.getCurrentItem() == null) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        
        // Spin button (slot 17)
        if (slot == 17) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            player.performCommand("fruitadmin spin " + player.getName());
            return;
        }
        
        // Get fruit ID from clicked item
        String fruitId = Fruit.getFruitId(event.getCurrentItem());
        if (fruitId != null) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
            player.performCommand("fruitadmin give " + player.getName() + " " + fruitId);
        }
    }
    
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // Prevent dragging in our GUI
        if (event.getInventory().getHolder() instanceof AdminGUI.GUIHolder) {
            event.setCancelled(true);
        }
    }
}
