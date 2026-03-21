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
import org.bukkit.util.EulerAngle;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
    private final int TOTAL_DURATION_TICKS = 300; // 15 seconds
    private Location originalLocation;
    private Location cameraCenter;
    private ArmorStand centerDisplay;
    private List<ArmorStand> fruitHolograms = new ArrayList<>();
    private List<ItemDisplay> fruitDisplays = new ArrayList<>();
    private Random random = new Random();
    private Fruit currentHighlightFruit = null;
    private int lastHighlightIndex = 0;
    
    // For resource pack custom model data
    private static final Map<String, Integer> FRUIT_CUSTOM_MODELS = new HashMap<>();
    
    public CinematicSpinWheel(FruitsPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.allFruits = new ArrayList<>(plugin.getFruitRegistry().getAllFruits());
        this.fruitWeights = new HashMap<>();
        
        // Initialize custom model data mapping (for resource pack)
        initCustomModels();
        
        // Setup weighted rewards
        setupWeights();
    }
    
    private void initCustomModels() {
        // Assign custom model data IDs for each fruit (matches your resource pack)
        FRUIT_CUSTOM_MODELS.put("nature_dye", 1001);
        FRUIT_CUSTOM_MODELS.put("water_dye", 1002);
        FRUIT_CUSTOM_MODELS.put("cyclone_dye", 1003);
        FRUIT_CUSTOM_MODELS.put("dracula_dye", 1004);
        FRUIT_CUSTOM_MODELS.put("portal_dye", 1005);
        FRUIT_CUSTOM_MODELS.put("throne_dye", 1006);
        FRUIT_CUSTOM_MODELS.put("thief_dye", 1007);
        FRUIT_CUSTOM_MODELS.put("star_dye", 1008);
        FRUIT_CUSTOM_MODELS.put("shadow_dye", 1009);
        FRUIT_CUSTOM_MODELS.put("primordial_dye", 1010);
    }
    
    private void setupWeights() {
        for(Fruit fruit : allFruits) {
            int weight = 100; // Default weight
            // Rarer fruits have lower weight
            switch(fruit.getId()) {
                case "primordial_dye": weight = 5; break;
                case "dracula_dye": weight = 10; break;
                case "portal_dye": weight = 15; break;
                case "star_dye": weight = 20; break;
                case "shadow_dye": weight = 25; break;
                case "thief_dye": weight = 30; break;
                case "throne_dye": weight = 35; break;
                case "cyclone_dye": weight = 40; break;
                case "water_dye": weight = 50; break;
                case "nature_dye": weight = 60; break;
                default: weight = 45;
            }
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
        
        // ========== 1. FREEZE PLAYER ==========
        freezePlayer();
        
        // ========== 2. CREATE 3D DISPLAY ARENA ==========
        create3DArena();
        
        // ========== 3. START MAIN SPIN LOOP ==========
        startSpinLoop();
        
        // ========== 4. START PARTICLE EFFECTS ==========
        startParticleSystems();
        
        // ========== 5. START CAMERA MOVEMENT ==========
        startCameraMovement();
        
        // ========== 6. SEND TITLE ==========
        player.sendTitle("§6§l🎰 CINEMATIC SPIN", "§eWatch the fruits appear!", 10, 40, 10);
        player.sendMessage("§6§l═══════════════════════════════");
        player.sendMessage("§a✨ CINEMATIC SPIN STARTED! ✨");
        player.sendMessage("§7Watch the fruits dance around you!");
        player.sendMessage("§6§l═══════════════════════════════");
    }
    
    private void freezePlayer() {
        // Save original state
        player.setWalkSpeed(0);
        player.setFlySpeed(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 255, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 400, 128, false, false));
        player.setInvulnerable(true);
        player.setFlying(true);
        player.setAllowFlight(true);
        
        // Hide player for cinematic view
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p != player) {
                p.hidePlayer(plugin, player);
            }
        }
        
        // Teleport to center
        player.teleport(cameraCenter);
        
        player.sendMessage("§c⚠ You are frozen! Watch the magic unfold... ⚠");
    }
    
    private void create3DArena() {
        World world = player.getWorld();
        
        // ========== CENTER DISPLAY STAND ==========
        centerDisplay = (ArmorStand) world.spawnEntity(cameraCenter.clone().add(0, 1, 0), EntityType.ARMOR_STAND);
        centerDisplay.setVisible(false);
        centerDisplay.setGravity(false);
        centerDisplay.setInvulnerable(true);
        centerDisplay.setCustomNameVisible(true);
        centerDisplay.setCustomName("§6§l⚡ SPINNING... ⚡");
        
        // ========== CREATE FRUIT DISPLAYS IN A CIRCLE ==========
        int fruitCount = allFruits.size();
        for(int i = 0; i < fruitCount; i++) {
            Fruit fruit = allFruits.get(i);
            double angle = (i * 360.0 / fruitCount) * Math.PI / 180;
            double radius = 4.5;
            double x = cameraCenter.getX() + Math.cos(angle) * radius;
            double z = cameraCenter.getZ() + Math.sin(angle) * radius;
            Location fruitLoc = new Location(world, x, cameraCenter.getY(), z);
            
            // Create ItemDisplay entity (better for 3D items)
            ItemDisplay display = (ItemDisplay) world.spawnEntity(fruitLoc, EntityType.ITEM_DISPLAY);
            ItemStack fruitItem = createCustomModelItem(fruit);
            display.setItemStack(fruitItem);
            display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);
            display.setRotation(0, (float) (Math.toDegrees(angle)));
            display.setInvulnerable(true);
            display.setPersistent(true);
            
            // Add glow effect
            display.setGlowing(true);
            display.setGlowColorOverride(Color.YELLOW);
            
            fruitDisplays.add(display);
            
            // Add name hologram above each fruit
            ArmorStand nameTag = (ArmorStand) world.spawnEntity(fruitLoc.clone().add(0, 0.8, 0), EntityType.ARMOR_STAND);
            nameTag.setVisible(false);
            nameTag.setGravity(false);
            nameTag.setInvulnerable(true);
            nameTag.setCustomNameVisible(true);
            nameTag.setCustomName("§a" + fruit.getName());
            fruitHolograms.add(nameTag);
        }
        
        // ========== CREATE FLOATING PARTICLES AROUND CIRCLE ==========
        createParticleRing();
    }
    
    private ItemStack createCustomModelItem(Fruit fruit) {
        ItemStack item = fruit.createItemStack(1);
        ItemMeta meta = item.getItemMeta();
        
        // Set custom model data for resource pack
        Integer modelId = FRUIT_CUSTOM_MODELS.get(fruit.getId());
        if(modelId != null) {
            meta.setCustomModelData(modelId);
        }
        
        // Add glowing effect lore
        List<String> lore = new ArrayList<>();
        lore.add("§7✨ " + fruit.getAbilities().get(0).getName() + " ✨");
        if(fruit.getAbilities().size() > 1) {
            lore.add("§7🌀 " + fruit.getAbilities().get(1).getName() + " 🌀");
        }
        meta.setLore(lore);
        
        item.setItemMeta(meta);
        return item;
    }
    
    private void createParticleRing() {
        new BukkitRunnable() {
            int angle = 0;
            
            @Override
            public void run() {
                if(!spinning) {
                    this.cancel();
                    return;
                }
                
                // Create rotating particle ring
                for(int i = 0; i < 360; i += 10) {
                    double rad = Math.toRadians(i + angle);
                    double radius = 5;
                    double x = cameraCenter.getX() + Math.cos(rad) * radius;
                    double z = cameraCenter.getZ() + Math.sin(rad) * radius;
                    Location ringLoc = new Location(player.getWorld(), x, cameraCenter.getY() + 0.5, z);
                    
                    // Rainbow particles
                    Color color = Color.fromRGB(
                        (int)(Math.sin(rad) * 127 + 128),
                        (int)(Math.cos(rad) * 127 + 128),
                        (int)(Math.sin(rad * 2) * 127 + 128)
                    );
                    Particle.DustOptions dust = new Particle.DustOptions(color, 1.2f);
                    player.getWorld().spawnParticle(Particle.DUST, ringLoc, 1, 0, 0, 0, dust);
                }
                
                angle += 8;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void startSpinLoop() {
        spinTask = new BukkitRunnable() {
            int animationStep = 0;
            
            @Override
            public void run() {
                if(elapsedTicks >= TOTAL_DURATION_TICKS) {
                    finishSpin();
                    this.cancel();
                    return;
                }
                
                // Update center display text
                updateCenterDisplay();
                
                // Animate fruit displays (rotate and bounce)
                animateFruitDisplays();
                
                // Create fruit popups at player's cursor
                if(elapsedTicks % 4 == 0) {
                    createFruitPopupAtCursor();
                }
                
                // Highlight random fruits (speed increases over time)
                if(elapsedTicks % Math.max(2, 20 - (elapsedTicks / 15)) == 0) {
                    highlightRandomFruit();
                }
                
                // Play sound effects
                playSpinSounds();
                
                // Update timer title
                if(elapsedTicks % 20 == 0) {
                    int secondsLeft = (TOTAL_DURATION_TICKS - elapsedTicks) / 20;
                    player.sendTitle("§6§l🎰 SPINNING...", 
                        "§e" + secondsLeft + " seconds remaining", 
                        0, 30, 0);
                }
                
                elapsedTicks++;
                animationStep++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void updateCenterDisplay() {
        if(centerDisplay == null) return;
        
        String[] messages = {
            "§6§l⚡ SPINNING ⚡",
            "§e§l🎰 LUCKY SPIN 🎰",
            "§a§l✨ GET LUCKY ✨",
            "§d§l🌟 SPINNING 🌟",
            "§b§l🌀 FRUIT SPIN 🌀"
        };
        
        int index = (elapsedTicks / 20) % messages.length;
        centerDisplay.setCustomName(messages[index]);
        
        // Make it float up and down
        double yOffset = Math.sin(elapsedTicks * 0.1) * 0.2;
        centerDisplay.teleport(cameraCenter.clone().add(0, 1 + yOffset, 0));
    }
    
    private void animateFruitDisplays() {
        for(int i = 0; i < fruitDisplays.size(); i++) {
            ItemDisplay display = fruitDisplays.get(i);
            ArmorStand hologram = fruitHolograms.get(i);
            
            // Rotate each fruit
            float currentRot = display.getLocation().getYaw();
            display.setRotation(currentRot + 5, 0);
            
            // Bounce animation
            double yOffset = Math.sin((elapsedTicks * 0.1) + (i * 0.5)) * 0.15;
            Location newLoc = getOriginalFruitLocation(i).clone().add(0, yOffset, 0);
            display.teleport(newLoc);
            hologram.teleport(newLoc.clone().add(0, 0.8, 0));
            
            // Change glow color based on progress
            if(display.isGlowing()) {
                float progress = (float) elapsedTicks / TOTAL_DURATION_TICKS;
                Color color = Color.fromRGB(
                    (int)(255 * progress),
                    (int)(255 * (1 - progress)),
                    100
                );
                display.setGlowColorOverride(color);
            }
        }
    }
    
    private Location getOriginalFruitLocation(int index) {
        double angle = (index * 360.0 / allFruits.size()) * Math.PI / 180;
        double radius = 4.5;
        double x = cameraCenter.getX() + Math.cos(angle) * radius;
        double z = cameraCenter.getZ() + Math.sin(angle) * radius;
        return new Location(player.getWorld(), x, cameraCenter.getY(), z);
    }
    
    private void createFruitPopupAtCursor() {
        // Get where player is looking
        Location cursorLoc = getCursorLocation();
        if(cursorLoc == null) return;
        
        // Get random fruit
        Fruit randomFruit = getRandomWeightedFruit();
        
        // Create 3D floating fruit hologram
        ItemDisplay popup = (ItemDisplay) player.getWorld().spawnEntity(cursorLoc, EntityType.ITEM_DISPLAY);
        popup.setItemStack(createCustomModelItem(randomFruit));
        popup.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);
        popup.setGlowing(true);
        popup.setGlowColorOverride(getFruitColor(randomFruit));
        
        // Animate floating up and fade out
        new BukkitRunnable() {
            int height = 0;
            float scale = 1.0f;
            
            @Override
            public void run() {
                if(height >= 30) {
                    popup.remove();
                    this.cancel();
                    return;
                }
                
                // Float upward
                popup.teleport(popup.getLocation().add(0, 0.08, 0));
                
                // Scale down over time
                scale -= 0.03f;
                popup.setTransformationMatrix(
                    new org.joml.Matrix4f().scale(scale, scale, scale)
                );
                
                height++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        // Play pop sound
        player.playSound(cursorLoc, Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.5f);
        
        // Add particle burst
        player.getWorld().spawnParticle(Particle.FIREWORK, cursorLoc, 10, 0.2, 0.2, 0.2, 0.1);
    }
    
    private void highlightRandomFruit() {
        if(fruitDisplays.isEmpty()) return;
        
        // Remove previous highlight
        if(currentHighlightFruit != null) {
            int prevIndex = allFruits.indexOf(currentHighlightFruit);
            if(prevIndex >= 0 && prevIndex < fruitDisplays.size()) {
                fruitDisplays.get(prevIndex).setGlowColorOverride(Color.YELLOW);
            }
        }
        
        // Select new random fruit
        int randomIndex = random.nextInt(fruitDisplays.size());
        Fruit selectedFruit = allFruits.get(randomIndex);
        currentHighlightFruit = selectedFruit;
        
        // Highlight with color based on rarity
        Color highlightColor = getFruitRarityColor(selectedFruit);
        fruitDisplays.get(randomIndex).setGlowColorOverride(highlightColor);
        
        // Add lightning effect on highlight
        Location fruitLoc = fruitDisplays.get(randomIndex).getLocation();
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, fruitLoc, 20, 0.3, 0.3, 0.3, 0.1);
        player.playSound(fruitLoc, Sound.BLOCK_BEACON_POWER_SELECT, 0.6f, 1.2f);
        
        // Update center display with fruit name
        centerDisplay.setCustomName("§6§l✨ " + selectedFruit.getName() + " §6§l✨");
        
        lastHighlightIndex = randomIndex;
    }
    
    private Color getFruitRarityColor(Fruit fruit) {
        switch(fruit.getId()) {
            case "primordial_dye": return Color.fromRGB(255, 0, 255); // Purple
            case "dracula_dye": return Color.fromRGB(255, 0, 0); // Red
            case "portal_dye": return Color.fromRGB(128, 0, 128); // Dark Purple
            case "star_dye": return Color.fromRGB(255, 215, 0); // Gold
            case "shadow_dye": return Color.fromRGB(0, 0, 0); // Black
            default: return Color.fromRGB(255, 255, 0); // Yellow
        }
    }
    
    private Color getFruitColor(Fruit fruit) {
        switch(fruit.getId()) {
            case "nature_dye": return Color.fromRGB(0, 255, 0);
            case "water_dye": return Color.fromRGB(0, 255, 255);
            case "cyclone_dye": return Color.fromRGB(0, 200, 200);
            case "dracula_dye": return Color.fromRGB(255, 0, 0);
            case "portal_dye": return Color.fromRGB(255, 0, 255);
            case "throne_dye": return Color.fromRGB(255, 255, 0);
            case "thief_dye": return Color.fromRGB(80, 80, 80);
            case "star_dye": return Color.fromRGB(255, 200, 0);
            case "shadow_dye": return Color.fromRGB(50, 50, 50);
            case "primordial_dye": return Color.fromRGB(255, 100, 255);
            default: return Color.WHITE;
        }
    }
    
    private Location getCursorLocation() {
        Vector direction = player.getEyeLocation().getDirection();
        Location eyeLoc = player.getEyeLocation();
        
        // Ray trace up to 12 blocks
        for(double d = 1; d <= 12; d += 0.5) {
            Location check = eyeLoc.clone().add(direction.clone().multiply(d));
            if(check.getBlock().getType().isSolid()) {
                return check;
            }
        }
        return eyeLoc.clone().add(direction.clone().multiply(8));
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
    
    private void playSpinSounds() {
        // Increasing pitch over time
        float pitch = 0.5f + (elapsedTicks / (float)TOTAL_DURATION_TICKS) * 1.2f;
        
        if(elapsedTicks % 8 == 0) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.8f, pitch);
        }
        
        if(elapsedTicks % 15 == 0) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, pitch);
        }
        
        // Heartbeat effect near the end
        if(elapsedTicks > TOTAL_DURATION_TICKS - 60 && elapsedTicks % 10 == 0) {
            player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_HEARTBEAT, 1.0f, 1.0f);
            
            // Screen pulse effect
            player.sendTitle("", "§c§l!! FINAL SPIN !!", 0, 5, 0);
        }
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
                
                // Spiral particles around player
                for(int i = 0; i < 360; i += 15) {
                    double rad = Math.toRadians(i + angle);
                    double radius = 2.5;
                    double x = player.getLocation().getX() + Math.cos(rad) * radius;
                    double z = player.getLocation().getZ() + Math.sin(rad) * radius;
                    double y = player.getLocation().getY() + 1 + Math.sin(rad * 2) * 1.5;
                    
                    Location spiralLoc = new Location(player.getWorld(), x, y, z);
                    
                    // Rainbow spiral
                    Color color = Color.fromRGB(
                        (int)(Math.sin(rad) * 127 + 128),
                        (int)(Math.cos(rad) * 127 + 128),
                        (int)(Math.sin(rad * 2) * 127 + 128)
                    );
                    Particle.DustOptions dust = new Particle.DustOptions(color, 1.0f);
                    player.getWorld().spawnParticle(Particle.DUST, spiralLoc, 1, 0, 0, 0, dust);
                }
                
                angle += 12;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void startCameraMovement() {
        cameraTask = new BukkitRunnable() {
            double radius = 2.0;
            double angle = 0;
            
            @Override
            public void run() {
                if(!spinning || elapsedTicks >= TOTAL_DURATION_TICKS - 40) {
                    this.cancel();
                    return;
                }
                
                // Smooth circular camera movement
                angle += 0.03;
                double x = cameraCenter.getX() + Math.cos(angle) * radius;
                double z = cameraCenter.getZ() + Math.sin(angle) * radius;
                Location camLoc = new Location(player.getWorld(), x, cameraCenter.getY() + 1, z);
                
                // Make player look at center
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
        
        // Stop all tasks
        if(spinTask != null) spinTask.cancel();
        if(particleTask != null) particleTask.cancel();
        if(cameraTask != null) cameraTask.cancel();
        
        // Determine final reward (weighted)
        Fruit finalReward = getRandomWeightedFruit();
        int amount = random.nextInt(3) + 1; // 1-3 fruits
        
        // Give reward
        player.getInventory().addItem(createCustomModelItem(finalReward).asQuantity(amount));
        
        // ========== GRAND FINALE - GOKU TRANSFORMATION PARTICLES ==========
        playGrandFinale(finalReward, amount);
        
        // Cleanup
        cleanupArena();
        unfreezePlayer();
        
        // Send result
        player.sendTitle("§6§l🎉 SPIN COMPLETE! 🎉", 
            "§aYou won §6" + amount + "x " + finalReward.getName() + "§a!", 
            10, 100, 20);
        
        player.sendMessage("");
        player.sendMessage("§6§l╔═══════════════════════════════════╗");
        player.sendMessage("§6§l║ §e§l🎉 CONGRATULATIONS! 🎉 §6§l║");
        player.sendMessage("§6§l╠═══════════════════════════════════╣");
        player.sendMessage("§6§l║ §7You won: §6" + amount + "x " + finalReward.getName());
        player.sendMessage("§6§l║ §7Rarity: " + getRarityText(finalReward));
        player.sendMessage("§6§l╚═══════════════════════════════════╝");
        
        // Play victory sounds
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.5f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0f, 0.8f);
        player.playSound(player.getLocation(), Sound.MUSIC_DISC_PIGSTEP, 1.0f, 1.0f);
    }
    
    private String getRarityText(Fruit fruit) {
        switch(fruit.getId()) {
            case "primordial_dye": return "§5§lMYTHIC";
            case "dracula_dye": return "§c§lLEGENDARY";
            case "portal_dye": return "§5§lEPIC";
            case "star_dye": return "§6§lRARE";
            default: return "§a§lCOMMON";
        }
    }
    
    private void playGrandFinale(Fruit reward, int amount) {
        Location center = player.getLocation().clone().add(0, 2, 0);
        Color rewardColor = getFruitColor(reward);
        
        new BukkitRunnable() {
            int phase = 0;
            int subPhase = 0;
            
            @Override
            public void run() {
                if(phase >= 5) {
                    this.cancel();
                    return;
                }
                
                switch(phase) {
                    case 0: // Energy gathering
                        for(int i = 0; i < 50; i++) {
                            double x = center.getX() + (random.nextDouble() - 0.5) * 5;
                            double z = center.getZ() + (random.nextDouble() - 0.5) * 5;
                            double y = center.getY() + random.nextDouble() * 4;
                            Location energyLoc = new Location(player.getWorld(), x, y, z);
                            
                            // Particles flow to center
                            Vector toCenter = center.toVector().subtract(energyLoc.toVector()).normalize();
                            player.getWorld().spawnParticle(Particle.END_ROD, energyLoc, 1, toCenter.getX(), toCenter.getY(), toCenter.getZ(), 0.1);
                        }
                        break;
                        
                    case 1: // Massive explosion
                        for(int i = 0; i < 100; i++) {
                            double x = center.getX() + (random.nextDouble() - 0.5) * 6;
                            double z = center.getZ() + (random.nextDouble() - 0.5) * 6;
                            double y = center.getY() + random.nextDouble() * 5;
                            Location expLoc = new Location(player.getWorld(), x, y, z);
                            
                            player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, expLoc, 1, 0, 0, 0, 0);
                            player.getWorld().spawnParticle(Particle.FLASH, expLoc, 1, 0, 0, 0, 0);
                        }
                        player.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
                        break;
                        
                    case 2: // Energy beam spiral
                        for(int i = 0; i < 360; i += 10) {
                            double rad = Math.toRadians(i + subPhase * 20);
                            double radius = 2 + subPhase * 0.1;
                            double x = center.getX() + Math.cos(rad) * radius;
                            double z = center.getZ() + Math.sin(rad) * radius;
                            double y = center.getY() + Math.sin(rad) * 2 + subPhase * 0.1;
                            
                            Location beamLoc = new Location(player.getWorld(), x, y, z);
                            Particle.DustOptions dust = new Particle.DustOptions(rewardColor, 2.0f);
                            player.getWorld().spawnParticle(Particle.DUST, beamLoc, 1, 0, 0, 0, dust);
                            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, beamLoc, 2, 0.1, 0.1, 0.1, 0);
                        }
                        break;
                        
                    case 3: // Shockwave rings
                        for(int r = 0; r <= subPhase; r++) {
                            for(int i = 0; i < 360; i += 10) {
                                double rad = Math.toRadians(i);
                                double radius = r * 0.5;
                                double x = center.getX() + Math.cos(rad) * radius;
                                double z = center.getZ() + Math.sin(rad) * radius;
                                Location waveLoc = new Location(player.getWorld(), x, center.getY(), z);
                                player.getWorld().spawnParticle(Particle.CLOUD, waveLoc, 2, 0, 0.1, 0, 0);
                            }
                        }
                        break;
                        
                    case 4: // Reward reveal
                        if(subPhase == 0) {
                            // Create reward hologram
                            ArmorStand rewardDisplay = (ArmorStand) player.getWorld().spawnEntity(center.clone().add(0, 2, 0), EntityType.ARMOR_STAND);
                            rewardDisplay.setVisible(false);
                            rewardDisplay.setGravity(false);
                            rewardDisplay.setInvulnerable(true);
                            rewardDisplay.setCustomNameVisible(true);
                            rewardDisplay.setCustomName("§6§l✨ " + amount + "x " + reward.getName() + " ✨");
                            
                            // Confetti explosion
                            for(int i = 0; i < 200; i++) {
                                double x = center.getX() + (random.nextDouble() - 0.5) * 8;
                                double y = center.getY() + random.nextDouble() * 6;
                                double z = center.getZ() + (random.nextDouble() - 0.5) * 8;
                                Location confettiLoc = new Location(player.getWorld(), x, y, z);
                                
                                Color randomColor = Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                                Particle.DustOptions dust = new Particle.DustOptions(randomColor, 1.0f);
                                player.getWorld().spawnParticle(Particle.DUST, confettiLoc, 2, 0.1, 0.1, 0.1, dust);
                                player.getWorld().spawnParticle(Particle.FIREWORK, confettiLoc, 1, 0, 0, 0, 0);
                            }
                            
                            player.playSound(center, Sound.UI_TOAST_CHALLENGE_COMPLETE, 2.0f, 1.0f);
                            player.playSound(center, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0f, 1.5f);
                            
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    rewardDisplay.remove();
                                }
                            }.runTaskLater(plugin, 60L);
                        }
                        break;
                }
                
                subPhase++;
                if(subPhase >= 20) {
                    subPhase = 0;
                    phase++;
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
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
        
        // Show player to others
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p != player) {
                p.showPlayer(plugin, player);
            }
        }
        
        // Teleport back
        player.teleport(originalLocation);
        
        player.sendMessage("§a✓ Spin complete! You can move again!");
    }
}
