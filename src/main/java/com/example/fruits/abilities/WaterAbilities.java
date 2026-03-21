package com.example.fruits.abilities;

import com.example.fruits.FruitsPlugin;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import java.util.*;

public class WaterAbilities {
    
    // Store active waves
    private static final Map<UUID, WaveData> activeWaves = new HashMap<>();
    
    // ==================== ABILITY 1: WATER GEYSER (Right Click) ====================
    
    public static void waterGeyser(Player player) {
        player.sendMessage("§b§l💧 WATER GEYSER!");
        player.sendMessage("§7Water erupts beneath all nearby entities!");
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.0f, 1.2f);
        
        List<Entity> targets = player.getWorld().getNearbyEntities(player.getLocation(), 15, 8, 15)
            .stream()
            .filter(e -> e != player && e instanceof LivingEntity)
            .toList();
        
        if(targets.isEmpty()) {
            player.sendMessage("§cNo targets nearby!");
            return;
        }
        
        for(Entity target : targets) {
            createWaterGeyser(player, target);
        }
        
        player.sendMessage("§a💦 " + targets.size() + " entities launched into the air!");
    }
    
    private static void createWaterGeyser(Player player, Entity target) {
        Location targetLoc = target.getLocation();
        
        Block waterBlock = targetLoc.clone().add(0, -0.5, 0).getBlock();
        waterBlock.setType(Material.WATER);
        
        target.getWorld().playSound(targetLoc, Sound.ENTITY_GENERIC_SPLASH, 1.5f, 1.0f);
        target.getWorld().playSound(targetLoc, Sound.BLOCK_WATER_AMBIENT, 1.0f, 1.5f);
        
        target.setVelocity(new Vector(0, 2.2, 0));
        
        // Animated water particles rising
        new BukkitRunnable() {
            int height = 0;
            
            @Override
            public void run() {
                if(height >= 20) {
                    if(waterBlock.getType() == Material.WATER) {
                        waterBlock.setType(Material.AIR);
                    }
                    this.cancel();
                    return;
                }
                
                // FIXED: WATER_SPLASH -> CLOUD + ITEM_SNOWBALL
                for(int i = 0; i < 360; i += 30) {
                    double rad = Math.toRadians(i + height * 15);
                    double x = Math.cos(rad) * 0.6;
                    double z = Math.sin(rad) * 0.6;
                    
                    Location particleLoc = targetLoc.clone().add(x, height * 0.2, z);
                    particleLoc.getWorld().spawnParticle(Particle.CLOUD, particleLoc, 5, 0.1, 0.1, 0.1);
                    particleLoc.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, particleLoc, 2, 0.05, 0.05, 0.05);
                }
                
                // FIXED: WATER_BUBBLE -> BUBBLE_POP
                for(int i = 0; i < 5; i++) {
                    double offsetX = (Math.random() - 0.5) * 0.5;
                    double offsetZ = (Math.random() - 0.5) * 0.5;
                    Location bubbleLoc = targetLoc.clone().add(offsetX, height * 0.2, offsetZ);
                    bubbleLoc.getWorld().spawnParticle(Particle.BUBBLE_POP, bubbleLoc, 1, 0, 0, 0);
                }
                
                height++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
        
        // Cartoon launch effect - spinning
        new BukkitRunnable() {
            int spin = 0;
            @Override
            public void run() {
                if(spin >= 15 || target.isDead() || !target.isValid()) {
                    this.cancel();
                    return;
                }
                
                target.setVelocity(target.getVelocity().add(new Vector(0, 0.1, 0)));
                
                for(int i = 0; i < 360; i += 20) {
                    double rad = Math.toRadians(i + spin * 30);
                    double x = Math.cos(rad) * 0.8;
                    double z = Math.sin(rad) * 0.8;
                    target.getWorld().spawnParticle(Particle.CLOUD, target.getLocation().add(x, 0.5, z), 1, 0, 0, 0);
                }
                
                spin++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
        
        // Splash effect on landing
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!target.isDead() && target.isValid()) {
                    Location landLoc = target.getLocation();
                    landLoc.getWorld().spawnParticle(Particle.CLOUD, landLoc, 50, 0.5, 0.2, 0.5);
                    landLoc.getWorld().playSound(landLoc, Sound.ENTITY_GENERIC_SPLASH, 1.0f, 0.8f);
                    
                    for(int i = 0; i < 360; i += 10) {
                        double rad = Math.toRadians(i);
                        double x = Math.cos(rad) * 1.2;
                        double z = Math.sin(rad) * 1.2;
                        landLoc.getWorld().spawnParticle(Particle.CLOUD, landLoc.clone().add(x, 0.1, z), 2, 0, 0, 0);
                    }
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 40L);
    }
    
    // ==================== ABILITY 2: TIDAL WAVE ====================
    
    public static void tidalWave(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(activeWaves.containsKey(uuid)) {
            player.sendMessage("§b🌊 You are already riding a wave!");
            return;
        }
        
        player.sendMessage("§b§l🌊 TIDAL WAVE!");
        player.sendMessage("§7You become a powerful wave crashing forward!");
        player.playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 1.0f, 0.6f);
        
        boolean wasInvisible = player.hasPotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY);
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(
            org.bukkit.potion.PotionEffectType.INVISIBILITY, 80, 1, false, false, false));
        
        WaveData wave = new WaveData(player.getUniqueId(), System.currentTimeMillis() + 4000, player.getLocation(), 
                                      player.getLocation().getDirection(), wasInvisible);
        activeWaves.put(uuid, wave);
        
        createWaveEffect(player, wave);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activeWaves.containsKey(uuid)) {
                    endWave(player, wave);
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 80L);
    }
    
    private static void createWaveEffect(Player player, WaveData wave) {
        new BukkitRunnable() {
            int tick = 0;
            Location startLoc = player.getLocation();
            Vector direction = wave.direction.clone().normalize();
            double speed = 1.2;
            
            @Override
            public void run() {
                if(!activeWaves.containsKey(player.getUniqueId()) || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                if(System.currentTimeMillis() > wave.expiryTime) {
                    this.cancel();
                    return;
                }
                
                tick++;
                
                double distance = tick * speed;
                Location newLoc = startLoc.clone().add(direction.clone().multiply(distance));
                newLoc.setY(startLoc.getY());
                player.teleport(newLoc);
                
                // Create massive wave particles
                for(int i = -3; i <= 3; i++) {
                    for(int j = -2; j <= 2; j++) {
                        Vector offset = new Vector(
                            direction.getZ() * i + direction.getX() * j,
                            0,
                            -direction.getX() * i + direction.getZ() * j
                        ).normalize();
                        
                        Location waveLoc = newLoc.clone().add(offset.multiply(1.5)).add(0, 1, 0);
                        waveLoc.getWorld().spawnParticle(Particle.CLOUD, waveLoc, 5, 0.3, 0.2, 0.3);
                        waveLoc.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, waveLoc, 2, 0.2, 0.1, 0.2);
                    }
                }
        
                // Wave front effect
                for(double angle = -Math.PI/2; angle <= Math.PI/2; angle += Math.PI/8) {
                    double xOffset = Math.sin(angle) * 2;
                    double zOffset = Math.cos(angle) * 2;
                    
                    Location frontLoc = newLoc.clone().add(
                        direction.getX() * 1.5 + direction.getZ() * xOffset,
                        0.8,
                        direction.getZ() * 1.5 - direction.getX() * xOffset
                    );
                    
                    frontLoc.getWorld().spawnParticle(Particle.CLOUD, frontLoc, 8, 0.2, 0.2, 0.2);
                    frontLoc.getWorld().spawnParticle(Particle.BUBBLE_POP, frontLoc, 3, 0.1, 0.1, 0.1);
                }
        
                // Damage and push enemies
                player.getWorld().getNearbyEntities(newLoc, 4, 2, 4).forEach(e -> {
                    if(e != player && e instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity) e;
                        
                        Vector knockback = e.getLocation().toVector().subtract(newLoc.toVector()).normalize();
                        knockback.setY(0.5);
                        living.setVelocity(knockback.multiply(1.8));
                        living.damage(5, player);
                        
                        living.getWorld().spawnParticle(Particle.CLOUD, living.getLocation(), 30, 0.5, 0.3, 0.5);
                        living.getWorld().playSound(living.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.0f, 1.2f);
                        living.getWorld().spawnParticle(Particle.BUBBLE_POP, living.getLocation(), 20, 0.3, 0.3, 0.3);
                    }
                });
                
                // Trail particles
                for(int i = 0; i < 5; i++) {
                    double backDist = -1.5 - i * 0.5;
                    Location backLoc = newLoc.clone().add(direction.clone().multiply(backDist));
                    backLoc.getWorld().spawnParticle(Particle.CLOUD, backLoc, 4, 0.3, 0.1, 0.3);
                }
                
                // Water spray
                for(int i = 0; i < 10; i++) {
                    double sprayX = (Math.random() - 0.5) * 1.5;
                    double sprayZ = (Math.random() - 0.5) * 1.5;
                    Location sprayLoc = newLoc.clone().add(sprayX, 1.2, sprayZ);
                    sprayLoc.getWorld().spawnParticle(Particle.CLOUD, sprayLoc, 2, 0, 0.1, 0);
                }
                
                if(tick % 5 == 0) {
                    player.getWorld().playSound(newLoc, Sound.ENTITY_GENERIC_SPLASH, 1.0f, 0.8f);
                }
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void endWave(Player player, WaveData wave) {
        UUID uuid = player.getUniqueId();
        activeWaves.remove(uuid);
        
        if(player.isOnline()) {
            if(!wave.wasInvisible) {
                player.removePotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY);
            }
            
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.5f, 0.6f);
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 80, 1, 0.5, 1);
            
            for(int i = 0; i < 360; i += 10) {
                double rad = Math.toRadians(i);
                double x = Math.cos(rad) * 2;
                double z = Math.sin(rad) * 2;
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(x, 0.2, z), 2, 0, 0, 0);
            }
            
            player.sendMessage("§b🌊 The tidal wave crashes to an end!");
        }
    }
    
    public static boolean isRidingWave(Player player) {
        return activeWaves.containsKey(player.getUniqueId());
    }
    
    public static void cleanup() {
        for(Map.Entry<UUID, WaveData> entry : activeWaves.entrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());
            if(p != null && p.isOnline()) {
                if(!entry.getValue().wasInvisible) {
                    p.removePotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY);
                }
            }
        }
        activeWaves.clear();
    }
    
    private static class WaveData {
        final UUID playerId;
        final long expiryTime;
        final Location startLoc;
        final Vector direction;
        final boolean wasInvisible;
        
        WaveData(UUID playerId, long expiryTime, Location startLoc, Vector direction, boolean wasInvisible) {
            this.playerId = playerId;
            this.expiryTime = expiryTime;
            this.startLoc = startLoc;
            this.direction = direction;
            this.wasInvisible = wasInvisible;
        }
    }
}
