package com.example.fruits.abilities;

import com.example.fruits.FruitsPlugin;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;

public class VampireAbilities {
    
    // Store active vampire phases
    private static final Map<UUID, VampirePhase> activePhases = new HashMap<>();
    private static final Map<UUID, BatRide> activeBatRides = new HashMap<>();
    private static final Map<UUID, Long> biteCooldowns = new HashMap<>();
    
    // ==================== ABILITY 1: BLOODLUST PHASE (Right Click) ====================
    
    public static void bloodlustPhase(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(activePhases.containsKey(uuid)) {
            player.sendMessage("§c🩸 You are already in Bloodlust phase!");
            return;
        }
        
        player.sendMessage("§c§l🩸 BLOODLUST PHASE ACTIVATED!");
        player.sendMessage("§7For §e15 seconds§7, every 3 hits heals you by §c1 heart§7!");
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);
        
        createBloodlustEffect(player);
        
        VampirePhase phase = new VampirePhase(player.getUniqueId(), System.currentTimeMillis() + 15000, 0);
        activePhases.put(uuid, phase);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activePhases.containsKey(uuid)) {
                    activePhases.remove(uuid);
                    if(player.isOnline()) {
                        player.sendMessage("§c🩸 Bloodlust phase has ended!");
                        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 0.8f);
                    }
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 300L);
    }
    
    private static void createBloodlustEffect(Player player) {
        new BukkitRunnable() {
            int duration = 0;
            @Override
            public void run() {
                if(duration >= 300 || !activePhases.containsKey(player.getUniqueId())) {
                    this.cancel();
                    return;
                }
                
                if(!player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                // FIXED: REDSTONE -> END_ROD (golden sparkles)
                for(int i = 0; i < 360; i += 20) {
                    double rad = Math.toRadians(i + duration * 5);
                    double x = Math.cos(rad) * 1.5;
                    double z = Math.sin(rad) * 1.5;
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(x, 0.5, z), 1, 0, 0, 0);
                }
                
                // FIXED: SPELL_MOB -> DRAGON_BREATH (magical purple)
                for(int i = 0; i < 3; i++) {
                    double angle = Math.toRadians(duration * 10 + i * 120);
                    double x = Math.cos(angle) * 1.2;
                    double z = Math.sin(angle) * 1.2;
                    player.getWorld().spawnParticle(Particle.DRAGON_BREATH, player.getLocation().add(x, 1, z), 1, 0, 0.1, 0);
                }
                
                duration++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    public static void handleHit(Player player, LivingEntity target) {
        UUID uuid = player.getUniqueId();
        VampirePhase phase = activePhases.get(uuid);
        
        if(phase == null) return;
        if(System.currentTimeMillis() > phase.expiryTime) {
            activePhases.remove(uuid);
            return;
        }
        
        phase.hitCount++;
        
        if(phase.hitCount >= 3) {
            phase.hitCount = 0;
            
            double newHealth = Math.min(player.getHealth() + 2, player.getMaxHealth());
            player.setHealth(newHealth);
            
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
            player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 15, 0.5, 0.5, 0.5);
            // FIXED: REDSTONE -> END_ROD
            player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 30, 0.5, 0.5, 0.5);
            
            player.sendMessage("§c❤️ Bloodlust healed you for §c1 heart§c!");
            
            // FIXED: REDSTONE -> END_ROD
            target.getWorld().spawnParticle(Particle.END_ROD, target.getLocation(), 20, 0.3, 0.3, 0.3);
            target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 0.8f);
        }
        
        // FIXED: REDSTONE -> END_ROD
        target.getWorld().spawnParticle(Particle.END_ROD, target.getLocation(), 10, 0.2, 0.2, 0.2);
    }
    
    // ==================== ABILITY 2: BAT RIDE ====================
    
    public static void batRide(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(activeBatRides.containsKey(uuid)) {
            player.sendMessage("§c🦇 You are already riding a bat!");
            return;
        }
        
        player.sendMessage("§c§l🦇 BAT RIDE SUMMONED!");
        player.sendMessage("§7Use §eW/A/S/D §7to control the bat!");
        player.sendMessage("§7Left-click to perform a §cBlood Bite§7 attack!");
        player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.0f);
        
        Bat bat = (Bat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
        bat.setAI(false);
        bat.setInvulnerable(true);
        bat.setSilent(true);
        bat.addPassenger(player);
        
        BatRide ride = new BatRide(bat, System.currentTimeMillis() + 20000);
        activeBatRides.put(uuid, ride);
        
        BukkitRunnable controlTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(!activeBatRides.containsKey(uuid) || bat.isDead() || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                BatRide currentRide = activeBatRides.get(uuid);
                if(System.currentTimeMillis() > currentRide.expiryTime) {
                    endBatRide(player, bat);
                    this.cancel();
                    return;
                }
                
                Vector direction = new Vector(0, 0, 0);
                boolean moving = false;
                
                if(player.isSneaking()) {
                    direction.setY(-0.3);
                    moving = true;
                } else if(player.isSprinting()) {
                    direction.setY(0.3);
                    moving = true;
                }
                
                if(player.isOnline()) {
                    Location eyeLoc = player.getEyeLocation();
                    Vector lookDir = eyeLoc.getDirection().normalize();
                    
                    if(player.getVelocity().length() > 0.1) {
                        direction.add(lookDir.multiply(0.5));
                        moving = true;
                    }
                }
                
                if(moving) {
                    bat.setVelocity(direction);
                }
                
                bat.getWorld().spawnParticle(Particle.CLOUD, bat.getLocation(), 5, 0.3, 0.1, 0.3);
                bat.teleport(player.getLocation().add(0, -0.5, 0));
            }
        };
        
        ride.controlTaskId = controlTask.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L).getTaskId();
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activeBatRides.containsKey(uuid)) {
                    endBatRide(player, bat);
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 400L);
    }
    
    private static void endBatRide(Player player, Bat bat) {
        UUID uuid = player.getUniqueId();
        BatRide ride = activeBatRides.remove(uuid);
        
        if(ride != null && ride.controlTaskId != -1) {
            Bukkit.getScheduler().cancelTask(ride.controlTaskId);
        }
        
        if(bat != null && !bat.isDead()) {
            bat.remove();
        }
        
        if(player.isOnline()) {
            player.sendMessage("§c🦇 Bat ride has ended!");
            player.playSound(player.getLocation(), Sound.ENTITY_BAT_DEATH, 1.0f, 1.0f);
        }
    }
    
    // ==================== BLOOD BITE ATTACK ====================
    
    public static void bloodBite(Player player) {
        UUID uuid = player.getUniqueId();
        
        if(!activeBatRides.containsKey(uuid)) {
            return;
        }
        
        if(biteCooldowns.containsKey(uuid) && System.currentTimeMillis() < biteCooldowns.get(uuid)) {
            return;
        }
        
        Entity target = getTargetEntity(player, 5);
        
        if(target == null || !(target instanceof LivingEntity)) {
            player.sendMessage("§c🦇 No target in range!");
            return;
        }
        
        LivingEntity livingTarget = (LivingEntity) target;
        livingTarget.damage(2, player);
        
        double newHealth = Math.min(player.getHealth() + 2, player.getMaxHealth());
        player.setHealth(newHealth);
        
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.5f);
        player.getWorld().playSound(livingTarget.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 0.8f);
        
        // FIXED: REDSTONE -> END_ROD + HEART
        for(int i = 0; i < 360; i += 15) {
            double rad = Math.toRadians(i);
            double x = Math.cos(rad) * 0.8;
            double z = Math.sin(rad) * 0.8;
            livingTarget.getWorld().spawnParticle(Particle.END_ROD, livingTarget.getLocation().add(x, 0.5, z), 1, 0, 0, 0);
        }
        
        // Blood spray effect using END_ROD
        for(int i = 0; i < 20; i++) {
            double dx = (Math.random() - 0.5) * 0.5;
            double dy = Math.random() * 0.5;
            double dz = (Math.random() - 0.5) * 0.5;
            livingTarget.getWorld().spawnParticle(Particle.END_ROD, livingTarget.getLocation().add(dx, 1 + dy, dz), 1, 0, 0, 0);
        }
        
        player.sendMessage("§c🦇 Blood Bite! You drained §c1 heart§c from " + getEntityName(livingTarget) + "!");
        
        biteCooldowns.put(uuid, System.currentTimeMillis() + 3000);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                biteCooldowns.remove(uuid);
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 60L);
    }
    
    private static Entity getTargetEntity(Player player, double range) {
        return player.getWorld().getNearbyEntities(player.getEyeLocation(), range, range, range)
            .stream()
            .filter(e -> e != player && e.getLocation().distance(player.getEyeLocation()) <= range)
            .min(Comparator.comparingDouble(e -> e.getLocation().distance(player.getEyeLocation())))
            .orElse(null);
    }
    
    private static String getEntityName(Entity entity) {
        if(entity instanceof Player) return ((Player) entity).getName();
        return entity.getType().name().toLowerCase().replace("_", " ");
    }
    
    public static boolean hasActivePhase(Player player) {
        VampirePhase phase = activePhases.get(player.getUniqueId());
        if(phase == null) return false;
        if(System.currentTimeMillis() > phase.expiryTime) {
            activePhases.remove(player.getUniqueId());
            return false;
        }
        return true;
    }
    
    public static boolean isRidingBat(Player player) {
        return activeBatRides.containsKey(player.getUniqueId());
    }
    
    public static void cleanup() {
        for(Map.Entry<UUID, BatRide> entry : activeBatRides.entrySet()) {
            BatRide ride = entry.getValue();
            if(ride.bat != null && !ride.bat.isDead()) {
                ride.bat.remove();
            }
            if(ride.controlTaskId != -1) {
                Bukkit.getScheduler().cancelTask(ride.controlTaskId);
            }
        }
        activePhases.clear();
        activeBatRides.clear();
        biteCooldowns.clear();
    }
    
    // ==================== HELPER CLASSES ====================
    
    private static class VampirePhase {
        final UUID playerId;
        final long expiryTime;
        int hitCount;
        
        VampirePhase(UUID playerId, long expiryTime, int hitCount) {
            this.playerId = playerId;
            this.expiryTime = expiryTime;
            this.hitCount = hitCount;
        }
    }
    
    private static class BatRide {
        final Bat bat;
        final long expiryTime;
        int controlTaskId = -1;
        
        BatRide(Bat bat, long expiryTime) {
            this.bat = bat;
            this.expiryTime = expiryTime;
        }
    }
}
