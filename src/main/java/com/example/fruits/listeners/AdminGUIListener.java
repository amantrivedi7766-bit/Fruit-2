package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.gui.AdminGUI;
import com.example.fruits.models.Fruit;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

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
                p.sendMessage(" §7- §f" + player.getName());
            }
        }
        else if(slot == 12) {
            p.closeInventory();
            for(Player player : Bukkit.getOnlinePlayers()) {
                Fruit[] fruits = FruitsPlugin.getInstance().getFruitRegistry().getAllFruits().toArray(new Fruit[0]);
                Fruit random = fruits[new Random().nextInt(fruits.length)];
                player.getInventory().addItem(random.createItem());
                player.sendMessage("§aYou received a random fruit from admin!");
            }
            p.sendMessage("§aGave random fruits to all players!");
        }
        // Server Controls
        else if(slot == 36) {
            p.closeInventory();
            p.sendMessage("§aType your broadcast message in chat:");
        }
        else if(slot == 37) {
            for(int i = 0; i < 100; i++) {
                Bukkit.broadcastMessage("");
            }
            Bukkit.broadcastMessage("§c§lChat cleared by admin!");
        }
        else if(slot == 38) {
            Bukkit.getWorlds().forEach(w -> w.setTime(1000));
            Bukkit.broadcastMessage("§e☀️ Time set to day!");
        }
        else if(slot == 39) {
            Bukkit.getWorlds().forEach(w -> w.setTime(13000));
            Bukkit.broadcastMessage("§8🌙 Time set to night!");
        }
        else if(slot == 44) {
            p.closeInventory();
            Bukkit.broadcastMessage("§c§lServer shutting down...");
            Bukkit.getServer().shutdown();
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
                Player random = players.get(new Random().nextInt(players.size()));
                p.performCommand("fruitadmin spin " + random.getName());
                p.sendMessage("§aSpun for random player: §e" + random.getName());
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
