package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.Ability;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    
    private final FruitsPlugin plugin;
    
    public PlayerInteractListener(FruitsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if(item == null) return;
        
        String fruitId = Fruit.getFruitId(item);
        if(fruitId == null) return;
        
        Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
        if(fruit == null) return;
        
        Action action = event.getAction();
        boolean isRightClick = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
        
        if(!isRightClick) return;
        
        event.setCancelled(true);
        
        // Get target entity (what player is looking at)
        Entity target = getTargetEntity(player, 6);
        
        // Check if sneaking for second ability
        boolean isSneaking = player.isSneaking();
        
        if(!isSneaking) {
            // First ability (normal right click)
            useAbility(player, fruit, 0, target);
        } else {
            // Second ability (sneak + right click)
            if(fruit.getAbilities().size() > 1) {
                useAbility(player, fruit, 1, target);
            } else {
                player.sendMessage("§cThis fruit has only one ability!");
                useAbility(player, fruit, 0, target);
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if(item == null) return;
        
        String fruitId = Fruit.getFruitId(item);
        if(fruitId == null) return;
        
        Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
        if(fruit == null) return;
        
        event.setCancelled(true);
        
        Entity target = event.getRightClicked();
        
        // Check if sneaking for second ability
        boolean isSneaking = player.isSneaking();
        
        if(!isSneaking) {
            // First ability (normal right click on entity)
            useAbility(player, fruit, 0, target);
        } else {
            // Second ability (sneak + right click on entity)
            if(fruit.getAbilities().size() > 1) {
                useAbility(player, fruit, 1, target);
            } else {
                player.sendMessage("§cThis fruit has only one ability!");
                useAbility(player, fruit, 0, target);
            }
        }
    }
    
    private void useAbility(Player player, Fruit fruit, int index, Entity target) {
        if(index < 0 || index >= fruit.getAbilities().size()) {
            player.sendMessage("§cInvalid ability!");
            return;
        }
        
        Ability ability = fruit.getAbilities().get(index);
        String cooldownKey = fruit.getId() + "_" + index;
        
        // Check cooldown
        if(plugin.getCooldownManager().hasCooldown(player, cooldownKey)) {
            long remaining = plugin.getCooldownManager().getRemaining(player, cooldownKey);
            player.sendMessage("§c" + ability.getName() + " on cooldown! §7" + remaining + " seconds remaining");
            return;
        }
        
        // Execute ability
        try {
            ability.getExecutor().execute(player, target);
        } catch(Exception e) {
            player.sendMessage("§cError executing ability!");
            plugin.getLogger().warning("Ability error: " + e.getMessage());
        }
        
        // Set cooldown
        plugin.getCooldownManager().setCooldown(player, cooldownKey, ability.getCooldown(), ability.getName());
        
        // Play effect
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
        
        // Send message
        if(target != null) {
            String targetName = target instanceof Player ? ((Player) target).getName() : 
                               target.getType().name().toLowerCase().replace("_", " ");
            player.sendMessage("§a⚡ Used §6" + ability.getName() + "§a on §e" + targetName + "§a!");
        } else {
            player.sendMessage("§a⚡ Used §6" + ability.getName() + "§a!");
        }
        
        // Debug
        if(plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info(player.getName() + " used " + ability.getName() + " from " + fruit.getName());
        }
    }
    
    private Entity getTargetEntity(Player player, int range) {
        return player.getWorld().getNearbyEntities(player.getEyeLocation(), range, range, range)
            .stream()
            .filter(e -> e != player && e.getLocation().distance(player.getEyeLocation()) <= range)
            .min((e1, e2) -> Double.compare(
                e1.getLocation().distance(player.getEyeLocation()),
                e2.getLocation().distance(player.getEyeLocation())
            ))
            .orElse(null);
    }
}
