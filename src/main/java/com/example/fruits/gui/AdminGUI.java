package com.example.fruits.gui;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.*;

public class AdminGUI {
    
    private static final String GUI_TITLE = "§6§l🍎 FRUIT ADMIN PANEL";
    
    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, GUI_TITLE);
        
        // Player Management
        ItemStack players = createItem(Material.PLAYER_HEAD, "§a§l👥 Player Management", 
            "§7Manage player fruits and stats",
            "§7Click to view all players");
        inv.setItem(10, players);
        
        // Fruit Management
        ItemStack fruits = createItem(Material.CHEST, "§e§l🍎 Fruit Management",
            "§7Manage magical fruits",
            "§7View all fruits and their abilities");
        inv.setItem(12, fruits);
        
        // Reward Settings
        ItemStack reward = createItem(Material.COMMAND_BLOCK, "§b§l🎁 Reward Settings",
            "§7Toggle join reward system",
            "§7Current: " + (FruitsPlugin.getInstance().getConfigManager().isRewardEnabled() ? "§aENABLED" : "§cDISABLED"));
        inv.setItem(14, reward);
        
        // Grace Period
        ItemStack grace = createItem(Material.CLOCK, "§3§l🛡️ Grace Period",
            "§7Manage global grace period",
            "§7Protect all players temporarily");
        inv.setItem(16, grace);
        
        // Statistics
        ItemStack stats = createItem(Material.PAPER, "§d§l📊 Statistics",
            "§7View plugin statistics",
            "§7Total fruits given: §e" + getTotalFruitsGiven(),
            "§7Active players: §e" + FruitsPlugin.getInstance().getActivePlayers().size());
        inv.setItem(22, stats);
        
        // Close Button
        ItemStack close = createItem(Material.BARRIER, "§c§l✖ Close",
            "§7Click to close");
        inv.setItem(26, close);
        
        player.openInventory(inv);
    }
    
    public void openPlayerManagement(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, "§a§l👥 Player Management");
        
        List<Player> players = FruitsPlugin.getInstance().getActivePlayers();
        int slot = 0;
        
        for(Player p : players) {
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(p);
            meta.setDisplayName("§a" + p.getName());
            
            String fruit = FruitsPlugin.getInstance().getPlayerManager().getPlayerFruit(p);
            List<String> lore = new ArrayList<>();
            lore.add("§7Fruit: " + (fruit != null ? "§e" + fruit : "§cNone"));
            lore.add("§7Abilities Used: §e" + FruitsPlugin.getInstance().getPlayerManager().getUsedAbilities(p));
            lore.add("");
            lore.add("§eClick to manage this player");
            
            meta.setLore(lore);
            skull.setItemMeta(meta);
            inv.setItem(slot, skull);
            slot++;
        }
        
        // Back button
        ItemStack back = createItem(Material.ARROW, "§7§l« Back", "§7Return to main menu");
        inv.setItem(49, back);
        
        player.openInventory(inv);
    }
    
    public void openFruitManagement(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§e§l🍎 Fruit Management");
        
        int slot = 0;
        for(Fruit fruit : FruitsPlugin.getInstance().getFruitRegistry().getAllFruits()) {
            ItemStack item = fruit.createItem();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7ID: §e" + fruit.getId());
            lore.add("§7Material: §e" + fruit.getMaterial().name());
            lore.add("");
            lore.add("§eClick to give to a player");
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
            slot++;
        }
        
        // Back button
        ItemStack back = createItem(Material.ARROW, "§7§l« Back", "§7Return to main menu");
        inv.setItem(26, back);
        
        player.openInventory(inv);
    }
    
    public void openRewardSettings(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§b§l🎁 Reward Settings");
        
        boolean enabled = FruitsPlugin.getInstance().getConfigManager().isRewardEnabled();
        
        // Toggle Button
        ItemStack toggle = createItem(enabled ? Material.LIME_WOOL : Material.RED_WOOL,
            enabled ? "§a§l✓ Reward: ENABLED" : "§c§l✗ Reward: DISABLED",
            "§7Click to " + (enabled ? "disable" : "enable") + " join reward",
            "§7New players will " + (enabled ? "receive" : "not receive") + " a fruit on first join");
        inv.setItem(13, toggle);
        
        // Test Spin
        ItemStack test = createItem(Material.NETHER_STAR, "§e§l🎲 Test Spin",
            "§7Test the spin animation",
            "§7Click to test");
        inv.setItem(11, test);
        
        // Back button
        ItemStack back = createItem(Material.ARROW, "§7§l« Back", "§7Return to main menu");
        inv.setItem(22, back);
        
        player.openInventory(inv);
    }
    
    public void openGracePeriod(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§3§l🛡️ Grace Period");
        
        // 30 Seconds
        ItemStack grace30 = createItem(Material.CLOCK, "§e§l30 Seconds",
            "§7Start 30 second grace period",
            "§7All players protected");
        inv.setItem(11, grace30);
        
        // 60 Seconds
        ItemStack grace60 = createItem(Material.CLOCK, "§e§l60 Seconds",
            "§7Start 60 second grace period",
            "§7All players protected");
        inv.setItem(13, grace60);
        
        // 120 Seconds
        ItemStack grace120 = createItem(Material.CLOCK, "§e§l120 Seconds",
            "§7Start 120 second grace period",
            "§7All players protected");
        inv.setItem(15, grace120);
        
        // Cancel Grace
        ItemStack cancel = createItem(Material.BARRIER, "§c§l✖ Cancel Grace",
            "§7Cancel active grace period");
        inv.setItem(22, cancel);
        
        // Back button
        ItemStack back = createItem(Material.ARROW, "§7§l« Back", "§7Return to main menu");
        inv.setItem(26, back);
        
        player.openInventory(inv);
    }
    
    public void openPlayerFruitSelect(Player admin, Player target) {
        Inventory inv = Bukkit.createInventory(null, 27, "§e§lGive Fruit to " + target.getName());
        
        int slot = 0;
        for(Fruit fruit : FruitsPlugin.getInstance().getFruitRegistry().getAllFruits()) {
            ItemStack item = fruit.createItem();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to give this fruit to " + target.getName());
            meta.setLore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot, item);
            slot++;
        }
        
        // Back button
        ItemStack back = createItem(Material.ARROW, "§7§l« Back", "§7Return to player management");
        inv.setItem(26, back);
        
        // Store target in inventory title for later reference
        player.openInventory(inv);
    }
    
    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    private int getTotalFruitsGiven() {
        // Calculate total fruits given
        int total = 0;
        for(Player p : FruitsPlugin.getInstance().getActivePlayers()) {
            if(FruitsPlugin.getInstance().getPlayerManager().hasFruit(p)) {
                total++;
            }
        }
        return total;
    }
}
