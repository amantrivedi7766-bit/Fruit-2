package com.example.fruits.gui;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.PlayerFruitData;
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
        
        // ========== SECTION 1: FRUITS (SLOTS 0-9) ==========
        addFruitsSection(inv);
        
        // ========== SECTION 2: ADMIN CONTROLS (SLOTS 10-17) ==========
        addAdminControls(inv);
        
        // ========== SECTION 3: PLAYER MANAGEMENT (SLOTS 18-35) ==========
        addPlayerManagement(inv);
        
        // ========== SECTION 4: GRACE PERIOD CONTROLS (SLOTS 36-44) ==========
        addGracePeriodControls(inv);
        
        // ========== SECTION 5: SPIN CONTROLS (SLOTS 45-53) ==========
        addSpinControls(inv);
        
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
    }
    
    private static void addFruitsSection(Inventory inv) {
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
        
        // Remove All Powers
        ItemStack removeAll = new ItemStack(Material.BARRIER);
        ItemMeta removeAllMeta = removeAll.getItemMeta();
        removeAllMeta.setDisplayName("§c§l🗑️ Remove All Powers");
        removeAllMeta.setLore(Arrays.asList("§7Remove fruit powers from ALL players", "§c⚠️ WARNING: Cannot be undone!"));
        removeAll.setItemMeta(removeAllMeta);
        inv.setItem(13, removeAll);
    }
    
    private static void addPlayerManagement(Inventory inv) {
        int slot = 18;
        for(Player p : Bukkit.getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(p);
            
            PlayerFruitData data = FruitsPlugin.getInstance().getActivePlayers().get(p.getUniqueId());
            boolean hasPower = data != null && data.getFruit() != null;
            String fruitName = hasPower ? data.getFruit().getDisplayName() : "§cNone";
            int uses = hasPower ? 3 - data.getUsedAbilities() : 0;
            
            meta.setDisplayName(hasPower ? "§a§l" + p.getName() : "§7§l" + p.getName());
            meta.setLore(Arrays.asList(
                "§7§m-------------------",
                "§6✨ Power: " + fruitName,
                "§e⚡ Uses left: " + uses,
                "§7§m-------------------",
                "§a👆 Left Click: §7View details",
                "§c🔴 Right Click: §7Remove power"
            ));
            head.setItemMeta(meta);
            inv.setItem(slot++, head);
            
            if(slot > 35) break;
        }
    }
    
    private static void addGracePeriodControls(Inventory inv) {
        // Start Grace Period
        ItemStack startGrace = new ItemStack(Material.CLOCK);
        ItemMeta startMeta = startGrace.getItemMeta();
        startMeta.setDisplayName("§6§l⏰ Start Grace Period");
        startMeta.setLore(Arrays.asList("§7Start a global grace period", "§7Players won't lose powers on death", "§eClick to start!"));
        startGrace.setItemMeta(startMeta);
        inv.setItem(36, startGrace);
        
        // End Grace Period
        ItemStack endGrace = new ItemStack(Material.REDSTONE);
        ItemMeta endMeta = endGrace.getItemMeta();
        endMeta.setDisplayName("§c§l⏰ End Grace Period");
        endMeta.setLore(Arrays.asList("§7End current grace period", "§7Players will lose powers on death", "§eClick to end!"));
        endGrace.setItemMeta(endMeta);
        inv.setItem(37, endGrace);
        
        // Set Death Loss
        ItemStack deathLoss = new ItemStack(Material.SKELETON_SKULL);
        ItemMeta deathMeta = deathLoss.getItemMeta();
        deathMeta.setDisplayName("§4§l💀 Death Settings");
        boolean losePower = FruitsPlugin.getInstance().getConfig().getBoolean("death.lose_power", true);
        deathMeta.setLore(Arrays.asList(
            "§7Current: " + (losePower ? "§cLOSE POWER ON DEATH" : "§aKEEP POWER ON DEATH"),
            "§eClick to toggle!"
        ));
        deathLoss.setItemMeta(deathMeta);
        inv.setItem(38, deathLoss);
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
        all
