package com.example.fruits.gui;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.utils.SpinWheel;
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

public class AdminMenu implements Listener {
    
    private final FruitsPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    
    public AdminMenu(FruitsPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void open(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§c§l⚡ ADMIN CONTROL PANEL");
        
        // ========== ROW 1: Fruit Management ==========
        gui.setItem(10, createGuiItem(Material.DIAMOND, "§a§l🎁 GIVE FRUIT",
            "§7Give fruits to players",
            "§eClick to open give menu"));
        
        gui.setItem(11, createGuiItem(Material.REDSTONE_BLOCK, "§c§l❌ REMOVE FRUIT",
            "§7Remove fruits from players",
            "§eClick to open remove menu"));
        
        gui.setItem(12, createGuiItem(Material.COMMAND_BLOCK, "§6§l🎲 SPIN WHEEL",
            "§7Configure spin wheel rewards",
            "§eCurrent rewards: §f" + SpinWheel.getRewardCount() + " fruits"));
        
        gui.setItem(13, createGuiItem(Material.BEACON, "§d§l👥 SPIN ALL",
            "§7Start spin for all online players",
            "§eClick to spin everyone!"));
        
        gui.setItem(14, createGuiItem(Material.CLOCK, "§e§l⏰ COOLDOWN MANAGER",
            "§7Manage player cooldowns",
            "§eReset or set custom cooldowns"));
        
        // ========== ROW 2: Auto Give & Join Settings ==========
        gui.setItem(28, createGuiItem(Material.GRASS_BLOCK, "§a§l🔄 AUTO GIVE ON JOIN",
            "§7Configure auto-give for new players",
            "§eStatus: " + (plugin.isAutoGiveEnabled() ? "§aENABLED" : "§cDISABLED"),
            "§7Click to toggle/settings"));
        
        gui.setItem(29, createGuiItem(Material.PLAYER_HEAD, "§b§l👤 JOIN FRUIT",
            "§7Set fruit for first join",
            "§eCurrent: " + plugin.getJoinFruitName() + " x" + plugin.getJoinFruitAmount()));
        
        gui.setItem(30, createGuiItem(Material.CHEST, "§e§l📦 MASS GIVE",
            "§7Give fruits to all online",
            "§eClick to give to everyone"));
        
        gui.setItem(31, createGuiItem(Material.BARRIER, "§c§l🛑 STOP ALL SPINS",
            "§7Stop all active spin wheels",
            "§eEmergency stop"));
        
        // ========== ROW 3: Player Management ==========
        gui.setItem(46, createGuiItem(Material.PLAYER_HEAD, "§a§l👥 PLAYER LIST",
            "§7View all online players",
            "§eClick to manage specific player"));
        
        gui.setItem(49, createGuiItem(Material.REPEATER, "§6§l🔄 RELOAD PLUGIN",
            "§7Reload all configurations",
            "§eClick to reload"));
        
        gui.setItem(52, createGuiItem(Material.REDSTONE, "§c§l❌ CLOSE",
            "§7Close admin panel"));
        
        admin.openInventory(gui);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        if(!event.getView().getTitle().contains("ADMIN CONTROL PANEL")) return;
        
        event.setCancelled(true);
        Player admin = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if(clicked == null || !clicked.hasItemMeta()) return;
        
        int slot = event.getSlot();
        
        // Give Fruit
        if(slot == 10) {
            openGiveMenu(admin);
        }
        // Remove Fruit
        else if(slot == 11) {
            openRemoveMenu(admin);
        }
        // Spin Wheel Config
        else if(slot == 12) {
            openSpinConfig(admin);
        }
        // Spin All
        else if(slot == 13) {
            admin.closeInventory();
            Bukkit.dispatchCommand(admin, "fruit spinall 3");
            admin.sendMessage("§a✓ Started spin for all online players!");
        }
        // Cooldown Manager
        else if(slot == 14) {
            openCooldownManager(admin);
        }
        // Auto Give Toggle
        else if(slot == 28) {
            boolean enabled = plugin.toggleAutoGive();
            admin.sendMessage((enabled ? "§a✓" : "§c✗") + " Auto-give " + (enabled ? "enabled" : "disabled"));
            open(admin); // Refresh menu
        }
        // Join Fruit Settings
        else if(slot == 29) {
            openJoinFruitMenu(admin);
        }
        // Mass Give
        else if(slot == 30) {
            openMassGiveMenu(admin);
        }
        // Stop All Spins
        else if(slot == 31) {
            admin.closeInventory();
            Bukkit.dispatchCommand(admin, "fruit stopspin all");
            admin.sendMessage("§c✓ Stopped all active spins!");
        }
        // Player List
        else if(slot == 46) {
            openPlayerList(admin);
        }
        // Reload
        else if(slot == 49) {
            plugin.reloadConfig();
            admin.sendMessage("§a✓ Plugin reloaded!");
            admin.closeInventory();
        }
        // Close
        else if(slot == 52) {
            admin.closeInventory();
        }
    }
    
    private void openGiveMenu(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§a§l🎁 GIVE FRUIT");
        
        // Show all fruits
        int slot = 0;
        for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
            ItemStack display = fruit.createItemStack(1);
            ItemMeta meta = display.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to give this fruit");
            lore.add("§eThen select player and amount");
            meta.setLore(lore);
            display.setItemMeta(meta);
            gui.setItem(slot++, display);
        }
        
        // Player selector
        gui.setItem(49, createGuiItem(Material.PLAYER_HEAD, "§e§lSELECT PLAYER",
            "§7Click to choose a player"));
        
        gui.setItem(50, createGuiItem(Material.GOLD_INGOT, "§6§lSELECT AMOUNT",
            "§7Click to set amount (1-64)"));
        
        admin.openInventory(gui);
    }
    
    private void openRemoveMenu(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§c§l❌ REMOVE FRUIT");
        
        // Similar to give menu but for removal
        int slot = 0;
        for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
            ItemStack display = fruit.createItemStack(1);
            ItemMeta meta = display.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§cClick to remove this fruit");
            meta.setLore(lore);
            display.setItemMeta(meta);
            gui.setItem(slot++, display);
        }
        
        admin.openInventory(gui);
    }
    
    private void openSpinConfig(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§6§l🎲 SPIN WHEEL CONFIG");
        
        gui.setItem(20, createGuiItem(Material.EMERALD_BLOCK, "§a§lADD REWARD",
            "§7Add a fruit to spin wheel"));
        
        gui.setItem(22, createGuiItem(Material.REDSTONE_BLOCK, "§c§lREMOVE REWARD",
            "§7Remove a fruit from spin wheel"));
        
        gui.setItem(24, createGuiItem(Material.CHEST, "§e§lVIEW REWARDS",
            "§7See current rewards",
            "§eTotal: §f" + SpinWheel.getRewardCount() + " fruits"));
        
        admin.openInventory(gui);
    }
    
    private void openCooldownManager(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§e§l⏰ COOLDOWN MANAGER");
        
        gui.setItem(20, createGuiItem(Material.PLAYER_HEAD, "§a§lRESET PLAYER COOLDOWNS",
            "§7Reset all cooldowns for a player"));
        
        gui.setItem(22, createGuiItem(Material.CLOCK, "§6§lSET GLOBAL COOLDOWN",
            "§7Set cooldown for all abilities"));
        
        gui.setItem(24, createGuiItem(Material.COMMAND_BLOCK, "§b§lVIEW ACTIVE COOLDOWNS",
            "§7See who has active cooldowns"));
        
        admin.openInventory(gui);
    }
    
    private void openJoinFruitMenu(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§b§l👤 JOIN FRUIT SETTINGS");
        
        int slot = 0;
        for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
            ItemStack display = fruit.createItemStack(1);
            ItemMeta meta = display.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to set as join fruit");
            lore.add("§eCurrent: " + (fruit.getId().equals(plugin.getJoinFruit()) ? "✓ SELECTED" : ""));
            meta.setLore(lore);
            display.setItemMeta(meta);
            gui.setItem(slot++, display);
        }
        
        gui.setItem(49, createGuiItem(Material.GOLD_INGOT, "§6§lSET AMOUNT",
            "§7Current amount: §f" + plugin.getJoinFruitAmount(),
            "§eClick to change"));
        
        gui.setItem(50, createGuiItem(plugin.isJoinFruitEnabled() ? Material.LIME_WOOL : Material.RED_WOOL,
            (plugin.isJoinFruitEnabled() ? "§a§lENABLED" : "§c§lDISABLED"),
            "§7Click to toggle join fruit"));
        
        admin.openInventory(gui);
    }
    
    private void openMassGiveMenu(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§e§l📦 MASS GIVE");
        
        int slot = 0;
        for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
            ItemStack display = fruit.createItemStack(1);
            ItemMeta meta = display.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7Give to all §e" + Bukkit.getOnlinePlayers().size() + "§7 players");
            lore.add("§eClick to give 1x");
            meta.setLore(lore);
            display.setItemMeta(meta);
            gui.setItem(slot++, display);
        }
        
        admin.openInventory(gui);
    }
    
    private void openPlayerList(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§a§l👥 ONLINE PLAYERS");
        
        int slot = 0;
        for(Player player : Bukkit.getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(player);
            meta.setDisplayName("§e" + player.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§7Health: §f" + (int) player.getHealth() + "/" + (int) player.getMaxHealth());
            lore.add("§7Fruits: §f" + getPlayerFruitCount(player));
            lore.add("");
            lore.add("§aClick to manage this player");
            meta.setLore(lore);
            head.setItemMeta(meta);
            gui.setItem(slot++, head);
        }
        
        admin.openInventory(gui);
    }
    
    private int getPlayerFruitCount(Player player) {
        int count = 0;
        for(ItemStack item : player.getInventory().getContents()) {
            if(item != null && com.example.fruits.models.Fruit.getFruitId(item) != null) {
                count += item.getAmount();
            }
        }
        return count;
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
