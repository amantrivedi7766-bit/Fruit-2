package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.PlayerFruitData;
import com.example.fruits.models.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is holding a fruit
        if(event.getItem() == null) return;
        String fruitId = Fruit.getFruitId(event.getItem());
        if(fruitId == null) return;
        
        // Check if player has eaten the fruit
        PlayerFruitData data = FruitsPlugin.getInstance().getActivePlayers().get(player.getUniqueId());
        
        // If player hasn't eaten the fruit yet - EAT IT
        if(data == null || data.getFruit() == null) {
            // This will be handled by PlayerEatListener
            return;
        }
        
        // Player has eaten the fruit - use abilities with hotkeys
        
        Fruit fruit = data.getFruit();
        Action action = event.getAction();
        
        // RIGHT CLICK = Ability 1
        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            useAbility(player, data, fruit, 0); // Index 0 = first ability
            return;
        }
        
        // SHIFT + RIGHT CLICK = Ability 2
        if(player.isSneaking() && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)) {
            event.setCancelled(true);
            useAbility(player, data, fruit, 1); // Index 1 = second ability
            return;
        }
    }
    
    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        
        // Check if player is holding a fruit
        if(player.getInventory().getItemInMainHand() == null) return;
        String fruitId = Fruit.getFruitId(player.getInventory().getItemInMainHand());
        if(fruitId == null) return;
        
        // Check if player has eaten the fruit
        PlayerFruitData data = FruitsPlugin.getInstance().getActivePlayers().get(player.getUniqueId());
        if(data == null || data.getFruit() == null) return;
        
        Fruit fruit = data.getFruit();
        
        // SHIFT + LEFT CLICK = Ability 3
        if(player.isSneaking()) {
            event.setCancelled(true);
            useAbility(player, data, fruit, 2); // Index 2 = third ability
        }
    }
    
    private void useAbility(Player player, PlayerFruitData data, Fruit fruit, int index) {
        if(index < 0 || index >= fruit.getAbilities().size()) {
            player.sendMessage("§c❌ Invalid ability!");
            return;
        }
        
        Ability ability = fruit.getAbilities().get(index);
        String cooldownKey = fruit.getId() + "_" + index;
        
        // Check cooldown
        if(!FruitsPlugin.getInstance().getCooldownManager().checkCooldown(player, cooldownKey)) {
            return;
        }
        
        // Execute ability
        ability.getExecutor().execute(player);
        FruitsPlugin.getInstance().getCooldownManager().setCooldown(player, cooldownKey, ability.getCooldown(), ability.getName());
        
        data.incrementUsed();
        
        // Send message
        player.sendMessage("§a⚡ Used §6" + ability.getName() + "§a! (" + data.getUsedAbilities() + "/3)");
        
        // Show remaining uses in action bar
        int remaining = 3 - data.getUsedAbilities();
        player.sendMessage("§7Remaining uses: §e" + remaining);
        
        // Check if fruit returned
        if(data.getFruit() == null) {
            player.sendMessage("§a🔄 Fruit returned to inventory! Eat again to reuse abilities!");
        }
    }
}
