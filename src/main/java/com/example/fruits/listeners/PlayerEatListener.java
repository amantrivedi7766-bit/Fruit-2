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

        item.setAmount(item.getAmount() - 1);
        
        PlayerFruitData data = new PlayerFruitData(player, fruit);
        FruitsPlugin.getInstance().getActivePlayers().put(player.getUniqueId(), data);

        String abilities = fruit.getAbilities().get(0).getName() + " §7| §e" + 
                          fruit.getAbilities().get(1).getName() + " §7| §e" + 
                          fruit.getAbilities().get(2).getName();
        
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
            TextComponent.fromLegacyText("§a🍎 " + fruit.getDisplayName() + " §7| §e" + abilities));
        
        player.sendMessage("§a✅ You ate " + fruit.getDisplayName() + "!");
        player.sendMessage("§e⚡ Use §6/fruit use <1|2|3> §eto use abilities!");
    }
}
