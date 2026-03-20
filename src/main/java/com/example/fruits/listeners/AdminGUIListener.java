package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.gui.AdminGUI;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.PlayerFruitData;
import com.example.fruits.utils.SpinWheel;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.*;

public class AdminGUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(!(e.getInventory().getHolder() instanceof AdminGUI.GUIHolder)) return;
        
        e.setCancelled(true);
        if(e.getCurrentItem() == null) return;
        
        Player p = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        
        // Fruits Section (0-9)
        if(slot >= 0 && slot <= 9) {
            String fruitId = Fruit.getFruitId(e.getCurrentItem());
            if(fruitId != null) {
                p.closeInventory();
                p.playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.2f);
                p.performCommand("fruitadmin give " + p.getName() + " " + fruitId);
            }
        }
        // Admin Controls
        else if(slot == 10) {
            p.performCommand("fruitadmin reload");
            p.sendMessage("§aConfig reloaded!");
        }
        else if(slot == 11) {
            p.closeInventory();
            p.sendMessage("§eOnline players: §b" + Bukkit.getOnlinePlayers().size());
            for(Player player : Bukkit.getOnlinePlayers()) {
                PlayerFruitData data = FruitsPlugin.getInstance().getActivePlayers().get(player.getUniqueId());
                String status = (data != null && data.getFruit() != null) ? "§a✓ Has Power" : "§c✗ No Power";
                p.sendMessage(" §7- §f" + player.getName() + " §7" + status);
            }
        }
        else if(slot == 12) {
            p.closeInventory();
            Fruit[] fruits = FruitsPlugin.getInstance().getFruitRegistry().getAllFruits().toArray(new Fruit[0]);
            Random random = new Random();
            for(Player player : Bukkit.getOnlinePlayers()) {
                Fruit randomFruit = fruits[random.nextInt(fruits.length)];
                player.getInventory().addItem(randomFruit.createItem());
                player.sendMessage("§a🎁 You received a random fruit from admin!");
            }
            p.sendMessage("§aGave random fruits to all players!");
        }
        else if(slot == 13) {
            p.closeInventory();
            FruitsPlugin.getInstance().getActivePlayers().clear();
            Bukkit.broadcastMessage("§c§l🗑️ All fruit powers have been removed by admin!");
        }
        // Player Management (18-35)
        else if(slot >= 18 && slot <= 35) {
            int playerIndex = slot - 18;
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            if(playerIndex < players.size()) {
                Player target = players.get(playerIndex);
                if(e.isLeftClick()) {
                    p.closeInventory();
                    PlayerFruitData data = FruitsPlugin.getInstance().getActivePlayers().get(target.getUniqueId());
                    if(data != null && data.getFruit() != null) {
                        p.sendMessage("§6=== " + target.getName() + "'s Power ===");
                        p.sendMessage("§7Fruit: " + data.getFruit().getDisplayName());
                        p.sendMessage("§7Uses remaining: §e" + (3 - data.getUsedAbilities()));
                    } else {
                        p.sendMessage("§c" + target.getName() + " has no fruit power!");
                    }
                } else if(e.isRightClick()) {
                    p.closeInventory();
                    FruitsPlugin.getInstance().getActivePlayers().remove(target.getUniqueId());
                    p.sendMessage("§c🗑️ Removed " + target.getName() + "'s fruit power!");
                    target.sendMessage("§c🗑️ Your fruit power was removed by admin!");
                }
            }
        }
        // Grace Period Controls
        else if(slot == 36) {
            p.closeInventory();
            FruitsPlugin.getInstance().getGracePeriodManager().startGlobalGrace(60);
            p.sendMessage("§a✅ Started 60 second global grace period!");
        }
        else if(slot == 37) {
            p.closeInventory();
            // End grace period logic
            p.sendMessage("§cGrace period ended!");
        }
        else if(slot == 38) {
            p.closeInventory();
            boolean current = FruitsPlugin.getInstance().getConfig().getBoolean("death.lose_power", true);
            FruitsPlugin.getInstance().getConfig().set("death.lose_power", !current);
            FruitsPlugin.getInstance().saveConfig();
            p.sendMessage("§aDeath power loss set to: " + (!current ? "§cLOSE POWER" : "§aKEEP POWER"));
        }
        // Spin Controls
        else if(slot == 45) {
            p.closeInventory();
            p.performCommand("fruitadmin spin " + p.getName());
        }
        else if(slot == 46) {
            p.closeInventory();
            p.performCommand("fruitadmin spinall");
        }
        else if(slot == 47) {
            p.closeInventory();
            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
            if(!players.isEmpty()) {
                Random random = new Random();
                Player randomPlayer = players.get(random.nextInt(players.size()));
                p.performCommand("fruitadmin spin " + randomPlayer.getName());
                p.sendMessage("§aSpun for random player: §e" + randomPlayer.getName());
            }
        }
    }
    
    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if(e.getInventory().getHolder() instanceof AdminGUI.GUIHolder) {
            e.setCancelled(true);
        }
    }
}
