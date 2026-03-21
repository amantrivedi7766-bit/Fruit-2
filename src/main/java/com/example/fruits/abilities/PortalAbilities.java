package com.example.fruits.abilities;

import com.example.fruits.FruitsPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;

public class PortalAbilities {
    
    // Store active portals
    private static final Map<UUID, PortalData> pendingPortals = new HashMap<>();
    private static final Map<UUID, PortalPair> activePortals = new HashMap<>();
    private static final Map<UUID, Long> portalCooldowns = new HashMap<>();
    private static final Map<UUID, Long> summonCooldowns = new HashMap<>();
    
    // ==================== ABILITY 1: PORTAL LINK (Right Click) ====================
    
    public static void portalLink(Player player) {
        UUID uuid = player.getUniqueId();
        
        // Check if player already has pending portal
        if(pendingPortals.containsKey(uuid)) {
            PortalData pending = pendingPortals.get(uuid);
            
            // Check time limit (20 seconds)
            if(System.currentTimeMillis() > pending.expiryTime) {
                pendingPortals.remove(uuid);
                player.sendMessage("§5⚠️ First portal expired! Start over!");
                return;
            }
            
            // Create second portal and link them
            Location secondLoc = player.getTargetBlock(null, 30).getLocation();
            Location firstLoc = pending.location;
            
            createPortalPair(player, firstLoc, secondLoc);
            pendingPortals.remove(uuid);
            
            // Set cooldown
            portalCooldowns.put(uuid, System.currentTimeMillis() + 120000); // 2 minutes cooldown
            player.sendMessage("§5✨ Portal linked! Step through to teleport!");
            player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 1.0f);
            
        } else {
            // Create first portal
            Location firstLoc = player.getTargetBlock(null, 30).getLocation();
            if(firstLoc == null || firstLoc.getBlock().getType() == Material.AIR) {
                player.sendMessage("§c❌ You must aim at a solid block!");
                return;
            }
            
            pendingPortals.put(uuid, new PortalData(firstLoc, System.currentTimeMillis() + 20000));
            player.sendMessage("§5✨ First portal placed! You have §e20 seconds §5to place the second!");
            player.sendMessage("§5Right-click again on another location to link them!");
            
            // Temporary portal effect
            showTemporaryPortal(firstLoc, player);
            
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
        }
    }
    
    private static void createPortalPair(Player player, Location loc1, Location loc2) {
        // Create portal at first location
        Portal portal1 = createPortalBlock(loc1);
        Portal portal2 = createPortalBlock(loc2);
        
        PortalPair pair = new PortalPair(portal1, portal2, System.currentTimeMillis() + 60000); // 60 seconds duration
        activePortals.put(player.getUniqueId(), pair);
        
        // Start portal effects
        startPortalEffects(player, portal1, portal2);
        
        // Remove portals after duration
        new BukkitRunnable() {
            @Override
            public void run() {
                if(activePortals.containsKey(player.getUniqueId())) {
                    PortalPair p = activePortals.get(player.getUniqueId());
                    removePortal(p.portal1);
                    removePortal(p.portal2);
                    activePortals.remove(player.getUniqueId());
                    player.sendMessage("§5🌌 Portals have faded away!");
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 1200L); // 60 seconds
    }
    
    private static Portal createPortalBlock(Location loc) {
        // Get the block where portal will be placed
        Block block = loc.getBlock();
        Location portalLoc = block.getLocation().add(0.5, 0, 0.5);
        
        // Create armor stand as portal marker
        ArmorStand portal = (ArmorStand) loc.getWorld().spawnEntity(portalLoc, EntityType.ARMOR_STAND);
        portal.setVisible(false);
        portal.setGravity(false);
        portal.setInvulnerable(true);
        portal.setMarker(true);
        portal.setCustomName("§5§l🌀 VOID PORTAL");
        portal.setCustomNameVisible(true);
        
        // Store portal data
        Portal portalData = new Portal(portal, portalLoc, new ArrayList<>());
        
        // Start portal particles
        startPortalParticles(portalData);
        
        return portalData;
    }
    
    private static void startPortalParticles(Portal portal) {
        new BukkitRunnable() {
            int angle = 0;
            @Override
            public void run() {
                if(portal.armorStand == null || portal.armorStand.isDead()) {
                    this.cancel();
                    return;
                }
                
                Location center = portal.location;
                
                // Spiral particles
                for(int i = 0; i < 360; i += 10) {
                    double rad = Math.toRadians(i + angle);
                    double x = Math.cos(rad) * 1.2;
                    double z = Math.sin(rad) * 1.2;
                    
                    center.getWorld().spawnParticle(Particle.PORTAL, center.clone().add(x, 0.5, z), 1, 0, 0, 0);
                    center.getWorld().spawnParticle(Particle.END_ROD, center.clone().add(x, 1, z), 1, 0, 0, 0);
                }
                
                // Floating orbs
                for(int i = 0; i < 5; i++) {
                    double rad = Math.toRadians(angle + i * 72);
                    double x = Math.cos(rad) * 1.5;
                    double z = Math.sin(rad) * 1.5;
                    center.getWorld().spawnParticle(Particle.DRAGON_BREATH, center.clone().add(x, 1.2, z), 1, 0, 0.1, 0);
                }
                
                // Vertical beam
                for(int y = 0; y <= 2; y++) {
                    center.getWorld().spawnParticle(Particle.SPELL_MOB, center.clone().add(0, y, 0), 3, 0.1, 0.1, 0.1);
                }
                
                angle += 5;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    private static void removePortal(Portal portal) {
        if(portal.armorStand != null) {
            portal.armorStand.remove();
        }
        
        // Explosion effect on removal
        portal.location.getWorld().playSound(portal.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        for(int i = 0; i < 50; i++) {
            portal.location.getWorld().spawnParticle(Particle.PORTAL, portal.location, 1, 0.5, 0.5, 0.5);
        }
    }
    
    private static void showTemporaryPortal(Location loc, Player player) {
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if(count >= 40) { // 2 seconds
                    this.cancel();
                    return;
                }
                
                for(int i = 0; i < 360; i += 20) {
                    double rad = Math.toRadians(i + count * 10);
                    double x = Math.cos(rad) * 1;
                    double z = Math.sin(rad) * 1;
                    loc.getWorld().spawnParticle(Particle.PORTAL, loc.clone().add(x, 0.5, z), 1, 0, 0, 0);
                }
                count++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
    }
    
    // Handle player stepping into portal
    public static void handlePortalTeleport(Player player, Location location) {
        for(Map.Entry<UUID, PortalPair> entry : activePortals.entrySet()) {
            PortalPair pair = entry.getValue();
            
            if(isNearPortal(player.getLocation(), pair.portal1.location)) {
                teleportPlayer(player, pair.portal2.location);
                return;
            }
            if(isNearPortal(player.getLocation(), pair.portal2.location)) {
                teleportPlayer(player, pair.portal1.location);
                return;
            }
        }
    }
    
    private static boolean isNearPortal(Location playerLoc, Location portalLoc) {
        return playerLoc.distance(portalLoc) <= 1.5;
    }
    
    private static void teleportPlayer(Player player, Location target) {
        player.teleport(target.clone().add(0, 1, 0));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
        
        // Teleport effect
        for(int i = 0; i < 360; i += 10) {
            double rad = Math.toRadians(i);
            double x = Math.cos(rad) * 1;
            double z = Math.sin(rad) * 1;
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(x, 0.5, z), 1, 0, 0, 0);
        }
    }
    
    private static void startPortalEffects(Player player, Portal p1, Portal p2) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!activePortals.containsKey(player.getUniqueId())) {
                    this.cancel();
                    return;
                }
                
                // Create beam between portals
                Location loc1 = p1.location;
                Location loc2 = p2.location;
                
                Vector direction = loc2.toVector().subtract(loc1.toVector()).normalize();
                double distance = loc1.distance(loc2);
                
                for(double t = 0; t <= distance; t += 0.3) {
                    Location point = loc1.clone().add(direction.clone().multiply(t));
                    point.getWorld().spawnParticle(Particle.END_ROD, point, 1, 0, 0, 0);
                    point.getWorld().spawnParticle(Particle.PORTAL, point, 1, 0, 0, 0);
                }
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 5L);
    }
    
    // ==================== ABILITY 2: PORTAL SUMMON (Crouch + Right Click) ====================
    
    public static void portalSummon(Player player) {
        UUID uuid = player.getUniqueId();
        
        // Check cooldown (20 minutes)
        if(summonCooldowns.containsKey(uuid) && System.currentTimeMillis() < summonCooldowns.get(uuid)) {
            long remaining = (summonCooldowns.get(uuid) - System.currentTimeMillis()) / 1000;
            player.sendMessage("§c⏰ Portal Summon on cooldown for " + (remaining / 60) + " minutes " + (remaining % 60) + " seconds!");
            return;
        }
        
        // Get cursor location
        Location cursorLoc = player.getTargetBlock(null, 50).getLocation();
        if(cursorLoc == null) {
            player.sendMessage("§c❌ You must aim at a valid location!");
            return;
        }
        
        // Create summon portal
        Portal summonPortal = createPortalBlock(cursorLoc);
        player.sendMessage("§5🌟 Summon Portal created! Left-click on it to summon a player!");
        player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 1.2f);
        
        // Store for left-click interaction
        pendingSummonPortals.put(uuid, summonPortal);
        
        // Remove after 30 seconds if not used
        new BukkitRunnable() {
            @Override
            public void run() {
                if(pendingSummonPortals.containsKey(uuid)) {
                    Portal p = pendingSummonPortals.remove(uuid);
                    removePortal(p);
                    player.sendMessage("§5🌌 Summon portal faded away!");
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 600L);
    }
    
    private static final Map<UUID, Portal> pendingSummonPortals = new HashMap<>();
    
    public static void handleSummonPortalClick(Player player, Location clickedLoc) {
        UUID uuid = player.getUniqueId();
        
        if(!pendingSummonPortals.containsKey(uuid)) return;
        
        Portal portal = pendingSummonPortals.get(uuid);
        if(portal == null || !isNearPortal(clickedLoc, portal.location)) return;
        
        // Open player selection GUI
        openSummonGUI(player, portal);
    }
    
    private static void openSummonGUI(Player player, Portal portal) {
        Inventory gui = Bukkit.createInventory(null, 54, "§5§l🌀 SUMMON PLAYER");
        
        List<Player> targets = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p != player) {
                targets.add(p);
            }
        }
        
        int slot = 0;
        for(Player target : targets) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(target);
            meta.setDisplayName("§d§l" + target.getName());
            
            List<String> lore = new ArrayList<>();
            lore.add("§7=================================");
            lore.add("§eClick to summon " + target.getName());
            lore.add("§7They will be teleported to you!");
            lore.add("§7=================================");
            meta.setLore(lore);
            
            head.setItemMeta(meta);
            gui.setItem(slot, head);
            slot++;
        }
        
        ItemStack close = createItem(Material.BARRIER, "§c§l✖ CLOSE", "§7Click to close");
        gui.setItem(49, close);
        
        player.openInventory(gui);
        
        // Store portal reference
        summonGUIPortals.put(player.getUniqueId(), portal);
        
        // Auto-close after 30 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if(player.getOpenInventory() != null && player.getOpenInventory().getTitle().equals("§5§l🌀 SUMMON PLAYER")) {
                    player.closeInventory();
                }
                summonGUIPortals.remove(player.getUniqueId());
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 600L);
    }
    
    private static final Map<UUID, Portal> summonGUIPortals = new HashMap<>();
    
    public static void handleSummonSelection(Player summoner, Player target) {
        Portal portal = summonGUIPortals.remove(summoner.getUniqueId());
        if(portal == null) return;
        
        // Teleport target to summoner
        target.teleport(summoner.getLocation().add(0, 1, 0));
        
        // Effects
        target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        summoner.playSound(summoner.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        
        // Portal effect on both players
        for(int i = 0; i < 360; i += 10) {
            double rad = Math.toRadians(i);
            double x = Math.cos(rad) * 1.5;
            double z = Math.sin(rad) * 1.5;
            target.getWorld().spawnParticle(Particle.PORTAL, target.getLocation().add(x, 0.5, z), 1, 0, 0, 0);
            summoner.getWorld().spawnParticle(Particle.PORTAL, summoner.getLocation().add(x, 0.5, z), 1, 0, 0, 0);
        }
        
        // Remove portal
        removePortal(portal);
        pendingSummonPortals.remove(summoner.getUniqueId());
        
        // Set cooldown (20 minutes)
        summonCooldowns.put(summoner.getUniqueId(), System.currentTimeMillis() + 1200000);
        
        summoner.sendMessage("§5✨ You summoned §d" + target.getName() + " §5through the void!");
        target.sendMessage("§5✨ You were summoned by §d" + summoner.getName() + " §5through a void portal!");
    }
    
    public static boolean hasActivePortal(Player player) {
        return activePortals.containsKey(player.getUniqueId());
    }
    
    public static void cleanup() {
        for(PortalPair pair : activePortals.values()) {
            removePortal(pair.portal1);
            removePortal(pair.portal2);
        }
        for(Portal portal : pendingSummonPortals.values()) {
            removePortal(portal);
        }
        activePortals.clear();
        pendingPortals.clear();
        pendingSummonPortals.clear();
        summonGUIPortals.clear();
    }
    
    private static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
    
    // ==================== HELPER CLASSES ====================
    
    private static class PortalData {
        final Location location;
        final long expiryTime;
        
        PortalData(Location location, long expiryTime) {
            this.location = location;
            this.expiryTime = expiryTime;
        }
    }
    
    private static class PortalPair {
        final Portal portal1;
        final Portal portal2;
        final long expiryTime;
        
        PortalPair(Portal portal1, Portal portal2, long expiryTime) {
            this.portal1 = portal1;
            this.portal2 = portal2;
            this.expiryTime = expiryTime;
        }
    }
    
    private static class Portal {
        final ArmorStand armorStand;
        final Location location;
        final List<Integer> taskIds;
        
        Portal(ArmorStand armorStand, Location location, List<Integer> taskIds) {
            this.armorStand = armorStand;
            this.location = location;
            this.taskIds = taskIds;
        }
    }
}
