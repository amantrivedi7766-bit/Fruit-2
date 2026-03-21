package com.example.fruits.utils;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
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
        
        // Freeze player movement
        player.setWalkSpeed(0);
        player.setFlySpeed(0);
        
        // Disable PVP for all players during spin
        FruitsPlugin.getInstance().getConfig().set("pvp_disabled", true);
        
        Bukkit.broadcastMessage("§5✨ §l" + player.getName() + " §dis spinning the fruit wheel! §5✨");
        Bukkit.broadcastMessage("§7§l▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓ 15s §7| §eNo PVP during spin!");
        
        // Create epic particle effects
        createEpicParticles(player);
        
        // Get all fruits
        Fruit[] fruits = FruitsPlugin.getInstance().getFruitRegistry().getAllFruits().toArray(new Fruit[0]);
        
        // Create horizontal scrolling display
        List<ArmorStand> display = createHorizontalDisplay(player, fruits);
        
        // Animate spin
        animateSpin(player, display, fruits);
        
        // Schedule end of spin
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if(ticks >= 300) { // 15 seconds
                    // Select random fruit
                    Random random = new Random();
                    int selectedIndex = random.nextInt(fruits.length);
                    Fruit selectedFruit = fruits[selectedIndex];
                    
                    // End animation
                    for(ArmorStand stand : display) {
                        stand.remove();
                    }
                    
                    // Show result with epic effects
                    showResult(player, selectedFruit);
                    
                    // Give fruit
                    player.getInventory().addItem(selectedFruit.createItem());
                    
                    // Announce result
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§6§l═══════════════════════════════════");
                    Bukkit.broadcastMessage("§5✨ " + player.getName() + " §dspun §5" + selectedFruit.getDisplayName() + "§d!");
                    Bukkit.broadcastMessage("§6§l═══════════════════════════════════");
                    Bukkit.broadcastMessage("");
                    
                    // Unfreeze player
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
    
    private static void createEpicParticles(Player player) {
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if(!isSpinning || spinningPlayer != player) {
                    this.cancel();
                    return;
                }
                
                Location loc = player.getLocation().add(0, 1, 0);
                for(int i = 0; i < 360; i += 10) {
                    double rad = Math.toRadians(i + ticks * 10);
                    double x = Math.cos(rad) * 2;
                    double z = Math.sin(rad) * 2;
                    player.getWorld().spawnParticle(Particle.FIREWORK, loc.clone().add(x, 1, z), 0, 0, 0, 0, 1);
                    player.getWorld().spawnParticle(Particle.END_ROD, loc.clone().add(x, 1 + Math.sin(rad), z), 0, 0, 0, 0, 1);
                }
                ticks++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static List<ArmorStand> createHorizontalDisplay(Player player, Fruit[] fruits) {
        List<ArmorStand> stands = new ArrayList<>();
        Location start = player.getEyeLocation().add(player.getLocation().getDirection().multiply(5));
        
        for(int i = 0; i < fruits.length * 3; i++) {
            int index = i % fruits.length;
            Location loc = start.clone().add(i * 1.5, 0, 0);
            ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setInvulnerable(true);
            stand.setMarker(true);
            stand.setCustomName(fruits[index].getDisplayName());
            stand.setCustomNameVisible(true);
            stand.setItemInHand(fruits[index].createItem());
            stand.setRightArmPose(new org.bukkit.util.EulerAngle(Math.toRadians(90), 0, 0));
            stands.add(stand);
        }
        return stands;
    }
    
    private static void animateSpin(Player player, List<ArmorStand> display, Fruit[] fruits) {
        new BukkitRunnable() {
            int scroll = 0;
            int speed = 12;
            @Override
            public void run() {
                if(!isSpinning || spinningPlayer != player) {
                    this.cancel();
                    return;
                }
                
                // Gradually slow down
                if(scroll > 200) speed = 8;
                if(scroll > 250) speed = 4;
                if(scroll > 280) speed = 2;
                
                scroll += speed;
                
                Location start = player.getEyeLocation().add(player.getLocation().getDirection().multiply(5));
                for(int i = 0; i < display.size(); i++) {
                    int offset = (i + scroll) % (fruits.length * 3);
                    int fruitIndex = offset % fruits.length;
                    display.get(i).setCustomName(fruits[fruitIndex].getDisplayName());
                    display.get(i).setItemInHand(fruits[fruitIndex].createItem());
                    
                    // Move horizontally
                    Location newLoc = start.clone().add(i * 1.5 - (scroll % 10) * 0.15, 0, 0);
                    display.get(i).teleport(newLoc);
                    
                    // Particles
                    player.getWorld().spawnParticle(Particle.END_ROD, newLoc, 3, 0.2, 0.2, 0.2, 0.02);
                }
                
                // Sound effects
                if(scroll % 5 == 0) {
                    float pitch = 1.0f - (scroll / 300.0f);
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, pitch);
                }
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void showResult(Player player, Fruit fruit) {
        Location center = player.getEyeLocation().add(player.getLocation().getDirection().multiply(3));
        
        // Epic explosion
        player.getWorld().spawnParticle(Particle.EXPLOSION, center, 1);
        player.getWorld().spawnParticle(Particle.FIREWORK, center, 200, 2, 2, 2, 0.5);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0f, 1.5f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);
        
        // Create giant text
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
