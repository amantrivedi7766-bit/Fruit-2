package com.example.fruits.listeners;

import com.example.fruits.abilities.PortalAbilities;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PortalListener implements Listener {
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PortalAbilities.handlePortalTeleport(player, player.getLocation());
    }
    
    @EventHandler
    public void onLeftClickPortal(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        
        if(action == Action.LEFT_CLICK_BLOCK) {
            if(event.getClickedBlock() != null) {
                PortalAbilities.handleSummonPortalClick(player, event.getClickedBlock().getLocation());
            }
        }
    }
    
    @EventHandler
    public void onSummonGUI(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;
        
        String title = event.getView().getTitle();
        if(!title.equals("§5§l🌀 SUMMON PLAYER")) return;
        
        event.setCancelled(true);
        
        Player summoner = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        
        if(clicked == null) return;
        
        if(clicked.getType() == Material.BARRIER) {
            summoner.closeInventory();
            return;
        }
        
        if(clicked.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) clicked.getItemMeta();
            String targetName = meta.getDisplayName().replace("§d§l", "");
            Player target = org.bukkit.Bukkit.getPlayer(targetName);
            
            if(target != null && target.isOnline()) {
                PortalAbilities.handleSummonSelection(summoner, target);
                summoner.closeInventory();
            }
        }
    }
}
