package com.example.fruits.gui;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class FruitGUI implements Listener {
    
    private final FruitsPlugin plugin;
    private final Player player;
    
    public FruitGUI(FruitsPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void open() {
        Inventory gui = Bukkit.createInventory(null, 54, "§6§l🍎 FRUIT SYSTEM §8| §eMain Menu");
        
        // Row 1: Main Actions
        gui.setItem(10, createGuiItem(Material.EMERALD, "§a§l🎲 SPIN WHEEL", 
            "§7Click to spin for random fruits!"));
        
        gui.setItem(11, createGuiItem(Material.GOLD_INGOT, "§6§l💼 TRADE SYSTEM",
            "§7Trade fruits with other players"));
        
        gui.setItem(12, createGuiItem(Material.IRON_SWORD, "§c§l🗡️ STEAL FRUIT",
            "§7Steal fruits from other players"));
        
        gui.setItem(13, createGuiItem(Material.CHEST, "§a§l📦 MY FRUITS",
            "§7View all fruits in your inventory"));
        
        gui.setItem(14, createGuiItem(Material.BOOK, "§b§l📋 FRUIT LIST",
            "§7View all available fruits"));
        
        gui.setItem(15, createGuiItem(Material.CLOCK, "§e§l⏰ COOLDOWNS",
            "§7View your active cooldowns"));
        
        // Row 2: Stats
        gui.setItem(49, createGuiItem(Material.NETHER_STAR, "§6§l🏆 TOP COLLECTORS",
            "§7View top fruit collectors"));
        
        gui.setItem(53, createGuiItem(Material.REDSTONE, "§c§l❌ CLOSE",
            "§7Close this menu"));
        
        player.openInventory(gui);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        if(!event.getView().getTitle().contains("FRUIT SYSTEM")) return;
        
        event.setCancelled(true);
        Player clicker = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if(clicked == null || !clicked.hasItemMeta()) return;
        
        int slot = event.getSlot();
        
        switch(slot) {
            case 10: // Spin
                clicker.closeInventory();
                Bukkit.dispatchCommand(clicker, "fruit spin");
                break;
            case 11: // Trade
                clicker.closeInventory();
                clicker.sendMessage("§cTrade system: Use /fruit trade <player>");
                break;
            case 12: // Steal
                clicker.closeInventory();
                clicker.sendMessage("§cSteal: Use /fruit steal <player>");
                break;
            case 13: // My Fruits
                openMyFruits(clicker);
                break;
            case 14: // Fruit List
                openFruitList(clicker);
                break;
            case 15: // Cooldowns
                clicker.closeInventory();
                Bukkit.dispatchCommand(clicker, "fruit cooldown");
                break;
            case 49: // Top
                clicker.closeInventory();
                Bukkit.dispatchCommand(clicker, "fruit top");
                break;
            case 53: // Close
                clicker.closeInventory();
                break;
        }
    }
    
    private void openMyFruits(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "§a§l📦 MY FRUITS");
        
        Map<String, Integer> fruitCounts = new HashMap<>();
        for(ItemStack item : player.getInventory().getContents()) {
            if(item != null) {
                String fruitId = com.example.fruits.models.Fruit.getFruitId(item);
                if(fruitId != null) {
                    fruitCounts.put(fruitId, fruitCounts.getOrDefault(fruitId, 0) + item.getAmount());
                }
            }
        }
        
        int slot = 0;
        for(Map.Entry<String, Integer> entry : fruitCounts.entrySet()) {
            Fruit fruit = plugin.getFruitRegistry().getFruit(entry.getKey());
            if(fruit != null) {
                ItemStack display = fruit.createItemStack(1);
                ItemMeta meta = display.getItemMeta();
                List<String> lore = new ArrayList<>();
                lore.add("§7Amount: §e" + entry.getValue());
                meta.setLore(lore);
                display.setItemMeta(meta);
                gui.setItem(slot++, display);
            }
        }
        
        player.openInventory(gui);
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(player.getOpenInventory().getTitle().equals("§a§l📦 MY FRUITS")) {
                player.closeInventory();
            }
        }, 200L);
    }
    
    private void openFruitList(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "§b§l📋 ALL FRUITS");
        
        int slot = 0;
        for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
            ItemStack display = fruit.createItemStack(1);
            ItemMeta meta = display.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7ID: §f" + fruit.getId());
            lore.add("§7Abilities:");
            for(int i = 0; i < fruit.getAbilities().size(); i++) {
                lore.add("  §" + (i == 0 ? "a" : "b") + fruit.getAbilities().get(i).getName() + 
                        " §7(CD: " + fruit.getAbilities().get(i).getCooldown() + "s)");
            }
            meta.setLore(lore);
            display.setItemMeta(meta);
            gui.setItem(slot++, display);
        }
        
        player.openInventory(gui);
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(player.getOpenInventory().getTitle().equals("§b§l📋 ALL FRUITS")) {
                player.closeInventory();
            }
        }, 200L);
    }
    
    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if(lore.length > 0) meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
}
