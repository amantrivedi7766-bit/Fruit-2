package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.gui.AdminGUI;
import com.example.fruits.models.Fruit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AdminGUIListener implements Listener {
    
    private final AdminGUI adminGUI = new AdminGUI();
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clicked = event.getCurrentItem();
        
        if(clicked == null || clicked.getType() == Material.AIR) return;
        
        event.setCancelled(true);
        
        // Main Menu
        if(title.equals("§6§l🍎 FRUIT ADMIN PANEL")) {
            handleMainMenu(player, clicked);
        }
        // Player Management
        else if(title.equals("§a§l👥 Player Management")) {
            handlePlayerManagement(player, clicked);
        }
        // Fruit Management
        else if(title.equals("§e§l🍎 Fruit Management")) {
            handleFruitManagement(player, clicked);
        }
        // Reward Settings
        else if(title.equals("§b§l🎁 Reward Settings")) {
            handleRewardSettings(player, clicked);
        }
        // Grace Period
        else if(title.equals("§3§l🛡️ Grace Period")) {
            handleGracePeriod(player, clicked);
        }
        // Give Fruit to Player
        else if(title.startsWith("§e§lGive Fruit to ")) {
            String targetName = title.replace("§e§lGive Fruit to ", "");
            Player target = player.getServer().getPlayer(targetName);
            handleGiveFruit(player, target, clicked);
        }
    }
    
    private void handleMainMenu(Player player, ItemStack clicked) {
        String name = clicked.getItemMeta().getDisplayName();
        
        if(name.contains("Player Management")) {
            adminGUI.openPlayerManagement(player);
        }
        else if(name.contains("Fruit Management")) {
            adminGUI.openFruitManagement(player);
        }
        else if(name.contains("Reward Settings")) {
            adminGUI.openRewardSettings(player);
        }
        else if(name.contains("Grace Period")) {
            adminGUI.openGracePeriod(player);
        }
        else if(name.contains("Statistics")) {
            player.sendMessage("§e§l📊 STATISTICS");
            player.sendMessage("§7Active Players: §e" + FruitsPlugin.getInstance().getActivePlayers().size());
            player.sendMessage("§7Join Reward: " + (FruitsPlugin.getInstance().getConfigManager().isRewardEnabled() ? "§aENABLED" : "§cDISABLED"));
        }
        else if(name.contains("Close")) {
            player.closeInventory();
        }
    }
    
    private void handlePlayerManagement(Player player, ItemStack clicked) {
        if(clicked.getType() == Material.ARROW) {
            adminGUI.openMainMenu(player);
            return;
        }
        
        if(clicked.getType() == Material.PLAYER_HEAD) {
            String targetName = clicked.getItemMeta().getDisplayName().replace("§a", "");
            Player target = player.getServer().getPlayer(targetName);
            if(target != null) {
                adminGUI.openPlayerFruitSelect(player, target);
            }
        }
    }
    
    private void handleFruitManagement(Player player, ItemStack clicked) {
        if(clicked.getType() == Material.ARROW) {
            adminGUI.openMainMenu(player);
            return;
        }
        
        // Get fruit from clicked item
        String fruitId = Fruit.getFruitId(clicked);
        if(fruitId != null) {
            adminGUI.openPlayerFruitSelect(player, player);
        }
    }
    
    private void handleRewardSettings(Player player, ItemStack clicked) {
        String name = clicked.getItemMeta().getDisplayName();
        
        if(name.contains("Reward")) {
            boolean enabled = FruitsPlugin.getInstance().getConfigManager().isRewardEnabled();
            FruitsPlugin.getInstance().getConfigManager().setRewardEnabled(!enabled);
            player.sendMessage("§a✅ Join reward " + (!enabled ? "enabled" : "disabled") + "!");
            adminGUI.openRewardSettings(player);
        }
        else if(name.contains("Test Spin")) {
            FruitsPlugin.getInstance().getSpinManager().startSpin(player);
        }
        else if(clicked.getType() == Material.ARROW) {
            adminGUI.openMainMenu(player);
        }
    }
    
    private void handleGracePeriod(Player player, ItemStack clicked) {
        String name = clicked.getItemMeta().getDisplayName();
        
        if(name.contains("30 Seconds")) {
            FruitsPlugin.getInstance().getGracePeriodManager().startGlobalGrace(30);
            player.sendMessage("§a✅ 30 second grace period started!");
            adminGUI.openMainMenu(player);
        }
        else if(name.contains("60 Seconds")) {
            FruitsPlugin.getInstance().getGracePeriodManager().startGlobalGrace(60);
            player.sendMessage("§a✅ 60 second grace period started!");
            adminGUI.openMainMenu(player);
        }
        else if(name.contains("120 Seconds")) {
            FruitsPlugin.getInstance().getGracePeriodManager().startGlobalGrace(120);
            player.sendMessage("§a✅ 120 second grace period started!");
            adminGUI.openMainMenu(player);
        }
        else if(name.contains("Cancel Grace")) {
            FruitsPlugin.getInstance().getGracePeriodManager().endGlobalGrace();
            player.sendMessage("§c❌ Grace period cancelled!");
            adminGUI.openMainMenu(player);
        }
        else if(clicked.getType() == Material.ARROW) {
            adminGUI.openMainMenu(player);
        }
    }
    
    private void handleGiveFruit(Player admin, Player target, ItemStack clicked) {
        if(clicked.getType() == Material.ARROW) {
            adminGUI.openPlayerManagement(admin);
            return;
        }
        
        String fruitId = Fruit.getFruitId(clicked);
        if(fruitId != null && target != null) {
            Fruit fruit = FruitsPlugin.getInstance().getFruitRegistry().getFruit(fruitId);
            if(fruit != null) {
                ItemStack fruitItem = fruit.createItem();
                target.getInventory().addItem(fruitItem);
                FruitsPlugin.getInstance().getPlayerManager().setPlayerFruit(target, fruitId);
                admin.sendMessage("§a✅ Gave " + fruit.getName() + " §ato " + target.getName());
                target.sendMessage("§a🎁 You received " + fruit.getName() + " §afrom admin!");
                adminGUI.openPlayerManagement(admin);
            }
        }
    }
}
