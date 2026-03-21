package com.example.fruits.abilities;

import com.example.fruits.FruitsPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;

public class CycloneAbilities {
    
    // Store active tornados
    private static final Map<UUID, TornadoData> activeTornados = new HashMap<>();
    private static final Map<UUID, List<FlyingBlock>> flyingBlocks = new HashMap<>();
    
    // ==================== ABILITY 1: SPEED TORNADO (Right Click) ====================
    
    public static void speedTornado(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(activeTornados.containsKey(uuid)) {
            player.sendMessage("§3🌀 You already have an active tornado!");
            return;
        }
        
        player.sendMessage("§3§l🌀 SPEED TORNADO ACTIVATED!");
        player.sendMessage("§7You gain §e10x speed§7 and create a deadly tornado!");
        player.playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 1.0f, 0.8f);
        
        // Apply speed boost (10x)
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(
            org.bukkit.potion.PotionEffectType.SPEED, 200, 9, false, false, true));
        
        // Create tornado effect around player
        TornadoData tornado = new TornadoData(player.getLocation(), System.currentTimeMillis() + 10000);
        activeTornados.put(uuid, tornado);
        
        // Start tornado effect
        startSpeedTornadoEffect(player, tornado);
        
        // Remove after 10 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activeTornados.containsKey(uuid)) {
                    activeTornados.remove(uuid);
                    if(player.isOnline()) {
                        player.sendMessage("§3🌀 Speed tornado has dissipated!");
                        player.playSound(player.getLocation(), Sound.ENTITY_BREEZE_DEATH, 1.0f, 0.8f);
                    }
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 200L);
    }
    
    private static void startSpeedTornadoEffect(Player player, TornadoData tornado) {
        new BukkitRunnable() {
            int angle = 0;
            @Override
            public void run() {
                if(!activeTornados.containsKey(player.getUniqueId()) || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                Location center = player.getLocation();
                
                // Create tornado spirals
                for(int radius = 0; radius <= 4; radius++) {
                    for(int i = 0; i < 360; i += 15) {
                        double rad = Math.toRadians(i + angle * (radius + 1) * 5);
                        double x = Math.cos(rad) * radius;
                        double z = Math.sin(rad) * radius;
                        
                        // Particle spiral
                        player.getWorld().spawnParticle(Particle.CLOUD, center.clone().add(x, 1, z), 1, 0, 0.1, 0);
                        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, center.clone().add(x, 1.5, z), 1, 0, 0, 0);
                    }
                }
                
                // Suck in and launch nearby entities
                player.getWorld().getNearbyEntities(center, 8, 5, 8).forEach(e -> {
                    if(e != player && e instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity) e;
                        
                        // Pull towards center
                        Vector direction = center.toVector().subtract(living.getLocation().toVector()).normalize();
                        double distance = living.getLocation().distance(center);
                        
                        if(distance < 3) {
                            // Launch up to 25 blocks
                            living.setVelocity(new Vector(0, 2.5, 0));
                            living.getWorld().playSound(living.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);
                            living.damage(8, player);
                        } else {
                            // Rotate around tornado
                            double rotAngle = Math.toRadians(angle * 10);
                            double rotX = Math.cos(rotAngle) * 1.5;
                            double rotZ = Math.sin(rotAngle) * 1.5;
                            living.setVelocity(direction.multiply(0.5).add(new Vector(rotX, 0.2, rotZ)));
                        }
                        
                        // Damage over time
                        living.damage(2, player);
                        
                        // Particle trail
                        living.getWorld().spawnParticle(Particle.CLOUD, living.getLocation(), 10, 0.3, 0.3, 0.3);
                    }
                });
                
                angle += 10;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 2L);
    }
    
    // ==================== ABILITY 2: BLOCK TORNADO (Crouch + Right Click) ====================
    
    public static void blockTornado(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(activeTornados.containsKey(uuid)) {
            player.sendMessage("§3🌀 You already have an active tornado!");
            return;
        }
        
        player.sendMessage("§3§l🌀 BLOCK TORNADO SUMMONED!");
        player.sendMessage("§7Blocks rise from the ground! Left-click to launch them!");
        player.playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 1.0f, 0.5f);
        
        Location center = player.getLocation();
        TornadoData tornado = new TornadoData(center, System.currentTimeMillis() + 10000);
        activeTornados.put(uuid, tornado);
        
        List<FlyingBlock> blocks = new ArrayList<>();
        flyingBlocks.put(uuid, blocks);
        
        // Start block tornado effect
        startBlockTornadoEffect(player, tornado, blocks);
        
        // Remove after 10 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activeTornados.containsKey(uuid)) {
                    activeTornados.remove(uuid);
                    // Drop remaining blocks
                    for(FlyingBlock fb : blocks) {
                        if(fb.entity != null) fb.entity.remove();
                    }
                    flyingBlocks.remove(uuid);
                    if(player.isOnline()) {
                        player.sendMessage("§3🌀 Block tornado has dissipated!");
                        player.playSound(player.getLocation(), Sound.ENTITY_BREEZE_DEATH, 1.0f, 0.8f);
                    }
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 200L);
    }
    
    private static void startBlockTornadoEffect(Player player, TornadoData tornado, List<FlyingBlock> blocks) {
        new BukkitRunnable() {
            int angle = 0;
            int blockCollectTimer = 0;
            
            @Override
            public void run() {
                if(!activeTornados.containsKey(player.getUniqueId()) || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                Location center = player.getLocation();
                
                // Collect new blocks every 10 ticks
                blockCollectTimer++;
                if(blockCollectTimer >= 10 && blocks.size() < 30) {
                    collectNearbyBlocks(center, blocks, player);
                    blockCollectTimer = 0;
                }
                
                // Update all flying blocks
                Iterator<FlyingBlock> iterator = blocks.iterator();
                while(iterator.hasNext()) {
                    FlyingBlock fb = iterator.next();
                    
                    if(fb.entity == null || fb.entity.isDead()) {
                        iterator.remove();
                        continue;
                    }
                    
                    // Calculate new position (rotating around center)
                    double radius = fb.radius;
                    double height = fb.height;
                    
                    fb.angle += 8;
                    double rad = Math.toRadians(fb.angle);
                    double x = Math.cos(rad) * radius;
                    double z = Math.sin(rad) * radius;
                    
                    // Update height (spiral up)
                    if(height < 4) {
                        height += 0.05;
                    } else {
                        height = 4;
                    }
                    fb.height = height;
                    
                    Location newLoc = center.clone().add(x, height, z);
                    fb.entity.teleport(newLoc);
                    
                    // Rotation effect for falling blocks
                    if(fb.entity instanceof ItemDisplay) {
                        ((ItemDisplay) fb.entity).setRotation((float) fb.angle, 0);
                    }
                    
                    // Particles around block
                    newLoc.getWorld().spawnParticle(Particle.CLOUD, newLoc, 3, 0.1, 0.1, 0.1);
                    newLoc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, newLoc, 1, 0.1, 0.1, 0.1);
                    
                    // Damage entities near block
                    newLoc.getWorld().getNearbyEntities(newLoc, 0.8, 0.8, 0.8).forEach(e -> {
                        if(e != player && e instanceof LivingEntity) {
                            ((LivingEntity) e).damage(3, player);
                        }
                    });
                }
                
                angle += 5;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void collectNearbyBlocks(Location center, List<FlyingBlock> blocks, Player player) {
        int radius = 5;
        for(int x = -radius; x <= radius; x++) {
            for(int z = -radius; z <= radius; z++) {
                for(int y = -2; y <= 2; y++) {
                    if(blocks.size() >= 30) return;
                    
                    Location blockLoc = center.clone().add(x, y, z);
                    Block block = blockLoc.getBlock();
                    
                    if(block.getType() != Material.AIR && block.getType() != Material.BEDROCK) {
                        // Check if block already collected
                        boolean alreadyCollected = false;
                        for(FlyingBlock fb : blocks) {
                            if(fb.originalBlock.equals(block.getLocation())) {
                                alreadyCollected = true;
                                break;
                            }
                        }
                        
                        if(!alreadyCollected) {
                            Material blockType = block.getType();
                            org.bukkit.block.data.BlockData blockData = block.getBlockData();
                            
                            // Create display entity
                            Location spawnLoc = block.getLocation().add(0.5, 0.5, 0.5);
                            ItemDisplay display = (ItemDisplay) block.getWorld().spawnEntity(spawnLoc, EntityType.ITEM_DISPLAY);
                            
                            // FIXED: Use ItemStack instead of setBlockDisplayData
                            display.setItemStack(new ItemStack(blockType));
                            
                            // Store block info
                            FlyingBlock fb = new FlyingBlock(display, block.getLocation(), blockType, blockData);
                            fb.radius = Math.sqrt(x*x + z*z) + 1;
                            fb.angle = (int)(Math.atan2(z, x) * 180 / Math.PI);
                            fb.height = 0.5;
                            
                            blocks.add(fb);
                            
                            // Remove original block
                            block.setType(Material.AIR);
                            
                            // Particle effect
                            block.getWorld().spawnParticle(Particle.CLOUD, blockLoc, 20, 0.2, 0.2, 0.2);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    // ==================== LAUNCH BLOCKS (Left Click) ====================
    
    public static void launchBlocks(Player player) {
        UUID uuid = player.getUniqueId();
        List<FlyingBlock> blocks = flyingBlocks.get(uuid);
        
        if(blocks == null || blocks.isEmpty()) {
            player.sendMessage("§3🌀 No blocks to launch!");
            return;
        }
        
        player.sendMessage("§3💨 Launching all blocks!");
        player.playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 1.0f, 1.2f);
        
        // Get cursor direction
        Vector direction = player.getLocation().getDirection().normalize();
        
        for(FlyingBlock fb : blocks) {
            if(fb.entity != null && !fb.entity.isDead()) {
                // Launch block in cursor direction
                Vector launchVel = direction.clone().multiply(2.5);
                launchVel.setY(launchVel.getY() + 0.5);
                fb.entity.setVelocity(launchVel);
                
                // Add gravity back
                fb.entity.setGravity(true);
                
                // Schedule removal after impact
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(fb.entity != null && !fb.entity.isDead()) {
                            Location loc = fb.entity.getLocation();
                            
                            // Damage entities in impact area
                            loc.getWorld().getNearbyEntities(loc, 1.5, 1.5, 1.5).forEach(e -> {
                                if(e != player && e instanceof LivingEntity) {
                                    ((LivingEntity) e).damage(6, player);
                                    e.setVelocity(e.getVelocity().add(new Vector(0, 0.5, 0)));
                                    
                                    // Impact particles
                                    e.getWorld().spawnParticle(Particle.EXPLOSION, e.getLocation(), 5);
                                    e.getWorld().playSound(e.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
                                }
                            });
                            
                            // Restore block or drop item
                            Block targetBlock = loc.getBlock();
                            if(targetBlock.getType() == Material.AIR) {
                                targetBlock.setType(fb.blockType);
                                targetBlock.setBlockData(fb.blockData);
                            } else {
                                // Drop item
                                loc.getWorld().dropItem(loc, new ItemStack(fb.blockType));
                            }
                            
                            fb.entity.remove();
                        }
                    }
                }.runTaskLater(FruitsPlugin.getInstance(), 10L);
            }
        }
        
        // Clear blocks list
        blocks.clear();
        flyingBlocks.remove(uuid);
    }
    
    public static boolean hasActiveTornado(Player player) {
        return activeTornados.containsKey(player.getUniqueId());
    }
    
    public static boolean hasActiveBlockTornado(Player player) {
        return flyingBlocks.containsKey(player.getUniqueId()) && !flyingBlocks.get(player.getUniqueId()).isEmpty();
    }
    
    public static void cleanup() {
        for(Map.Entry<UUID, List<FlyingBlock>> entry : flyingBlocks.entrySet()) {
            for(FlyingBlock fb : entry.getValue()) {
                if(fb.entity != null) fb.entity.remove();
            }
        }
        activeTornados.clear();
        flyingBlocks.clear();
    }
    
    // ==================== HELPER CLASSES ====================
    
    private static class TornadoData {
        final Location center;
        final long expiryTime;
        
        TornadoData(Location center, long expiryTime) {
            this.center = center;
            this.expiryTime = expiryTime;
        }
    }
    
    private static class FlyingBlock {
        final Entity entity;
        final Location originalBlock;
        final Material blockType;
        final org.bukkit.block.data.BlockData blockData;
        double radius;
        int angle;
        double height;
        
        FlyingBlock(Entity entity, Location original, Material type, org.bukkit.block.data.BlockData data) {
            this.entity = entity;
            this.originalBlock = original;
            this.blockType = type;
            this.blockData = data;
            this.radius = 1;
            this.angle = 0;
            this.height = 0.5;
        }
    }
}
