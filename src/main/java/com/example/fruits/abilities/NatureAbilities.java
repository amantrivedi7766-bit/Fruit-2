package com.example.fruits.abilities;

import com.example.fruits.FruitsPlugin;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class NatureAbilities {
    
    // Store attached players: Target -> Owner
    private static final Map<UUID, UUID> attachedPlayers = new HashMap<>();
    private static final Map<UUID, Integer> attachTasks = new HashMap<>();
    
    // ==================== ABILITY 1: VINE ATTACH ====================
    
    public static void vineAttach(Player player, Entity target) {
        if(!(target instanceof Player)) {
            player.sendMessage("§c❌ You need to target a player!");
            return;
        }
        
        Player targetPlayer = (Player) target;
        
        if(attachedPlayers.containsKey(targetPlayer.getUniqueId())) {
            player.sendMessage("§c❌ This player is already attached to someone!");
            return;
        }
        
        attachedPlayers.put(targetPlayer.getUniqueId(), player.getUniqueId());
        
        player.sendMessage("§a🌿 You attached to §e" + targetPlayer.getName() + "§a! They will follow your cursor!");
        targetPlayer.sendMessage("§e" + player.getName() + " §aattached vines to you! You will follow their cursor for 15 seconds!");
        
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_VINE_STEP, 1.0f, 1.5f);
        player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 30, 0.5, 0.5, 0.5);
        
        BukkitRunnable followTask = new BukkitRunnable() {
            int duration = 0;
            
            @Override
            public void run() {
                if(duration >= 300) {
                    detachPlayer(targetPlayer);
                    this.cancel();
                    return;
                }
                
                if(!targetPlayer.isOnline() || !player.isOnline()) {
                    detachPlayer(targetPlayer);
                    this.cancel();
                    return;
                }
                
                Location cursorLoc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
                Location targetLoc = cursorLoc.clone();
                targetLoc.setYaw(targetPlayer.getLocation().getYaw());
                targetLoc.setPitch(targetPlayer.getLocation().getPitch());
                
                targetPlayer.teleport(targetLoc);
                
                targetPlayer.getWorld().spawnParticle(Particle.HEART, targetPlayer.getLocation(), 15, 0.3, 0.3, 0.3);
                targetPlayer.getWorld().spawnParticle(Particle.END_ROD, targetPlayer.getLocation().add(0, 1, 0), 10, 0.2, 0.5, 0.2);
                
                for(int i = 0; i < 5; i++) {
                    double x = Math.sin(System.currentTimeMillis() / 100.0 + i) * 0.5;
                    double z = Math.cos(System.currentTimeMillis() / 100.0 + i) * 0.5;
                    targetPlayer.getWorld().spawnParticle(Particle.COMPOSTER, 
                        targetPlayer.getLocation().add(x, 0.5, z), 1, 0, 0, 0);
                }
                
                duration++;
            }
        };
        
        int taskId = followTask.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L).getTaskId();
        attachTasks.put(targetPlayer.getUniqueId(), taskId);
        
        player.sendMessage("§e💥 Left-click within 15 seconds to launch them as a cannonball!");
    }
    
    private static void detachPlayer(Player target) {
        UUID ownerId = attachedPlayers.remove(target.getUniqueId());
        Integer taskId = attachTasks.remove(target.getUniqueId());
        
        if(taskId != null) {
            org.bukkit.Bukkit.getScheduler().cancelTask(taskId);
        }
        
        if(ownerId != null) {
            Player owner = org.bukkit.Bukkit.getPlayer(ownerId);
            if(owner != null && owner.isOnline()) {
                owner.sendMessage("§c🌿 Vine attach ended!");
            }
        }
        
        target.sendMessage("§c🌿 Vine attach ended!");
        target.getWorld().playSound(target.getLocation(), Sound.BLOCK_VINE_BREAK, 1.0f, 1.0f);
    }
    
    // ==================== HANDLE LAUNCH FROM LEFT CLICK ====================
    
    public static void handleLaunch(Player player) {
        UUID attachedId = null;
        Player attached = null;
        
        for(Map.Entry<UUID, UUID> entry : attachedPlayers.entrySet()) {
            if(entry.getValue().equals(player.getUniqueId())) {
                attachedId = entry.getKey();
                attached = org.bukkit.Bukkit.getPlayer(attachedId);
                break;
            }
        }
        
        if(attached == null) {
            return;
        }
        
        Vector direction = player.getLocation().getDirection().normalize();
        attached.setVelocity(direction.multiply(3));
        
        attached.getWorld().playSound(attached.getLocation(), Sound.ENTITY_GHAST_SHOOT, 2.0f, 0.8f);
        attached.getWorld().spawnParticle(Particle.EXPLOSION, attached.getLocation(), 5, 0.5, 0.5, 0.5);
        attached.getWorld().spawnParticle(Particle.CLOUD, attached.getLocation(), 30, 0.5, 0.5, 0.5);
        
        player.sendMessage("§a💥 You launched §e" + attached.getName() + "§a like a cannonball!");
        attached.sendMessage("§c💥 You were launched by §e" + player.getName() + "§c!");
        
        detachPlayer(attached);
    }
    
    // ==================== ABILITY 2: OAK HAMMER ====================
    
    public static void oakHammer(Player player, Entity target) {
        player.sendMessage("§6🔨 Summoning Oak Hammer...");
        
        Location targetLoc;
        if(target != null && target instanceof LivingEntity) {
            targetLoc = target.getLocation();
        } else {
            LivingEntity nearest = getNearestEnemy(player, 10);
            if(nearest != null) {
                targetLoc = nearest.getLocation();
            } else {
                targetLoc = player.getTargetBlock(null, 10).getLocation();
            }
        }
        
        ArmorStand hammer = (ArmorStand) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.ARMOR_STAND);
        hammer.setVisible(false);
        hammer.setGravity(false);
        hammer.setInvulnerable(true);
        hammer.setMarker(true);
        
        ItemStack hammerItem = new ItemStack(Material.OAK_WOOD);
        hammer.setItemInHand(hammerItem);
        hammer.setRightArmPose(new org.bukkit.util.EulerAngle(Math.toRadians(90), 0, 0));
        
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 1.0f, 0.5f);
        
        double startY = player.getEyeLocation().getY();
        double endY = targetLoc.getY();
        double distance = startY - endY;
        
        new BukkitRunnable() {
            int height = 0;
            int maxHeight = 30;
            boolean smashing = false;
            int smashFrame = 0;
            
            @Override
            public void run() {
                if(!player.isOnline() || hammer.isDead()) {
                    hammer.remove();
                    this.cancel();
                    return;
                }
                
                if(!smashing) {
                    double progress = (double) height / maxHeight;
                    double y = startY + (distance * progress);
                    Location hammerLoc = targetLoc.clone().add(0, y - endY, 0);
                    hammer.teleport(hammerLoc);
                    
                    player.getWorld().spawnParticle(Particle.CRIT, hammerLoc, 20, 0.3, 0.1, 0.3);
                    player.getWorld().playSound(hammerLoc, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 0.5f, 0.8f);
                    
                    height++;
                    
                    if(height >= maxHeight) {
                        smashing = true;
                        player.getWorld().playSound(hammer.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.2f);
                        player.getWorld().spawnParticle(Particle.FIREWORK, hammer.getLocation(), 30, 0.5, 0.2, 0.5);
                    }
                }
                else {
                    double progress = 1.0 - (double) smashFrame / maxHeight;
                    double y = startY + (distance * progress);
                    Location hammerLoc = targetLoc.clone().add(0, y - endY, 0);
                    hammer.teleport(hammerLoc);
                    
                    // FIXED: BLOCK_CRACK -> BLOCK with block data
                    player.getWorld().spawnParticle(Particle.BLOCK, hammerLoc, 50, 0.5, 0.2, 0.5, Material.OAK_LOG.createBlockData());
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, hammerLoc, 30, 0.5, 0.2, 0.5);
                    player.getWorld().playSound(hammerLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.6f);
                    
                    for(int i = 0; i < 360; i += 15) {
                        double rad = Math.toRadians(i);
                        double x = Math.cos(rad) * 2;
                        double z = Math.sin(rad) * 2;
                        Location groundLoc = hammerLoc.clone().add(x, -0.5, z);
                        player.getWorld().spawnParticle(Particle.CLOUD, groundLoc, 5, 0, 0, 0);
                    }
                    
                    if(smashFrame == maxHeight - 1) {
                        hammer.remove();
                        
                        player.getWorld().createExplosion(targetLoc, 0, false, false);
                        player.getWorld().playSound(targetLoc, Sound.ENTITY_IRON_GOLEM_ATTACK, 2.0f, 0.8f);
                        player.getWorld().playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
                        
                        player.getWorld().spawnParticle(Particle.EXPLOSION, targetLoc, 3);
                        player.getWorld().spawnParticle(Particle.BLOCK, targetLoc, 100, 1, 1, 1, Material.OAK_LOG.createBlockData());
                        player.getWorld().spawnParticle(Particle.FLASH, targetLoc, 5);
                        
                        player.getWorld().getNearbyEntities(targetLoc, 4, 3, 4).forEach(e -> {
                            if(e != player && e instanceof LivingEntity) {
                                LivingEntity living = (LivingEntity) e;
                                living.damage(12, player);
                                
                                Vector kb = living.getLocation().toVector().subtract(targetLoc.toVector()).normalize().multiply(1.5);
                                living.setVelocity(kb.add(new Vector(0, 0.8, 0)));
                                
                                living.getWorld().spawnParticle(Particle.CRIT, living.getLocation(), 30, 0.3, 0.3, 0.3);
                                living.getWorld().playSound(living.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
                            }
                        });
                        
                        // FIXED: BLOCK_CRACK -> BLOCK with block data
                        for(int x = -3; x <= 3; x++) {
                            for(int z = -3; z <= 3; z++) {
                                if(Math.abs(x) + Math.abs(z) <= 4) {
                                    Location crackLoc = targetLoc.clone().add(x, -1, z);
                                    if(crackLoc.getBlock().getType() != Material.AIR) {
                                        player.getWorld().spawnParticle(Particle.BLOCK, crackLoc, 5, 0.2, 0.1, 0.2, crackLoc.getBlock().getBlockData());
                                    }
                                }
                            }
                        }
                        
                        player.sendMessage("§a🔨 §lSMASH! §aOak Hammer crushed the area!");
                        this.cancel();
                    }
                    
                    smashFrame++;
                }
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static LivingEntity getNearestEnemy(Player player, double range) {
        return player.getWorld().getNearbyEntities(player.getLocation(), range, range, range)
            .stream()
            .filter(e -> e != player && e instanceof LivingEntity && !(e instanceof Player))
            .map(e -> (LivingEntity) e)
            .min(Comparator.comparingDouble(e -> e.getLocation().distance(player.getLocation())))
            .orElse(null);
    }
    
    public static boolean isPlayerAttached(Player player) {
        return attachedPlayers.containsKey(player.getUniqueId());
    }
    
    public static void removeAllAttachments() {
        attachedPlayers.clear();
        attachTasks.clear();
    }
}
