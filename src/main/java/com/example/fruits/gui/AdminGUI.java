package com.example.fruits.gui;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.List;

public class AdminGUI {
    
    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(new GUIHolder(), 27, "§8§l✨ FRUIT ADMIN PANEL ✨");
        
        int slot = 0;
        for(Fruit fruit : FruitsPlugin.getInstance().getFruitRegistry().getAllFruits()) {
            ItemStack item = fruit.createItem();
            ItemMeta meta = item.getItemMeta();
            
            // Add ability info to lore
            List<String> lore = meta.getLore() == null ? new java.util.ArrayList<>() : meta.getLore();
            lore.add("");
            lore.add("§7§m-------------------");
            lore.add("§6✨ Abilities:");
            for(int i = 0; i < fruit.getAbilities().size(); i++) {
                lore.add("§7 " + (i+1) + ". §f" + fruit.getAbilities().get(i).getName() + " §7(§b" + fruit.getAbilities().get(i).getCooldown() + "s§7)");
            }
            lore.add("§7§m-------------------");
            lore.add("§a✅ Click to give this fruit!");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }
        
        // Fill empty slots with glass
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName("§7");
        filler.setItemMeta(fillerMeta);
        
        for(int i = slot; i < 26; i++) {
            inv.setItem(i, filler);
        }
        
        // Spin button
        ItemStack spin = new ItemStack(Material.COMPASS);
        ItemMeta spinMeta = spin.getItemMeta();
        spinMeta.setDisplayName("§a§l🎲 RANDOM FRUIT");
        spinMeta.setLore(Arrays.asList("§7§m-------------------", "§7Click to get a random fruit!", "§7§m-------------------"));
        spin.setItemMeta(spinMeta);
        inv.setItem(26, spin);
        
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
    }

    public static class GUIHolder implements InventoryHolder {
        @Override public Inventory getInventory() { return null; }
    }
}
