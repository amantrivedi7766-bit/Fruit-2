package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.Ability;
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
        
        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if(!player.isSneaking()) {
                event.setCancelled(true);
                Entity target = getTargetEntity(player, 20);
                useAbility(player, fruit, 0, target);
            }
        }
        
        if(player.isSneaking() && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);
            Entity target = getTargetEntity(player, 20);
            useAbility(player, fruit, 1, target);
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
        
        if(!player.isSneaking()) {
            useAbility(player, fruit, 0, event.getRightClicked());
        } else {
            useAbility(player, fruit, 1, event.getRightClicked());
        }
    }
    
    private void useAbility(Player player, Fruit fruit, int index, Entity target) {
        if(index < 0 || index >= fruit.getAbilities().size()) {
            player.sendMessage("§c❌ Invalid ability!");
            return;
        }
        
        Ability ability = fruit.getAbilities().get(index);
        String cooldownKey = fruit.getId() + "_" + index;
        
        if(!FruitsPlugin.getInstance().getCooldownManager().checkCooldown(player, cooldownKey)) return;
        
        ability.getExecutor().execute(player, target);
        FruitsPlugin.getInstance().getCooldownManager().setCooldown(player, cooldownKey, ability.getCooldown(), ability.getName());
        
        if(target != null) {
            String targetName = target instanceof Player ? ((Player) target).getName() : target.getType().name().toLowerCase().replace("_", " ");
            player.sendMessage("§a⚡ Used §6" + ability.getName() + "§a on §e" + targetName + "§a!");
        } else {
            player.sendMessage("§a⚡ Used §6" + ability.getName() + "§a!");
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
