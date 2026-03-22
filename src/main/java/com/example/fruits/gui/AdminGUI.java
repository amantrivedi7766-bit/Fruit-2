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

public class AdminGUI implements Listener {
    
    private final FruitsPlugin plugin;
    
    public AdminGUI(FruitsPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    // ==================== MAIN MENU ====================
    
    public void openMainMenu(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§c§l⚡ ADMIN CONTROL PANEL");
        
        // Row 1: Management
        gui.setItem(10, createGuiItem(Material.PLAYER_HEAD, "§a§l👥 PLAYER MANAGEMENT",
            "§7Manage players, give/remove fruits",
            "§eClick to open player management"));
        
        gui.setItem(11, createGuiItem(Material.CHEST, "§e§l🍎 FRUIT MANAGEMENT",
            "§7Manage fruits, add/remove rewards",
            "§eClick to open fruit management"));
        
        gui.setItem(12, createGuiItem(Material.GOLD_INGOT, "§6§l🎁 REWARD SETTINGS",
            "§7Configure rewards and drops",
            "§eClick to open reward settings"));
        
        gui.setItem(13, createGuiItem(Material.CLOCK, "§b§l⏰ GRACE PERIOD",
            "§7Manage protection periods",
            "§eClick to open grace period settings"));
        
        // Row 2: Quick Actions
        gui.setItem(28, createGuiItem(Material.GRASS_BLOCK, "§a§l🔄 AUTO GIVE",
            "§7Status: " + (plugin.isAutoGiveEnabled() ? "§aON" : "§cOFF"),
            "§eClick to toggle auto-give on/off"));
        
        gui.setItem(29, createGuiItem(Material.BEACON, "§d§l🎲 SPIN ALL",
            "§7Start spin for all online players",
            "§eClick to spin everyone!"));
        
        gui.setItem(30, createGuiItem(Material.BARRIER, "§c§l🛑 STOP SPINS",
            "§7Stop all active spins",
            "§eEmergency stop"));
        
        gui.setItem(31, createGuiItem(Material.COMMAND_BLOCK, "§b§l📊 STATISTICS",
            "§7View server statistics",
            "§eTotal fruits, spins, etc."));
        
        // Row 3: System
        gui.setItem(49, createGuiItem(Material.REPEATER, "§6§l🔄 RELOAD",
            "§7Reload plugin configuration",
            "§eClick to reload"));
        
        gui.setItem(53, createGuiItem(Material.REDSTONE, "§c§l❌ CLOSE",
            "§7Close admin panel"));
        
        admin.openInventory(gui);
    }
    
    // ==================== PLAYER MANAGEMENT ====================
    
    public void openPlayerManagement(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§a§l👥 PLAYER MANAGEMENT");
        
        int slot = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(player);
            meta.setDisplayName("§e" + player.getName());
            List<String> lore = new ArrayList<>();
            lore.add("§7Health: §f" + (int) player.getHealth() + "/" + (int) player.getMaxHealth());
            lore.add("§7Fruits: §f" + plugin.getPlayerManager().getTotalFruits(player));
            lore.add("§7Spins Today: §f" + plugin.getSpinManager().getDailySpins(player));
            lore.add("");
            lore.add("§aLeft-click to give fruit");
            lore.add("§cRight-click to remove fruit");
            meta.setLore(lore);
            head.setItemMeta(meta);
            gui.setItem(slot++, head);
        }
        
        gui.setItem(49, createGuiItem(Material.ARROW, "§e§l« BACK", "§7Return to main menu"));
        gui.setItem(53, createGuiItem(Material.REDSTONE, "§c§lCLOSE", "§7Close menu"));
        
        admin.openInventory(gui);
    }
    
    public void openPlayerFruitSelect(Player admin, Player target) {
        Inventory gui = Bukkit.createInventory(null, 54, "§e§l🍎 Give Fruit to " + target.getName());
        
        int slot = 0;
        for (Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
            ItemStack display = fruit.createItemStack(1);
            ItemMeta meta = display.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to give 1x " + fruit.getName());
            lore.add("§eShift-click to give 5x");
            meta.setLore(lore);
            display.setItemMeta(meta);
            gui.setItem(slot++, display);
        }
        
        gui.setItem(49, createGuiItem(Material.ARROW, "§e§l« BACK", "§7Return to player management"));
        gui.setItem(53, createGuiItem(Material.REDSTONE, "§c§lCLOSE", "§7Close menu"));
        
        admin.openInventory(gui);
    }
    
    // ==================== FRUIT MANAGEMENT ====================
    
    public void openFruitManagement(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§e§l🍎 FRUIT MANAGEMENT");
        
        int slot = 0;
        for (Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
            ItemStack display = fruit.createItemStack(1);
            ItemMeta meta = display.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7ID: §f" + fruit.getId());
            lore.add("§7Abilities: §f" + fruit.getAbilities().size());
            lore.add("");
            lore.add("§aClick to view details");
            meta.setLore(lore);
            display.setItemMeta(meta);
            gui.setItem(slot++, display);
        }
        
        gui.setItem(49, createGuiItem(Material.ARROW, "§e§l« BACK", "§7Return to main menu"));
        gui.setItem(53, createGuiItem(Material.REDSTONE, "§c§lCLOSE", "§7Close menu"));
        
        admin.openInventory(gui);
    }
    
    // ==================== REWARD SETTINGS ====================
    
    public void openRewardSettings(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§6§l🎁 REWARD SETTINGS");
        
        boolean rewardEnabled = plugin.getConfigManager().isRewardEnabled();
        
        gui.setItem(20, createGuiItem(rewardEnabled ? Material.LIME_WOOL : Material.RED_WOOL,
            (rewardEnabled ? "§a§lENABLED" : "§c§lDISABLED"),
            "§7Rewards from spins and drops",
            "§eClick to toggle"));
        
        gui.setItem(22, createGuiItem(Material.CHEST, "§e§lSET REWARD CHANCES",
            "§7Configure drop chances for fruits",
            "§eComing soon"));
        
        gui.setItem(24, createGuiItem(Material.DIAMOND, "§b§lVIEW STATISTICS",
            "§7See reward distribution stats",
            "§eTotal rewards given: §f" + getTotalRewardsGiven()));
        
        gui.setItem(49, createGuiItem(Material.ARROW, "§e§l« BACK", "§7Return to main menu"));
        gui.setItem(53, createGuiItem(Material.REDSTONE, "§c§lCLOSE", "§7Close menu"));
        
        admin.openInventory(gui);
    }
    
    // ==================== GRACE PERIOD ====================
    
    public void openGracePeriod(Player admin) {
        Inventory gui = Bukkit.createInventory(null, 54, "§b§l⏰ GRACE PERIOD");
        
        boolean globalGrace = plugin.getGracePeriodManager().isGlobalGraceActive();
        
        gui.setItem(20, createGuiItem(Material.CLOCK, "§a§lSTART GLOBAL GRACE",
            "§7Protect all players for X seconds",
            "§eClick to choose duration (5, 10, 30, 60)"));
        
        gui.setItem(22, createGuiItem(globalGrace ? Material.RED_WOOL : Material.GRAY_WOOL,
            globalGrace ? "§c§lEND GLOBAL GRACE" : "§7§lNO ACTIVE GRACE",
            globalGrace ? "§7End active global grace period" : "§7No global grace active"));
        
        gui.setItem(24, createGuiItem(Material.PLAYER_HEAD, "§b§lVIEW ACTIVE",
            "§7See players currently in grace period",
            "§eActive: §f" + plugin.getGracePeriodManager().getAllActivePeriods().size()));
        
        gui.setItem(49, createGuiItem(Material.ARROW, "§e§l« BACK", "§7Return to main menu"));
        gui.setItem(53, createGuiItem(Material.REDSTONE, "§c§lCLOSE", "§7Close menu"));
        
        admin.openInventory(gui);
    }
    
    // ==================== STATISTICS ====================
    
    private int getTotalRewardsGiven() {
        // Calculate total rewards given
        int total = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            total += plugin.getPlayerManager().getTotalFruits(player);
        }
        return total;
    }
    
    // ==================== EVENT HANDLER ====================
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();
        
        if (clicked == null || !clicked.hasItemMeta()) return;
        
        // Handle Admin GUI clicks
        if (title.contains("ADMIN CONTROL PANEL")) {
            event.setCancelled(true);
            String displayName = clicked.getItemMeta().getDisplayName();
            
            if (displayName.contains("PLAYER MANAGEMENT")) {
                openPlayerManagement(player);
            } else if (displayName.contains("FRUIT MANAGEMENT")) {
                openFruitManagement(player);
            } else if (displayName.contains("REWARD SETTINGS")) {
                openRewardSettings(player);
            } else if (displayName.contains("GRACE PERIOD")) {
                openGracePeriod(player);
            } else if (displayName.contains("AUTO GIVE")) {
                boolean enabled = plugin.toggleAutoGive();
                player.sendMessage((enabled ? "§a✓" : "§c✗") + " Auto-give " + (enabled ? "enabled" : "disabled"));
                openMainMenu(player);
            } else if (displayName.contains("SPIN ALL")) {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    plugin.getSpinManager().startSpin(p, 1);
                }
                player.sendMessage("§a✓ Started spin for all players!");
                openMainMenu(player);
            } else if (displayName.contains("STOP SPINS")) {
                plugin.getSpinManager().stopAllSpins();
                player.sendMessage("§a✓ Stopped all spins!");
                openMainMenu(player);
            } else if (displayName.contains("STATISTICS")) {
                player.sendMessage("§6=== Server Statistics ===");
                player.sendMessage("§7Online Players: §e" + Bukkit.getOnlinePlayers().size());
                player.sendMessage("§7Total Fruits: §e" + getTotalRewardsGiven());
                player.sendMessage("§7Active Spins: §e" + plugin.getSpinManager().getSpinningPlayers().size());
                openMainMenu(player);
            } else if (displayName.contains("RELOAD")) {
                plugin.reloadConfig();
                plugin.getConfigManager().reload();
                player.sendMessage("§a✓ Plugin reloaded!");
                openMainMenu(player);
            } else if (displayName.contains("BACK") || displayName.contains("CLOSE")) {
                player.closeInventory();
            }
        }
        
        // Handle Player Management clicks
        else if (title.contains("PLAYER MANAGEMENT")) {
            event.setCancelled(true);
            
            if (clicked.getItemMeta().getDisplayName().contains("BACK")) {
                openMainMenu(player);
            } else if (clicked.getItemMeta().getDisplayName().contains("CLOSE")) {
                player.closeInventory();
            } else if (clicked.getType() == Material.PLAYER_HEAD) {
                String playerName = org.bukkit.ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                Player target = plugin.getServer().getPlayer(playerName);
                if (target != null) {
                    if (event.isLeftClick()) {
                        openPlayerFruitSelect(player, target);
                    } else if (event.isRightClick()) {
                        // Remove fruit logic
                        player.sendMessage("§cRemove fruit feature - select fruit to remove");
                        openPlayerFruitSelect(player, target);
                    }
                }
            }
        }
        
        // Handle Fruit Select clicks
        else if (title.contains("Give Fruit to")) {
            event.setCancelled(true);
            
            if (clicked.getItemMeta().getDisplayName().contains("BACK")) {
                openPlayerManagement(player);
            } else if (clicked.getItemMeta().getDisplayName().contains("CLOSE")) {
                player.closeInventory();
            } else {
                String fruitId = com.example.fruits.models.Fruit.getFruitId(clicked);
                if (fruitId != null) {
                    String playerName = title.substring(title.lastIndexOf(" ") + 1);
                    Player target = plugin.getServer().getPlayer(playerName);
                    
                    if (target != null) {
                        int amount = event.isShiftClick() ? 5 : 1;
                        Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
                        if (fruit != null) {
                            target.getInventory().addItem(fruit.createItemStack(amount));
                            player.sendMessage("§a✓ Gave §6" + amount + "x " + fruit.getName() + "§a to §e" + target.getName());
                        }
                    }
                }
            }
        }
        
        // Handle Reward Settings clicks
        else if (title.contains("REWARD SETTINGS")) {
            event.setCancelled(true);
            
            if (clicked.getItemMeta().getDisplayName().contains("BACK")) {
                openMainMenu(player);
            } else if (clicked.getItemMeta().getDisplayName().contains("ENABLED") || 
                       clicked.getItemMeta().getDisplayName().contains("DISABLED")) {
                boolean enabled = !plugin.getConfigManager().isRewardEnabled();
                plugin.getConfigManager().setRewardEnabled(enabled);
                player.sendMessage((enabled ? "§a✓" : "§c✗") + " Rewards " + (enabled ? "enabled" : "disabled"));
                openRewardSettings(player);
            }
        }
        
        // Handle Grace Period clicks
        else if (title.contains("GRACE PERIOD")) {
            event.setCancelled(true);
            
            if (clicked.getItemMeta().getDisplayName().contains("BACK")) {
                openMainMenu(player);
            } else if (clicked.getItemMeta().getDisplayName().contains("START GLOBAL GRACE")) {
                player.closeInventory();
                player.sendMessage("§eSelect grace duration:");
                player.sendMessage("§a/fruit grace 5 §7- 5 seconds");
                player.sendMessage("§a/fruit grace 10 §7- 10 seconds");
                player.sendMessage("§a/fruit grace 30 §7- 30 seconds");
                player.sendMessage("§a/fruit grace 60 §7- 60 seconds");
            } else if (clicked.getItemMeta().getDisplayName().contains("END GLOBAL GRACE")) {
                plugin.getGracePeriodManager().endGlobalGrace();
                player.sendMessage("§a✓ Global grace period ended!");
                openGracePeriod(player);
            } else if (clicked.getItemMeta().getDisplayName().contains("VIEW ACTIVE")) {
                player.sendMessage("§6Active grace periods: §e" + 
                    plugin.getGracePeriodManager().getAllActivePeriods().size());
                openGracePeriod(player);
            }
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    private ItemStack createGuiItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
                                               }
