package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.Ability;
import com.example.fruits.abilities.NatureAbilities;
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
        
        // Right click for abilities
        if((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);
            
            if(!player.isSneaking()) {
                // FIRST ABILITY - VINE ATTACH (Right Click)
                if(fruitId.equals("vine_weaver") && fruit.getAbilities().size() > 0) {
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
            } else {
                // SECOND ABILITY - OAK HAMMER (Crouch + Right Click)
                if(fruitId.equals("vine_weaver") && fruit.getAbilities().size() > 1) {
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
        
        // LEFT CLICK - LAUNCH ATTACHED PLAYER
        if((action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK)) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if(item != null) {
                String id = Fruit.getFruitId(item);
                if(id != null && id.equals("vine_weaver")) {
                    // Try to launch any attached player
                    NatureAbilities.handleLaunch(player);
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
        
        // FIRST ABILITY on entity click
        if(!player.isSneaking()) {
            if(fruitId.equals("vine_weaver") && fruit.getAbilities().size() > 0) {
                Ability ability = fruit.getAbilities().get(0);
                String cooldownKey = fruitId + "_0";
                if(FruitsPlugin.getInstance().getCooldownManager().checkCooldown(player, cooldownKey)) {
                    ability.getExecutor().execute(player, event.getRightClicked());
                    FruitsPlugin.getInstance().getCooldownManager().setCooldown(player, cooldownKey, ability.getCooldown(), ability.getName());
                }
            }
        } else {
            // SECOND ABILITY on entity click
            if(fruitId.equals("vine_weaver") && fruit.getAbilities().size() > 1) {
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
