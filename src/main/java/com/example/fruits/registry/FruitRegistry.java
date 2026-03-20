package com.example.fruits.registry;

import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import java.util.*;

public class FruitRegistry {
    private final Map<String, Fruit> fruits = new HashMap<>();

    public FruitRegistry() {
        registerFruits();
    }

    private void registerFruits() {
        // 1. CRIMSON STAR
        fruits.put("crimson_star", new Fruit("crimson_star", "§c§l⚡ Crimson Star", Material.APPLE, 1001,
            Arrays.asList(
                new Ability("§cThunder Strike", 20, p -> {
                    p.getWorld().strikeLightning(p.getTargetBlock(null, 20).getLocation());
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
                    p.getWorld().spawnParticle(Particle.FIREWORK, p.getLocation(), 30, 2, 2, 2);
                }),
                new Ability("§cMeteor Crash", 25, p -> {
                    p.getWorld().createExplosion(p.getTargetBlock(null, 15).getLocation(), 4, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
                }),
                new Ability("§cInferno Blast", 30, p -> {
                    p.getNearbyEntities(8, 5, 8).forEach(e -> e.setFireTicks(100));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.8f);
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 50, 3, 2, 3);
                })
            )));

        // 2. MOON CRESCENT
        fruits.put("moon_crescent", new Fruit("moon_crescent", "§e§l🌙 Moon Crescent", Material.GOLDEN_CARROT, 1002,
            Arrays.asList(
                new Ability("§eGravity Pull", 20, p -> {
                    p.getNearbyEntities(10, 5, 10).forEach(e -> e.teleport(p.getLocation().add(0, 2, 0)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                }),
                new Ability("§eBlack Hole", 25, p -> {
                    p.getNearbyEntities(8, 6, 8).forEach(e -> {
                        Vector toCenter = p.getLocation().toVector().subtract(e.getLocation().toVector());
                        e.setVelocity(toCenter.normalize().multiply(2));
                    });
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.3f);
                }),
                new Ability("§eAnti-Gravity", 30, p -> {
                    p.getNearbyEntities(12, 8, 12).forEach(e -> e.setVelocity(new Vector(0, 3, 0)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.6f);
                })
            )));

        // 3. VOID CLUSTER
        fruits.put("void_cluster", new Fruit("void_cluster", "§5§l🌀 Void Cluster", Material.GLOW_BERRIES, 1003,
            Arrays.asList(
                new Ability("§5Void Rift", 20, p -> {
                    Random rand = new Random();
                    int x = rand.nextInt(20) - 10;
                    int z = rand.nextInt(20) - 10;
                    p.teleport(p.getLocation().add(x, 5, z));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
                }),
                new Ability("§5Phase Shift", 25, p -> {
                    p.setVelocity(p.getLocation().getDirection().multiply(4));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.2f);
                }),
                new Ability("§5Dimensional Slam", 30, p -> {
                    p.getNearbyEntities(6, 4, 6).forEach(e -> e.setVelocity(e.getVelocity().setY(3).multiply(1.5)));
                    p.getWorld().createExplosion(p.getLocation(), 3, false, false);
                })
            )));

        // 4. SOLAR ORB
        fruits.put("solar_orb", new Fruit("solar_orb", "§6§l☀️ Solar Orb", Material.ORANGE_DYE, 1004,
            Arrays.asList(
                new Ability("§6Solar Flare", 20, p -> {
                    p.getWorld().createExplosion(p.getTargetBlock(null, 15).getLocation(), 4, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.2f);
                }),
                new Ability("§6Sun Burst", 25, p -> {
                    p.getNearbyEntities(7, 5, 7).forEach(e -> e.setFireTicks(80));
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 80, 4, 3, 4);
                }),
                new Ability("§6Supernova", 30, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 6, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.6f);
                })
            )));

        // 5. THORNED CROWN
        fruits.put("thorned_crown", new Fruit("thorned_crown", "§a§l🌿 Thorned Crown", Material.PUMPKIN_PIE, 1005,
            Arrays.asList(
                new Ability("§aVine Trap", 20, p -> {
                    p.getNearbyEntities(5, 3, 5).forEach(e -> e.setVelocity(new Vector(0, -2, 0)));
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_GRASS_BREAK, 1.0f, 0.7f);
                }),
                new Ability("§aThorn Barrage", 25, p -> {
                    for(int i=0; i<10; i++) {
                        Arrow arrow = p.launchProjectile(Arrow.class);
                        arrow.setVelocity(p.getLocation().getDirection().add(new Vector(Math.random()*0.5-0.25, Math.random()*0.3, Math.random()*0.5-0.25)).normalize().multiply(2));
                    }
                }),
                new Ability("§aNature's Fury", 30, p -> {
                    p.getNearbyEntities(10, 6, 10).forEach(e -> e.setVelocity(e.getVelocity().setY(2)));
                    p.getWorld().strikeLightning(p.getLocation());
                })
            )));

        // 6. ICE SHARD
        fruits.put("ice_shard", new Fruit("ice_shard", "§b§l❄️ Ice Shard", Material.SNOWBALL, 1006,
            Arrays.asList(
                new Ability("§bFreeze", 20, p -> {
                    p.getNearbyEntities(6, 4, 6).forEach(e -> e.setVelocity(new Vector(0, -1, 0)));
                    p.getWorld().spawnParticle(Particle.SNOWFLAKE, p.getLocation(), 80, 2, 1, 2);
                }),
                new Ability("§bIce Spear", 25, p -> {
                    for(int i=0; i<5; i++) {
                        Arrow arrow = p.launchProjectile(Arrow.class);
                        arrow.setVelocity(p.getLocation().getDirection().add(new Vector(Math.random()*0.3-0.15, Math.random()*0.2, Math.random()*0.3-0.15)).normalize().multiply(2.5));
                    }
                }),
                new Ability("§bBlizzard", 30, p -> {
                    p.getNearbyEntities(12, 8, 12).forEach(e -> e.setVelocity(new Vector(Math.random()*2-1, -1, Math.random()*2-1)));
                    p.getWorld().playSound(p.getLocation(), Sound.WEATHER_RAIN, 1.0f, 0.5f);
                })
            )));

        // 7. PRIMORDIAL ESSENCE - GOD FRUIT
        fruits.put("primordial_essence", new Fruit("primordial_essence", "§5§l✨ Primordial Essence", 
            Material.ENCHANTED_GOLDEN_APPLE, 1010,
            Arrays.asList(
                new Ability("§c§l💀 ONE SHOT", 120, p -> {
                    if(p.getLevel() < 30) {
                        p.sendMessage("§c❌ Need 30 XP levels!");
                        return;
                    }
                    Player target = getTarget(p, 15);
                    if(target != null) {
                        target.setHealth(0);
                        p.setLevel(p.getLevel() - 30);
                        p.getWorld().strikeLightningEffect(target.getLocation());
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 0.5f);
                        p.sendMessage("§c§l💀 ONE SHOT! Killed " + target.getName());
                        Bukkit.broadcastMessage("§5✨ " + p.getName() + " §dused ONE SHOT!");
                    }
                }),
                new Ability("§5Apocalypse", 60, p -> {
                    for(int i=0; i<12; i++) {
                        p.getWorld().strikeLightning(p.getLocation().add(Math.random()*15-7.5, 0, Math.random()*15-7.5));
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f, 0.6f);
                }),
                new Ability("§5Divine Judgment", 90, p -> {
                    p.getNearbyEntities(15, 10, 15).forEach(e -> {
                        e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(3));
                        if(e instanceof Player) {
                            ((Player) e).damage(20, p);
                        }
                    });
                    p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation(), 150, 3, 4, 3);
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
