package com.example.fruits.abilities;

import com.example.fruits.FruitsPlugin;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;

public class MeteorAbilities {
    
    // Store active meteor phases
    private static final Map<UUID, MeteorPhase> activePhases = new HashMap<>();
    
    // ==================== ABILITY 1: METEOR SHOWER (Right Click) ====================
    
    public static void meteorShower(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(activePhases.containsKey(uuid)) {
            player.sendMessage("§e☄️ You are already in Meteor Shower mode!");
            return;
        }
        
        player.sendMessage("§6§l☄️ METEOR SHOWER!");
        player.sendMessage("§7Meteors rain from the sky for 15 seconds!");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);
        
        MeteorPhase phase = new MeteorPhase(uuid, System.currentTimeMillis() + 15000);
        activePhases.put(uuid, phase);
        
        startMeteorShower(player, phase);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activePhases.containsKey(uuid)) {
                    activePhases.remove(uuid);
                    if(player.isOnline()) {
                        player.sendMessage("§e☄️ Meteor Shower has ended!");
                        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 0.8f);
                    }
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 300L);
    }
    
    private static void startMeteorShower(Player player, MeteorPhase phase) {
        new BukkitRunnable() {
            int meteorCount = 0;
            int tick = 0;
            
            @Override
            public void run() {
                if(!activePhases.containsKey(player.getUniqueId()) || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                if(System.currentTimeMillis() > phase.expiryTime) {
                    this.cancel();
                    return;
                }
                
                tick++;
                
                // Summon meteor every 20 ticks (1 second)
                if(tick >= 20 && meteorCount < 15) {
                    summonMeteor(player);
                    meteorCount++;
                    tick = 0;
                }
                
                // Meteor shower particles around player
                for(int i = 0; i < 360; i += 20) {
                    double rad = Math.toRadians(i + tick * 10);
                    double x = Math.cos(rad) * 3;
                    double z = Math.sin(rad) * 3;
                    player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(x, 2, z), 2, 0, 0.1, 0);
                    player.getWorld().spawnParticle(Particle.SMOKE, player.getLocation().add(x, 2, z), 1, 0, 0.1, 0);
                }
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void summonMeteor(Player player) {
        double angle = Math.random() * 2 * Math.PI;
        double distance = 8 + Math.random() * 7;
        double x = player.getLocation().getX() + Math.cos(angle) * distance;
        double z = player.getLocation().getZ() + Math.sin(angle) * distance;
        double y = player.getLocation().getY() + 10 + Math.random() * 5;
        
        Location meteorLoc = new Location(player.getWorld(), x, y, z);
        
        ArmorStand meteor = (ArmorStand) player.getWorld().spawnEntity(meteorLoc, EntityType.ARMOR_STAND);
        meteor.setVisible(false);
        meteor.setGravity(false);
        meteor.setInvulnerable(true);
        meteor.setMarker(true);
        meteor.setCustomName("§6§l☄️");
        meteor.setCustomNameVisible(true);
        
        new BukkitRunnable() {
            int fallTicks = 0;
            Location startLoc = meteorLoc.clone();
            
            @Override
            public void run() {
                if(meteor == null || meteor.isDead()) {
                    this.cancel();
                    return;
                }
                
                if(fallTicks >= 20) {
                    meteor.remove();
                    createMeteorImpact(startLoc, player);
                    this.cancel();
                    return;
                }
                
                double progress = fallTicks / 20.0;
                double newY = startLoc.getY() - (10 * progress);
                Location newLoc = startLoc.clone();
                newLoc.setY(newY);
                meteor.teleport(newLoc);
                
                for(int i = 0; i < 10; i++) {
                    double offsetX = (Math.random() - 0.5) * 0.5;
                    double offsetZ = (Math.random() - 0.5) * 0.5;
                    newLoc.getWorld().spawnParticle(Particle.FLAME, newLoc.clone().add(offsetX, -0.5, offsetZ), 1, 0, 0, 0);
                    newLoc.getWorld().spawnParticle(Particle.SMOKE, newLoc.clone().add(offsetX, -0.5, offsetZ), 1, 0, 0, 0);
                }
                
                fallTicks++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
        
        player.getWorld().playSound(meteorLoc, Sound.ENTITY_GHAST_SHOOT, 1.0f, 0.8f);
    }
    
    private static void createMeteorImpact(Location impactLoc, Player player) {
        impactLoc.getWorld().createExplosion(impactLoc, 2.5f, false, true);
        impactLoc.getWorld().playSound(impactLoc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.6f);
        
        for(int i = 0; i < 360; i += 10) {
            double rad = Math.toRadians(i);
            double x = Math.cos(rad) * 2;
            double z = Math.sin(rad) * 2;
            impactLoc.getWorld().spawnParticle(Particle.FLAME, impactLoc.clone().add(x, 0.5, z), 2, 0, 0.1, 0);
            impactLoc.getWorld().spawnParticle(Particle.SMOKE, impactLoc.clone().add(x, 0.5, z), 3, 0, 0.1, 0);
        }
        
        impactLoc.getWorld().getNearbyEntities(impactLoc, 4, 3, 4).forEach(e -> {
            if(e != player && e instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) e;
                living.damage(10, player);
                living.setVelocity(new Vector(0, 1, 0));
                living.setFireTicks(60);
                living.getWorld().spawnParticle(Particle.FLAME, living.getLocation(), 30, 0.5, 0.5, 0.5);
            }
        });
        
        Location groundLoc = impactLoc.clone().add(0, -1, 0);
        if(groundLoc.getBlock().getType() != Material.AIR) {
            Material originalType = groundLoc.getBlock().getType();
            groundLoc.getBlock().setType(Material.OBSIDIAN);
            
            new BukkitRunnable() {
                @Override
                public void run() {
                    groundLoc.getBlock().setType(originalType);
                }
            }.runTaskLater(FruitsPlugin.getInstance(), 100L);
        }
    }
    
    // ==================== ABILITY 2: METEOR STRIKE (Crouch + Right Click) ====================
    
    public static void meteorStrike(Player player) {
        player.sendMessage("§6§l🔥 METEOR STRIKE!");
        player.sendMessage("§7A massive meteor crashes down at your target!");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);
        
        Location targetLoc = player.getTargetBlock(null, 30).getLocation();
        if(targetLoc == null) {
            targetLoc = player.getLocation().add(0, 5, 0);
        }
        
        // Create massive meteor effect
        createMassiveMeteor(player, targetLoc);
    }
    
    private static void createMassiveMeteor(Player player, Location targetLoc) {
        // Summon meteor high in the sky
        Location meteorStart = targetLoc.clone().add(0, 20, 0);
        
        ArmorStand meteor = (ArmorStand) player.getWorld().spawnEntity(meteorStart, EntityType.ARMOR_STAND);
        meteor.setVisible(false);
        meteor.setGravity(false);
        meteor.setInvulnerable(true);
        meteor.setMarker(true);
        meteor.setCustomName("§c§l🔥 METEOR");
        meteor.setCustomNameVisible(true);
        
        // Scale up the meteor (using particles to make it look big)
        new BukkitRunnable() {
            int fallTicks = 0;
            Location startLoc = meteorStart.clone();
            
            @Override
            public void run() {
                if(meteor == null || meteor.isDead()) {
                    this.cancel();
                    return;
                }
                
                if(fallTicks >= 25) {
                    meteor.remove();
                    createMassiveImpact(targetLoc, player);
                    this.cancel();
                    return;
                }
                
                double progress = fallTicks / 25.0;
                double newY = startLoc.getY() - (20 * progress);
                Location newLoc = startLoc.clone();
                newLoc.setY(newY);
                meteor.teleport(newLoc);
                
                // Massive trail particles
                for(int radius = 0; radius <= 2; radius++) {
                    for(int i = 0; i < 360; i += 15) {
                        double rad = Math.toRadians(i + fallTicks * 15);
                        double x = Math.cos(rad) * (1.5 + radius);
                        double z = Math.sin(rad) * (1.5 + radius);
                        newLoc.getWorld().spawnParticle(Particle.FLAME, newLoc.clone().add(x, -0.5, z), 2, 0, 0, 0);
                        newLoc.getWorld().spawnParticle(Particle.SMOKE, newLoc.clone().add(x, -0.5, z), 3, 0, 0, 0);
                        newLoc.getWorld().spawnParticle(Particle.LAVA, newLoc.clone().add(x, -0.5, z), 1, 0, 0, 0);
                    }
                }
                
                // Shockwave effect
                if(fallTicks == 20) {
                    newLoc.getWorld().playSound(newLoc, Sound.ENTITY_GHAST_SHOOT, 2.0f, 0.5f);
                }
                
                fallTicks++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
        
        player.getWorld().playSound(meteorStart, Sound.ENTITY_GHAST_SHOOT, 2.0f, 0.5f);
    }
    
    private static void createMassiveImpact(Location impactLoc, Player player) {
        // Massive explosion
        impactLoc.getWorld().createExplosion(impactLoc, 5.0f, true, true);
        impactLoc.getWorld().playSound(impactLoc, Sound.ENTITY_GENERIC_EXPLODE, 3.0f, 0.5f);
        
        // Firework effect
        for(int i = 0; i < 5; i++) {
            impactLoc.getWorld().spawnParticle(Particle.FIREWORK, impactLoc, 50, 1, 1, 1);
        }
        
        // Fire rings
        for(int radius = 1; radius <= 5; radius++) {
            for(int i = 0; i < 360; i += 10) {
                double rad = Math.toRadians(i);
                double x = Math.cos(rad) * radius;
                double z = Math.sin(rad) * radius;
                impactLoc.getWorld().spawnParticle(Particle.FLAME, impactLoc.clone().add(x, 0.2, z), 2, 0, 0, 0);
                impactLoc.getWorld().spawnParticle(Particle.SMOKE, impactLoc.clone().add(x, 0.5, z), 1, 0, 0, 0);
            }
        }
        
        // Damage all entities in large radius
        impactLoc.getWorld().getNearbyEntities(impactLoc, 8, 5, 8).forEach(e -> {
            if(e != player && e instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) e;
                double distance = living.getLocation().distance(impactLoc);
                double damage = 20 * (1 - distance / 8);
                living.damage(Math.max(5, damage), player);
                living.setVelocity(new Vector(0, 1.2, 0));
                living.setFireTicks(100);
                living.getWorld().spawnParticle(Particle.FLAME, living.getLocation(), 50, 0.5, 0.5, 0.5);
            }
        });
        
        // Crater effect
        for(int x = -2; x <= 2; x++) {
            for(int z = -2; z <= 2; z++) {
                Location craterLoc = impactLoc.clone().add(x, -1, z);
                if(craterLoc.getBlock().getType() != Material.AIR) {
                    craterLoc.getBlock().setType(Material.MAGMA_BLOCK);
                }
            }
        }
        
        player.sendMessage("§c§l💥 MASSIVE METEOR IMPACT!");
    }
    
    public static boolean isInMeteorPhase(Player player) {
        return activePhases.containsKey(player.getUniqueId());
    }
    
    public static void cleanup() {
        activePhases.clear();
    }
    
    private static class MeteorPhase {
        final UUID playerId;
        final long expiryTime;
        
        MeteorPhase(UUID playerId, long expiryTime) {
            this.playerId = playerId;
            this.expiryTime = expiryTime;
        }
    }
}
