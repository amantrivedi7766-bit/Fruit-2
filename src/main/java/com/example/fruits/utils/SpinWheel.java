package com.example.fruits.utils;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import java.util.*;

public class SpinWheel {
    
    public static void spin(Player player) {
        Bukkit.broadcastMessage("§5✨ &lNEW ERA OF FOOD BEGINS! §5✨");
        
        Fruit[] fruits = FruitsPlugin.getInstance().getFruitRegistry().getAllFruits().toArray(new Fruit[0]);
        
        // Create particles around player
        createSpiral(player);
        
        // Create wheel at player's eye level (in front of screen)
        List<ArmorStand> wheel = createScreenWheel(player, fruits);
        
        // Animate the spin
        animateSpin(player, wheel, fruits);
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
                
                Location loc = player.getLocation().add(0, 1.5, 0);
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
    
    private static List<ArmorStand> createScreenWheel(Player player, Fruit[] fruits) {
        // Position in front of player at eye level
        Location center = player.getEyeLocation().add(player.getLocation().getDirection().multiply(3));
        List<ArmorStand> wheel = new ArrayList<>();
        
        for(int i = 0; i < fruits.length; i++) {
            double angle = 2 * Math.PI * i / fruits.length;
            double x = Math.cos(angle) * 2.5;
            double y = Math.sin(angle) * 1.5;
            Location loc = center.clone().add(x, y + 1, 0);
            
            ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setInvulnerable(true);
            stand.setMarker(true);
            stand.setCustomName(fruits[i].getDisplayName());
            stand.setCustomNameVisible(true);
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
            int maxSpin = 80;
            int startSpeed = 28;
            int endSpeed = 3;
            
            @Override
            public void run() {
                if(spinCount >= maxSpin) {
                    for(ArmorStand stand : wheel) {
                        stand.remove();
                    }
                    
                    Random random = new Random();
                    int selectedIndex = random.nextInt(fruits.length);
                    Fruit selectedFruit = fruits[selectedIndex];
                    
                    showResult(player, selectedFruit);
                    player.getInventory().addItem(selectedFruit.createItem());
                    
                    player.sendMessage("§d✨ &lYou received " + selectedFruit.getDisplayName() + "! §d✨");
                    Bukkit.broadcastMessage("§6🎲 " + player.getName() + " §aspun §6" + selectedFruit.getDisplayName() + "§a!");
                    
                    this.cancel();
                    return;
                }
                
                double progress = (double) spinCount / maxSpin;
                int currentSpeed = startSpeed - (int)(progress * (startSpeed - endSpeed));
                if(currentSpeed < endSpeed) currentSpeed = endSpeed;
                
                rotation += currentSpeed;
                
                Location center = player.getEyeLocation().add(player.getLocation().getDirection().multiply(3));
                
                for(int i = 0; i < wheel.size(); i++) {
                    double angle = 2 * Math.PI * (i + rotation / 10.0) / wheel.size();
                    double x = Math.cos(angle) * 2.5;
                    double y = Math.sin(angle) * 1.5;
                    wheel.get(i).teleport(center.clone().add(x, y + 1, 0));
                    
                    player.getWorld().spawnParticle(Particle.END_ROD, wheel.get(i).getLocation(), 2, 0.1, 0.1, 0.1, 0.05);
                }
                
                if(spinCount % 5 == 0) {
                    float pitch = 1.0f - (float)progress * 0.7f;
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, pitch);
                }
                
                spinCount++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void showResult(Player player, Fruit fruit) {
        Location center = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
        
        player.getWorld().spawnParticle(Particle.EXPLOSION, center, 1);
        player.getWorld().spawnParticle(Particle.FIREWORK, center, 100, 1, 1, 1, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0f, 1.5f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);
    }
    
    public static void spinAll() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            spin(player);
        }
        Bukkit.broadcastMessage("§6🎲 §lMEGA SPIN! All players received fruits!");
    }
}
