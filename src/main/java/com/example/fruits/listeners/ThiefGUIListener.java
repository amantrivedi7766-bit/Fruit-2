package com.example.fruits.listeners;

import com.example.fruits.abilities.ThiefAbilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ThiefGUIListener implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        if(!title.equals("§8§l🌑 SHADOW STEAL")) return;
        
        event.setCancelled(true);
        
        Player thief = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if(clicked == null) return;
        
        // Close button
        if(clicked.getType() == org.bukkit.Material.BARRIER) {
            thief.closeInventory();
            thief.sendMessage("§cShadow steal cancelled!");
            return;
        }
        
        // Player head clicked
        if(clicked.getType() == org.bukkit.Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) clicked.getItemMeta();
            String displayName = meta.getDisplayName();
            // Remove color codes from display name
            String targetName = displayName.replace("§c§l", "");
            Player target = org.bukkit.Bukkit.getPlayer(targetName);
            
            if(target != null && target.isOnline()) {
                ThiefAbilities.handleGUIClick(thief, target);
            } else {
                thief.sendMessage("§c❌ That player is no longer online!");
                thief.closeInventory();
            }
        }
    }
}
