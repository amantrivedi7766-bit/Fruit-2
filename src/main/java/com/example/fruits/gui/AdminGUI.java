package com.example.fruits.gui;  // ✅ SAME PACKAGE

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
        Inventory inv = Bukkit.createInventory(new GUIHolder(), 18, "§8⚡ Fruits Admin");
        
        int slot = 0;
        for (Fruit fruit : FruitsPlugin.getInstance().getFruitRegistry().getAllFruits()) {
            ItemStack item = fruit.createItem();
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Arrays.asList("§7Click to give this fruit"));
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }
        
        // Spin button
        ItemStack spin = new ItemStack(Material.COMPASS);
        ItemMeta spinMeta = spin.getItemMeta();
        spinMeta.setDisplayName("§a§l🎲 Random Fruit");
        spin.setItemMeta(spinMeta);
        inv.setItem(17, spin);
        
        player.openInventory(inv);
    }

    public static class GUIHolder implements InventoryHolder {
        @Override public Inventory getInventory() { return null; }
    }
}
