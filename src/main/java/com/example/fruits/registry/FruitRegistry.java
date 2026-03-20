package com.example.fruits.registry;

import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.util.*;

public class FruitRegistry {
    private final Map<String, Fruit> fruits = new HashMap<>();

    public FruitRegistry() {
        // 1. CRIMSON STAR
        fruits.put("crimson_star", new Fruit("crimson_star", "§c§l⚡ Crimson Star", Material.APPLE,
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
                    p.getWorld().spawnParticle(Particle.EXPLOSION, p.getLocation(), 1);
                    p.sendMessage("§c☄️ METEOR CRASH!");
                }),
                new Ability("§cInferno Blast", 30, p -> {
                    p.getNearbyEntities(8, 5, 8).forEach(e -> e.setFireTicks(100));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.8f);
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 50, 3, 2, 3);
                    p.sendMessage("§c🔥 INFERNO BLAST!");
                })
            )));

        // 2. MOON CRESCENT
        fruits.put("moon_crescent", new Fruit("moon_crescent", "§e§l🌙 Moon Crescent", Material.GOLDEN_CARROT,
            Arrays.asList(
                new Ability("§eLunar Beam", 20, p -> {
                    Player target = getTarget(p, 15);
                    if(target != null) {
                        target.damage(12, p);
                        target.getWorld().strikeLightningEffect(target.getLocation());
                        target.getWorld().playSound(target.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.0f);
                        p.sendMessage("§e🌙 LUNAR BEAM on " + target.getName());
                    }
                }),
                new Ability("§eMoon Gravity", 25, p -> {
                    p.getNearbyEntities(7, 5, 7).forEach(e -> e.setVelocity(e.getVelocity().setY(2)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.5f);
                    p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 40, 2, 1, 2);
                    p.sendMessage("§e🌕 MOON GRAVITY!");
                }),
                new Ability("§eCosmic Wave", 30, p -> {
                    p.getNearbyEntities(10, 6, 10).forEach(e -> e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).multiply(1.5)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.6f);
                    p.getWorld().spawnParticle(Particle.DRAGON_BREATH, p.getLocation(), 60, 4, 2, 4);
                    p.sendMessage("§e🌊 COSMIC WAVE!");
                })
            )));

        // 3. BLOOD GEM
        fruits.put("blood_gem", new Fruit("blood_gem", "§c§l💎 Blood Gem", Material.SWEET_BERRIES,
            Arrays.asList(
                new Ability("§cBlood Explosion", 20, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 3, false, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.7f);
                    p.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 80, 2, 1, 2, Material.REDSTONE_BLOCK.createBlockData());
                    p.sendMessage("§c💥 BLOOD EXPLOSION!");
                }),
                new Ability("§cVampiric Drain", 25, p -> {
                    p.getNearbyEntities(5, 3, 5).forEach(e -> {
                        if(e instanceof Player) {
                            Player target = (Player) e;
                            target.damage(8, p);
                            p.setHealth(Math.min(p.getHealth() + 4, p.getMaxHealth()));
                        }
                    });
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 0.8f);
                    p.getWorld().spawnParticle(Particle.HEART, p.getLocation(), 30, 2, 1, 2);
                    p.sendMessage("§c🩸 VAMPIRIC DRAIN!");
                }),
                new Ability("§cBlood Storm", 30, p -> {
                    for(int i=0; i<5; i++) {
                        p.getWorld().strikeLightning(p.getLocation().add(Math.random()*8-4, 0, Math.random()*8-4));
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.5f);
                    p.getWorld().spawnParticle(Particle.FIREWORK, p.getLocation(), 60, 3, 2, 3);
                    p.sendMessage("§c🌩️ BLOOD STORM!");
                })
            )));

        // 4. VOID CLUSTER
        fruits.put("void_cluster", new Fruit("void_cluster", "§5§l🌌 Void Cluster", Material.GLOW_BERRIES,
            Arrays.asList(
                new Ability("§5Void Pull", 20, p -> {
                    p.getNearbyEntities(8, 5, 8).forEach(e -> e.teleport(p.getLocation().add(0, 1, 0)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
                    p.getWorld().spawnParticle(Particle.PORTAL, p.getLocation(), 50, 2, 1, 2);
                    p.sendMessage("§5🌀 VOID PULL!");
                }),
                new Ability("§5Black Hole", 25, p -> {
                    p.getNearbyEntities(6, 4, 6).forEach(e -> e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(-1.5)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.3f);
                    p.getWorld().spawnParticle(Particle.DRAGON_BREATH, p.getLocation(), 70, 3, 2, 3);
                    p.sendMessage("§5🕳️ BLACK HOLE!");
                }),
                new Ability("§5Void Eruption", 30, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 5, true, false);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.4f);
                    p.getWorld().spawnParticle(Particle.EXPLOSION, p.getLocation(), 1);
                    p.sendMessage("§5🌋 VOID ERUPTION!");
                })
            )));

        // 5. SOLAR ORB
        fruits.put("solar_orb", new Fruit("solar_orb", "§6§l☀️ Solar Orb", Material.ORANGE_DYE,
            Arrays.asList(
                new Ability("§6Solar Flare", 20, p -> {
                    p.getWorld().createExplosion(p.getTargetBlock(null, 15).getLocation(), 4, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.2f);
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 60, 3, 2, 3);
                    p.sendMessage("§6☀️ SOLAR FLARE!");
                }),
                new Ability("§6Sun Beam", 25, p -> {
                    Player target = getTarget(p, 20);
                    if(target != null) {
                        target.setFireTicks(80);
                        target.damage(10, p);
                        target.getWorld().strikeLightningEffect(target.getLocation());
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.5f);
                    p.sendMessage("§6🔆 SUN BEAM!");
                }),
                new Ability("§6Supernova", 30, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 6, true, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.6f);
                    p.getWorld().spawnParticle(Particle.FIREWORK, p.getLocation(), 100, 4, 3, 4);
                    p.sendMessage("§6💥 SUPERNOVA!");
                })
            )));

        // 6. THORNED CROWN
        fruits.put("thorned_crown", new Fruit("thorned_crown", "§a§l👑 Thorned Crown", Material.PUMPKIN_PIE,
            Arrays.asList(
                new Ability("§aThorn Vines", 20, p -> {
                    p.getNearbyEntities(6, 4, 6).forEach(e -> e.setVelocity(e.getVelocity().setY(-1)));
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_GRASS_BREAK, 1.0f, 0.7f);
                    p.getWorld().spawnParticle(Particle.ITEM_CRACK, p.getLocation(), 50, 2, 1, 2, new org.bukkit.inventory.ItemStack(Material.VINE));
                    p.sendMessage("§a🌿 THORN VINES!");
                }),
                new Ability("§aNature's Wrath", 25, p -> {
                    for(int i=0; i<3; i++) {
                        p.getWorld().strikeLightning(p.getTargetBlock(null, 15).getLocation().add(Math.random()*3-1.5, 0, Math.random()*3-1.5));
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.8f);
                    p.sendMessage("§a🌳 NATURE'S WRATH!");
                }),
                new Ability("§aForest Fury", 30, p -> {
                    p.getNearbyEntities(10, 6, 10).forEach(e -> e.setVelocity(e.getVelocity().setY(2).multiply(1.5)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.5f);
                    p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, p.getLocation(), 80, 3, 2, 3);
                    p.sendMessage("§a🌲 FOREST FURY!");
                })
            )));

        // 7. RUBY HEART
        fruits.put("ruby_heart", new Fruit("ruby_heart", "§c§l❤️ Ruby Heart", Material.SWEET_BERRIES,
            Arrays.asList(
                new Ability("§cHeart Pulse", 20, p -> {
                    p.getNearbyEntities(5, 3, 5).forEach(e -> e.setVelocity(e.getVelocity().setY(1.5)));
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.8f);
                    p.getWorld().spawnParticle(Particle.HEART, p.getLocation(), 40, 2, 1, 2);
                    p.sendMessage("§c💓 HEART PULSE!");
                }),
                new Ability("§cCrimson Wave", 25, p -> {
                    p.getNearbyEntities(7, 4, 7).forEach(e -> e.setFireTicks(60));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.0f, 0.9f);
                    p.getWorld().spawnParticle(Particle.FLAME, p.getLocation(), 50, 2, 1, 2);
                    p.sendMessage("§c🌊 CRIMSON WAVE!");
                }),
                new Ability("§cHeart Break", 30, p -> {
                    p.getWorld().createExplosion(p.getLocation(), 4, false, true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 1.0f, 0.7f);
                    p.getWorld().spawnParticle(Particle.EXPLOSION, p.getLocation(), 1);
                    p.sendMessage("§c💔 HEART BREAK!");
                })
            )));

        // 8. JADE MELON
        fruits.put("jade_melon", new Fruit("jade_melon", "§a§l🍈 Jade Melon", Material.MELON_SLICE,
            Arrays.asList(
                new Ability("§aJade Slam", 20, p -> {
                    p.setVelocity(p.getLocation().getDirection().multiply(2).setY(1.5));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.6f);
                    p.getWorld().spawnParticle(Particle.BLOCK_CRACK, p.getLocation(), 50, 1, 1, 1, Material.MELON.createBlockData());
                    p.sendMessage("§a🔨 JADE SLAM!");
                }),
                new Ability("§aMelon Rain", 25, p -> {
                    for(int i=0; i<10; i++) {
                        p.getWorld().spawn(p.getLocation().add(Math.random()*5-2.5, 3, Math.random()*5-2.5), org.bukkit.entity.Egg.class);
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EGG_THROW, 1.0f, 1.2f);
                    p.sendMessage("§a🍉 MELON RAIN!");
                }),
                new Ability("§aJade Cannon", 30, p -> {
                    org.bukkit.entity.Fireball fb = p.launchProjectile(org.bukkit.entity.Fireball.class);
                    fb.setVelocity(p.getLocation().getDirection().multiply(2));
                    fb.setYield(3);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 0.8f);
                    p.sendMessage("§a🚀 JADE CANNON!");
                })
            )));

        // 9. DRAKE'S TEAR
        fruits.put("drakes_tear", new Fruit("drakes_tear", "§d§l🐉 Drake's Tear", Material.CHORUS_FRUIT,
            Arrays.asList(
                new Ability("§dDragon Breath", 20, p -> {
                    p.getNearbyEntities(8, 5, 8).forEach(e -> e.setFireTicks(80));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 1.0f, 0.5f);
                    p.getWorld().spawnParticle(Particle.DRAGON_BREATH, p.getLocation(), 60, 3, 2, 3);
                    p.sendMessage("§d🐉 DRAGON BREATH!");
                }),
                new Ability("§dWyvern Flight", 25, p -> {
                    p.setAllowFlight(true);
                    p.setFlying(true);
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.7f);
                    p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 40, 1, 1, 1);
                    p.sendMessage("§d🪽 WYVERN FLIGHT!");
                    // Disable after 5 seconds
                    org.bukkit.Bukkit.getScheduler().runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), () -> {
                        p.setFlying(false);
                        p.setAllowFlight(false);
                    }, 100);
                }),
                new Ability("§dDrake's Roar", 30, p -> {
                    p.getNearbyEntities(12, 7, 12).forEach(e -> e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(2)));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.4f);
                    p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation(), 70, 3, 2, 3);
                    p.sendMessage("§d🗣️ DRAKE'S ROAR!");
                })
            )));

        // 10. PRIMORDIAL ESSENCE (GOD FRUIT)
        fruits.put("primordial_essence", new Fruit("primordial_essence", "§5§l✨ Primordial Essence", 
            Material.ENCHANTED_GOLDEN_APPLE,
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
                        p.getWorld().spawnParticle(Particle.EXPLOSION, target.getLocation(), 1);
                        p.sendMessage("§c§l💀 ONE SHOT! Killed " + target.getName());
                        org.bukkit.Bukkit.broadcastMessage("§5§l✨ " + p.getName() + " §dused ONE SHOT on §5" + target.getName());
                    }
                }),
                new Ability("§5⚡ God's Wrath", 60, p -> {
                    for(int i=0; i<10; i++) {
                        p.getWorld().strikeLightning(p.getLocation().add(Math.random()*12-6, 0, Math.random()*12-6));
                    }
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.0f, 0.6f);
                    p.getWorld().spawnParticle(Particle.FIREWORK, p.getLocation(), 100, 4, 3, 4);
                    p.sendMessage("§5⚡ GOD'S WRATH!");
                }),
                new Ability("§5🛡️ Divine Shield", 90, p -> {
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE, 300, 3));
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.ABSORPTION, 300, 4));
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.REGENERATION, 300, 2));
                    p.getWorld().playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 2.0f, 0.5f);
                    p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation(), 100, 2, 3, 2);
                    p.sendMessage("§5🛡️ DIVINE SHIELD!");
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
