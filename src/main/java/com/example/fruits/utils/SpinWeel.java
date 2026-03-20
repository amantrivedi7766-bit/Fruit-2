package com.example.fruits.utils;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import java.util.*;

public class SpinWheel {
    
    private static final List<Color> COLORS = Arrays.asList(
        Color.RED, Color.YELLOW, Color.MAROON, Color.PURPLE, Color.ORANGE,
        Color.GREEN, Color.RED, Color.LIME, Color.FUCHSIA, Color.NAVY
    );
    
    public static void spin(Player player) {
        // Announce spin
        Bukkit.broadcastMessage("§5✨ &lNEW ERA OF FOOD BEGINS! &5✨");
        
        // Get all 10 fruits
        Fruit[] fruits = FruitsPlugin.getInstance().getFruitRegistry().getAllFruits().toArray(new Fruit[0]);
        
        // Create particle spiral
        createSpiral(player);
        
        // Create 3D wheel with all fruits
        List<ArmorStand> wheel = createWheel(player, fruits);
        
        // Animate the spin
        animateSpin(player, wheel, fruits);
        
        // Select random fruit (can repeat)
        Random random = new Random();
        int selectedIndex = random.nextInt(fruits.length);
        Fruit selectedFruit = fruits[selectedIndex];
        
        // Show result with epic effects
        showResult(player, selectedFruit, selectedIndex);
        
        // Give the fruit
        player.getInventory().addItem(selectedFruit.createItem());
        
        // Send result message
        player.sendMessage("§d✨ &lYou received " + selectedFruit.getDisplayName() + "! §d✨");
        player.sendMessage("§7Use §e/fruit use <1|2|3> §7to activate abilities!");
        
        // Broadcast to server
        Bukkit.broadcastMessage("§6🎲 " + player.getName() + " §aspun §6" + selectedFruit.getDisplayName() + "§a!");
    }
    
    private static void createSpiral(Player player) {
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if(ticks >= 40) {
                    this.cancel();
                    return;
                }
                
                Location loc = player.getLocation().add(0, 2, 0);
                double radius = ticks / 5.0;
                for(int i = 0; i < 360; i += 30) {
                    double angle = Math.toRadians(i + ticks * 15);
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;
                    Location particleLoc = loc.clone().add(x, Math.sin(angle) * 0.5, z);
                    player.getWorld().spawnParticle(Particle.FIREWORK, particleLoc, 0, 0, 0, 0, 1);
                    player.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 0, 0, 0, 0, 1);
                }
                ticks++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static List<ArmorStand> createWheel(Player player, Fruit[] fruits) {
        Location center = player.getLocation().add(0, 4, 0);
        List<ArmorStand> wheel = new ArrayList<>();
        
        for(int i = 0; i < fruits.length; i++) {
            double angle = 2 * Math.PI * i / fruits.length;
            double x = Math.cos(angle) * 3;
            double z = Math.sin(angle) * 3;
            Location loc = center.clone().add(x, 0, z);
            
            ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setInvulnerable(true);
            stand.setMarker(true);
            stand.setCustomName(fruits[i].getDisplayName());
            stand.setCustomNameVisible(true);
            
            // Set item in hand
            stand.setItemInHand(fruits[i].createItem());
            stand.setRightArmPose(new EulerAngle(Math.toRadians(90), 0, 0));
            
            wheel.add(stand);
        }
        
        return wheel;
    }
    
    private static void animateSpin(Player player, List<ArmorStand> wheel, Fruit[] fruits) {
        new BukkitRunnable() {
            int rotation = 0;
            int spinCount = 0;
            @Override
            public void run() {
                if(spinCount >= 60) {
                    for(ArmorStand stand : wheel) {
                        stand.remove();
                    }
                    this.cancel();
                    return;
                }
                
                rotation += 36;
                for(int i = 0; i < wheel.size(); i++) {
                    double angle = 2 * Math.PI * (i + rotation / 10.0) / wheel.size();
                    double x = Math.cos(angle) * 3;
                    double z = Math.sin(angle) * 3;
                    wheel.get(i).teleport(player.getLocation().add(0, 4, 0).clone().add(x, Math.sin(angle) * 0.5, z));
                    
                    player.getWorld().spawnParticle(Particle.SPELL_WITCH, wheel.get(i).getLocation(), 3, 0.2, 0.2, 0.2, 0.1);
                }
                
                if(spinCount % 10 == 0) {
                    float pitch = 0.5f + (spinCount * 0.02f);
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, pitch);
                }
                
                spinCount++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void showResult(Player player, Fruit fruit, int index) {
        Location center = player.getLocation().add(0, 5, 0);
        
        // Epic explosion
        player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, center, 1);
        player.getWorld().spawnParticle(Particle.FIREWORK, center, 100, 1, 1, 1, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0f, 1.5f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);
        
        // Color beam
        new BukkitRunnable() {
            int height = 0;
            @Override
            public void run() {
                if(height >= 12) {
                    this.cancel();
                    return;
                }
                Location beamLoc = player.getLocation().add(0, height, 0);
                player.getWorld().spawnParticle(Particle.FIREWORK, beamLoc, 15, 0.5, 0, 0.5, 0.1);
                height++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    public static void spinAll() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            spin(player);
        }
        Bukkit.broadcastMessage("§6🎲 §lMEGA SPIN! All players received fruits!");
    }
}
