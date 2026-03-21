package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.Ability;
import com.example.fruits.abilities.NatureAbilities;
import com.example.fruits.abilities.ThiefAbilities;  // ✅ ADD THIS IMPORT
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if(event.getItem() == null) return;
        String fruitId = Fruit.getFruitId(event.getItem());
        if(fruitId == null) return;
        
        Fruit fruit = FruitsPlugin.getInstance().getFruitRegistry().getFruit(fruitId);
        if(fruit == null) return;
        
        Action action = event.getAction();
        
        // LEFT CLICK - LAUNCH ATTACHED PLAYER (Vine Weaver)
        if((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
            if(fruitId.equals("vine_weaver")) {
                NatureAbilities.handleLaunch(player);
package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.Ability;
import com.example.fruits.abilities.NatureAbilities;
import com.example.fruits.abilities.ThiefAbilities;
import com.example.fruits.abilities.VampireAbilities;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if(event.getItem() == null) return;
        String fruitId = Fruit.getFruitId(event.getItem());
        if(fruitId == null) return;
        
        Fruit fruit = FruitsPlugin.getInstance().getFruitRegistry().getFruit(fruitId);
        if(fruit == null) return;
        
        Action action = event.getAction();
        
        // LEFT CLICK - Handle special left-click abilities
        if((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
            // Vine Weaver - Launch attached player
            if(fruitId.equals("vine_weaver")) {
                NatureAbilities.handleLaunch(player);
                return;
            }
            
            // Dracula Bites - Blood Bite while riding bat
            if(fruitId.equals("dracula_bites") && VampireAbilities.isRidingBat(player)) {
                VampireAbilities.bloodBite(player);
                return;
            }
            return;
        }
        
        // RIGHT CLICK for abilities
        if((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);
            
            // Check for stolen ability first (Shadowweaver)
            if(ThiefAbilities.hasStolenAbility(player)) {
                Entity target = getTargetEntity(player, 15);
                if(ThiefAbilities.useStolenAbility(player, target)) {
                    return;
                }
            }
            
            // FIRST ABILITY - Normal Right Click
            if(!player.isSneaking()) {
                if(fruit.getAbilities().size() > 0) {
                    Entity target = getTargetEntity(player, 15);
                    Ability ability = fruit.getAbilities().get(0);
                    String cooldownKey = fruitId + "_0";
                    if(FruitsPlugin.getInstance().getCooldownManager().checkCooldown(player, cooldownKey)) {
                        ability.getExecutor().execute(player, target);
                        FruitsPlugin.getInstance().getCooldownManager().setCooldown(player, cooldownKey, ability.getCooldown(), ability.getName());
                    }
                } else {
                    player.sendMessage("§e🍎 " + fruit.getName() + " §7- Right-click ability coming soon!");
                }
            } 
            // SECOND ABILITY - Crouch + Right Click
            else {
                if(fruit.getAbilities().size() > 1) {
                    Entity target = getTargetEntity(player, 20);
                    Ability ability = fruit.getAbilities().get(1);
                    String cooldownKey = fruitId + "_1";
                    if(FruitsPlugin.getInstance().getCooldownManager().checkCooldown(player, cooldownKey)) {
                        ability.getExecutor().execute(player, target);
                        FruitsPlugin.getInstance().getCooldownManager().setCooldown(player, cooldownKey, ability.getCooldown(), ability.getName());
                    }
                } else {
                    player.sendMessage("§e🍎 " + fruit.getName() + " §7- Crouch + Right-click ability coming soon!");
                }
            }
        }
    }
    
    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        
        if(event.getHand() != EquipmentSlot.HAND) return;
        
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item == null) return;
        
        String fruitId = Fruit.getFruitId(item);
        if(fruitId == null) return;
        
        Fruit fruit = FruitsPlugin.getInstance().getFruitRegistry().getFruit(fruitId);
        if(fruit == null) return;
        
        event.setCancelled(true);
        
        // Check for stolen ability first
        if(ThiefAbilities.hasStolenAbility(player)) {
            if(ThiefAbilities.useStolenAbility(player, event.getRightClicked())) {
                return;
            }
        }
        
        // Normal fruit ability on entity click
        if(!player.isSneaking()) {
            if(fruit.getAbilities().size() > 0) {
                Ability ability = fruit.getAbilities().get(0);
                String cooldownKey = fruitId + "_0";
                if(FruitsPlugin.getInstance().getCooldownManager().checkCooldown(player, cooldownKey)) {
                    ability.getExecutor().execute(player, event.getRightClicked());
                    FruitsPlugin.getInstance().getCooldownManager().setCooldown(player, cooldownKey, ability.getCooldown(), ability.getName());
                }
            }
        } else {
            if(fruit.getAbilities().size() > 1) {
                Ability ability = fruit.getAbilities().get(1);
                String cooldownKey = fruitId + "_1";
                if(FruitsPlugin.getInstance().getCooldownManager().checkCooldown(player, cooldownKey)) {
                    ability.getExecutor().execute(player, event.getRightClicked());
                    FruitsPlugin.getInstance().getCooldownManager().setCooldown(player, cooldownKey, ability.getCooldown(), ability.getName());
                }
            }
        }
    }
    
    private Entity getTargetEntity(Player player, int range) {
        return player.getWorld().getNearbyEntities(player.getEyeLocation(), range, range, range)
            .stream()
            .filter(e -> e != player && e.getLocation().distance(player.getEyeLocation()) <= range)
            .min((e1, e2) -> {
                double d1 = e1.getLocation().distance(player.getEyeLocation());
                double d2 = e2.getLocation().distance(player.getEyeLocation());
                return Double.compare(d1, d2);
            })
            .orElse(null);
    }
}
