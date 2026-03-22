package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.gui.AdminGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AdminGUIListener implements Listener {
    
    private final FruitsPlugin plugin;
    private final AdminGUI adminGUI;
    
    public AdminGUIListener(FruitsPlugin plugin) {
        this.plugin = plugin;
        this.adminGUI = new AdminGUI(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
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
                adminGUI.openPlayerManagement(player);
            } else if (displayName.contains("FRUIT MANAGEMENT")) {
                adminGUI.openFruitManagement(player);
            } else if (displayName.contains("REWARD SETTINGS")) {
                adminGUI.openRewardSettings(player);
            } else if (displayName.contains("GRACE PERIOD")) {
                adminGUI.openGracePeriod(player);
            } else if (displayName.contains("AUTO GIVE")) {
                boolean enabled = plugin.toggleAutoGive();
                player.sendMessage((enabled ? "§a✓" : "§c✗") + " Auto-give " + (enabled ? "enabled" : "disabled"));
                adminGUI.openMainMenu(player);
            } else if (displayName.contains("SPIN ALL")) {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    plugin.getSpinManager().startSpin(p, 1);
                }
                player.sendMessage("§a✓ Started spin for all!");
                adminGUI.openMainMenu(player);
            } else if (displayName.contains("STOP SPINS")) {
                plugin.getSpinManager().stopAllSpins();
                player.sendMessage("§a✓ Stopped all spins!");
                adminGUI.openMainMenu(player);
            } else if (displayName.contains("RELOAD")) {
                plugin.reloadConfig();
                plugin.getConfigManager().reload();
                player.sendMessage("§a✓ Plugin reloaded!");
                adminGUI.openMainMenu(player);
            } else if (displayName.contains("BACK")) {
                adminGUI.openMainMenu(player);
            } else if (displayName.contains("CLOSE")) {
                player.closeInventory();
            }
        }
        
        // Handle Player Management clicks
        else if (title.contains("PLAYER MANAGEMENT")) {
            event.setCancelled(true);
            
            if (clicked.getItemMeta().getDisplayName().contains("BACK")) {
                adminGUI.openMainMenu(player);
            } else if (clicked.getType() == org.bukkit.Material.PLAYER_HEAD) {
                // Get player name from head
                String playerName = org.bukkit.ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                Player target = plugin.getServer().getPlayer(playerName);
                if (target != null) {
                    adminGUI.openPlayerFruitSelect(player, target);
                }
            }
        }
        
        // Handle Fruit Select clicks
        else if (title.contains("Give Fruit to")) {
            event.setCancelled(true);
            
            int cmd = clicked.getItemMeta().getCustomModelData();
            String fruitId = com.example.fruits.models.Fruit.getFruitId(clicked);
            
            if (fruitId != null) {
                String playerName = title.substring(title.lastIndexOf(" ") + 1);
                Player target = plugin.getServer().getPlayer(playerName);
                
                if (target != null) {
                    int amount = event.isShiftClick() ? 5 : 1;
                    com.example.fruits.models.Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
                    if (fruit != null) {
                        target.getInventory().addItem(fruit.createItemStack(amount));
                        player.sendMessage("§a✓ Gave §6" + amount + "x " + fruit.getName() + "§a to §e" + target.getName());
                        adminGUI.openPlayerManagement(player);
                    }
                }
            } else if (clicked.getItemMeta().getDisplayName().contains("BACK")) {
                adminGUI.openPlayerManagement(player);
            }
        }
    }
}
