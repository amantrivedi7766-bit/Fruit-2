uits() { return fruits.values(); }
}
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
        // ==================== 1. METEOR FRUIT ====================
        fruits.put("meteor_fruit", new Fruit("meteor_fruit", "§c§l☄️ Meteor Fruit", Material.APPLE, 1001,
            Arrays.asList(
                new Ability("§cMeteor Storm", 30, p -> {
                    Location target = p.getTargetBlock(null, 30).getLocation();
                    if(target == null) target = p.getLocation().add(p.getLocation().getDirection().multiply(10));
                    
                    for(int i = 0; i < 5; i++) {
                        int finalI = i;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Location spikeLoc = target.clone().add(finalI * 2, 0, 0);
                                for(int y = 0; y < 5; y++) {
                                    Location fallLoc = spikeLoc.clone().add(0, 10 - y, 0);
                                    p.getWorld().spawnParticle(Particle.FLAME, fallLoc, 20, 0.3, 0.3, 0.3, 0.05);
                                    p.getWorld().spawnParticle(Particle.LAVA, fallLoc, 10, 0.2, 0.2, 0.2, 0.02);
                                }
                                spikeLoc.getBlock().setType(Material.MAGMA_BLOCK);
                                p.getWorld().playSound(spikeLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);
                                Bukkit.getScheduler().runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), () -> {
                                    spikeLoc.getBlock().setType(Material.AIR);
                                }, 200L);
                                spikeLoc.getWorld().getNearbyEntities(spikeLoc, 2, 2, 2).forEach(e -> {
                                    if(e instanceof Player && e != p) {
                                        ((Player) e).damage(8, p);
                                    }
                                });
                            }
                        }.runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), i * 5L);
                    }
                    p.sendMessage("§c☄️ METEOR STORM! §7(5 meteors falling)");
                }),
                new Ability("§cMagma Shield", 45, p -> {
                    Location center = p.getLocation();
                    for(int angle = 0; angle < 360; angle += 30) {
                        double rad = Math.toRadians(angle);
                        for(int r = 0; r < 10; r++) {
                            double x = Math.cos(rad) * r;
                            double z = Math.sin(rad) * r;
                            Location loc = center.clone().add(x, r / 2, z);
                            loc.getBlock().setType(Material.MAGMA_BLOCK);
                            new BukkitRunnable() {
                                int wave = 0;
                                @Override
                                public void run() {
                                    if(wave >= 20) {
                                        loc.getBlock().setType(Material.AIR);
                                        this.cancel();
                                        return;
                                    }
                                    loc.getWorld().spawnParticle(Particle.FLAME, loc, 10, 0.2, 0.2, 0.2, 0.02);
                                    wave++;
                                }
                            }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 2L);
                        }
                    }
                    new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            if(ticks >= 100) {
                                this.cancel();
                                return;
                            }
                            p.getNearbyEntities(10, 5, 10).forEach(e -> {
                                if(e instanceof Player && e != p) {
                                    e.setFireTicks(60);
                                    ((Player) e).damage(2, p);
                                    e.getWorld().spawnParticle(Particle.LAVA, e.getLocation(), 20, 0.5, 0.5, 0.5);
                                }
                            });
                            ticks++;
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 10L);
                    p.sendMessage("§c🛡️ MAGMA SHIELD! §7(Shield active for 10 seconds)");
                })
            )));

        // ==================== 2. WIND MONSTER FRUIT ====================
        fruits.put("wind_monster", new Fruit("wind_monster", "§b§l🌪️ Wind Monster Fruit", Material.GOLDEN_CARROT, 1002,
            Arrays.asList(
                new Ability("§bWind Monster", 60, p -> {
                    p.setVelocity(new Vector(0, 2, 0));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WIND_CHARGE_WIND_BURST, 2.0f, 1.2f);
                    new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            if(ticks >= 100) {
                                this.cancel();
                                return;
                            }
                            p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.05);
                            p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation().add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.02);
                            if(ticks % 10 == 0) {
                                Location fistLoc = p.getLocation().add(p.getLocation().getDirection().multiply(3));
                                p.getWorld().spawnParticle(Particle.EXPLOSION, fistLoc, 30, 1, 1, 1);
                                p.getWorld().playSound(fistLoc, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.5f, 0.8f);
                                p.getNearbyEntities(5, 5, 5).forEach(e -> {
                                    if(e instanceof Player && e != p) {
                                        ((Player) e).damage(6, p);
                                        e.setVelocity(new Vector(0, 1, 0));
                                    }
                                });
                            }
                            ticks++;
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 2L);
                    p.sendMessage("§b🌪️ WIND MONSTER! §7(You are a wind monster for 10 seconds!)");
                }),
                new Ability("§bStorm Monster", 60, p -> {
                    new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            if(ticks >= 100) {
                                this.cancel();
                                return;
                            }
                            p.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, p.getLocation().add(0, 1, 0), 40, 0.5, 0.5, 0.5, 0.1);
                            p.getWorld().spawnParticle(Particle.FIREWORK, p.getLocation().add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.05);
                            if(ticks % 8 == 0) {
                                Location fistLoc = p.getLocation().add(p.getLocation().getDirection().multiply(3));
                                p.getWorld().strikeLightningEffect(fistLoc);
                                p.getWorld().playSound(fistLoc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.5f, 1.0f);
                                p.getNearbyEntities(6, 4, 6).forEach(e -> {
                                    if(e instanceof Player && e != p) {
                                        ((Player) e).damage(8, p);
                                        ((Player) e).setVelocity(new Vector(0, 0.5, 0));
                                    }
                                });
                            }
                            ticks++;
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 2L);
                    p.sendMessage("§b⚡ STORM MONSTER! §7(Electric attacks for 10 seconds!)");
                })
            )));

        // ==================== 3. TIME FREEZE FRUIT ====================
        fruits.put("time_freeze", new Fruit("time_freeze", "§d§l⏰ Time Freeze Fruit", Material.CLOCK, 1003,
            Arrays.asList(
                new Ability("§dTime Freeze", 50, p -> {
                    Player target = getTarget(p, 20);
                    if(target != null) {
                        target.setWalkSpeed(0);
                        target.setFlySpeed(0);
                        target.setVelocity(new Vector(0, 0, 0));
                        new BukkitRunnable() {
                            int timer = 0;
                            @Override
                            public void run() {
                                if(timer >= 200) {
                                    target.setWalkSpeed(0.2f);
                                    target.setFlySpeed(0.1f);
                                    target.sendMessage("§aTime unfrozen!");
                                    this.cancel();
                                    return;
                                }
                                // FIXED: Use END_ROD particle (works in all versions)
                                target.getWorld().spawnParticle(Particle.END_ROD, target.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5);
                                target.getWorld().playSound(target.getLocation(), Sound.BLOCK_BEACON_AMBIENT, 0.5f, 0.5f);
                                timer++;
                            }
                        }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 1L);
                        p.sendMessage("§d⏰ TIME FREEZE on " + target.getName() + "! (10 seconds)");
                    }
                }),
                new Ability("§dMagnetic Pull", 35, p -> {
                    Player target = getTarget(p, 15);
                    if(target != null) {
                        target.teleport(p.getLocation().add(0, 1, 0));
                        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 1.0f);
                        target.getWorld().spawnParticle(Particle.PORTAL, target.getLocation(), 50, 0.5, 0.5, 0.5);
                        p.sendMessage("§d🧲 MAGNETIC PULL! §7Pulled " + target.getName());
                    }
                })
            )));

        // ==================== 4. DRAGON FRUIT ====================
        fruits.put("dragon_fruit", new Fruit("dragon_fruit", "§4§l🐉 Dragon Fruit", Material.CHORUS_FRUIT, 1004,
            Arrays.asList(
                new Ability("§4Dragon Breath", 30, p -> {
                    Location target = p.getTargetBlock(null, 20).getLocation();
                    for(int i = 0; i < 20; i++) {
                        Location breathLoc = target.clone().add(Math.random()*3-1.5, Math.random()*2, Math.random()*3-1.5);
                        p.getWorld().spawnParticle(Particle.DRAGON_BREATH, breathLoc, 10, 0.2, 0.2, 0.2);
                        breathLoc.getWorld().getNearbyEntities(breathLoc, 2, 2, 2).forEach(e -> {
                            if(e instanceof Player && e != p) ((Player) e).damage(5);
                        });
                    }
                    p.getWorld().playSound(target, Sound.ENTITY_ENDER_DRAGON_SHOOT, 2.0f, 0.8f);
                }),
                new Ability("§4Dragon Roar", 45, p -> {
                    p.getNearbyEntities(12, 8, 12).forEach(e -> {
                        e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(3));
                        if(e instanceof Player) ((Player) e).damage(10);
                    });
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 2.0f, 0.6f);
                })
            )));

        // ==================== 5. PHOENIX FRUIT ====================
        fruits.put("phoenix_fruit", new Fruit("phoenix_fruit", "§6§l🔥 Phoenix Fruit", Material.GOLDEN_APPLE, 1005,
            Arrays.asList(
                new Ability("§6Phoenix Dive", 30, p -> {
                    Location target = p.getTargetBlock(null, 20).getLocation();
                    p.teleport(target);
                    p.getWorld().createExplosion(target, 3, false, true);
                    p.getWorld().playSound(target, Sound.ENTITY_BLAZE_SHOOT, 2.0f, 1.2f);
                }),
                new Ability("§6Rebirth", 50, p -> {
                    p.setHealth(p.getMaxHealth());
                    p.setFireTicks(0);
                    p.getWorld().spawnParticle(Particle.FIREWORK, p.getLocation(), 100, 1, 2, 1);
                })
            )));

        // ==================== 6. VOID FRUIT ====================
        fruits.put("void_fruit", new Fruit("void_fruit", "§5§l🌀 Void Fruit", Material.GLOW_BERRIES, 1006,
            Arrays.asList(
                new Ability("§5Void Walk", 30, p -> {
                    Location target = p.getTargetBlock(null, 25).getLocation();
                    p.teleport(target);
                    p.getWorld().playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.5f);
                }),
                new Ability("§5Black Hole", 45, p -> {
                    p.getNearbyEntities(10, 7, 10).forEach(e -> {
                        Vector toCenter = p.getLocation().toVector().subtract(e.getLocation().toVector());
                        e.setVelocity(toCenter.normalize().multiply(2.5));
                        if(e instanceof Player) ((Player) e).damage(8);
                    });
                })
            )));

        // ==================== 7. ICE DRAGON FRUIT ====================
        fruits.put("ice_dragon", new Fruit("ice_dragon", "§b§l❄️ Ice Dragon Fruit", Material.SNOWBALL, 1007,
            Arrays.asList(
                new Ability("§bIce Breath", 30, p -> {
                    Location target = p.getTargetBlock(null, 20).getLocation();
                    for(int i = 0; i < 20; i++) {
                        Location breathLoc = target.clone().add(Math.random()*2-1, Math.random()*2, Math.random()*2-1);
                        p.getWorld().spawnParticle(Particle.SNOWFLAKE, breathLoc, 15, 0.2, 0.2, 0.2);
                        breathLoc.getWorld().getNearbyEntities(breathLoc, 2, 2, 2).forEach(e -> {
                            if(e instanceof Player && e != p) {
                                ((Player) e).setVelocity(new Vector(0, -1, 0));
                                ((Player) e).damage(4);
                            }
                        });
                    }
                }),
                new Ability("§bIce Wall", 40, p -> {
                    Location target = p.getTargetBlock(null, 15).getLocation();
                    for(int i = 0; i < 5; i++) {
                        target.clone().add(i, 0, 0).getBlock().setType(Material.ICE);
                        target.clone().add(-i, 0, 0).getBlock().setType(Material.ICE);
                    }
                    Bukkit.getScheduler().runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), () -> {
                        for(int i = -5; i <= 5; i++) {
                            target.clone().add(i, 0, 0).getBlock().setType(Material.AIR);
                        }
                    }, 100L);
                })
            )));

        // ==================== 8. LAVA FRUIT ====================
        fruits.put("lava_fruit", new Fruit("lava_fruit", "§c§l🌋 Lava Fruit", Material.MAGMA_CREAM, 1008,
            Arrays.asList(
                new Ability("§cLava Wave", 30, p -> {
                    Location target = p.getTargetBlock(null, 15).getLocation();
                    for(int i = 0; i < 10; i++) {
                        target.clone().add(i, 0, 0).getBlock().setType(Material.LAVA);
                        target.clone().add(-i, 0, 0).getBlock().setType(Material.LAVA);
                    }
                    Bukkit.getScheduler().runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), () -> {
                        for(int i = -10; i <= 10; i++) {
                            target.clone().add(i, 0, 0).getBlock().setType(Material.AIR);
                        }
                    }, 80L);
                }),
                new Ability("§cVolcano", 45, p -> {
                    for(int i = 0; i < 8; i++) {
                        Location loc = p.getLocation().add(Math.random()*8-4, 0, Math.random()*8-4);
                        p.getWorld().createExplosion(loc, 2, true, false);
                        p.getWorld().spawnParticle(Particle.LAVA, loc, 50, 1, 1, 1);
                    }
                })
            )));

        // ==================== 9. THUNDER FRUIT ====================
        fruits.put("thunder_fruit", new Fruit("thunder_fruit", "§e§l⚡ Thunder Fruit", Material.LIGHTNING_ROD, 1009,
            Arrays.asList(
                new Ability("§eLightning Strike", 25, p -> {
                    Location target = p.getTargetBlock(null, 30).getLocation();
                    p.getWorld().strikeLightning(target);
                    p.getWorld().createExplosion(target, 2, false, false);
                }),
                new Ability("§eThunder Storm", 50, p -> {
                    for(int i = 0; i < 15; i++) {
                        Location loc = p.getLocation().add(Math.random()*15-7.5, 0, Math.random()*15-7.5);
                        p.getWorld().strikeLightning(loc);
                    }
                })
            )));

        // ==================== 10. PRIMORDIAL ESSENCE (GOD FRUIT) ====================
        fruits.put("primordial_essence", new Fruit("primordial_essence", "§5§l✨ Primordial Essence", 
            Material.ENCHANTED_GOLDEN_APPLE, 1010,
            Arrays.asList(
                new Ability("§c§l💀 ONE SHOT", 110, p -> {
                    if(p.getLevel() < 30) {
                        p.sendMessage("§c❌ Need 30 XP levels!");
                        return;
                    }
                    Player target = getTarget(p, 20);
                    if(target != null) {
                        target.setHealth(0);
                        p.setLevel(p.getLevel() - 30);
                        p.getWorld().strikeLightningEffect(target.getLocation());
                        p.getWorld().createExplosion(target.getLocation(), 4, false, false);
                        p.sendMessage("§c§l💀 ONE SHOT! Killed " + target.getName());
                        Bukkit.broadcastMessage("§5✨ " + p.getName() + " §dused ONE SHOT!");
                    }
                }),
                new Ability("§5Apocalypse", 55, p -> {
                    for(int i = 0; i < 20; i++) {
                        Location loc = p.getLocation().add(Math.random()*20-10, 0, Math.random()*20-10);
                        p.getWorld().strikeLightning(loc);
                        p.getWorld().createExplosion(loc, 2.5f, false, false);
                    }
                }),
                new Ability("§5Divine Shield", 85, p -> {
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE, 300, 4));
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.ABSORPTION, 300, 5));
                    p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation(), 150, 2, 3, 2);
                })
            )));
    }

    private Player getTarget(Player p, int range) {
        return p.getWorld().getNearbyPlayers(p.getLocation(), range).stream()
            .filter(e -> !e.equals(p)).findFirst().orElse(null);
    }

    public Fruit getFruit(String id) { return fruits.get(id); }
    public Collection<Fruit> getAllFruits() { return fruits.values(); }
}
          
