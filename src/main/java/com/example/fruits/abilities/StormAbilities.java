package com.example.fruits.abilities;

import com.example.fruits.FruitsPlugin;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;

public class StormAbilities {
    
    // Store active storm monsters
    private static final Map<UUID, StormData> activeStorms = new HashMap<>();
    
    // ==================== ABILITY 1: WIND MONSTER (Right Click) ====================
    
    public static void windMonster(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(activeStorms.containsKey(uuid)) {
            player.sendMessage("§b🌬️ You are already in monster form!");
            return;
        }
        
        player.sendMessage("§b§l🌬️ WIND MONSTER TRANSFORMATION!");
        player.sendMessage("§7You rise into the air with the power of wind!");
        player.playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 1.0f, 0.8f);
        
        StormData storm = new StormData(player.getUniqueId(), System.currentTimeMillis() + 10000, "wind");
        activeStorms.put(uuid, storm);
        
        Location targetLoc = player.getLocation().clone().add(0, 6, 0);
        animateRise(player, targetLoc);
        
        startWindMonsterEffects(player, storm);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activeStorms.containsKey(uuid)) {
                    endStormForm(player);
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 200L);
    }
    
    private static void startWindMonsterEffects(Player player, StormData storm) {
        new BukkitRunnable() {
            int attackCooldown = 0;
            int tick = 0;
            
            @Override
            public void run() {
                if(!activeStorms.containsKey(player.getUniqueId()) || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                if(System.currentTimeMillis() > storm.expiryTime) {
                    this.cancel();
                    return;
                }
                
                tick++;
                
                Location loc = player.getLocation();
                if(loc.getY() < player.getWorld().getHighestBlockYAt(loc) + 5) {
                    player.setVelocity(new Vector(0, 0.3, 0));
                }
                
                // Cloud particles from hands and back
                for(int i = 0; i < 360; i += 30) {
                    double rad = Math.toRadians(i + tick * 10);
                    double x = Math.cos(rad) * 1.2;
                    double z = Math.sin(rad) * 1.2;
                    
                    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(x, 1.2, z), 2, 0.1, 0.1, 0.1);
                    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(x, 0.8, z), 2, 0.1, 0.1, 0.1);
                    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(-x, 1, -z), 2, 0.1, 0.1, 0.1);
                }
                
                // Wind swirl effect
                for(int r = 0; r <= 3; r++) {
                    double rad = Math.toRadians(tick * 15 + r * 60);
                    double x = Math.cos(rad) * (2 + r);
                    double z = Math.sin(rad) * (2 + r);
                    player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().add(x, 1, z), 1, 0, 0, 0);
                }
                
                attackCooldown++;
                if(attackCooldown >= 30) {
                    performWindSlam(player);
                    attackCooldown = 0;
                }
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void performWindSlam(Player player) {
        Location center = player.getLocation();
        
        createGiantFist(player, center, Color.fromRGB(200, 230, 255), "wind");
        
        for(int x = -2; x <= 2; x++) {
            for(int z = -2; z <= 2; z++) {
                Location checkLoc = center.clone().add(x, -3, z);
                
                checkLoc.getWorld().getNearbyEntities(checkLoc, 1.5, 2, 1.5).forEach(e -> {
                    if(e != player && e instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity) e;
                        living.damage(6, player);
                        living.setVelocity(new Vector(0, -0.5, 0));
                        
                        living.getWorld().spawnParticle(Particle.CLOUD, living.getLocation(), 30, 0.5, 0.5, 0.5);
                        living.getWorld().playSound(living.getLocation(), Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.0f, 0.8f);
                    }
                });
                
                // FIXED: BLOCK_CRACK -> BLOCK with block data
                if(checkLoc.getBlock().getType() != Material.AIR) {
                    checkLoc.getWorld().spawnParticle(Particle.BLOCK, checkLoc, 20, 0.3, 0.1, 0.3, 
                        checkLoc.getBlock().getBlockData());
                }
            }
        }
        
        for(int i = 0; i < 360; i += 10) {
            double rad = Math.toRadians(i);
            double x = Math.cos(rad) * 3;
            double z = Math.sin(rad) * 3;
            player.getWorld().spawnParticle(Particle.CLOUD, center.clone().add(x, -2, z), 1, 0, 0, 0);
        }
        
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.6f);
    }
    
    // ==================== ABILITY 2: STORM MONSTER ====================
    
    public static void stormMonster(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(activeStorms.containsKey(uuid)) {
            player.sendMessage("§b🌬️ You are already in monster form!");
            return;
        }
        
        player.sendMessage("§9§l⚡ STORM MONSTER TRANSFORMATION!");
        player.sendMessage("§7You harness the power of lightning and storms!");
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 0.5f);
        
        StormData storm = new StormData(player.getUniqueId(), System.currentTimeMillis() + 10000, "storm");
        activeStorms.put(uuid, storm);
        
        Location targetLoc = player.getLocation().clone().add(0, 6, 0);
        animateRise(player, targetLoc);
        
        startStormMonsterEffects(player, storm);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activeStorms.containsKey(uuid)) {
                    endStormForm(player);
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 200L);
    }
    
    private static void startStormMonsterEffects(Player player, StormData storm) {
        new BukkitRunnable() {
            int attackCooldown = 0;
            int tick = 0;
            
            @Override
            public void run() {
                if(!activeStorms.containsKey(player.getUniqueId()) || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                if(System.currentTimeMillis() > storm.expiryTime) {
                    this.cancel();
                    return;
                }
                
                tick++;
                
                Location loc = player.getLocation();
                if(loc.getY() < player.getWorld().getHighestBlockYAt(loc) + 5) {
                    player.setVelocity(new Vector(0, 0.3, 0));
                }
                
                // Storm particles - electric and blue
                for(int i = 0; i < 360; i += 20) {
                    double rad = Math.toRadians(i + tick * 15);
                    double x = Math.cos(rad) * 1.5;
                    double z = Math.sin(rad) * 1.5;
                    
                    player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().add(x, 1.2, z), 2, 0.1, 0.1, 0.1);
                    player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().add(x, 0.6, z), 2, 0.1, 0.1, 0.1);
                    player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(x, 1, z), 1, 0, 0, 0);
                }
                
                // Lightning aura
                for(int i = 0; i < 5; i++) {
                    double angle = Math.toRadians(tick * 20 + i * 72);
                    double x = Math.cos(angle) * 2;
                    double z = Math.sin(angle) * 2;
                    player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().add(x, 0.8, z), 3, 0, 0.2, 0);
                }
                
                // FIXED: SPELL_WITCH -> ENCHANT (magical particles)
                for(int i = 0; i < 360; i += 30) {
                    double rad = Math.toRadians(i);
                    double x = Math.cos(rad) * 1.8;
                    double z = Math.sin(rad) * 1.8;
                    player.getWorld().spawnParticle(Particle.ENCHANT, player.getLocation().add(x, 0.5, z), 1, 0.1, 0.1, 0.1);
                }
                
                attackCooldown++;
                if(attackCooldown >= 30) {
                    performStormSlam(player);
                    attackCooldown = 0;
                }
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void performStormSlam(Player player) {
        Location center = player.getLocation();
        
        createGiantFist(player, center, Color.fromRGB(0, 100, 255), "storm");
        
        for(int x = -2; x <= 2; x++) {
            for(int z = -2; z <= 2; z++) {
                Location checkLoc = center.clone().add(x, -3, z);
                
                if(Math.random() < 0.3) {
                    checkLoc.getWorld().strikeLightningEffect(checkLoc);
                }
                
                checkLoc.getWorld().getNearbyEntities(checkLoc, 1.5, 2, 1.5).forEach(e -> {
                    if(e != player && e instanceof LivingEntity) {
                        LivingEntity living = (LivingEntity) e;
                        living.damage(8, player);
                        living.setVelocity(new Vector(0, -0.8, 0));
                        
                        living.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, living.getLocation(), 40, 0.5, 0.5, 0.5);
                        living.getWorld().playSound(living.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.8f);
                        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
                    }
                });
                
                if(checkLoc.getBlock().getType() != Material.AIR) {
                    checkLoc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, checkLoc, 10, 0.2, 0.1, 0.2);
                }
            }
        }
        
        for(int i = 0; i < 360; i += 10) {
            double rad = Math.toRadians(i);
            double x = Math.cos(rad) * 3.5;
            double z = Math.sin(rad) * 3.5;
            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, center.clone().add(x, -2, z), 2, 0, 0, 0);
        }
        
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 0.5f);
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.7f);
    }
    
    // ==================== HELPER METHODS ====================
    
    private static void createGiantFist(Player player, Location center, Color color, String type) {
        new BukkitRunnable() {
            int frame = 0;
            List<Location> fistLocations = new ArrayList<>();
            
            @Override
            public void run() {
                if(frame >= 15) {
                    for(Location loc : fistLocations) {
                        loc.getWorld().spawnParticle(Particle.CLOUD, loc, 5, 0.1, 0.1, 0.1);
                    }
                    this.cancel();
                    return;
                }
                
                fistLocations.clear();
                double progress = frame / 15.0;
                double yOffset = 3 - (progress * 5);
                
                for(int x = -1; x <= 1; x++) {
                    for(int z = -1; z <= 1; z++) {
                        for(int y = -1; y <= 1; y++) {
                            if(Math.abs(x) + Math.abs(z) + Math.abs(y) <= 2) {
                                Location fistLoc = center.clone().add(x * 1.2, yOffset + y, z * 1.2);
                                fistLocations.add(fistLoc);
                                
                                if(type.equals("wind")) {
                                    fistLoc.getWorld().spawnParticle(Particle.CLOUD, fistLoc, 3, 0.1, 0.1, 0.1);
                                    fistLoc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, fistLoc, 2, 0.05, 0.05, 0.05);
                                } else {
                                    fistLoc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, fistLoc, 5, 0.1, 0.1, 0.1);
                                    fistLoc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, fistLoc, 2, 0.05, 0.05, 0.05);
                                }
                            }
                        }
                    }
                }
                
                if(frame == 10) {
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);
                    for(int i = 0; i < 360; i += 15) {
                        double rad = Math.toRadians(i);
                        double x = Math.cos(rad) * 2.5;
                        double z = Math.sin(rad) * 2.5;
                        player.getWorld().spawnParticle(Particle.CLOUD, center.clone().add(x, -2, z), 2, 0, 0, 0);
                    }
                }
                
                frame++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void animateRise(Player player, Location targetLoc) {
        new BukkitRunnable() {
            int riseTicks = 0;
            Location startLoc = player.getLocation();
            
            @Override
            public void run() {
                if(riseTicks >= 12) {
                    this.cancel();
                    return;
                }
                
                double progress = riseTicks / 12.0;
                double x = startLoc.getX() + (targetLoc.getX() - startLoc.getX()) * progress;
                double y = startLoc.getY() + (targetLoc.getY() - startLoc.getY()) * progress;
                double z = startLoc.getZ() + (targetLoc.getZ() - startLoc.getZ()) * progress;
                
                player.teleport(new Location(player.getWorld(), x, y, z));
                player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 20, 0.5, 0.1, 0.5);
                
                riseTicks++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void endStormForm(Player player) {
        UUID uuid = player.getUniqueId();
        StormData storm = activeStorms.remove(uuid);
        
        if(storm != null && player.isOnline()) {
            player.sendMessage("§b🌬️ Monster form has ended!");
            player.playSound(player.getLocation(), Sound.ENTITY_BREEZE_DEATH, 1.0f, 0.8f);
            
            new BukkitRunnable() {
                int descendTicks = 0;
                Location startLoc = player.getLocation();
                Location targetLoc = player.getLocation().clone().subtract(0, 6, 0);
                
                @Override
                public void run() {
                    if(descendTicks >= 12) {
                        this.cancel();
                        return;
                    }
                    
                    double progress = descendTicks / 12.0;
                    double x = startLoc.getX() + (targetLoc.getX() - startLoc.getX()) * progress;
                    double y = startLoc.getY() + (targetLoc.getY() - startLoc.getY()) * progress;
                    double z = startLoc.getZ() + (targetLoc.getZ() - startLoc.getZ()) * progress;
                    
                    player.teleport(new Location(player.getWorld(), x, y, z));
                    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 15, 0.3, 0.1, 0.3);
                    
                    descendTicks++;
                }
            }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
        }
    }
    
    public static boolean isInStormForm(Player player) {
        return activeStorms.containsKey(player.getUniqueId());
    }
    
    public static void cleanup() {
        activeStorms.clear();
    }
    
    private static class StormData {
        final UUID playerId;
        final long expiryTime;
        final String type;
        
        StormData(UUID playerId, long expiryTime, String type) {
            this.playerId = playerId;
            this.expiryTime = expiryTime;
            this.type = type;
        }
    }
}
