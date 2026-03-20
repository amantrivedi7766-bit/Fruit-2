package com.example.fruits.utils;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.util.Random;

public class SpinWheel {
    
    public static void spin(Player player) {
        Fruit[] fruits = FruitsPlugin.getInstance().getFruitRegistry().getAllFruits().toArray(new Fruit[0]);
        Fruit randomFruit = fruits[new Random().nextInt(fruits.length)];
        
        player.getInventory().addItem(randomFruit.createItem());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        
        String msg = FruitsPlugin.getInstance().getConfig().getString("messages.spin_result")
            .replace("{player}", player.getName())
            .replace("{fruit}", randomFruit.getDisplayName())
            .replace('&', '§');
        
        Bukkit.broadcastMessage(msg);
    }
}
