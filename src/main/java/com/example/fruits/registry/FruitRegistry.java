package com.example.fruits.registry;

import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class FruitRegistry {
    private final Map<String, Fruit> fruits = new HashMap<>();

    public FruitRegistry() {
        registerFruits();
    }

    private void registerFruits() {
        // ==================== 1. NATURE DYE ====================
        fruits.put("nature_dye", new Fruit("nature_dye", "§a§l🌿 Nature Dye", Material.GREEN_DYE, 1001,
            Arrays.asList(
                new Ability("§aVine Attach", 25, (p, target) -> {
                    if(!(target instanceof Player)) {
                        p.sendMessage("§cTarget a player!");
                        return;
                    }
                    Player attached = (Player) target;
                    new BukkitRunnable() {
                        int timer = 0;
                        @Override
                        public void run() {
                            if(timer >= 300 || !attached.isOnline()) {
                                attached.sendMessage("§aVine attach ended!");
                                this.cancel();
                                return;
                            }
                            attached.teleport(p.getEyeLocation().add(p.getLocation().getDirection().multiply(2)));
                            attached.getWorld().spawnParticle(Particle.HEART, attached.getLocation(), 20, 0.3, 0.3, 0.3);
                            timer++;
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 1L);
                    p.sendMessage("§a🌿 Vine Attach on " + attached.getName() + "!");
                }),
                new Ability("§aOak Hammer", 35, (p, target) -> {
                    Location targetLoc = target != null ? target.getLocation() : p.getTargetBlock(null, 10).getLocation();
                    ArmorStand hammer = (ArmorStand) p.getWorld().spawnEntity(p.getEyeLocation(), EntityType.ARMOR_STAND);
                    hammer.setVisible(false);
                    hammer.setGravity(false);
                    hammer.setItemInHand(new ItemStack(Material.OAK_WOOD));
                    hammer.setRightArmPose(new org.bukkit.util.EulerAngle(Math.toRadians(90), 0, 0));
                    
                    new BukkitRunnable() {
                        int height = 0;
                        @Override
                        public void run() {
                            if(height >= 20) {
                                hammer.remove();
                                targetLoc.getWorld().createExplosion(targetLoc, 3, false, true);
                                targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.8f);
                                targetLoc.getWorld().spawnParticle(Particle.EXPLOSION, targetLoc, 1);
                                targetLoc.getWorld().getNearbyEntities(targetLoc, 4, 4, 4).forEach(e -> {
                                    if(e != p && e instanceof LivingEntity) ((LivingEntity) e).damage(12, p);
                                });
                                this.cancel();
                                return;
                            }
                            Location hammerLoc = targetLoc.clone().add(0, 5 - height, 0);
                            hammer.teleport(hammerLoc);
                            p.getWorld().spawnParticle(Particle.BLOCK, hammerLoc, 30, 0.5, 0.2, 0.5, Material.OAK_LOG.createBlockData());
                            height++;
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 1L);
                    p.sendMessage("§a🔨 Oak Hammer summoned!");
                })
            )));

        // ==================== 2. WATER DYE ====================
        fruits.put("water_dye", new Fruit("water_dye", "§b§l💧 Water Dye", Material.LIGHT_BLUE_DYE, 1002,
            Arrays.asList(
                new Ability("§bWater Geyser", 20, (p, target) -> {
                    p.getNearbyEntities(8, 5, 8).forEach(e -> {
                        Location under = e.getLocation().clone().add(0, -1, 0);
                        under.getBlock().setType(Material.WATER);
                        e.setVelocity(new Vector(0, 3, 0));
                        e.getWorld().playSound(e.getLocation(), Sound.ENTITY_GENERIC_SPLASH, 1.0f, 1.2f);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                under.getBlock().setType(Material.AIR);
                            }
                        }.runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), 20L);
                    });
                    p.sendMessage("§b💧 Water Geyser erupted!");
                }),
                new Ability("§bWater Wave", 30, (p, target) -> {
                    Location start = p.getLocation();
                    Vector direction = p.getLocation().getDirection().normalize();
                    new BukkitRunnable() {
                        int distance = 0;
                        @Override
                        public void run() {
                            if(distance >= 20) {
                                this.cancel();
                                return;
                            }
                            Location waveLoc = start.clone().add(direction.clone().multiply(distance));
                            waveLoc.getWorld().spawnParticle(Particle.WATER_BUBBLE, waveLoc, 50, 1, 0.5, 1, 0.1);
                            waveLoc.getWorld().playSound(waveLoc, Sound.ENTITY_GENERIC_SPLASH, 1.0f, 1.0f);
                            waveLoc.getWorld().getNearbyEntities(waveLoc, 3, 2, 3).forEach(e -> {
                                if(e != p && e instanceof LivingEntity) {
                                    ((LivingEntity) e).damage(6, p);
                                    e.setVelocity(direction.clone().multiply(2));
                                }
                            });
                            distance++;
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 2L);
                    p.sendMessage("§b🌊 Water Wave launched!");
                })
            )));

        // ==================== 3. CYCLONE DYE ====================
        fruits.put("cyclone_dye", new Fruit("cyclone_dye", "§3§l🌀 Cyclone Dye", Material.CYAN_DYE, 1003,
            Arrays.asList(
                new Ability("§3Speed Tornado", 25, (p, target) -> {
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, 200, 3));
                    p.getNearbyEntities(10, 8, 10).forEach(e -> {
                        if(e != p && e instanceof LivingEntity) {
                            e.setVelocity(new Vector(0, 2, 0));
                            new BukkitRunnable() {
                                int angle = 0;
                                @Override
                                public void run() {
                                    if(angle >= 360) { this.cancel(); return; }
                                    double rad = Math.toRadians(angle);
                                    double x = Math.cos(rad) * 2;
                                    double z = Math.sin(rad) * 2;
                                    e.teleport(e.getLocation().add(x, 0.2, z));
                                    e.getWorld().spawnParticle(Particle.CLOUD, e.getLocation(), 10, 0.2, 0.2, 0.2);
                                    angle += 20;
                                }
                            }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 1L);
                        }
                    });
                    p.sendMessage("§3🌀 Speed Tornado activated!");
                }),
                new Ability("§3Block Tornado", 40, (p, target) -> {
                    List<org.bukkit.block.Block> blocks = new ArrayList<>();
                    for(int x = -3; x <= 3; x++) {
                        for(int z = -3; z <= 3; z++) {
                            org.bukkit.block.Block b = p.getLocation().add(x, 0, z).getBlock();
                            if(b.getType() != Material.AIR) {
                                blocks.add(b);
                            }
                        }
                    }
                    new BukkitRunnable() {
                        int angle = 0;
                        int height = 0;
                        @Override
                        public void run() {
                            if(height >= 50) {
                                for(org.bukkit.block.Block b : blocks) b.setType(Material.AIR);
                                this.cancel();
                                return;
                            }
                            for(org.bukkit.block.Block b : blocks) {
                                double rad = Math.toRadians(angle + b.getX() * 10);
                                double x = Math.cos(rad) * 2;
                                double z = Math.sin(rad) * 2;
                                Location loc = b.getLocation().add(x, height * 0.2, z);
                                p.getWorld().spawnParticle(Particle.BLOCK, loc, 5, 0.1, 0.1, 0.1, b.getBlockData());
                                if(height % 10 == 0) {
                                    loc.getWorld().getNearbyEntities(loc, 1, 1, 1).forEach(e -> {
                                        if(e != p && e instanceof LivingEntity) ((LivingEntity) e).damage(5, p);
                                    });
                                }
                            }
                            angle += 15;
                            height++;
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 1L);
                    p.sendMessage("§3🌪️ Block Tornado summoned!");
                })
            )));

        // ==================== 4. DRACULA DYE ====================
        fruits.put("dracula_dye", new Fruit("dracula_dye", "§c§l🦇 Dracula Dye", Material.RED_DYE, 1004,
            Arrays.asList(
                new Ability("§cVampire Phase", 20, (p, target) -> {
                    p.sendMessage("§c🦇 Vampire Phase active for 15 seconds! Every 3 hits heals 1 heart!");
                    new BukkitRunnable() {
                        int hits = 0;
                        int ticks = 0;
                        @Override
                        public void run() {
                            if(ticks >= 300) {
                                p.sendMessage("§cVampire phase ended!");
                                this.cancel();
                                return;
                            }
                            if(hits >= 3) {
                                p.setHealth(Math.min(p.getHealth() + 2, p.getMaxHealth()));
                                p.getWorld().spawnParticle(Particle.HEART, p.getLocation(), 20, 0.5, 0.5, 0.5);
                                hits = 0;
                            }
                            ticks++;
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 1L);
                }),
                new Ability("§cBat Ride", 45, (p, target) -> {
                    Bat bat = (Bat) p.getWorld().spawnEntity(p.getLocation(), EntityType.BAT);
                    bat.setAI(false);
                    bat.addPassenger(p);
                    p.sendMessage("§c🦇 You are riding a bat! Left-click to attack!");
                    
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(!p.isOnline() || bat.isDead()) {
                                this.cancel();
                                return;
                            }
                            Vector dir = p.getLocation().getDirection().normalize();
                            bat.setVelocity(dir.multiply(1.5));
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 1L);
                    
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            bat.remove();
                        }
                    }.runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), 200L);
                })
            )));

        // ==================== 5. PORTAL DYE ====================
        fruits.put("portal_dye", new Fruit("portal_dye", "§5§l🌀 Portal Dye", Material.PURPLE_DYE, 1005,
            Arrays.asList(
                new Ability("§5Portal Link", 30, (p, target) -> {
                    Location portal1 = p.getTargetBlock(null, 30).getLocation();
                    p.sendMessage("§5First portal set! Click again within 20 seconds!");
                    new BukkitRunnable() {
                        boolean portal2Set = false;
                        Location portal2 = null;
                        @Override
                        public void run() {
                            if(!portal2Set) {
                                portal2 = p.getTargetBlock(null, 30).getLocation();
                                portal2Set = true;
                                p.sendMessage("§5Portals linked!");
                                for(int i = 0; i < 360; i += 10) {
                                    double rad = Math.toRadians(i);
                                    double x = Math.cos(rad) * 2;
                                    double z = Math.sin(rad) * 2;
                                    portal1.clone().add(x, 0, z).getWorld().spawnParticle(Particle.PORTAL, portal1.clone().add(x, 0, z), 1);
                                    portal2.clone().add(x, 0, z).getWorld().spawnParticle(Particle.PORTAL, portal2.clone().add(x, 0, z), 1);
                                }
                            }
                            if(portal2Set) {
                                portal1.getWorld().getNearbyEntities(portal1, 2, 2, 2).forEach(e -> e.teleport(portal2));
                                portal2.getWorld().getNearbyEntities(portal2, 2, 2, 2).forEach(e -> e.teleport(portal1));
                            }
                        }
                    }.runTaskTimer(com.example.fruits.FruitsPlugin.getInstance(), 0L, 1L);
                }),
                new Ability("§5Summon Portal", 120, (p, target) -> {
                    Location portal = p.getTargetBlock(null, 50).getLocation();
                    portal.getBlock().setType(Material.NETHER_PORTAL);
                    p.sendMessage("§5Portal created! Right-click to summon a player!");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            portal.getBlock().setType(Material.AIR);
                        }
                    }.runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), 200L);
                })
            )));

        // ==================== 6. THRONE DYE ====================
        fruits.put("throne_dye", new Fruit("throne_dye", "§6§l👑 Throne Dye", Material.YELLOW_DYE, 1006,
            Arrays.asList(
                new Ability("§6Royal Shield", 25, (p, target) -> {
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE, 300, 1));
                    p.getWorld().spawnParticle(Particle.ENCHANT, p.getLocation(), 50, 1, 2, 1);
                    p.sendMessage("§6🛡️ Royal Shield active for 15 seconds!");
                }),
                new Ability("§6Stone Wall", 35, (p, target) -> {
                    Location wallStart = p.getLocation().add(p.getLocation().getDirection().multiply(3));
                    List<org.bukkit.block.Block> wallBlocks = new ArrayList<>();
                    for(int x = -3; x <= 3; x++) {
                        org.bukkit.block.Block b = wallStart.clone().add(x, 0, 0).getBlock();
                        b.setType(Material.STONE);
                        wallBlocks.add(b);
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for(org.bukkit.block.Block b : wallBlocks) b.setType(Material.AIR);
                        }
                    }.runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), 300L);
                    p.sendMessage("§6🧱 Stone Wall created!");
                })
            )));

        // ==================== 7. THIEF DYE ====================
        fruits.put("thief_dye", new Fruit("thief_dye", "§8§l🗡️ Thief Dye", Material.BLACK_DYE, 1007,
            Arrays.asList(
                new Ability("§8Ability Steal", 120, (p, target) -> {
                    p.sendMessage("§8Select a player to steal from!");
                    p.getNearbyEntities(30, 30, 30).forEach(e -> {
                        if(e instanceof Player && e != p) {
                            ((Player) e).setWalkSpeed(0);
                            ((Player) e).sendMessage("§8You have been frozen by a thief!");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    ((Player) e).setWalkSpeed(0.2f);
                                }
                            }.runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), 100L);
                        }
                    });
                }),
                new Ability("§8Decoy", 40, (p, target) -> {
                    ArmorStand decoy = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
                    decoy.setCustomName("§cDECOY");
                    decoy.setCustomNameVisible(true);
                    decoy.setItemInHand(p.getInventory().getItemInMainHand());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            decoy.remove();
                        }
                    }.runTaskLater(com.example.fruits.FruitsPlugin.getInstance(), 100L);
                    p.sendMessage("§8🎭 Decoy summoned!");
                })
            )));

        // ==================== 8. STAR DYE ====================
        fruits.put("star_dye", new Fruit("star_dye", "§e§l⭐ Star Dye", Material.ORANGE_DYE, 1008,
            Arrays.asList(
                new Ability("§eShooting Star", 25, (p, target) -> {
                    Location targetLoc = target != null ? target.getLocation() : p.getTargetBlock(null, 30).getLocation();
                    for(int i = 0; i < 10; i++) {
                        Location starLoc = targetLoc.clone().add(Math.random()*10-5, 10, Math.random()*10-5);
                        p.getWorld().spawnParticle(Particle.FIREWORK, starLoc, 20, 0.2, 0.2, 0.2);
                        starLoc.getWorld().createExplosion(starLoc, 1, false, false);
                    }
                    p.sendMessage("§e⭐ Shooting Star summoned!");
                }),
                new Ability("§eMeteor Rain", 40, (p, target) -> {
                    for(int i = 0; i < 20; i++) {
                        Location meteor = p.getLocation().add(Math.random()*20-10, 15, Math.random()*20-10);
                        meteor.getWorld().strikeLightning(meteor);
                    }
                    p.sendMessage("§e☄️ Meteor Rain activated!");
                })
            )));

        // ==================== 9. SHADOW DYE ====================
        fruits.put("shadow_dye", new Fruit("shadow_dye", "§7§l🌑 Shadow Dye", Material.GRAY_DYE, 1009,
            Arrays.asList(
                new Ability("§7Shadow Cloak", 30, (p, target) -> {
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.INVISIBILITY, 200, 1));
                    p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    p.sendMessage("§7🌑 Shadow Cloak activated!");
                }),
                new Ability("§7Dark Pulse", 35, (p, target) -> {
                    p.getNearbyEntities(8, 5, 8).forEach(e -> {
                        if(e != p && e instanceof LivingEntity) {
                            ((LivingEntity) e).damage(8, p);
                            e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().multiply(2));
                        }
                    });
                    p.sendMessage("§7⚫ Dark Pulse released!");
                })
            )));

        // ==================== 10. PRIMORDIAL DYE ====================
        fruits.put("primordial_dye", new Fruit("primordial_dye", "§5§l✨ Primordial Dye", Material.MAGENTA_DYE, 1010,
            Arrays.asList(
                new Ability("§c§l💀 ONE SHOT", 110, (p, target) -> {
                    if(p.getLevel() < 30) {
                        p.sendMessage("§c❌ Need 30 XP levels!");
                        return;
                    }
                    if(target instanceof Player) {
                        ((Player) target).setHealth(0);
                        p.setLevel(p.getLevel() - 30);
                        p.getWorld().strikeLightningEffect(target.getLocation());
                        p.sendMessage("§c§l💀 ONE SHOT! Killed " + ((Player) target).getName());
                    }
                }),
                new Ability("§5Divine Protection", 85, (p, target) -> {
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE, 400, 3));
                    p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.REGENERATION, 400, 2));
                    p.sendMessage("§5✨ Divine Protection activated!");
                })
            )));
    }

    public Fruit getFruit(String id) { return fruits.get(id); }
    public Collection<Fruit> getAllFruits() { return fruits.values(); }
}
