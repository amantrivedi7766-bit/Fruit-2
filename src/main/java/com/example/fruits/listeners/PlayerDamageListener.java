package com.example.fruits.listeners;

import com.example.fruits.abilities.ThroneAbilities;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageListener implements Listener {
    
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        Entity damager = event.getDamager();
        double damage = event.getDamage();
        
        // Check if player has active shield
        if(ThroneAbilities.hasActiveShield(player)) {
            double newDamage = ThroneAbilities.handleDamage(player, damage, damager);
            event.setDamage(newDamage);
        }
    }
}
