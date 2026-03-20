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
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class AdminGUI {
    
    private static final int GUI_SIZE = 54;
    
    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(new GUIHolder(), GUI_SIZE, "§8§l⚡ SERVER CONTROL PANEL ⚡");
        
        int slot = 0;
        for(Fruit fruit : FruitsPlugin.getInstance().getFruitRegistry().getAllFruits()) {
            ItemStack item = fruit.createItem();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add("");
            lore.add("§7§m-------------------");
            lore.add("§a✅ Click to give this fruit");
            lore.add("§7§m-------------------");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }
        
        addAdminControls(inv);
        addPlayerManagement(inv);
        addServerControls(inv);
        addSpinControls(inv);
        
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
    }
    
    private static void addAdminControls(Inventory inv) {
        // Reload Config
        ItemStack reload = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta reloadMeta = reload.getItemMeta();
        reloadMeta.setDisplayName("§a§l🔄 Reload Config");
        reloadMeta.setLore(Arrays.asList("§7Reload plugin configuration", "§eClick to reload!"));
        reload.setItemMeta(reloadMeta);
        inv.setItem(10, reload);
        
        // All Players List
        ItemStack players = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta playerMeta = (SkullMeta) players.getItemMeta();
        playerMeta.setDisplayName("§b§l👥 All Players");
        playerMeta.setLore(Arrays.asList("§7View online players", "§eClick to view!"));
        players.setItemMeta(playerMeta);
        inv.setItem(11, players);
        
        // Give All Fruits
        ItemStack giveAll = new ItemStack(Material.CHEST);
        ItemMeta giveAllMeta = giveAll.getItemMeta();
        giveAllMeta.setDisplayName("§6§l🎁 Give All Fruits");
        giveAllMeta.setLore(Arrays.asList("§7Give random fruits to all players", "§eClick to give!"));
        giveAll.setItemMeta(giveAllMeta);
        inv.setItem(12, giveAll);
    }
    
    private static void addPlayerManagement(Inventory inv) {
        int slot = 18;
        for(Player p : Bukkit.getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(p);
            meta.setDisplayName("§a§l" + p.getName());
            meta.setLore(Arrays.asList(
                "§7Health: §c" + (int) p.getHealth() + "/" + (int) p.getMaxHealth(),
                "§7Level: §b" + p.getLevel(),
                "§eClick to manage"
            ));
            head.setItemMeta(meta);
            inv.setItem(slot++, head);
            if(slot > 35) break;
        }
    }
    
    private static void addServerControls(Inventory inv) {
        // Set Day
        ItemStack day = new ItemStack(Material.SUNFLOWER);
        ItemMeta dayMeta = day.getItemMeta();
        dayMeta.setDisplayName("§e§l☀️ Set Day");
        dayMeta.setLore(Arrays.asList("§7Set time to day", "§eClick to set!"));
        day.setItemMeta(dayMeta);
        inv.setItem(38, day);
        
        // Set Night
        ItemStack night = new ItemStack(Material.CLOCK);
        ItemMeta nightMeta = night.getItemMeta();
        nightMeta.setDisplayName("§8§l🌙 Set Night");
        nightMeta.setLore(Arrays.asList("§7Set time to night", "§eClick to set!"));
        night.setItemMeta(nightMeta);
        inv.setItem(39, night);
    }
    
    private static void addSpinControls(Inventory inv) {
        // Single Spin
        ItemStack singleSpin = new ItemStack(Material.COMPASS);
        ItemMeta singleMeta = singleSpin.getItemMeta();
        singleMeta.setDisplayName("§a§l🎲 Single Spin");
        singleMeta.setLore(Arrays.asList("§7Spin for yourself", "§eClick to spin!"));
        singleSpin.setItemMeta(singleMeta);
        inv.setItem(45, singleSpin);
        
        // All Players Spin
        ItemStack allSpin = new ItemStack(Material.NETHER_STAR);
        ItemMeta allMeta = allSpin.getItemMeta();
        allMeta.setDisplayName("§6§l🌟 ALL PLAYERS SPIN");
        allMeta.setLore(Arrays.asList("§7Spin for ALL online players", "§eClick to mega spin!"));
        allSpin.setItemMeta(allMeta);
        inv.setItem(46, allSpin);
        
        // Random Player Spin
        ItemStack randomSpin = new ItemStack(Material.ENDER_PEARL);
        ItemMeta randomMeta = randomSpin.getItemMeta();
        randomMeta.setDisplayName("§5§l🌀 Random Spin");
        randomMeta.setLore(Arrays.asList("§7Spin for random player", "§eClick to spin random!"));
        randomSpin.setItemMeta(randomMeta);
        inv.setItem(47, randomSpin);
        
        // Filler Glass
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName("§7");
        glass.setItemMeta(glassMeta);
        for(int i = 48; i <= 53; i++) {
            inv.setItem(i, glass);
        }
    }
    
    public static class GUIHolder implements InventoryHolder {
        @Override 
        public Inventory getInventory() { 
            return null; 
        }
    }
}
