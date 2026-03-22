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
    private BukkitTask cameraTask;
    private boolean spinning = false;
    private int elapsedTicks = 0;
    private final int TOTAL_DURATION_TICKS = 300;
    private Location originalLocation;
    private Location cameraCenter;
    private ArmorStand centerDisplay;
    private List<ItemDisplay> fruitDisplays = new ArrayList<>();
    private List<ArmorStand> fruitHolograms = new ArrayList<>();
    private Random random = new Random();
    private Fruit finalReward = null;
    
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
        originalLocation = player.getLocation().clone();
        cameraCenter = player.getLocation().clone().add(0, 2, 0);
        
        freezePlayer();
        create3DArena();
        startSpinLoop();
        startParticleSystems();
        startCameraMovement();
        
        player.sendTitle("§6§l🎰 CINEMATIC SPIN", "§eWatch the fruits appear!", 10, 40, 10);
        player.sendMessage("§a✨ SPIN STARTED! Watch the magic! ✨");
    }
    
    private void freezePlayer() {
        player.setWalkSpeed(0);
        player.setFlySpeed(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 255, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 400, 128, false, false));
        player.setInvulnerable(true);
        player.setFlying(true);
        player.setAllowFlight(true);
        
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p != player) p.hidePlayer(plugin, player);
        }
        
        player.teleport(cameraCenter);
        player.sendMessage("§c⚠ You are frozen! Watch the spin! ⚠");
    }
    
    private void create3DArena() {
        World world = player.getWorld();
        
        centerDisplay = (ArmorStand) world.spawnEntity(cameraCenter.clone().add(0, 1, 0), EntityType.ARMOR_STAND);
        centerDisplay.setVisible(false);
        centerDisplay.setGravity(false);
        centerDisplay.setInvulnerable(true);
        centerDisplay.setCustomNameVisible(true);
        centerDisplay.setCustomName("§6§l⚡ SPINNING... ⚡");
        
        int fruitCount = allFruits.size();
        for(int i = 0; i < fruitCount; i++) {
            Fruit fruit = allFruits.get(i);
            double angle = (i * 360.0 / fruitCount) * Math.PI / 180;
            double radius = 4.5;
            double x = cameraCenter.getX() + Math.cos(angle) * radius;
            double z = cameraCenter.getZ() + Math.sin(angle) * radius;
            Location fruitLoc = new Location(world, x, cameraCenter.getY(), z);
            
            ItemDisplay display = (ItemDisplay) world.spawnEntity(fruitLoc, EntityType.ITEM_DISPLAY);
            display.setItemStack(createDisplayItem(fruit));
            display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);
            display.setRotation(0, (float) (Math.toDegrees(angle)));
            display.setInvulnerable(true);
            display.setGlowing(true);
            fruitDisplays.add(display);
            
            ArmorStand nameTag = (ArmorStand) world.spawnEntity(fruitLoc.clone().add(0, 0.8, 0), EntityType.ARMOR_STAND);
            nameTag.setVisible(false);
            nameTag.setGravity(false);
            nameTag.setInvulnerable(true);
            nameTag.setCustomNameVisible(true);
            nameTag.setCustomName("§a" + fruit.getName());
            fruitHolograms.add(nameTag);
        }
    }
    
    private ItemStack createDisplayItem(Fruit fruit) {
        ItemStack item = fruit.createItemStack(1);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("§7✨ " + fruit.getAbilities().get(0).getName() + " ✨");
        if(fruit.getAbilities().size() > 1) {
            lore.add("§7🌀 " + fruit.getAbilities().get(1).getName() + " 🌀");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    
    private void startSpinLoop() {
        spinTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(elapsedTicks >= TOTAL_DURATION_TICKS) {
                    finishSpin();
                    this.cancel();
                    return;
                }
                
                // Animate fruit displays
                for(int i = 0; i < fruitDisplays.size(); i++) {
                    ItemDisplay display = fruitDisplays.get(i);
                    float currentRot = display.getLocation().getYaw();
                    display.setRotation(currentRot + 8, 0);
                    
                    double yOffset = Math.sin((elapsedTicks * 0.15) + (i * 0.5)) * 0.2;
                    Location newLoc = getOriginalFruitLocation(i).clone().add(0, yOffset, 0);
                    display.teleport(newLoc);
                    fruitHolograms.get(i).teleport(newLoc.clone().add(0, 0.8, 0));
                }
                
                // Show fruit popups at cursor every few ticks
                if(elapsedTicks % 5 == 0) {
                    createFruitPopupAtCursor();
                }
                
                // Update center display
                if(centerDisplay != null) {
                    String[] messages = {"§6§l⚡ SPINNING ⚡", "§e§l🎰 LUCKY SPIN 🎰", "§a§l✨ GET LUCKY ✨"};
                    int index = (elapsedTicks / 30) % messages.length;
                    centerDisplay.setCustomName(messages[index]);
                    double yOffset = Math.sin(elapsedTicks * 0.1) * 0.2;
                    centerDisplay.teleport(cameraCenter.clone().add(0, 1 + yOffset, 0));
                }
                
                // Play sounds
                if(elapsedTicks % 10 == 0) {
                    float pitch = 0.5f + (elapsedTicks / (float)TOTAL_DURATION_TICKS);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.8f, pitch);
                }
                
                // Show timer
                if(elapsedTicks % 20 == 0) {
                    int secondsLeft = (TOTAL_DURATION_TICKS - elapsedTicks) / 20;
                    player.sendTitle("§6§l🎰 SPINNING...", "§e" + secondsLeft + " seconds", 0, 30, 0);
                }
                
                elapsedTicks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private Location getOriginalFruitLocation(int index) {
        double angle = (index * 360.0 / allFruits.size()) * Math.PI / 180;
        double radius = 4.5;
        double x = cameraCenter.getX() + Math.cos(angle) * radius;
        double z = cameraCenter.getZ() + Math.sin(angle) * radius;
        return new Location(player.getWorld(), x, cameraCenter.getY(), z);
    }
    
    private void createFruitPopupAtCursor() {
        Location cursorLoc = getCursorLocation();
        if(cursorLoc == null) return;
        
        Fruit randomFruit = getRandomWeightedFruit();
        
        ItemDisplay popup = (ItemDisplay) player.getWorld().spawnEntity(cursorLoc, EntityType.ITEM_DISPLAY);
        popup.setItemStack(createDisplayItem(randomFruit));
        popup.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);
        popup.setGlowing(true);
        
        new BukkitRunnable() {
            int height = 0;
            @Override
            public void run() {
                if(height >= 20) {
                    popup.remove();
                    this.cancel();
                    return;
                }
                popup.teleport(popup.getLocation().add(0, 0.1, 0));
                height++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        player.playSound(cursorLoc, Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.2f);
        player.getWorld().spawnParticle(Particle.FIREWORK, cursorLoc, 5, 0.2, 0.2, 0.2, 0);
    }
    
    private Location getCursorLocation() {
        Vector direction = player.getEyeLocation().getDirection();
        Location eyeLoc = player.getEyeLocation();
        
        for(double d = 1; d <= 10; d += 0.5) {
            Location check = eyeLoc.clone().add(direction.clone().multiply(d));
            if(check.getBlock().getType().isSolid()) {
                return check;
            }
        }
        return eyeLoc.clone().add(direction.clone().multiply(5));
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
    
    private void startParticleSystems() {
        particleTask = new BukkitRunnable() {
            int angle = 0;
            @Override
            public void run() {
                if(!spinning) {
                    this.cancel();
                    return;
                }
                
                for(int i = 0; i < 360; i += 20) {
                    double rad = Math.toRadians(i + angle);
                    double radius = 3;
                    double x = player.getLocation().getX() + Math.cos(rad) * radius;
                    double z = player.getLocation().getZ() + Math.sin(rad) * radius;
                    double y = player.getLocation().getY() + 1;
                    
                    Location spiralLoc = new Location(player.getWorld(), x, y, z);
                    Color color = Color.fromRGB(
                        (int)(Math.sin(rad) * 127 + 128),
                        (int)(Math.cos(rad) * 127 + 128),
                        (int)(Math.sin(rad * 2) * 127 + 128)
                    );
                    Particle.DustOptions dust = new Particle.DustOptions(color, 1.0f);
                    player.getWorld().spawnParticle(Particle.DUST, spiralLoc, 1, 0, 0, 0, dust);
                }
                angle += 10;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void startCameraMovement() {
        cameraTask = new BukkitRunnable() {
            double radius = 2.5;
            double angle = 0;
            @Override
            public void run() {
                if(!spinning || elapsedTicks >= TOTAL_DURATION_TICKS - 40) {
                    this.cancel();
                    return;
                }
                
                angle += 0.05;
                double x = cameraCenter.getX() + Math.cos(angle) * radius;
                double z = cameraCenter.getZ() + Math.sin(angle) * radius;
                Location camLoc = new Location(player.getWorld(), x, cameraCenter.getY() + 1, z);
                
                Vector direction = cameraCenter.toVector().subtract(camLoc.toVector()).normalize();
                float yaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
                float pitch = (float) Math.toDegrees(Math.asin(-direction.getY()));
                
                camLoc.setYaw(yaw);
                camLoc.setPitch(pitch);
                player.teleport(camLoc);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void finishSpin() {
        spinning = false;
        
        if(spinTask != null) spinTask.cancel();
        if(particleTask != null) particleTask.cancel();
        if(cameraTask != null) cameraTask.cancel();
        
        finalReward = getRandomWeightedFruit();
        int amount = random.nextInt(3) + 1;
        
        // Give reward
        player.getInventory().addItem(finalReward.createItemStack(amount));
        
        cleanupArena();
        unfreezePlayer();
        
        // Celebration effects
        Location center = player.getLocation().clone().add(0, 2, 0);
        for(int i = 0; i < 50; i++) {
            double x = center.getX() + (random.nextDouble() - 0.5) * 5;
            double z = center.getZ() + (random.nextDouble() - 0.5) * 5;
            double y = center.getY() + random.nextDouble() * 3;
            Location confettiLoc = new Location(player.getWorld(), x, y, z);
            player.getWorld().spawnParticle(Particle.FIREWORK, confettiLoc, 2, 0.1, 0.1, 0.1, 0);
        }
        
        player.sendTitle("§6§l🎉 SPIN COMPLETE! 🎉", 
            "§aYou won §6" + amount + "x " + finalReward.getName() + "§a!", 10, 80, 20);
        
        player.sendMessage("");
        player.sendMessage("§6§l╔═══════════════════════════════════╗");
        player.sendMessage("§6§l║ §e§l🎉 CONGRATULATIONS! 🎉 §6§l║");
        player.sendMessage("§6§l╠═══════════════════════════════════╣");
        player.sendMessage("§6§l║ §7You won: §6" + amount + "x " + finalReward.getName());
        player.sendMessage("§6§l╚═══════════════════════════════════╝");
        
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
    }
    
    private void cleanupArena() {
        if(centerDisplay != null) centerDisplay.remove();
        for(ItemDisplay display : fruitDisplays) {
            if(display != null) display.remove();
        }
        for(ArmorStand hologram : fruitHolograms) {
            if(hologram != null) hologram.remove();
        }
        fruitDisplays.clear();
        fruitHolograms.clear();
    }
    
    private void unfreezePlayer() {
        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.JUMP_BOOST);
        player.setInvulnerable(false);
        player.setFlying(false);
        player.setAllowFlight(false);
        
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p != player) p.showPlayer(plugin, player);
        }
        
        player.teleport(originalLocation);
        player.sendMessage("§a✓ Spin complete! You can move again!");
    }
    
    public static void stopSpin(Player player) {
        // Implementation if needed
    }
}
