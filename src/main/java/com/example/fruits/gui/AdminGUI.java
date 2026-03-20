package com.example.fruits.gui;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class AdminGUI {
    
    public static void open(Player player) {
        // Create inventory with 18 slots (9x2)
        Inventory inv = Bukkit.createInventory(new GUIHolder(), 18, "§8⚡ Fruits Admin Panel");
        
        int slot = 0;
        // Add all fruits to GUI
        for (Fruit fruit : FruitsPlugin.getInstance().getFruitRegistry().getAllFruits()) {
            ItemStack item = fruit.createItem();
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Arrays.asList(
                "§7§m-------------------",
                "§a✅ Click to give this fruit",
                "§7§m-------------------"
            ));
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }
        
        // Fill empty slots with glass panes
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName("§7");
        filler.setItemMeta(fillerMeta);
        
        for (int i = slot; i < 17; i++) {
            inv.setItem(i, filler);
        }
        
        // Spin button (slot 17)
        ItemStack spin = new ItemStack(Material.COMPASS);
        ItemMeta spinMeta = spin.getItemMeta();
        spinMeta.setDisplayName("§a§l🎲 RANDOM FRUIT");
        spinMeta.setLore(Arrays.asList(
            "§7§m-------------------",
            "§7Click to get a random fruit!",
            "§7§m-------------------"
        ));
        spin.setItemMeta(spinMeta);
        inv.setItem(17, spin);
        
        player.openInventory(inv);
    }

    // Public Holder class - important for event checking
    public static class GUIHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
