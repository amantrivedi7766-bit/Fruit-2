package com.example.fruits.listeners;

import com.example.fruits.abilities.VampireAbilities;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class VampireListener implements Listener {
    
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        // Player hitting entity
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(event.getEntity() instanceof LivingEntity) {
                VampireAbilities.handleHit(player, (LivingEntity) event.getEntity());
            }
        }
        
        // Bat bite attack
        if(event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if(VampireAbilities.isRidingBat(player)) {
                // This is handled separately in PlayerInteractListener for left click
            }
        }
    }
}
