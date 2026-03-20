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
        if(e.getSlot() == 17) {
            p.performCommand("fruitadmin spin " + p.getName());
            p.closeInventory();
            return;
        }
        String fruitId = Fruit.getFruitId(e.getCurrentItem());
        if(fruitId != null) {
            p.performCommand("fruitadmin give " + p.getName() + " " + fruitId);
            p.closeInventory();
        }
    }
    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if(e.getInventory().getHolder() instanceof AdminGUI.GUIHolder) e.setCancelled(true);
    }
}
