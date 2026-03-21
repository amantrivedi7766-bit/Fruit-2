package com.example.fruits.utils;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;

public class SpinWheel {
    
    private static boolean isSpinning = false;
    private static Player spinningPlayer = null;
    private static List<ArmorStand> activeDisplays = new ArrayList<>();
    
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
        
        // Epic announcement
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§5§l═══════════════════════════════════");
        Bukkit.broadcastMessage("§5✨ §l" + player.getName() + " §dstarted the Fruit Wheel! §5✨");
        Bukkit.broadcastMessage("§7§l▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓ 15s §7| §eNo PVP!");
        Bukkit.broadcastMessage("§5§l═══════════════════════════════════");
        Bukkit.broadcastMessage("");
        
        // Get all fruits
        Fruit[] fruits = FruitsPlugin.getInstance().getFruitRegistry().getAllFruits().toArray(new Fruit[0]);
        
        // Start cinematic spin
        startCinematicSpin(player, fruits);
        
        // End after 15 seconds
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if(ticks >= 300) {
                    // Cleanup displays
                    for(ArmorStand stand : activeDisplays) {
                        stand.remove();
                    }
                    activeDisplays.clear();
                    
                    // Select random fruit
                    Random random = new Random();
                    int selectedIndex = random.nextInt(fruits.length);
                    Fruit selectedFruit = fruits[selectedIndex];
                    
                    // Epic result
                    showResult(player, selectedFruit);
                    player.getInventory().addItem(selectedFruit.createItem());
                    
                    // Announce winner
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§6§l═══════════════════════════════════");
                    Bukkit.broadcastMessage("§5✨ " + player.getName() + " §dspun §5" + selectedFruit.getDisplayName() + "§d!");
                    Bukkit.broadcastMessage("§6§l═══════════════════════════════════");
                    Bukkit.broadcastMessage("");
                    
                    // Unfreeze
                    player.setWalkSpeed(0.2f);
                    player.setFlySpeed(0.1f);
                    FruitsPlugin.getInstance().getConfig().set("pvp_disabled", false);
                    
                    isSpinning = false;
                    spinningPlayer = null;
                    this.cancel();
                }
                ticks++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void startCinematicSpin(Player player, Fruit[] fruits) {
        new BukkitRunnable() {
            int currentIndex = 0;
            int scrollSpeed = 2;
            int totalFrames = 0;
            ArmorStand currentDisplay = null;
            
            @Override
            public void run() {
                if(!isSpinning || spinningPlayer != player) {
                    this.cancel();
                    return;
                }
                
                if(totalFrames >= 300) {
                    this.cancel();
                    return;
                }
                
                // Update display position to follow player's cursor
                Location displayLoc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(3));
                
                // Create new display
                if(currentDisplay != null) currentDisplay.remove();
                
                currentDisplay = (ArmorStand) player.getWorld().spawnEntity(displayLoc, EntityType.ARMOR_STAND);
                currentDisplay.setVisible(false);
                currentDisplay.setGravity(false);
                currentDisplay.setInvulnerable(true);
                currentDisplay.setMarker(true);
                currentDisplay.setCustomName(fruits[currentIndex].getDisplayName());
                currentDisplay.setCustomNameVisible(true);
                currentDisplay.setItemInHand(fruits[currentIndex].createItem());
                currentDisplay.setRightArmPose(new org.bukkit.util.EulerAngle(Math.toRadians(90), 0, 0));
                activeDisplays.add(currentDisplay);
                
                // Particle burst around fruit
                player.getWorld().spawnParticle(Particle.FIREWORK, displayLoc, 20, 0.3, 0.3, 0.3, 0.05);
                player.getWorld().spawnParticle(Particle.END_ROD, displayLoc, 10, 0.2, 0.2, 0.2, 0.02);
                
                // Sound effect
                float pitch = 0.8f + (currentIndex / 20.0f);
                player.getWorld().playSound(displayLoc, Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, pitch);
                
                // Next fruit
                currentIndex = (currentIndex + 1) % fruits.length;
                totalFrames++;
                
                // Slow down near the end
                if(totalFrames > 250) {
                    if(totalFrames % 3 == 0) currentIndex = (currentIndex + 1) % fruits.length;
                }
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 2L);
    }
    
    private static void showResult(Player player, Fruit fruit) {
        Location center = player.getEyeLocation();
        
        // Epic cinematic explosion
        for(int i = 0; i < 3; i++) {
            player.getWorld().spawnParticle(Particle.EXPLOSION, center, 1);
            player.getWorld().spawnParticle(Particle.FIREWORK, center, 150, 2, 2, 2, 0.3);
        }
        player.getWorld().playSound(center, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0f, 1.5f);
        player.getWorld().playSound(center, Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);
        
        // Spiral beam
        new BukkitRunnable() {
            int angle = 0;
            int height = 0;
            @Override
            public void run() {
                if(height >= 30) {
                    this.cancel();
                    return;
                }
                
                double rad = Math.toRadians(angle);
                double x = Math.cos(rad) * 1.5;
                double z = Math.sin(rad) * 1.5;
                Location beamLoc = player.getLocation().add(x, height, z);
                
                player.getWorld().spawnParticle(Particle.FIREWORK, beamLoc, 30, 0.2, 0.1, 0.2, 0.05);
                player.getWorld().spawnParticle(Particle.END_ROD, beamLoc, 15, 0.1, 0.1, 0.1, 0.02);
                
                angle += 30;
                height++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
        
        // Title animation
        player.sendTitle("§5§l" + fruit.getDisplayName(), "§7You received a magical fruit!", 10, 40, 10);
    }
    
    public static void spinAll() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            spin(player);
        }
    }
    
    public static boolean isSpinning() { return isSpinning; }
}
