package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.PlayerFruitData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class PlayerEatListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        
        if(action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;
        
        ItemStack item = player.getInventory().getItemInMainHand();
        if(item == null || item.getType() == Material.AIR) return;
        
        String fruitId = Fruit.getFruitId(item);
        if(fruitId == null) return;
        
        event.setCancelled(true);
        
        Fruit fruit = FruitsPlugin.getInstance().getFruitRegistry().getFruit(fruitId);
        if(fruit == null) return;
        
        // Check if player already has active fruit
        PlayerFruitData existingData = FruitsPlugin.getInstance().getActivePlayers().get(player.getUniqueId());
        if(existingData != null && existingData.getFruit() != null) {
            player.sendMessage("§c❌ You already have an active fruit! Use all abilities first!");
            return;
        }
        
        // Remove ONE fruit from hand
        item.setAmount(item.getAmount() - 1);
        
        // Store player's active fruit
        PlayerFruitData data = new PlayerFruitData(player, fruit);
        FruitsPlugin.getInstance().getActivePlayers().put(player.getUniqueId(), data);
        
        // Start grace period
        if(FruitsPlugin.getInstance().getConfig().getBoolean("grace_period.enabled", true)) {
            int duration = FruitsPlugin.getInstance().getConfig().getInt("grace_period.duration", 60);
            FruitsPlugin.getInstance().getGracePeriodManager().startGracePeriod(player, duration);
        }
        
        // Show abilities
        String abilities = fruit.getAbilities().get(0).getName() + " §7| §e" + fruit.getAbilities().get(1).getName();
        
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
            TextComponent.fromLegacyText("§a🍎 " + fruit.getDisplayName() + " §7| §e" + abilities));
        
        player.sendMessage("§a✅ You ate " + fruit.getDisplayName() + "!");
        player.sendMessage("§e⚡ Hotkeys:");
        player.sendMessage("§7  • §eRight Click §7→ §f" + fruit.getAbilities().get(0).getName());
        player.sendMessage("§7  • §eShift + Right Click §7→ §f" + fruit.getAbilities().get(1).getName());
    }
}
