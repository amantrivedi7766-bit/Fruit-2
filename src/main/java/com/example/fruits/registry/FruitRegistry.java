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
        // 1. CRIMSON STAR - Lightning & Fire (STRONGER)
        fruits.put("crimson_star", new Fruit("crimson_star", "§c§l⚡ Crimson Star", Material.APPLE, 1001,
            Arrays.asList(
                new Ability("§cThunder Strike", 18, p -> {
                    Location target = p.getTargetBlock(null, 25).getLocation();
                    p.getWorld().strikeLightning(target);
                    p.getWorld().createExplosion(target, 2.5f, false, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.5f, 1.0f);
                    p.getWorld().spawnParticle(Particle.FIREWORK, target, 50, 2, 2, 2);
                    p.sendMessage("§c⚡ THUNDER STRIKE! §7(12 damage)");
                }),
                new Ability("§cMeteor Crash", 22, p -> {
                    Location target = p.getTargetBlock(null, 20).getLocation();
                    p.getWorld().createExplosion(target, 5.5f, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.5f);
                    p.getWorld().spawnParticle(Particle.EXPLOSION, target, 1);
                    p.sendMessage("§c☄️ METEOR CRASH! §7(15 damage)");
                }),
                new Ability("§cInferno Blast", 28, p -> {
                    p.getNearbyEntities(10, 6, 10).forEach(e -> {
                        e.setFireTicks(150);
                        if(e instanceof Player) ((Player) e).damage(10);
                    });
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.5f, 0.7f);
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 80, 4, 3, 4);
                    p.sendMessage("§c🔥 INFERNO BLAST! §7(10 damage + fire)");
                })
            )));

        // 2. MOON CRESCENT - Gravity & Pull (STRONGER)
        fruits.put("moon_crescent", new Fruit("moon_crescent", "§e§l🌙 Moon Crescent", Material.GOLDEN_CARROT, 1002,
            Arrays.asList(
                new Ability("§eGravity Pull", 18, p -> {
                    p.getNearbyEntities(12, 7, 12).forEach(e -> e.teleport(p.getLocation().add(0, 3, 0)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.5f);
                    p.sendMessage("§e🌀 GRAVITY PULL! §7(Pulls all enemies)");
                }),
                new Ability("§eBlack Hole", 23, p -> {
                    p.getNearbyEntities(10, 8, 10).forEach(e -> {
                        Vector toCenter = p.getLocation().toVector().subtract(e.getLocation().toVector());
                        e.setVelocity(toCenter.normalize().multiply(3));
                        if(e instanceof Player) ((Player) e).damage(8);
                    });
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.5f, 0.3f);
                    p.sendMessage("§e🕳️ BLACK HOLE! §7(8 damage + pull)");
                }),
                new Ability("§eAnti-Gravity", 28, p -> {
                    p.getNearbyEntities(15, 10, 15).forEach(e -> e.setVelocity(new Vector(0, 4, 0)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.5f, 0.6f);
                    p.sendMessage("§e🌕 ANTI-GRAVITY! §7(Sends enemies flying)");
                })
            )));

        // 3. VOID CLUSTER - Teleport & Push (STRONGER)
        fruits.put("void_cluster", new Fruit("void_cluster", "§5§l🌀 Void Cluster", Material.GLOW_BERRIES, 1003,
            Arrays.asList(
                new Ability("§5Void Rift", 18, p -> {
                    Random rand = new Random();
                    int x = rand.nextInt(30) - 15;
                    int z = rand.nextInt(30) - 15;
                    p.teleport(p.getLocation().add(x, 8, z));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 1.5f);
                    p.sendMessage("§5🌀 VOID RIFT! §7(Teleported)");
                }),
                new Ability("§5Phase Shift", 22, p -> {
                    p.setVelocity(p.getLocation().getDirection().multiply(6));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.5f, 1.2f);
                    p.sendMessage("§5✨ PHASE SHIFT! §7(Dash forward)");
                }),
                new Ability("§5Dimensional Slam", 28, p -> {
                    p.getNearbyEntities(8, 6, 8).forEach(e -> {
                        e.setVelocity(e.getVelocity().setY(4).multiply(2));
                        if(e instanceof Player) ((Player) e).damage(12);
                    });
                    p.getWorld().createExplosion(p.getLocation(), 4, false, false);
                    p.sendMessage("§5💥 DIMENSIONAL SLAM! §7(12 damage)");
                })
            )));

        // 4. THORNED CROWN - Nature & Vines (STRONGER)
        fruits.put("thorned_crown", new Fruit("thorned_crown", "§a§l🌿 Thorned Crown", Material.PUMPKIN_PIE, 1004,
            Arrays.asList(
                new Ability("§aVine Trap", 18, p -> {
                    p.getNearbyEntities(7, 5, 7).forEach(e -> e.setVelocity(new Vector(0, -3, 0)));
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_GRASS_BREAK, 1.5f, 0.7f);
                    p.sendMessage("§a🌿 VINE TRAP! §7(Immobilizes enemies)");
                }),
                new Ability("§aThorn Barrage", 22, p -> {
                    for(int i=0; i<15; i++) {
                        Arrow arrow = p.launchProjectile(Arrow.class);
                        arrow.setVelocity(p.getLocation().getDirection().add(new Vector(Math.random()*0.6-0.3, Math.random()*0.4, Math.random()*0.6-0.3)).normalize().multiply(2.5));
                        arrow.setDamage(6);
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.5f, 1.0f);
                    p.sendMessage("§a🌵 THORN BARRAGE! §7(15 arrows)");
                }),
                new Ability("§aNature's Fury", 28, p -> {
                    p.getNearbyEntities(12, 8, 12).forEach(e -> e.setVelocity(e.getVelocity().setY(3)));
                    for(int i=0; i<5; i++) {
                        p.getWorld().strikeLightning(p.getLocation().add(Math.random()*10-5, 0, Math.random()*10-5));
                    }
                    p.sendMessage("§a🌳 NATURE'S FURY! §7(Lightning + knockback)");
                })
            )));

        // 5. STORM EYE - Wind & Lightning (STRONGER)
        fruits.put("storm_eye", new Fruit("storm_eye", "§3§l🌪️ Storm Eye", Material.CHORUS_FRUIT, 1005,
            Arrays.asList(
                new Ability("§3Wind Push", 18, p -> {
                    p.getNearbyEntities(10, 7, 10).forEach(e -> e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(4)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.5f, 0.8f);
                    p.sendMessage("§3💨 WIND PUSH! §7(Pushes enemies away)");
                }),
                new Ability("§3Cyclone", 22, p -> {
                    p.getNearbyEntities(8, 6, 8).forEach(e -> e.setVelocity(new Vector(Math.random()*3-1.5, 2, Math.random()*3-1.5)));
                    p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation(), 150, 4, 3, 4);
                    p.sendMessage("§3🌀 CYCLONE! §7(Traps enemies)");
                }),
                new Ability("§3Thunderstorm", 28, p -> {
                    for(int i=0; i<12; i++) {
                        p.getWorld().strikeLightning(p.getLocation().add(Math.random()*15-7.5, 0, Math.random()*15-7.5));
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.5f, 0.8f);
                    p.sendMessage("§3⛈️ THUNDERSTORM! §7(12 lightning strikes)");
                })
            )));

        // 6. INFERNO HEART - Fire & Explosions (STRONGER)
        fruits.put("inferno_heart", new Fruit("inferno_heart", "§c§l🔥 Inferno Heart", Material.GOLDEN_APPLE, 1006,
            Arrays.asList(
                new Ability("§cFlame Wave", 18, p -> {
                    p.getNearbyEntities(10, 6, 10).forEach(e -> e.setFireTicks(160));
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 150, 4, 3, 4);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.5f, 0.9f);
                    p.sendMessage("§c🔥 FLAME WAVE! §7(Fire damage)");
                }),
                new Ability("§cFirestorm", 22, p -> {
                    for(int i=0; i<25; i++) {
                        p.getWorld().spawnParticle(Particle.FLAME, p.getLocation().add(Math.random()*12-6, Math.random()*4, Math.random()*12-6), 5);
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.5f, 0.7f);
                    p.sendMessage("§c🌋 FIRESTORM! §7(Fire rain)");
                }),
                new Ability("§cEruption", 28, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 6.5f, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.5f);
                    p.sendMessage("§c💥 ERUPTION! §7(Massive explosion)");
                })
            )));

        // 7. CRYSTAL HEART - Healing & Light (STRONGER)
        fruits.put("crystal_heart", new Fruit("crystal_heart", "§b§l💎 Crystal Heart", Material.SWEET_BERRIES, 1007,
            Arrays.asList(
                new Ability("§bHealing Pulse", 18, p -> {
                    p.setHealth(Math.min(p.getHealth() + 12, p.getMaxHealth()));
                    p.getNearbyEntities(8, 5, 8).forEach(e -> {
                        if(e instanceof Player) {
                            ((Player) e).setHealth(Math.min(((Player) e).getHealth() + 6, ((Player) e).getMaxHealth()));
                        }
                    });
                    p.getWorld().spawnParticle(Particle.HEART, p.getLocation(), 80, 3, 2, 3);
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.5f, 1.8f);
                    p.sendMessage("§b💖 HEALING PULSE! §7(Heals 12 hearts)");
                }),
                new Ability("§bCrystal Wall", 22, p -> {
                    p.getNearbyEntities(7, 5, 7).forEach(e -> e.setVelocity(e.getVelocity().multiply(-2)));
                    p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 100, 4, 3, 4);
                    p.sendMessage("§b🔮 CRYSTAL WALL! §7(Pushes enemies back)");
                }),
                new Ability("§bLight Beam", 28, p -> {
                    Player target = getTarget(p, 25);
                    if(target != null) {
                        target.damage(16, p);
                        target.getWorld().strikeLightningEffect(target.getLocation());
                        target.setFireTicks(60);
                        p.sendMessage("§b✨ LIGHT BEAM on §e" + target.getName() + "§b! §7(16 damage)");
                    }
                })
            )));

        // 8. PRIMORDIAL ESSENCE - GOD FRUIT (STRONGER ONE SHOT)
        fruits.put("primordial_essence", new Fruit("primordial_essence", "§5§l✨ Primordial Essence", 
            Material.ENCHANTED_GOLDEN_APPLE, 1010,
            Arrays.asList(
                new Ability("§c§l💀 ONE SHOT", 110, p -> {
                    if(p.getLevel() < 30) {
                        p.sendMessage("§c❌ Need 30 XP levels!");
                        return;
                    }
                    Player target = getTarget(p, 18);
                    if(target != null) {
                        target.setHealth(0);
                        p.setLevel(p.getLevel() - 30);
                        p.getWorld().strikeLightningEffect(target.getLocation());
                        p.getWorld().createExplosion(target.getLocation(), 3, false, false);
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.5f, 0.5f);
                        p.sendMessage("§c§l💀 ONE SHOT! Killed " + target.getName() + "!");
                        Bukkit.broadcastMessage("§5✨ " + p.getName() + " §dused ONE SHOT on §5" + target.getName());
                    }
                }),
                new Ability("§5Apocalypse", 55, p -> {
                    for(int i=0; i<15; i++) {
                        Location loc = p.getLocation().add(Math.random()*18-9, 0, Math.random()*18-9);
                        p.getWorld().strikeLightning(loc);
                        p.getWorld().createExplosion(loc, 2, false, false);
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f, 0.6f);
                    p.sendMessage("§5🌋 APOCALYPSE! §7(15 lightning strikes)");
                }),
                new Ability("§5Divine Judgment", 85, p -> {
                    p.getNearbyEntities(20, 12, 20).forEach(e -> {
                        e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(4));
                        if(e instanceof Player) {
                            ((Player) e).damage(25, p);
                        }
                    });
                    p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation(), 200, 4, 5, 4);
                    p.sendMessage("§5⚖️ DIVINE JUDGMENT! §7(25 damage + knockback)");
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
