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

import java.util.*;

public class AdminMenu implements Listener {
    
    private final FruitsPlugin plugin;
    
    public AdminMenu(FruitsPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void open(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§c§l⚡ ADMIN CONTROL PANEL");
        
        // Row 1: Fruit Management
        gui.setItem(10, createGuiItem(Material.DIAMOND, "§a§l🎁 GIVE FRUIT",
            "§7Give fruits to players"));
        gui.setItem(11, createGuiItem(Material.REDSTONE_BLOCK, "§c§l❌ REMOVE FRUIT",
            "§7Remove fruits from players"));
        gui.setItem(12, createGuiItem(Material.BEACON, "§d§l👥 SPIN ALL",
            "§7Start spin for all online players"));
        gui.setItem(13, createGuiItem(Material.BARRIER, "§c§l🛑 STOP SPINS",
            "§7Stop all active spins"));
        
        // Row 2: Auto Give
        gui.setItem(28, createGuiItem(Material.GRASS_BLOCK, "§a§l🔄 AUTO GIVE",
            "§7Status: " + (plugin.isAutoGiveEnabled() ? "§aON" : "§cOFF"),
            "§eClick to toggle"));
        
        gui.setItem(29, createGuiItem(Material.PLAYER_HEAD, "§b§l👤 JOIN FRUIT",
            "§7Current: " + plugin.getJoinFruitName() + " x" + plugin.getJoinFruitAmount()));
        
        // Row 3: System
        gui.setItem(49, createGuiItem(Material.REPEATER, "§6§l🔄 RELOAD",
            "§7Reload plugin configuration"));
        gui.setItem(53, createGuiItem(Material.REDSTONE, "§c§l❌ CLOSE",
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
        
        switch(slot) {
            case 10: // Give
                admin.closeInventory();
                admin.sendMessage("§cUse: /fruit give <player> <fruit> [amount]");
                break;
            case 11: // Remove
                admin.closeInventory();
                admin.sendMessage("§cUse: /fruit remove <player> <fruit> [amount]");
                break;
            case 12: // Spin All
                admin.closeInventory();
                Bukkit.dispatchCommand(admin, "fruit spinall");
                break;
            case 13: // Stop Spins
                admin.closeInventory();
                if(plugin.getSpinManager() != null) {
                    plugin.getSpinManager().stopAllSpins();
                }
                admin.sendMessage("§a✓ Stopped all active spins!");
                break;
            case 28: // Auto Give Toggle
                boolean enabled = plugin.toggleAutoGive();
                admin.sendMessage((enabled ? "§a✓" : "§c✗") + " Auto-give " + (enabled ? "enabled" : "disabled"));
                open(admin);
                break;
            case 49: // Reload
                plugin.reloadConfig();
                admin.sendMessage("§a✓ Plugin reloaded!");
                admin.closeInventory();
                break;
            case 53: // Close
                admin.closeInventory();
                break;
        }
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
