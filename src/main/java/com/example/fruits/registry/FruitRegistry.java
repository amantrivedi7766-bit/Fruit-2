package com.example.fruits.registry;

import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;

public class FruitRegistry {
    private final Map<String, Fruit> fruits = new HashMap<>();

    public FruitRegistry() {
        // 1. CRIMSON STAR - APPLE
        fruits.put("crimson_star", new Fruit("crimson_star", "Crimson Star", Material.APPLE,
            Arrays.asList(
                new Ability("Vine Grab", 20, p -> {
                    Player target = getTarget(p);
                    if(target != null) target.teleport(p.getLocation());
                }),
                new Ability("Crimson Slash", 15, p -> {
                    p.getNearbyEntities(5, 3, 5).forEach(e -> e.setVelocity(e.getVelocity().setY(1)));
                }),
                new Ability("Nature's Wrath", 30, p -> {
                    p.getWorld().strikeLightning(p.getTargetBlock(null, 20).getLocation());
                })
            )));

        // 2. MOON CRESCENT - GOLDEN_CARROT
        fruits.put("moon_crescent", new Fruit("moon_crescent", "Moon Crescent", Material.GOLDEN_CARROT,
            Arrays.asList(
                new Ability("Lunar Slip", 15, p -> p.setVelocity(p.getLocation().getDirection().multiply(2))),
                new Ability("Crescent Throw", 20, p -> p.launchProjectile(org.bukkit.entity.Arrow.class)),
                new Ability("Lunar Frenzy", 30, p -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
                })
            )));

        // 3. BLOOD GEM - SWEET_BERRIES
        fruits.put("blood_gem", new Fruit("blood_gem", "Blood Gem", Material.SWEET_BERRIES,
            Arrays.asList(
                new Ability("Blood Bomb", 20, p -> p.getWorld().createExplosion(p.getLocation(), 2, false, false)),
                new Ability("Crimson Bloom", 25, p -> p.setHealth(Math.min(p.getHealth() + 4, p.getMaxHealth()))),
                new Ability("Sanguine Beam", 30, p -> {
                    p.getNearbyEntities(10, 5, 10).forEach(e -> e.setFireTicks(100));
                })
            )));

        // 4. VOID CLUSTER - GLOW_BERRIES
        fruits.put("void_cluster", new Fruit("void_cluster", "Void Cluster", Material.GLOW_BERRIES,
            Arrays.asList(
                new Ability("Void Splash", 15, p -> p.getWorld().createExplosion(p.getLocation(), 3)),
                new Ability("Cluster Shot", 20, p -> {
                    for(int i=0; i<3; i++) p.launchProjectile(org.bukkit.entity.Snowball.class);
                }),
                new Ability("Abyssal Whip", 25, p -> {
                    p.getNearbyEntities(5, 5, 5).forEach(e -> e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(-2)));
                })
            )));

        // 5. SOLAR ORB - ORANGE_DYE
        fruits.put("solar_orb", new Fruit("solar_orb", "Solar Orb", Material.ORANGE_DYE,
            Arrays.asList(
                new Ability("Solar Slice", 15, p -> p.getWorld().strikeLightning(p.getTargetBlock(null, 20).getLocation())),
                new Ability("Radiant Spray", 20, p -> {
                    p.getNearbyEntities(5, 5, 5).forEach(e -> e.setFireTicks(40));
                }),
                new Ability("Supernova", 30, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 5, true, true);
                })
            )));

        // 6. THORNED CROWN - PUMPKIN_PIE
        fruits.put("thorned_crown", new Fruit("thorned_crown", "Thorned Crown", Material.PUMPKIN_PIE,
            Arrays.asList(
                new Ability("Thorned Roll", 20, p -> p.setVelocity(p.getLocation().getDirection().multiply(3))),
                new Ability("Crown Launcher", 25, p -> p.launchProjectile(org.bukkit.entity.Fireball.class)),
                new Ability("Nature's Storm", 35, p -> {
                    p.getWorld().setStorm(true);
                    p.getWorld().setThundering(true);
                })
            )));

        // 7. RUBY HEART - SWEET_BERRIES
        fruits.put("ruby_heart", new Fruit("ruby_heart", "Ruby Heart", Material.SWEET_BERRIES,
            Arrays.asList(
                new Ability("Ruby Trap", 20, p -> {
                    p.getLocation().getBlock().setType(Material.REDSTONE_BLOCK);
                }),
                new Ability("Heart Rush", 25, p -> p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1))),
                new Ability("Crimson Blast", 30, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 4, false, true);
                })
            )));

        // 8. JADE MELON - MELON_SLICE
        fruits.put("jade_melon", new Fruit("jade_melon", "Jade Melon", Material.MELON_SLICE,
            Arrays.asList(
                new Ability("Jade Slam", 20, p -> {
                    p.setVelocity(new org.bukkit.util.Vector(0, 2, 0));
                }),
                new Ability("Seed Barrage", 15, p -> {
                    for(int i=0; i<5; i++) p.launchProjectile(org.bukkit.entity.Egg.class);
                }),
                new Ability("Melon Cannon", 30, p -> {
                    p.launchProjectile(org.bukkit.entity.Fireball.class);
                })
            )));

        // 9. DRAKE'S TEAR - CHORUS_FRUIT
        fruits.put("drakes_tear", new Fruit("drakes_tear", "Drake's Tear", Material.CHORUS_FRUIT,
            Arrays.asList(
                new Ability("Drake's Breath", 20, p -> {
                    p.getNearbyEntities(5, 5, 5).forEach(e -> e.setFireTicks(60));
                }),
                new Ability("Dragon Scales", 25, p -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1));
                }),
                new Ability("Wyvern Flight", 35, p -> {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                })
            )));

        // 10. PRIMORDIAL ESSENCE - ENCHANTED_GOLDEN_APPLE (GOD FRUIT)
        fruits.put("primordial_essence", new Fruit("primordial_essence", "Primordial Essence", 
            Material.ENCHANTED_GOLDEN_APPLE,
            Arrays.asList(
                new Ability("One Shot", 120, p -> {
                    if(p.getLevel() < 30) {
                        p.sendMessage("§c❌ Need 30 XP levels!");
                        return;
                    }
                    Player target = getTarget(p);
                    if(target != null) {
                        target.setHealth(0);
                        p.setLevel(p.getLevel() - 30);
                        p.sendMessage("§c💀 ONE SHOT!");
                    }
                }),
                new Ability("God's Wrath", 60, p -> {
                    for(int i=0; i<5; i++) {
                        p.getWorld().strikeLightning(p.getLocation().add(Math.random()*10-5, 0, Math.random()*10-5));
                    }
                }),
                new Ability("Divine Shield", 90, p -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 3));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 2));
                })
            )));
    }

    private Player getTarget(Player p) {
        return p.getWorld().getNearbyPlayers(p.getLocation(), 10).stream()
            .filter(e -> !e.equals(p)).findFirst().orElse(null);
    }

    public Fruit getFruit(String id) { return fruits.get(id); }
    public Collection<Fruit> getAllFruits() { return fruits.values(); }
}
