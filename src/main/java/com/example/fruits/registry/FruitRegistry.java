package com.example.fruits.registry;

import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import org.bukkit.Bukkit;  // ✅ IMPORT ADDED
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class FruitRegistry {
    private final Map<String, Fruit> fruits = new HashMap<>();

    public FruitRegistry() {
        // 1. CRIMSON STAR - Apple
        fruits.put("crimson_star", new Fruit("crimson_star", "§c§lCrimson Star", Material.APPLE,
            Arrays.asList(
                new Ability("§4Vine Grab", 20, p -> {
                    Player target = getTarget(p, 10);
                    if(target != null) {
                        target.teleport(p.getLocation().add(0, 1, 0));
                        target.damage(6.0, p);
                        p.getWorld().spawnParticle(Particle.HEART, target.getLocation(), 20, 0.5, 1, 0.5);
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);  // ✅ FIXED: float values
                        p.sendMessage("§a✅ Pulled " + target.getName());
                    }
                }),
                new Ability("§4Crimson Slash", 15, p -> {
                    p.getNearbyEntities(5, 3, 5).forEach(e -> {
                        e.setVelocity(e.getVelocity().setY(1));
                    });
                    p.getWorld().spawnParticle(Particle.SWEEP_ATTACK, p.getLocation(), 30, 2, 1, 2);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);  // ✅ FIXED
                }),
                new Ability("§4Nature's Wrath", 30, p -> {
                    p.getWorld().strikeLightning(p.getTargetBlock(null, 20).getLocation());
                    p.getWorld().spawnParticle(Particle.FIREWORK, p.getLocation(), 50, 3, 2, 3);  // ✅ FIXED: FIREWORKS_SPARK → FIREWORK
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);  // ✅ FIXED
                })
            )));

        // 2. MOON CRESCENT - Golden Carrot
        fruits.put("moon_crescent", new Fruit("moon_crescent", "§e§lMoon Crescent", Material.GOLDEN_CARROT,
            Arrays.asList(
                new Ability("§6Lunar Slip", 15, p -> {
                    Vector dir = p.getLocation().getDirection().multiply(2).setY(0.5);
                    p.setVelocity(dir);
                    p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 30, 0.5, 0.5, 0.5);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 2.0f);  // ✅ FIXED
                }),
                new Ability("§6Crescent Throw", 20, p -> {
                    org.bukkit.entity.Arrow arrow = p.launchProjectile(org.bukkit.entity.Arrow.class);
                    arrow.setDamage(8.0);
                    arrow.setCritical(true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);  // ✅ FIXED
                }),
                new Ability("§6Lunar Frenzy", 30, p -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 1));  // ✅ FIXED: INCREASE_DAMAGE → STRENGTH
                    p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation(), 50, 1, 2, 1);
                    p.getWorld().playSound(p.getLocation(), Sound.EVENT_RAID_HORN, 1.0f, 1.0f);  // ✅ FIXED
                })
            )));

        // 3. BLOOD GEM - Sweet Berries
        fruits.put("blood_gem", new Fruit("blood_gem", "§c§lBlood Gem", Material.SWEET_BERRIES,
            Arrays.asList(
                new Ability("§cBlood Bomb", 20, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 2, false, false);
                    p.getWorld().spawnParticle(Particle.LAVA, p.getLocation(), 50, 2, 1, 2);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);  // ✅ FIXED
                }),
                new Ability("§cCrimson Bloom", 25, p -> {
                    p.setHealth(Math.min(p.getHealth() + 4, p.getMaxHealth()));
                    p.getWorld().spawnParticle(Particle.HEART, p.getLocation(), 30, 1, 1, 1);
                }),
                new Ability("§cSanguine Beam", 30, p -> {
                    p.getNearbyEntities(10, 5, 10).forEach(e -> e.setFireTicks(100));
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 100, 3, 2, 3);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.5f);  // ✅ FIXED
                })
            )));

        // 4. VOID CLUSTER - Glow Berries
        fruits.put("void_cluster", new Fruit("void_cluster", "§5§lVoid Cluster", Material.GLOW_BERRIES,
            Arrays.asList(
                new Ability("§5Void Splash", 15, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 3, false, false);
                    p.getWorld().spawnParticle(Particle.DRAGON_BREATH, p.getLocation(), 60, 3, 2, 3);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 0.5f);  // ✅ FIXED
                }),
                new Ability("§5Cluster Shot", 20, p -> {
                    for(int i=0; i<3; i++) {
                        p.launchProjectile(org.bukkit.entity.Snowball.class);
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EGG_THROW, 1.0f, 1.0f);  // ✅ FIXED
                }),
                new Ability("§5Abyssal Whip", 25, p -> {
                    p.getNearbyEntities(5, 5, 5).forEach(e -> 
                        e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(-2)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.5f);  // ✅ FIXED
                })
            )));

        // 5. SOLAR ORB - Orange Dye
        fruits.put("solar_orb", new Fruit("solar_orb", "§6§lSolar Orb", Material.ORANGE_DYE,
            Arrays.asList(
                new Ability("§6Solar Slice", 15, p -> {
                    p.getWorld().strikeLightning(p.getTargetBlock(null, 20).getLocation());
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);  // ✅ FIXED
                }),
                new Ability("§6Radiant Spray", 20, p -> {
                    p.getNearbyEntities(5, 5, 5).forEach(e -> e.setFireTicks(40));
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 40, 2, 1, 2);
                }),
                new Ability("§6Supernova", 30, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 5, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);  // ✅ FIXED
                })
            )));

        // 6. THORNED CROWN - Pumpkin Pie
        fruits.put("thorned_crown", new Fruit("thorned_crown", "§a§lThorned Crown", Material.PUMPKIN_PIE,
            Arrays.asList(
                new Ability("§aThorned Roll", 20, p -> {
                    p.setVelocity(p.getLocation().getDirection().multiply(3));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_HORSE_BREED, 1.0f, 1.0f);  // ✅ FIXED
                }),
                new Ability("§aCrown Launcher", 25, p -> {
                    p.launchProjectile(org.bukkit.entity.Fireball.class);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.0f);  // ✅ FIXED
                }),
                new Ability("§aNature's Storm", 35, p -> {
                    p.getWorld().setStorm(true);
                    p.getWorld().setThundering(true);
                    p.getWorld().playSound(p.getLocation(), Sound.WEATHER_RAIN, 1.0f, 1.0f);  // ✅ FIXED
                })
            )));

        // 7. RUBY HEART - Sweet Berries
        fruits.put("ruby_heart", new Fruit("ruby_heart", "§c§lRuby Heart", Material.SWEET_BERRIES,
            Arrays.asList(
                new Ability("§cRuby Trap", 20, p -> {
                    p.getLocation().getBlock().setType(Material.REDSTONE_BLOCK);
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_STONE_PLACE, 1.0f, 1.0f);  // ✅ FIXED
                }),
                new Ability("§cHeart Rush", 25, p -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
                    p.getWorld().spawnParticle(Particle.HEART, p.getLocation(), 30, 1, 1, 1);
                }),
                new Ability("§cCrimson Blast", 30, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 4, false, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);  // ✅ FIXED
                })
            )));

        // 8. JADE MELON - Melon Slice
        fruits.put("jade_melon", new Fruit("jade_melon", "§a§lJade Melon", Material.MELON_SLICE,
            Arrays.asList(
                new Ability("§aJade Slam", 20, p -> {
                    p.setVelocity(new org.bukkit.util.Vector(0, 2, 0));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);  // ✅ FIXED
                }),
                new Ability("§aSeed Barrage", 15, p -> {
                    for(int i=0; i<5; i++) {
                        p.launchProjectile(org.bukkit.entity.Egg.class);
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EGG_THROW, 1.0f, 1.0f);  // ✅ FIXED
                }),
                new Ability("§aMelon Cannon", 30, p -> {
                    p.launchProjectile(org.bukkit.entity.Fireball.class);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.0f);  // ✅ FIXED
                })
            )));

        // 9. DRAKE'S TEAR - Chorus Fruit
        fruits.put("drakes_tear", new Fruit("drakes_tear", "§d§lDrake's Tear", Material.CHORUS_FRUIT,
            Arrays.asList(
                new Ability("§dDrake's Breath", 20, p -> {
                    p.getNearbyEntities(5, 5, 5).forEach(e -> e.setFireTicks(60));
                    p.getWorld().spawnParticle(Particle.DRAGON_BREATH, p.getLocation(), 50, 2, 2, 2);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 0.5f);  // ✅ FIXED
                }),
                new Ability("§dDragon Scales", 25, p -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 200, 1));  // ✅ FIXED: DAMAGE_RESISTANCE → RESISTANCE
                    p.getWorld().playSound(p.getLocation(), Sound.ITEM_ARMOR_EQUIP_DIAMOND, 1.0f, 1.0f);  // ✅ FIXED
                }),
                new Ability("§dWyvern Flight", 35, p -> {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.5f);  // ✅ FIXED
                })
            )));

        // 10. PRIMORDIAL ESSENCE - Enchanted Golden Apple (GOD FRUIT)
        fruits.put("primordial_essence", new Fruit("primordial_essence", "§5§lPrimordial Essence", 
            Material.ENCHANTED_GOLDEN_APPLE,
            Arrays.asList(
                new Ability("§c§lONE SHOT", 120, p -> {
                    if(p.getLevel() < 30) {
                        p.sendMessage("§c❌ Need 30 XP levels!");
                        return;
                    }
                    Player target = getTarget(p, 15);
                    if(target != null) {
                        target.setHealth(0);
                        p.setLevel(p.getLevel() - 30);
                        
                        p.getWorld().strikeLightningEffect(target.getLocation());
                        p.getWorld().spawnParticle(Particle.EXPLOSION, target.getLocation(), 1);
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 0.5f);  // ✅ FIXED
                        
                        p.sendMessage("§c§l💀 ONE SHOT! Killed " + target.getName());
                        Bukkit.broadcastMessage("§5§l" + p.getName() + " §dused ONE SHOT!");  // ✅ FIXED: Bukkit works now
                    }
                }),
                new Ability("§5God's Wrath", 60, p -> {
                    for(int i=0; i<8; i++) {
                        p.getWorld().strikeLightning(p.getLocation().add(Math.random()*10-5, 0, Math.random()*10-5));
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f, 0.8f);  // ✅ FIXED
                }),
                new Ability("§5Divine Shield", 90, p -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 300, 3));  // ✅ FIXED: DAMAGE_RESISTANCE → RESISTANCE
                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 300, 4));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 2));
                    p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation(), 100, 2, 3, 2);
                    p.getWorld().playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 2.0f, 0.5f);  // ✅ FIXED
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
