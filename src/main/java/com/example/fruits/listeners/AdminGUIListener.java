package com.example.fruits.listeners;

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
    public void onClick(InventoryClickEvent e) {
        if(!(e.getInventory().getHolder() instanceof AdminGUI.GUIHolder)) return;
        
        e.setCancelled(true);
        if(e.getCurrentItem() == null) return;
        
        Player p = (Player) e.getWhoClicked();
        
        if(e.getSlot() == 26) {
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
            p.performCommand("fruitadmin spin " + p.getName());
            return;
        }
        
        String fruitId = Fruit.getFruitId(e.getCurrentItem());
        if(fruitId != null) {
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.2f);
            p.performCommand("fruitadmin give " + p.getName() + " " + fruitId);
        }
    }
    
    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if(e.getInventory().getHolder() instanceof AdminGUI.GUIHolder) {
            e.setCancelled(true);
        }
    }
}
