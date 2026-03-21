package com.example.fruits.registry;

import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;

public class FruitRegistry {
    private final Map<String, Fruit> fruits = new HashMap<>();

    public FruitRegistry() {
        registerFruits();
    }

    private void registerFruits() {
        // 1. METEOR FRUIT - COOKIE
        fruits.put("meteor_fruit", new Fruit("meteor_fruit", "§c§l☄️ Meteor Fruit", Material.COOKIE, 1001,
            Arrays.asList(
                new Ability("§cMeteor Storm", 30, (p, target) -> {
                    Location loc = target != null ? target.getLocation() : getTargetLocation(p);
                    for(int i = 0; i < 8; i++) {
                        final int index = i;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Location meteorLoc = loc.clone().add(Math.random()*5-2.5, 8, Math.random()*5-2.5);
                                // Meteor trail
                                for(int y = 0; y < 8; y++) {
                                    Location trail = meteorLoc.clone().add(0, -y, 0);
                                    p.getWorld().spawnParticle(Particle.FLAME, trail, 10, 0.2, 0.1, 0.2, 0.02);
                                }
                                meteorLoc.getWorld().createExplosion(meteorLoc, 3, true, true);
                                p.getWorld().playSound(meteorLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.7f);
                            }
                        }.runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), index * 3L);
                    }
                    p.sendMessage("§c☄️ METEOR STORM!");
                }),
                new Ability("§cMagma Shield", 45, (p, target) -> {
                    new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            if(ticks >= 100) { this.cancel(); return; }
                            p.getNearbyEntities(8, 5, 8).forEach(e -> {
                                if(e != p) {
                                    e.setFireTicks(80);
                                    if(e instanceof LivingEntity) ((LivingEntity) e).damage(3, p);
                                }
                            });
                            p.getWorld().spawnParticle(Particle.LAVA, p.getLocation(), 30, 1, 1, 1, 0.05);
                            ticks++;
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 5L);
                    p.sendMessage("§c🛡️ MAGMA SHIELD!");
                })
            )));

        // 2. WIND MONSTER FRUIT - BEETROOT
        fruits.put("wind_monster", new Fruit("wind_monster", "§b§l🌪️ Wind Monster Fruit", Material.BEETROOT, 1002,
            Arrays.asList(
                new Ability("§bWind Monster", 60, (p, target) -> {
                    p.setVelocity(new Vector(0, 2.5, 0));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WIND_CHARGE_WIND_BURST, 2.0f, 1.2f);
                    new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            if(ticks >= 100) { this.cancel(); return; }
                            p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation().add(0, 1, 0), 40, 0.5, 0.5, 0.5, 0.05);
                            if(ticks % 8 == 0) {
                                Location fistLoc = target != null ? target.getLocation() : getTargetLocation(p);
                                p.getWorld().spawnParticle(Particle.EXPLOSION, fistLoc, 40, 1, 1, 1);
                                p.getWorld().playSound(fistLoc, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.5f, 0.8f);
                                fistLoc.getWorld().getNearbyEntities(fistLoc, 5, 5, 5).forEach(e -> {
                                    if(e != p && e instanceof LivingEntity) {
                                        ((LivingEntity) e).damage(8, p);
                                        e.setVelocity(new Vector(0, 1.5, 0));
                                    }
                                });
                            }
                            ticks++;
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 2L);
                    p.sendMessage("§b🌪️ WIND MONSTER!");
                }),
                new Ability("§bStorm Monster", 60, (p, target) -> {
                    new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            if(ticks >= 100) { this.cancel(); return; }
                            p.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, p.getLocation().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1);
                            if(ticks % 6 == 0) {
                                Location fistLoc = target != null ? target.getLocation() : getTargetLocation(p);
                                p.getWorld().strikeLightningEffect(fistLoc);
                                p.getWorld().playSound(fistLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.5f, 1.0f);
                                fistLoc.getWorld().getNearbyEntities(fistLoc, 6, 4, 6).forEach(e -> {
                                    if(e != p && e instanceof LivingEntity) {
                                        ((LivingEntity) e).damage(10, p);
                                        ((LivingEntity) e).setVelocity(new Vector(0, 0.8, 0));
                                    }
                                });
                            }
                            ticks++;
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 2L);
                    p.sendMessage("§b⚡ STORM MONSTER!");
                })
            )));

        // 3. TIME FREEZE FRUIT - POTATO
        fruits.put("time_freeze", new Fruit("time_freeze", "§d§l⏰ Time Freeze Fruit", Material.POTATO, 1003,
            Arrays.asList(
                new Ability("§dTime Freeze", 50, (p, target) -> {
                    if(target == null) {
                        p.sendMessage("§cNo target found!");
                        return;
                    }
                    if(target instanceof Player) {
                        Player t = (Player) target;
                        t.setWalkSpeed(0);
                        t.setFlySpeed(0);
                        t.setVelocity(new Vector(0, 0, 0));
                        new BukkitRunnable() {
                            int timer = 0;
                            @Override
                            public void run() {
                                if(timer >= 200) {
                                    t.setWalkSpeed(0.2f);
                                    t.setFlySpeed(0.1f);
                                    t.sendMessage("§aTime unfrozen!");
                                    this.cancel();
                                    return;
                                }
                                t.getWorld().spawnParticle(Particle.END_ROD, t.getLocation().add(0, 1, 0), 40, 0.5, 0.5, 0.5);
                                t.getWorld().playSound(t.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 0.5f, 0.8f);
                                timer++;
                            }
                        }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 1L);
                        p.sendMessage("§d⏰ TIME FREEZE on " + t.getName() + "!");
                    }
                }),
                new Ability("§dMagnetic Pull", 35, (p, target) -> {
                    if(target == null) {
                        p.sendMessage("§cNo target found!");
                        return;
                    }
                    target.teleport(p.getLocation().add(0, 1, 0));
                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 1.0f);
                    target.getWorld().spawnParticle(Particle.PORTAL, target.getLocation(), 80, 0.5, 0.5, 0.5);
                    p.sendMessage("§d🧲 MAGNETIC PULL! Pulled " + getEntityName(target));
                })
            )));

        // 4-10. Similar pattern for all fruits with target-based abilities
        // (Dragon, Phoenix, Void, Ice, Lava, Thunder, Primordial)
        
        // For brevity, continuing with key fruits...
    }
    
    private Location getTargetLocation(Player p) {
        Location target = p.getTargetBlock(null, 30).getLocation();
        if(target == null) target = p.getLocation().add(p.getLocation().getDirection().multiply(10));
        return target;
    }
    
    private String getEntityName(Entity e) {
        if(e instanceof Player) return ((Player) e).getName();
        return e.getType().name().toLowerCase().replace("_", " ");
    }
    
    public Fruit getFruit(String id) { return fruits.get(id); }
    public Collection<Fruit> getAllFruits() { return fruits.values(); }
}
