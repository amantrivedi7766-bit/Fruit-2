package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.PlayerFruitData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEatListener implements Listener {

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        String fruitId = Fruit.getFruitId(item);
        if(fruitId == null) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        Fruit fruit = FruitsPlugin.getInstance().getFruitRegistry().getFruit(fruitId);
        
        if(fruit == null) return;

        // Remove one fruit from hand
        item.setAmount(item.getAmount() - 1);
        
        // Store player's active fruit
        PlayerFruitData data = new PlayerFruitData(player, fruit);
        FruitsPlugin.getInstance().getActivePlayers().put(player.getUniqueId(), data);

        // Show abilities in action bar
        String abilities = fruit.getAbilities().get(0).getName() + " §7| §e" + 
                          fruit.getAbilities().get(1).getName() + " §7| §e" + 
                          fruit.getAbilities().get(2).getName();
        
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
            TextComponent.fromLegacyText("§a🍎 " + fruit.getDisplayName() + " §7| §e" + abilities));
        
        player.sendMessage("§a✅ You ate " + fruit.getDisplayName() + "!");
        player.sendMessage("§e⚡ Hotkeys:");
        player.sendMessage("§7  • §eRight Click §7→ §f" + fruit.getAbilities().get(0).getName());
        player.sendMessage("§7  • §eShift + Right Click §7→ §f" + fruit.getAbilities().get(1).getName());
        player.sendMessage("§7  • §eShift + Left Click §7→ §f" + fruit.getAbilities().get(2).getName());
        player.sendMessage("§7  • §e/fruit use <1|2|3> §7→ Alternative command");
    }
}
