package com.example.fruits.abilities;

import com.example.fruits.FruitsPlugin;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;

public class IceAbilities {
    
    // Store active ice phases
    private static final Map<UUID, IcePhase> activePhases = new HashMap<>();
    private static final Map<UUID, Long> shardCooldowns = new HashMap<>();
    
    // ==================== ABILITY 1: ICE SHARD PHASE (Right Click) ====================
    
    public static void iceShardPhase(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(activePhases.containsKey(uuid)) {
            player.sendMessage("§b❄️ You are already in Ice Shard Phase!");
            return;
        }
        
        player.sendMessage("§b§l❄️ ICE SHARD PHASE!");
        player.sendMessage("§7Left-click to launch ice shards for 15 seconds!");
        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.2f);
        
        IcePhase phase = new IcePhase(uuid, System.currentTimeMillis() + 15000);
        activePhases.put(uuid, phase);
        
        startIceAura(player, phase);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activePhases.containsKey(uuid)) {
                    activePhases.remove(uuid);
                    if(player.isOnline()) {
                        player.sendMessage("§b❄️ Ice Shard Phase has ended!");
                        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 0.6f);
                    }
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 300L);
    }
    
    private static void startIceAura(Player player, IcePhase phase) {
        new BukkitRunnable() {
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
                
                // Ice aura particles
                for(int i = 0; i < 360; i += 15) {
                    double rad = Math.toRadians(i + tick * 8);
                    double x = Math.cos(rad) * 1.5;
                    double z = Math.sin(rad) * 1.5;
                    player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation().add(x, 0.5, z), 1, 0, 0, 0);
                    player.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, player.getLocation().add(x, 1, z), 1, 0, 0, 0);
                }
                
                // Floating ice crystals
                for(int i = 0; i < 5; i++) {
                    double angle = Math.toRadians(tick * 15 + i * 72);
                    double x = Math.cos(angle) * 1.2;
                    double z = Math.sin(angle) * 1.2;
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(x, 1.2, z), 1, 0, 0.1, 0);
                }
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    // ==================== ICE SHARD LAUNCH (Left Click during Phase) ====================
    
    public static void launchIceShard(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(!activePhases.containsKey(uuid)) {
            return;
        }
        
        IcePhase phase = activePhases.get(uuid);
        if(System.currentTimeMillis() > phase.expiryTime) {
            activePhases.remove(uuid);
            return;
        }
        
        if(shardCooldowns.containsKey(uuid) && System.currentTimeMillis() < shardCooldowns.get(uuid)) {
            return;
        }
        
        createIceShard(player);
        shardCooldowns.put(uuid, System.currentTimeMillis() + 500);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                shardCooldowns.remove(uuid);
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 10L);
    }
    
    private static void createIceShard(Player player) {
        Location eyeLoc = player.getEyeLocation();
        Vector direction = player.getLocation().getDirection().normalize();
        
        Snowball shard = player.getWorld().spawn(eyeLoc.add(direction.clone().multiply(1)), Snowball.class);
        shard.setShooter(player);
        shard.setVelocity(direction.multiply(2.5));
        
        new BukkitRunnable() {
            int trailTicks = 0;
            @Override
            public void run() {
                if(shard.isDead() || !shard.isValid()) {
                    this.cancel();
                    return;
                }
                
                if(trailTicks >= 40) {
                    this.cancel();
                    return;
                }
                
                Location loc = shard.getLocation();
                loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 5, 0.1, 0.1, 0.1);
                loc.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, loc, 2, 0.05, 0.05, 0.05);
                loc.getWorld().playSound(loc, Sound.BLOCK_GLASS_STEP, 0.5f, 1.5f);
                
                // Hit detection
                loc.getWorld().getNearbyEntities(loc, 0.5, 0.5, 0.5).forEach(e -> {
                    if(e != player && e instanceof LivingEntity && trailTicks > 2) {
                        LivingEntity living = (LivingEntity) e;
                        living.damage(6, player);
                        living.setVelocity(new Vector(0, 0.3, 0));
                        living.addPotionEffect(new org.bukkit.potion.PotionEffect(
                            org.bukkit.potion.PotionEffectType.SLOWNESS, 60, 1));
                        
                        living.getWorld().spawnParticle(Particle.SNOWFLAKE, living.getLocation(), 30, 0.5, 0.5, 0.5);
                        living.getWorld().playSound(living.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.2f);
                        shard.remove();
                        this.cancel();
                    }
                });
                
                trailTicks++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
        
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 0.8f, 1.5f);
    }
    
    // ==================== ABILITY 2: EXPLOSIVE ICE SHARD (Crouch + Right Click) ====================
    
    public static void explosiveIcePhase(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(activePhases.containsKey(uuid)) {
            player.sendMessage("§b❄️ You are already in Explosive Ice Phase!");
            return;
        }
        
        player.sendMessage("§b§l💥 EXPLOSIVE ICE SHARD PHASE!");
        player.sendMessage("§7Left-click to launch explosive ice shards for 15 seconds!");
        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 0.8f);
        
        IcePhase phase = new IcePhase(uuid, System.currentTimeMillis() + 15000);
        activePhases.put(uuid, phase);
        
        startExplosiveIceAura(player, phase);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activePhases.containsKey(uuid)) {
                    activePhases.remove(uuid);
                    if(player.isOnline()) {
                        player.sendMessage("§b❄️ Explosive Ice Phase has ended!");
                        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 0.6f);
                    }
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 300L);
    }
    
    private static void startExplosiveIceAura(Player player, IcePhase phase) {
        new BukkitRunnable() {
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
                
                // Red-tinged ice aura (explosive look)
                for(int i = 0; i < 360; i += 15) {
                    double rad = Math.toRadians(i + tick * 8);
                    double x = Math.cos(rad) * 1.8;
                    double z = Math.sin(rad) * 1.8;
                    player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation().add(x, 0.5, z), 2, 0, 0, 0);
                    player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(x, 1, z), 1, 0, 0, 0);
                }
                
                // Warning particles
                for(int i = 0; i < 3; i++) {
                    double angle = Math.toRadians(tick * 20 + i * 120);
                    double x = Math.cos(angle) * 1.5;
                    double z = Math.sin(angle) * 1.5;
                    player.getWorld().spawnParticle(Particle.CRIT, player.getLocation().add(x, 1, z), 1, 0, 0.1, 0);
                }
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    // ==================== EXPLOSIVE ICE SHARD LAUNCH ====================
    
    public static void launchExplosiveIceShard(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(!activePhases.containsKey(uuid)) {
            return;
        }
        
        IcePhase phase = activePhases.get(uuid);
        if(System.currentTimeMillis() > phase.expiryTime) {
            activePhases.remove(uuid);
            return;
        }
        
        if(shardCooldowns.containsKey(uuid) && System.currentTimeMillis() < shardCooldowns.get(uuid)) {
            return;
        }
        
        createExplosiveIceShard(player);
        shardCooldowns.put(uuid, System.currentTimeMillis() + 800);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                shardCooldowns.remove(uuid);
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 16L);
    }
    
    private static void createExplosiveIceShard(Player player) {
        Location eyeLoc = player.getEyeLocation();
        Vector direction = player.getLocation().getDirection().normalize();
        
        Snowball shard = player.getWorld().spawn(eyeLoc.add(direction.clone().multiply(1)), Snowball.class);
        shard.setShooter(player);
        shard.setVelocity(direction.multiply(2.0));
        
        new BukkitRunnable() {
            int trailTicks = 0;
            @Override
            public void run() {
                if(shard.isDead() || !shard.isValid()) {
                    this.cancel();
                    return;
                }
                
                if(trailTicks >= 40) {
                    createExplosion(shard.getLocation(), player);
                    shard.remove();
                    this.cancel();
                    return;
                }
                
                Location loc = shard.getLocation();
                loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 5, 0.1, 0.1, 0.1);
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 2, 0.05, 0.05, 0.05);
                loc.getWorld().spawnParticle(Particle.CRIT, loc, 1, 0, 0, 0);
                loc.getWorld().playSound(loc, Sound.BLOCK_GLASS_STEP, 0.5f, 1.2f);
                
                // Hit detection - explode on impact
                boolean hit = loc.getWorld().getNearbyEntities(loc, 0.8, 0.8, 0.8).stream()
                    .anyMatch(e -> e != player && e instanceof LivingEntity);
                
                if(hit && trailTicks > 2) {
                    createExplosion(loc, player);
                    shard.remove();
                    this.cancel();
                }
                
                trailTicks++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
        
        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 0.8f, 0.8f);
    }
    
    private static void createExplosion(Location loc, Player player) {
        // Ice explosion effect
        loc.getWorld().createExplosion(loc, 2.0f, false, false);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 1.0f);
        
        // Ice shrapnel particles
        for(int i = 0; i < 360; i += 15) {
            double rad = Math.toRadians(i);
            double x = Math.cos(rad) * 1.5;
            double z = Math.sin(rad) * 1.5;
            loc.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, loc.clone().add(x, 0.5, z), 2, 0, 0, 0);
            loc.getWorld().spawnParticle(Particle.SNOWFLAKE, loc.clone().add(x, 0.5, z), 3, 0, 0, 0);
        }
        
        // Damage and freeze nearby entities
        loc.getWorld().getNearbyEntities(loc, 3, 2, 3).forEach(e -> {
            if(e != player && e instanceof LivingEntity) {
                LivingEntity living = (LivingEntity) e;
                living.damage(8, player);
                living.setVelocity(new Vector(0, 0.5, 0));
                living.addPotionEffect(new org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.SLOWNESS, 100, 2));
                
                living.getWorld().spawnParticle(Particle.SNOWFLAKE, living.getLocation(), 40, 0.5, 0.5, 0.5);
                living.getWorld().playSound(living.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
            }
        });
        
        player.sendMessage("§b💥 Ice shard exploded!");
    }
    
    public static boolean isInIcePhase(Player player) {
        return activePhases.containsKey(player.getUniqueId());
    }
    
    public static void cleanup() {
        activePhases.clear();
        shardCooldowns.clear();
    }
    
    private static class IcePhase {
        final UUID playerId;
        final long expiryTime;
        
        IcePhase(UUID playerId, long expiryTime) {
            this.playerId = playerId;
            this.expiryTime = expiryTime;
        }
    }
    }
