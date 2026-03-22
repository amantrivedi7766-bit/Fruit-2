package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.Ability;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
            useAbilityAndConsume(player, fruit, 0, target, item);
        } else {
            // Second ability (sneak + right click)
            if(fruit.getAbilities().size() > 1) {
                useAbilityAndConsume(player, fruit, 1, target, item);
            } else {
                player.sendMessage("§cThis fruit has only one ability!");
                useAbilityAndConsume(player, fruit, 0, target, item);
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
            useAbilityAndConsume(player, fruit, 0, target, item);
        } else {
            if(fruit.getAbilities().size() > 1) {
                useAbilityAndConsume(player, fruit, 1, target, item);
            } else {
                player.sendMessage("§cThis fruit has only one ability!");
                useAbilityAndConsume(player, fruit, 0, target, item);
            }
        }
    }
    
    private void useAbilityAndConsume(Player player, Fruit fruit, int index, Entity target, ItemStack item) {
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
        
        // Play eat animation and sound first
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1.0f, 1.0f);
        
        // Consume the fruit (remove 1 from stack)
        if(player.getGameMode() != GameMode.CREATIVE) {
            if(item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        }
        
        // Small delay before executing ability (for eat animation)
        new BukkitRunnable() {
            @Override
            public void run() {
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
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
                
                // Send message
                if(target != null) {
                    String targetName = target instanceof Player ? ((Player) target).getName() : 
                                       target.getType().name().toLowerCase().replace("_", " ");
                    player.sendMessage("§a⚡ Used §6" + ability.getName() + "§a on §e" + targetName + "§a!");
                } else {
                    player.sendMessage("§a⚡ Used §6" + ability.getName() + "§a!");
                }
                
                // Particle effect
                player.getWorld().spawnParticle(org.bukkit.Particle.HEART, player.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3);
            }
        }.runTaskLater(plugin, 5L); // 0.25 second delay for eat animation
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
