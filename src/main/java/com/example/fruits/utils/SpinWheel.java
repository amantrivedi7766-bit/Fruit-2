package com.example.fruits.utils;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import java.util.*;

public class SpinWheel {
    
    private static boolean isSpinning = false;
    private static Player spinningPlayer = null;
    
    public static void spin(Player player) {
        if(isSpinning) {
            player.sendMessage("§cA spin is already in progress!");
            return;
        }
        
        isSpinning = true;
        spinningPlayer = player;
        
        // Freeze player
        player.setWalkSpeed(0);
        player.setFlySpeed(0);
        
        // Disable PVP
        FruitsPlugin.getInstance().getConfig().set("pvp_disabled", true);
        
        Bukkit.broadcastMessage("§5✨ §l" + player.getName() + " §dis spinning the fruit wheel! §5✨");
        Bukkit.broadcastMessage("§7§l▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓ 15s §7| §eNo PVP during spin!");
        
        // Get all fruits
        Fruit[] fruits = FruitsPlugin.getInstance().getFruitRegistry().getAllFruits().toArray(new Fruit[0]);
        
        // Create 3D circle around player
        List<ArmorStand> circle = createCircleAroundPlayer(player, fruits);
        
        // Animate spinning
        animateSpin(player, circle, fruits);
        
        // End after 15 seconds
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if(ticks >= 300) {
                    // Remove circle
                    for(ArmorStand stand : circle) {
                        stand.remove();
                    }
                    
                    // Select random fruit
                    Random random = new Random();
                    int selectedIndex = random.nextInt(fruits.length);
                    Fruit selectedFruit = fruits[selectedIndex];
                    
                    // Epic result effects
                    showResult(player, selectedFruit);
                    
                    // Give fruit
                    player.getInventory().addItem(selectedFruit.createItem());
                    
                    // Announce
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§6§l═══════════════════════════════════");
                    Bukkit.broadcastMessage("§5✨ " + player.getName() + " §dspun §5" + selectedFruit.getDisplayName() + "§d!");
                    Bukkit.broadcastMessage("§6§l═══════════════════════════════════");
                    Bukkit.broadcastMessage("");
                    
                    // Unfreeze
                    player.setWalkSpeed(0.2f);
                    player.setFlySpeed(0.1f);
                    
                    // Enable PVP
                    FruitsPlugin.getInstance().getConfig().set("pvp_disabled", false);
                    
                    isSpinning = false;
                    spinningPlayer = null;
                    this.cancel();
                }
                ticks++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static List<ArmorStand> createCircleAroundPlayer(Player player, Fruit[] fruits) {
        List<ArmorStand> circle = new ArrayList<>();
        int fruitCount = fruits.length;
        
        for(int i = 0; i < fruitCount; i++) {
            double angle = 2 * Math.PI * i / fruitCount;
            double x = Math.cos(angle) * 3;
            double z = Math.sin(angle) * 3;
            Location loc = player.getLocation().add(x, 1.5, z);
            
            ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setInvulnerable(true);
            stand.setMarker(true);
            stand.setCustomName(fruits[i].getDisplayName());
            stand.setCustomNameVisible(true);
            stand.setItemInHand(fruits[i].createItem());
            stand.setRightArmPose(new EulerAngle(Math.toRadians(90), 0, 0));
            
            circle.add(stand);
        }
        return circle;
    }
    
    private static void animateSpin(Player player, List<ArmorStand> circle, Fruit[] fruits) {
        new BukkitRunnable() {
            int rotation = 0;
            int spinCount = 0;
            int maxSpin = 300;
            int startSpeed = 12;
            int endSpeed = 2;
            
            @Override
            public void run() {
                if(!isSpinning || spinningPlayer != player) {
                    this.cancel();
                    return;
                }
                
                if(spinCount >= maxSpin) {
                    this.cancel();
                    return;
                }
                
                // Calculate speed (fast start, slow end)
                double progress = (double) spinCount / maxSpin;
                int currentSpeed = startSpeed - (int)(progress * (startSpeed - endSpeed));
                if(currentSpeed < endSpeed) currentSpeed = endSpeed;
                
                rotation += currentSpeed;
                
                // Update positions
                for(int i = 0; i < circle.size(); i++) {
                    double angle = 2 * Math.PI * (i + rotation / 10.0) / circle.size();
                    double x = Math.cos(angle) * 3;
                    double z = Math.sin(angle) * 3;
                    Location newLoc = player.getLocation().add(x, 1.5, z);
                    circle.get(i).teleport(newLoc);
                    
                    // Particles around each fruit
                    player.getWorld().spawnParticle(Particle.END_ROD, newLoc, 3, 0.2, 0.2, 0.2, 0.02);
                    player.getWorld().spawnParticle(Particle.FIREWORK, newLoc, 1, 0.1, 0.1, 0.1, 0.01);
                }
                
                // Sound effects
                if(spinCount % 10 == 0) {
                    float pitch = 1.0f - (float)progress * 0.7f;
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, pitch);
                }
                
                spinCount++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void showResult(Player player, Fruit fruit) {
        // Epic explosion at player location
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation().add(0, 1, 0), 1);
        player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation().add(0, 1, 0), 200, 2, 2, 2, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0f, 1.5f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);
        
        // Rainbow beam
        new BukkitRunnable() {
            int height = 0;
            @Override
            public void run() {
                if(height >= 20) {
                    this.cancel();
                    return;
                }
                Location beamLoc = player.getLocation().add(0, height, 0);
                player.getWorld().spawnParticle(Particle.FIREWORK, beamLoc, 20, 0.5, 0, 0.5, 0.1);
                player.getWorld().spawnParticle(Particle.END_ROD, beamLoc, 10, 0.3, 0, 0.3, 0.05);
                height++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    public static void spinAll() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            spin(player);
        }
    }
    
    public static boolean isSpinning() { return isSpinning; }
    public static Player getSpinningPlayer() { return spinningPlayer; }
}
