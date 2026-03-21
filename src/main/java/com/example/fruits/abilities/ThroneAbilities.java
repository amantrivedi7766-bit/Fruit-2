package com.example.fruits.abilities;

import com.example.fruits.FruitsPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;

public class ThroneAbilities {
    
    // Store active shields
    private static final Map<UUID, ShieldData> activeShields = new HashMap<>();
    private static final Map<UUID, WallData> activeWalls = new HashMap<>();
    
    // ==================== ABILITY 1: ROYAL AEGIS (Right Click) ====================
    
    public static void royalAegis(Player player) {
        // Check if already has shield
        if(activeShields.containsKey(player.getUniqueId())) {
            player.sendMessage("§6⚠️ You already have an active shield!");
            return;
        }
        
        player.sendMessage("§6§l🛡️ ROYAL AEGIS ACTIVATED!");
        player.sendMessage("§7For §e15 seconds§7, you reflect damage and take 50% less!");
        player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1.0f, 1.2f);
        
        // Create shield effect
        createShieldEffect(player);
        
        // Store shield data
        ShieldData shield = new ShieldData(player.getUniqueId(), System.currentTimeMillis() + 15000);
        activeShields.put(player.getUniqueId(), shield);
        
        // Remove shield after 15 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activeShields.containsKey(player.getUniqueId())) {
                    activeShields.remove(player.getUniqueId());
                    player.sendMessage("§c🛡️ Royal Aegis has faded!");
                    player.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1.0f, 0.5f);
                    
                    // Remove shield particles
                    for(int i = 0; i < 360; i += 10) {
                        double rad = Math.toRadians(i);
                        double x = Math.cos(rad) * 1.5;
                        double z = Math.sin(rad) * 1.5;
                        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(x, 1, z), 1, 0, 0, 0);
                    }
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 300L); // 15 seconds * 20 ticks = 300
    }
    
    private static void createShieldEffect(Player player) {
        // Continuous shield particle effect
        new BukkitRunnable() {
            int duration = 0;
            @Override
            public void run() {
                if(duration >= 300 || !activeShields.containsKey(player.getUniqueId())) {
                    this.cancel();
                    return;
                }
                
                if(!player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                // Golden ring around player
                for(int i = 0; i < 360; i += 15) {
                    double rad = Math.toRadians(i);
                    double x = Math.cos(rad) * 1.2;
                    double z = Math.sin(rad) * 1.2;
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(x, 0.5, z), 1, 0, 0, 0);
                    player.getWorld().spawnParticle(Particle.GLOW, player.getLocation().add(x, 1, z), 1, 0, 0, 0);
                }
                
                // Floating golden particles
                for(int i = 0; i < 10; i++) {
                    double angle = Math.toRadians(duration * 5 + i * 36);
                    double x = Math.cos(angle) * 1.5;
                    double z = Math.sin(angle) * 1.5;
                    player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(x, 1.2, z), 1, 0, 0.1, 0);
                }
                
                duration++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    // Called when player takes damage
    public static double handleDamage(Player player, double damage, Entity damager) {
        ShieldData shield = activeShields.get(player.getUniqueId());
        if(shield == null) return damage;
        
        if(System.currentTimeMillis() > shield.expiryTime) {
            activeShields.remove(player.getUniqueId());
            return damage;
        }
        
        // Calculate reduced damage (50% less)
        double reducedDamage = damage * 0.5;
        
        // Reflect damage back to attacker
        if(damager instanceof LivingEntity) {
            double reflectedDamage = damage * 0.75; // 75% of original damage reflected
            ((LivingEntity) damager).damage(reflectedDamage, player);
            
            // Reflection effects
            damager.getWorld().playSound(damager.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.5f);
            damager.getWorld().spawnParticle(Particle.FLASH, damager.getLocation(), 5);
            damager.getWorld().spawnParticle(Particle.CRIT, damager.getLocation(), 20, 0.3, 0.3, 0.3);
            
            player.sendMessage("§6⚔️ Reflected §c" + String.format("%.1f", reflectedDamage) + " §6damage to " + getEntityName(damager) + "!");
        }
        
        // Shield hit effect
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.2f);
        player.getWorld().spawnParticle(Particle.GLOW, player.getLocation(), 30, 0.5, 1, 0.5);
        
        player.sendMessage("§6🛡️ Shield absorbed §c" + String.format("%.1f", damage - reducedDamage) + " §6damage!");
        
        return reducedDamage;
    }
    
    // ==================== ABILITY 2: GOLDEN WALL (Crouch + Right Click) ====================
    
    public static void goldenWall(Player player) {
        // Check cooldown
        String cooldownKey = "golden_aegis_wall";
        if(!FruitsPlugin.getInstance().getCooldownManager().checkCooldown(player, cooldownKey)) {
            return;
        }
        
        player.sendMessage("§6§l🏰 GOLDEN WALL SUMMONED!");
        player.sendMessage("§7A wall rises before you for §e15 seconds§7!");
        player.playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f);
        
        // Get wall location (3 blocks in front)
        Location playerLoc = player.getLocation();
        Vector direction = playerLoc.getDirection().normalize();
        
        // Calculate wall center (3 blocks ahead)
        Location wallCenter = playerLoc.clone().add(direction.clone().multiply(3));
        wallCenter.setY(wallCenter.getY()); // Ground level
        
        // Store original blocks and create wall
        List<BlockData> wallBlocks = new ArrayList<>();
        List<Block> originalBlocks = new ArrayList<>();
        
        // Create 3x3 wall (width 3, height 3)
        for(int x = -1; x <= 1; x++) {
            for(int y = 0; y <= 2; y++) {
                // Calculate position perpendicular to player direction
                Vector right = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
                Location blockLoc = wallCenter.clone().add(right.clone().multiply(x)).add(0, y, 0);
                
                Block block = blockLoc.getBlock();
                originalBlocks.add(block);
                wallBlocks.add(new BlockData(block.getType(), block.getLocation(), block.getBlockData()));
                
                // Create golden wall
                block.setType(Material.GOLD_BLOCK);
                block.getWorld().playSound(block.getLocation(), Sound.BLOCK_STONE_PLACE, 0.8f, 1.2f);
                
                // Rising particles
                for(int i = 0; i < 10; i++) {
                    block.getWorld().spawnParticle(Particle.END_ROD, block.getLocation().add(0.5, i * 0.3, 0.5), 1, 0, 0, 0);
                }
            }
        }
        
        // Store wall data
        WallData wall = new WallData(wallBlocks, originalBlocks, System.currentTimeMillis() + 15000);
        activeWalls.put(player.getUniqueId(), wall);
        
        // Wall effects (push back enemies)
        new BukkitRunnable() {
            int duration = 0;
            @Override
            public void run() {
                if(duration >= 300 || !activeWalls.containsKey(player.getUniqueId())) {
                    // Remove wall and restore original blocks
                    for(BlockData blockData : wallBlocks) {
                        blockData.location.getBlock().setType(blockData.originalType);
                        blockData.location.getBlock().setBlockData(blockData.originalData);
                        blockData.location.getWorld().playSound(blockData.location, Sound.BLOCK_STONE_BREAK, 0.5f, 0.8f);
                    }
                    activeWalls.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }
                
                if(!player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                // Push back and damage enemies near wall
                for(BlockData blockData : wallBlocks) {
                    Location wallLoc = blockData.location.clone().add(0.5, 0.5, 0.5);
                    
                    // Get entities near wall block
                    player.getWorld().getNearbyEntities(wallLoc, 1.5, 1.5, 1.5).forEach(e -> {
                        if(e != player && e instanceof LivingEntity) {
                            LivingEntity living = (LivingEntity) e;
                            
                            // Calculate knockback direction (away from wall)
                            Vector knockback = living.getLocation().toVector().subtract(wallLoc.toVector()).normalize();
                            knockback.setY(0.5);
                            living.setVelocity(knockback.multiply(1.5));
                            
                            // Damage
                            living.damage(6, player);
                            
                            // Effect
                            living.getWorld().playSound(living.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.0f, 1.0f);
                            living.getWorld().spawnParticle(Particle.CRIT, living.getLocation(), 15, 0.3, 0.3, 0.3);
                        }
                    });
                    
                    // Golden glow particles
                    blockData.location.getWorld().spawnParticle(Particle.GLOW, blockData.location.clone().add(0.5, 0.5, 0.5), 3, 0.2, 0.2, 0.2);
                    blockData.location.getWorld().spawnParticle(Particle.END_ROD, blockData.location.clone().add(0.5, 0.5, 0.5), 2, 0.1, 0.1, 0.1);
                }
                
                duration++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
        
        // Set cooldown (25 seconds)
        FruitsPlugin.getInstance().getCooldownManager().setCooldown(player, cooldownKey, 25, "§6🏰 Golden Wall");
    }
    
    // ==================== UTILITY METHODS ====================
    
    public static boolean hasActiveShield(Player player) {
        ShieldData shield = activeShields.get(player.getUniqueId());
        if(shield == null) return false;
        if(System.currentTimeMillis() > shield.expiryTime) {
            activeShields.remove(player.getUniqueId());
            return false;
        }
        return true;
    }
    
    public static void cleanup() {
        // Restore all walls on plugin disable
        for(WallData wall : activeWalls.values()) {
            for(BlockData blockData : wall.blocks) {
                blockData.location.getBlock().setType(blockData.originalType);
                blockData.location.getBlock().setBlockData(blockData.originalData);
            }
        }
        activeShields.clear();
        activeWalls.clear();
    }
    
    private static String getEntityName(Entity entity) {
        if(entity instanceof Player) return ((Player) entity).getName();
        return entity.getType().name().toLowerCase().replace("_", " ");
    }
    
    // ==================== HELPER CLASSES ====================
    
    private static class ShieldData {
        final UUID playerId;
        final long expiryTime;
        
        ShieldData(UUID playerId, long expiryTime) {
            this.playerId = playerId;
            this.expiryTime = expiryTime;
        }
    }
    
    private static class WallData {
        final List<BlockData> blocks;
        final List<Block> originalBlocks;
        final long expiryTime;
        
        WallData(List<BlockData> blocks, List<Block> originalBlocks, long expiryTime) {
            this.blocks = blocks;
            this.originalBlocks = originalBlocks;
            this.expiryTime = expiryTime;
        }
    }
    
    private static class BlockData {
        final Material originalType;
        final Location location;
        final org.bukkit.block.data.BlockData originalData;
        
        BlockData(Material type, Location loc, org.bukkit.block.data.BlockData data) {
            this.originalType = type;
            this.location = loc;
            this.originalData = data;
        }
    }
}
