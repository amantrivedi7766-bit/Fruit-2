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
    
    private static final int GUI_SIZE = 54; // 6 rows
    
    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(new GUIHolder(), GUI_SIZE, "§8§l⚡ SERVER CONTROL PANEL ⚡");
        
        // Fruits Section (Slots 0-9)
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
        
        // Section 2: Admin Controls (Slots 10-17)
        addAdminControls(inv);
        
        // Section 3: Player Management (Slots 18-35)
        addPlayerManagement(inv, player);
        
        // Section 4: Server Controls (Slots 36-44)
        addServerControls(inv);
        
        // Section 5: Spin Controls (Slots 45-53)
        addSpinControls(inv);
        
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
    }
    
    private static void addAdminControls(Inventory inv) {
        // Reload Config
        ItemStack reload = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta reloadMeta = reload.getItemMeta();
        reloadMeta.setDisplayName("§a§l🔄 Reload Config");
        reloadMeta.setLore(Arrays.asList("§7Reload plugin configuration", "§7§m-------------------", "§eClick to reload!"));
        reload.setItemMeta(reloadMeta);
        inv.setItem(10, reload);
        
        // View All Players
        ItemStack players = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta playerMeta = (SkullMeta) players.getItemMeta();
        playerMeta.setDisplayName("§b§l👥 All Players");
        playerMeta.setLore(Arrays.asList("§7View online players", "§7§m-------------------", "§eClick to view!"));
        players.setItemMeta(playerMeta);
        inv.setItem(11, players);
        
        // Give All Fruits
        ItemStack giveAll = new ItemStack(Material.CHEST);
        ItemMeta giveAllMeta = giveAll.getItemMeta();
        giveAllMeta.setDisplayName("§6§l🎁 Give All Fruits");
        giveAllMeta.setLore(Arrays.asList("§7Give fruits to all players", "§7§m-------------------", "§eClick to give!"));
        giveAll.setItemMeta(giveAllMeta);
        inv.setItem(12, giveAll);
    }
    
    private static void addPlayerManagement(Inventory inv, Player admin) {
        int slot = 18;
        for(Player p : Bukkit.getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(p);
            meta.setDisplayName("§a§l" + p.getName());
            meta.setLore(Arrays.asList(
                "§7Health: §c" + (int) p.getHealth() + "/" + (int) p.getMaxHealth(),
                "§7Level: §b" + p.getLevel(),
                "§7Location: §e" + p.getWorld().getName(),
                "§7§m-------------------",
                "§eClick to manage " + p.getName()
            ));
            head.setItemMeta(meta);
            inv.setItem(slot++, head);
            
            if(slot > 35) break;
        }
    }
    
    private static void addServerControls(Inventory inv) {
        // Broadcast Message
        ItemStack broadcast = new ItemStack(Material.PAPER);
        ItemMeta broadcastMeta = broadcast.getItemMeta();
        broadcastMeta.setDisplayName("§d§l📢 Broadcast");
        broadcastMeta.setLore(Arrays.asList("§7Send a message to all players", "§7§m-------------------", "§eClick to broadcast!"));
        broadcast.setItemMeta(broadcastMeta);
        inv.setItem(36, broadcast);
        
        // Clear Chat
        ItemStack clearChat = new ItemStack(Material.BARRIER);
        ItemMeta clearMeta = clearChat.getItemMeta();
        clearMeta.setDisplayName("§c§l🗑️ Clear Chat");
        clearMeta.setLore(Arrays.asList("§7Clear all players' chat", "§7§m-------------------", "§eClick to clear!"));
        clearChat.setItemMeta(clearMeta);
        inv.setItem(37, clearChat);
        
        // Set Time Day
        ItemStack day = new ItemStack(Material.SUNFLOWER);
        ItemMeta dayMeta = day.getItemMeta();
        dayMeta.setDisplayName("§e§l☀️ Set Day");
        dayMeta.setLore(Arrays.asList("§7Set time to day", "§7§m-------------------", "§eClick to set!"));
        day.setItemMeta(dayMeta);
        inv.setItem(38, day);
        
        // Set Time Night
        ItemStack night = new ItemStack(Material.CLOCK);
        ItemMeta nightMeta = night.getItemMeta();
        nightMeta.setDisplayName("§8§l🌙 Set Night");
        nightMeta.setLore(Arrays.asList("§7Set time to night", "§7§m-------------------", "§eClick to set!"));
        night.setItemMeta(nightMeta);
        inv.setItem(39, night);
        
        // Stop Server
        ItemStack stop = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta stopMeta = stop.getItemMeta();
        stopMeta.setDisplayName("§c§l⛔ Stop Server");
        stopMeta.setLore(Arrays.asList("§7Shutdown the server", "§7§m-------------------", "§c⚠️ Click with caution!"));
        stop.setItemMeta(stopMeta);
        inv.setItem(44, stop);
    }
    
    private static void addSpinControls(Inventory inv) {
        // Single Spin
        ItemStack singleSpin = new ItemStack(Material.COMPASS);
        ItemMeta singleMeta = singleSpin.getItemMeta();
        singleMeta.setDisplayName("§a§l🎲 Single Spin");
        singleMeta.setLore(Arrays.asList("§7Spin for yourself", "§7§m-------------------", "§eClick to spin!"));
        singleSpin.setItemMeta(singleMeta);
        inv.setItem(45, singleSpin);
        
        // All Spin
        ItemStack allSpin = new ItemStack(Material.NETHER_STAR);
        ItemMeta allMeta = allSpin.getItemMeta();
        allMeta.setDisplayName("§6§l🌟 ALL PLAYERS SPIN");
        allMeta.setLore(Arrays.asList("§7Spin for ALL online players", "§7§m-------------------", "§eClick to mega spin!"));
        allSpin.setItemMeta(allMeta);
        inv.setItem(46, allSpin);
        
        // Random Spin
        ItemStack randomSpin = new ItemStack(Material.ENDER_PEARL);
        ItemMeta randomMeta = randomSpin.getItemMeta();
        randomMeta.setDisplayName("§5§l🌀 Random Spin");
        randomMeta.setLore(Arrays.asList("§7Spin for random player", "§7§m-------------------", "§eClick to spin random!"));
        randomSpin.setItemMeta(randomMeta);
        inv.setItem(47, randomSpin);
        
        // Fill background with glass
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName("§7");
        glass.setItemMeta(glassMeta);
        
        for(int i = 48; i <= 53; i++) {
            inv.setItem(i, glass);
        }
    }
    
    public static class GUIHolder implements InventoryHolder {
        @Override public Inventory getInventory() { return null; }
    }
}
