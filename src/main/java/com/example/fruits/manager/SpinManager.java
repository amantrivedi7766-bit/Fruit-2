package com.example.fruits.manager;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class SpinManager {
    private final Map<UUID, Integer> spinTasks = new HashMap<>();
    private final Random random = new Random();
    private final List<Fruit> fruits;
    
    public SpinManager() {
        this.fruits = new ArrayList<>(FruitsPlugin.getInstance().getFruitRegistry().getAllFruits());
    }
    
    public void startSpin(Player player) {
        player.sendMessage("§6§l=================================");
        player.sendMessage("§e§l🎁 WELCOME TO FRUITS PLUGIN!");
        player.sendMessage("§e§l🎲 Spinning for your magical fruit...");
        player.sendMessage("§6§l=================================");
        
        // Play spin sound
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
        
        // Spin animation - 20 spins over 2 seconds
        BukkitRunnable spinTask = new BukkitRunnable() {
            int spinCount = 0;
            final int maxSpins = 20;
            
            @Override
            public void run() {
                if(spinCount >= maxSpins) {
                    // Spin finished - give random fruit
                    Fruit selectedFruit = fruits.get(random.nextInt(fruits.size()));
                    giveFruitReward(player, selectedFruit);
                    spinTasks.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }
                
                // Show spinning animation message
                Fruit tempFruit = fruits.get(spinCount % fruits.size());
                player.sendMessage("§7🎲 Spinning... §f" + tempFruit.getName());
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f + (spinCount * 0.05f));
                
                spinCount++;
            }
        };
        
        spinTask.runTaskTimer(FruitsPlugin.getInstance(), 0L, 2L);
        spinTasks.put(player.getUniqueId(), spinTask.getTaskId());
    }
    
    private void giveFruitReward(Player player, Fruit fruit) {
        ItemStack fruitItem = fruit.createItem();
        
        // Check inventory space
        if(player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), fruitItem);
            player.sendMessage("§c⚠️ Inventory full! Fruit dropped on ground!");
        } else {
            player.getInventory().addItem(fruitItem);
        }
        
        // Celebration effects
        player.sendMessage("§6§l=================================");
        player.sendMessage("§a§l✨ CONGRATULATIONS! ✨");
        player.sendMessage("§aYou received: " + fruit.getName());
        player.sendMessage("§7Right-click to use abilities!");
        player.sendMessage("§6§l=================================");
        
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        
        // Firework effect
        player.getWorld().spawnParticle(org.bukkit.Particle.FIREWORK, player.getLocation(), 30, 0.5, 1, 0.5);
        
        Bukkit.getLogger().info("[Fruits] " + player.getName() + " received " + fruit.getId() + " from spin!");
    }
    
    public void cancelSpin(Player player) {
        Integer taskId = spinTasks.remove(player.getUniqueId());
        if(taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}
