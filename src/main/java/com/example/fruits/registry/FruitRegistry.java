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
        // 1. CRIMSON STAR - Lightning & Fire
        fruits.put("crimson_star", new Fruit("crimson_star", "§c§l⚡ Crimson Star", Material.APPLE, 1001,
            Arrays.asList(
                new Ability("§cThunder Strike", 20, p -> {
                    p.getWorld().strikeLightning(p.getTargetBlock(null, 20).getLocation());
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
                    p.getWorld().spawnParticle(Particle.FIREWORK, p.getLocation(), 30, 2, 2, 2);
                    p.sendMessage("§c⚡ THUNDER STRIKE!");
                }),
                new Ability("§cMeteor Crash", 25, p -> {
                    p.getWorld().createExplosion(p.getTargetBlock(null, 15).getLocation(), 4, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
                    p.sendMessage("§c☄️ METEOR CRASH!");
                }),
                new Ability("§cInferno Blast", 30, p -> {
                    p.getNearbyEntities(8, 5, 8).forEach(e -> e.setFireTicks(100));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.8f);
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 50, 3, 2, 3);
                    p.sendMessage("§c🔥 INFERNO BLAST!");
                })
            )));

        // 2. MOON CRESCENT - Gravity & Pull
        fruits.put("moon_crescent", new Fruit("moon_crescent", "§e§l🌙 Moon Crescent", Material.GOLDEN_CARROT, 1002,
            Arrays.asList(
                new Ability("§eGravity Pull", 20, p -> {
                    p.getNearbyEntities(10, 5, 10).forEach(e -> e.teleport(p.getLocation().add(0, 2, 0)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                    p.sendMessage("§e🌀 GRAVITY PULL!");
                }),
                new Ability("§eBlack Hole", 25, p -> {
                    p.getNearbyEntities(8, 6, 8).forEach(e -> {
                        Vector toCenter = p.getLocation().toVector().subtract(e.getLocation().toVector());
                        e.setVelocity(toCenter.normalize().multiply(2));
                    });
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.3f);
                    p.sendMessage("§e🕳️ BLACK HOLE!");
                }),
                new Ability("§eAnti-Gravity", 30, p -> {
                    p.getNearbyEntities(12, 8, 12).forEach(e -> e.setVelocity(new Vector(0, 3, 0)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.6f);
                    p.sendMessage("§e🌕 ANTI-GRAVITY!");
                })
            )));

        // 3. VOID CLUSTER - Teleport & Push
        fruits.put("void_cluster", new Fruit("void_cluster", "§5§l🌀 Void Cluster", Material.GLOW_BERRIES, 1003,
            Arrays.asList(
                new Ability("§5Void Rift", 20, p -> {
                    Random rand = new Random();
                    int x = rand.nextInt(20) - 10;
                    int z = rand.nextInt(20) - 10;
                    p.teleport(p.getLocation().add(x, 5, z));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
                    p.sendMessage("§5🌀 VOID RIFT!");
                }),
                new Ability("§5Phase Shift", 25, p -> {
                    p.setVelocity(p.getLocation().getDirection().multiply(4));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.2f);
                    p.sendMessage("§5✨ PHASE SHIFT!");
                }),
                new Ability("§5Dimensional Slam", 30, p -> {
                    p.getNearbyEntities(6, 4, 6).forEach(e -> e.setVelocity(e.getVelocity().setY(3).multiply(1.5)));
                    p.getWorld().createExplosion(p.getLocation(), 3, false, false);
                    p.sendMessage("§5💥 DIMENSIONAL SLAM!");
                })
            )));

        // 4. SOLAR ORB - Explosions & Fire
        fruits.put("solar_orb", new Fruit("solar_orb", "§6§l☀️ Solar Orb", Material.ORANGE_DYE, 1004,
            Arrays.asList(
                new Ability("§6Solar Flare", 20, p -> {
                    p.getWorld().createExplosion(p.getTargetBlock(null, 15).getLocation(), 4, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.2f);
                    p.sendMessage("§6☀️ SOLAR FLARE!");
                }),
                new Ability("§6Sun Burst", 25, p -> {
                    p.getNearbyEntities(7, 5, 7).forEach(e -> e.setFireTicks(80));
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 80, 4, 3, 4);
                    p.sendMessage("§6💥 SUN BURST!");
                }),
                new Ability("§6Supernova", 30, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 6, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.6f);
                    p.sendMessage("§6🌋 SUPERNOVA!");
                })
            )));

        // 5. THORNED CROWN - Nature & Vines
        fruits.put("thorned_crown", new Fruit("thorned_crown", "§a§l🌿 Thorned Crown", Material.PUMPKIN_PIE, 1005,
            Arrays.asList(
                new Ability("§aVine Trap", 20, p -> {
                    p.getNearbyEntities(5, 3, 5).forEach(e -> e.setVelocity(new Vector(0, -2, 0)));
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_GRASS_BREAK, 1.0f, 0.7f);
                    p.sendMessage("§a🌿 VINE TRAP!");
                }),
                new Ability("§aThorn Barrage", 25, p -> {
                    for(int i=0; i<10; i++) {
                        Arrow arrow = p.launchProjectile(Arrow.class);
                        arrow.setVelocity(p.getLocation().getDirection().add(new Vector(Math.random()*0.5-0.25, Math.random()*0.3, Math.random()*0.5-0.25)).normalize().multiply(2));
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
                    p.sendMessage("§a🌵 THORN BARRAGE!");
                }),
                new Ability("§aNature's Fury", 30, p -> {
                    p.getNearbyEntities(10, 6, 10).forEach(e -> e.setVelocity(e.getVelocity().setY(2)));
                    p.getWorld().strikeLightning(p.getLocation());
                    p.sendMessage("§a🌳 NATURE'S FURY!");
                })
            )));

        // 6. ICE SHARD - Freeze & Knockback
        fruits.put("ice_shard", new Fruit("ice_shard", "§b§l❄️ Ice Shard", Material.SNOWBALL, 1006,
            Arrays.asList(
                new Ability("§bFreeze", 20, p -> {
                    p.getNearbyEntities(6, 4, 6).forEach(e -> e.setVelocity(new Vector(0, -1, 0)));
                    p.getWorld().spawnParticle(Particle.SNOWFLAKE, p.getLocation(), 80, 2, 1, 2);
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.5f);
                    p.sendMessage("§b❄️ FREEZE!");
                }),
                new Ability("§bIce Spear", 25, p -> {
                    for(int i=0; i<5; i++) {
                        Arrow arrow = p.launchProjectile(Arrow.class);
                        arrow.setVelocity(p.getLocation().getDirection().add(new Vector(Math.random()*0.3-0.15, Math.random()*0.2, Math.random()*0.3-0.15)).normalize().multiply(2.5));
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.2f);
                    p.sendMessage("§b🗡️ ICE SPEAR!");
                }),
                new Ability("§bBlizzard", 30, p -> {
                    p.getNearbyEntities(12, 8, 12).forEach(e -> e.setVelocity(new Vector(Math.random()*2-1, -1, Math.random()*2-1)));
                    p.getWorld().playSound(p.getLocation(), Sound.WEATHER_RAIN, 1.0f, 0.5f);
                    p.getWorld().spawnParticle(Particle.SNOWFLAKE, p.getLocation(), 150, 4, 3, 4);
                    p.sendMessage("§b🌨️ BLIZZARD!");
                })
            )));

        // 7. STORM EYE - Wind & Lightning
        fruits.put("storm_eye", new Fruit("storm_eye", "§3§l🌪️ Storm Eye", Material.CHORUS_FRUIT, 1007,
            Arrays.asList(
                new Ability("§3Wind Push", 20, p -> {
                    p.getNearbyEntities(7, 5, 7).forEach(e -> e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(3)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.8f);
                    p.sendMessage("§3💨 WIND PUSH!");
                }),
                new Ability("§3Cyclone", 25, p -> {
                    p.getNearbyEntities(6, 4, 6).forEach(e -> e.setVelocity(new Vector(Math.random()*2-1, 1, Math.random()*2-1)));
                    p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 100, 3, 2, 3);
                    p.sendMessage("§3🌀 CYCLONE!");
                }),
                new Ability("§3Thunderstorm", 30, p -> {
                    for(int i=0; i<8; i++) {
                        p.getWorld().strikeLightning(p.getLocation().add(Math.random()*12-6, 0, Math.random()*12-6));
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.8f);
                    p.sendMessage("§3⛈️ THUNDERSTORM!");
                })
            )));

        // 8. INFERNO HEART - Fire & Explosions
        fruits.put("inferno_heart", new Fruit("inferno_heart", "§c§l🔥 Inferno Heart", Material.GOLDEN_APPLE, 1008,
            Arrays.asList(
                new Ability("§cFlame Wave", 20, p -> {
                    p.getNearbyEntities(8, 4, 8).forEach(e -> e.setFireTicks(120));
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 100, 3, 2, 3);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.9f);
                    p.sendMessage("§c🔥 FLAME WAVE!");
                }),
                new Ability("§cFirestorm", 25, p -> {
                    for(int i=0; i<15; i++) {
                        p.getWorld().spawnParticle(Particle.FLAME, p.getLocation().add(Math.random()*10-5, Math.random()*3, Math.random()*10-5), 5);
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 0.7f);
                    p.sendMessage("§c🌋 FIRESTORM!");
                }),
                new Ability("§cEruption", 30, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 5, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);
                    p.sendMessage("§c💥 ERUPTION!");
                })
            )));

        // 9. CRYSTAL HEART - Healing & Light
        fruits.put("crystal_heart", new Fruit("crystal_heart", "§b§l💎 Crystal Heart", Material.SWEET_BERRIES, 1009,
            Arrays.asList(
                new Ability("§bHealing Pulse", 20, p -> {
                    p.setHealth(Math.min(p.getHealth() + 8, p.getMaxHealth()));
                    p.getWorld().spawnParticle(Particle.HEART, p.getLocation(), 50, 2, 1, 2);
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.8f);
                    p.sendMessage("§b💖 HEALING PULSE!");
                }),
                new Ability("§bCrystal Wall", 25, p -> {
                    p.getNearbyEntities(5, 3, 5).forEach(e -> e.setVelocity(e.getVelocity().multiply(-1)));
                    p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 60, 3, 2, 3);
                    p.sendMessage("§b🔮 CRYSTAL WALL!");
                }),
                new Ability("§bLight Beam", 30, p -> {
                    Player target = getTarget(p, 20);
                    if(target != null) {
                        target.damage(12, p);
                        target.getWorld().strikeLightningEffect(target.getLocation());
                        p.sendMessage("§b✨ LIGHT BEAM on " + target.getName());
                    }
                })
            )));

        // 10. PRIMORDIAL ESSENCE - GOD FRUIT
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
                    p.sendMessage("§5🌋 APOCALYPSE!");
                }),
                new Ability("§5Divine Judgment", 90, p -> {
                    p.getNearbyEntities(15, 10, 15).forEach(e -> {
                        e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(3));
                        if(e instanceof Player) {
                            ((Player) e).damage(20, p);
                        }
                    });
                    p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation(), 150, 3, 4, 3);
                    p.sendMessage("§5⚖️ DIVINE JUDGMENT!");
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
