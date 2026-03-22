package com.example.fruits.utils;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CinematicSpinWheel {
    
    private final FruitsPlugin plugin;
    private final Player player;
    private final List<Fruit> allFruits;
    private final Map<Fruit, Integer> fruitWeights;
    private BukkitTask spinTask;
    private BukkitTask particleTask;
    private boolean spinning = false;
    private int elapsedTicks = 0;
    private final int TOTAL_DURATION_TICKS = 300; // 15 seconds
    private Location originalLocation;
    private Location cameraCenter;
    private List<ItemDisplay> fruitDisplays = new ArrayList<>();
    private Random random = new Random();
    private Fruit finalReward = null;
    private boolean spinCompleted = false;
    
    public CinematicSpinWheel(FruitsPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.allFruits = new ArrayList<>(plugin.getFruitRegistry().getAllFruits());
        this.fruitWeights = new HashMap<>();
        setupWeights();
    }
    
    private void setupWeights() {
        for(Fruit fruit : allFruits) {
            int weight = 100;
            String id = fruit.getId();
            if(id.contains("primordial")) weight = 5;
            else if(id.contains("dracula")) weight = 10;
            else if(id.contains("portal")) weight = 15;
            else if(id.contains("star")) weight = 20;
            else if(id.contains("shadow")) weight = 25;
            else if(id.contains("thief")) weight = 30;
            else if(id.contains("throne")) weight = 35;
            else if(id.contains("cyclone")) weight = 40;
            else if(id.contains("water")) weight = 50;
            else weight = 60;
            fruitWeights.put(fruit, weight);
        }
    }
    
    public void startSpin() {
        if(spinning) {
            player.sendMessage("§cAlready spinning!");
            return;
        }
        
        spinning = true;
        spinCompleted = false;
        originalLocation = player.getLocation().clone();
        cameraCenter = player.getLocation().clone().add(0, 2, 0);
        
        // Freeze player
        freezePlayer();
        
        // Create visual arena
        createVisualArena();
        
        // Start spin animation
        startSpinAnimation();
        
        // Start particles
        startParticleEffects();
        
        player.sendTitle("§6§l🎰 SPINNING!", "§eGet ready for rewards!", 10, 40, 10);
        player.sendMessage("§a✨ SPIN STARTED! Watch the magic! ✨");
        
        // Auto unfreeze after spin duration
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!spinCompleted) {
                    finishSpin();
                }
            }
        }.runTaskLater(plugin, TOTAL_DURATION_TICKS);
    }
    
    private void freezePlayer() {
        // Save original state
        player.setWalkSpeed(0);
        player.setFlySpeed(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 255, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 400, 128, false, false));
        player.setInvulnerable(true);
        
        player.sendMessage("§c⚠ You are frozen! Watch the spin! ⚠");
    }
    
    private void unfreezePlayer() {
        // Restore movement
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        player.setInvulnerable(false);
        
        // Teleport back
        player.teleport(originalLocation);
        player.sendMessage("§a✓ Spin complete! You can move again!");
    }
    
    private void createVisualArena() {
        World world = player.getWorld();
        
        // Create fruit displays in a circle
        int fruitCount = allFruits.size();
        for(int i = 0; i < fruitCount && i < 12; i++) {
            Fruit fruit = allFruits.get(i);
            double angle = (i * 360.0 / Math.min(fruitCount, 12)) * Math.PI / 180;
            double radius = 4;
            double x = cameraCenter.getX() + Math.cos(angle) * radius;
            double z = cameraCenter.getZ() + Math.sin(angle) * radius;
            Location fruitLoc = new Location(world, x, cameraCenter.getY(), z);
            
            ItemDisplay display = (ItemDisplay) world.spawnEntity(fruitLoc, EntityType.ITEM_DISPLAY);
            ItemStack displayItem = fruit.createItemStack(1);
            ItemMeta meta = displayItem.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7✨ " + fruit.getAbilities().get(0).getName() + " ✨");
            if(fruit.getAbilities().size() > 1) {
                lore.add("§7🌀 " + fruit.getAbilities().get(1).getName() + " 🌀");
            }
            meta.setLore(lore);
            displayItem.setItemMeta(meta);
            display.setItemStack(displayItem);
            display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);
            display.setInvulnerable(true);
            display.setGlowing(true);
            fruitDisplays.add(display);
        }
    }
    
    private void startSpinAnimation() {
        spinTask = new BukkitRunnable() {
            int angle = 0;
            
            @Override
            public void run() {
                if(elapsedTicks >= TOTAL_DURATION_TICKS) {
                    this.cancel();
                    return;
                }
                
                // Rotate fruit displays
                angle += 15;
                for(int i = 0; i < fruitDisplays.size(); i++) {
                    ItemDisplay display = fruitDisplays.get(i);
                    float rot = display.getLocation().getYaw();
                    display.setRotation(rot + 10, 0);
                    
                    double yOffset = Math.sin((elapsedTicks * 0.2) + (i * 0.5)) * 0.3;
                    Location loc = display.getLocation();
                    loc.setY(cameraCenter.getY() + yOffset);
                    display.teleport(loc);
                }
                
                // Show random fruit popups
                if(elapsedTicks % 10 == 0) {
                    showRandomFruitPopup();
                }
                
                // Play sound
                if(elapsedTicks % 15 == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.8f, 
                        0.8f + (elapsedTicks / (float)TOTAL_DURATION_TICKS));
                }
                
                // Update title
                if(elapsedTicks % 20 == 0) {
                    int secondsLeft = (TOTAL_DURATION_TICKS - elapsedTicks) / 20;
                    player.sendTitle("§6§l🎰 SPINNING...", "§e" + secondsLeft + " seconds left", 0, 20, 0);
                }
                
                elapsedTicks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void showRandomFruitPopup() {
        Location center = player.getLocation();
        
        for(int i = 0; i < 3; i++) {
            Fruit randomFruit = getRandomWeightedFruit();
            double x = center.getX() + (random.nextDouble() - 0.5) * 5;
            double z = center.getZ() + (random.nextDouble() - 0.5) * 5;
            double y = center.getY() + random.nextDouble() * 3;
            Location popupLoc = new Location(player.getWorld(), x, y, z);
            
            ItemDisplay popup = (ItemDisplay) player.getWorld().spawnEntity(popupLoc, EntityType.ITEM_DISPLAY);
            popup.setItemStack(randomFruit.createItemStack(1));
            popup.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);
            popup.setGlowing(true);
            
            // Animate and remove
            new BukkitRunnable() {
                int height = 0;
                @Override
                public void run() {
                    if(height >= 15) {
                        popup.remove();
                        this.cancel();
                        return;
                    }
                    popup.teleport(popup.getLocation().add(0, 0.1, 0));
                    height++;
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }
        
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.2f);
    }
    
    private void startParticleEffects() {
        particleTask = new BukkitRunnable() {
            int angle = 0;
            @Override
            public void run() {
                if(!spinning || spinCompleted) {
                    this.cancel();
                    return;
                }
                
                // Create spiral particles around player
                for(int i = 0; i < 360; i += 30) {
                    double rad = Math.toRadians(i + angle);
                    double radius = 2.5;
                    double x = player.getLocation().getX() + Math.cos(rad) * radius;
                    double z = player.getLocation().getZ() + Math.sin(rad) * radius;
                    double y = player.getLocation().getY() + 1;
                    
                    Location particleLoc = new Location(player.getWorld(), x, y, z);
                    player.getWorld().spawnParticle(Particle.FIREWORK, particleLoc, 1, 0, 0, 0, 0);
                }
                angle += 15;
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
    
    private Fruit getRandomWeightedFruit() {
        int totalWeight = fruitWeights.values().stream().mapToInt(Integer::intValue).sum();
        int randomWeight = random.nextInt(totalWeight);
        int currentWeight = 0;
        
        for(Map.Entry<Fruit, Integer> entry : fruitWeights.entrySet()) {
            currentWeight += entry.getValue();
            if(randomWeight < currentWeight) {
                return entry.getKey();
            }
        }
        return allFruits.get(0);
    }
    
    private void finishSpin() {
        if(spinCompleted) return;
        spinCompleted = true;
        spinning = false;
        
        // Stop tasks
        if(spinTask != null) spinTask.cancel();
        if(particleTask != null) particleTask.cancel();
        
        // Determine reward
        finalReward = getRandomWeightedFruit();
        int amount = random.nextInt(3) + 1;
        
        // Give reward
        player.getInventory().addItem(finalReward.createItemStack(amount));
        
        // Remove visual displays
        for(ItemDisplay display : fruitDisplays) {
            if(display != null) display.remove();
        }
        fruitDisplays.clear();
        
        // Celebration effects
        Location center = player.getLocation().clone().add(0, 1, 0);
        for(int i = 0; i < 50; i++) {
            double x = center.getX() + (random.nextDouble() - 0.5) * 4;
            double z = center.getZ() + (random.nextDouble() - 0.5) * 4;
            double y = center.getY() + random.nextDouble() * 3;
            Location effectLoc = new Location(player.getWorld(), x, y, z);
            player.getWorld().spawnParticle(Particle.FIREWORK, effectLoc, 2, 0.1, 0.1, 0.1, 0);
        }
        
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        
        player.sendTitle("§6§l🎉 SPIN COMPLETE! 🎉", 
            "§aYou won §6" + amount + "x " + finalReward.getName() + "§a!", 10, 80, 20);
        
        player.sendMessage("");
        player.sendMessage("§6§l╔═══════════════════════════════════╗");
        player.sendMessage("§6§l║ §e§l🎉 CONGRATULATIONS! 🎉 §6§l║");
        player.sendMessage("§6§l╠═══════════════════════════════════╣");
        player.sendMessage("§6§l║ §7You won: §6" + amount + "x " + finalReward.getName());
        player.sendMessage("§6§l╚═══════════════════════════════════╝");
        
        // Unfreeze player
        unfreezePlayer();
    }
}
