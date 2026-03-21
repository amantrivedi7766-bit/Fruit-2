package com.example.fruits.abilities;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.*;

public class ThiefAbilities {
    
    // Store stolen abilities
    private static final Map<UUID, StolenAbility> stolenAbilities = new HashMap<>();
    private static final Map<UUID, UUID> frozenPlayers = new HashMap<>();
    private static final Map<UUID, Integer> freezeTasks = new HashMap<>();
    
    // Store active GUI for players
    private static final Map<UUID, Inventory> activeGUIs = new HashMap<>();
    
    // ==================== ABILITY: SHADOW STEAL ====================
    
    public static void shadowSteal(Player player) {
        // Check cooldown
        String cooldownKey = "shadowweaver_steal";
        if(!FruitsPlugin.getInstance().getCooldownManager().checkCooldown(player, cooldownKey)) {
            return;
        }
        
        // Open GUI with all online players
        openStealGUI(player);
        
        player.sendMessage("§8§l🌑 SHADOWWEAVER ACTIVATED!");
        player.sendMessage("§7Select a player to steal their ability!");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        
        // Particle effect around player
        for(int i = 0; i < 360; i += 10) {
            double rad = Math.toRadians(i);
            double x = Math.cos(rad) * 2;
            double z = Math.sin(rad) * 2;
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(x, 0.5, z), 1, 0, 0, 0);
        }
    }
    
    private static void openStealGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "§8§l🌑 SHADOW STEAL");
        
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
            meta.setDisplayName("§c§l" + target.getName());
            
            // Get target's current fruit
            String fruitId = FruitsPlugin.getInstance().getPlayerManager().getPlayerFruit(target);
            Fruit fruit = fruitId != null ? FruitsPlugin.getInstance().getFruitRegistry().getFruit(fruitId) : null;
            
            List<String> lore = new ArrayList<>();
            lore.add("§7=================================");
            lore.add("§e§l🔮 " + (fruit != null ? fruit.getName() : "§cNo Fruit"));
            lore.add("§7Health: §c" + target.getHealth() + "❤");
            lore.add("§7Level: §e" + target.getLevel());
            lore.add("§7=================================");
            lore.add("§8Click to steal their ability!");
            lore.add("§7They will be frozen for 20 seconds!");
            lore.add("§c⚠️ Warning: They will know who stole!");
            meta.setLore(lore);
            
            head.setItemMeta(meta);
            gui.setItem(slot, head);
            slot++;
        }
        
        // Add decorative items
        ItemStack border = createItem(Material.BLACK_STAINED_GLASS_PANE, "§8 ", "");
        for(int i = 45; i < 54; i++) {
            if(gui.getItem(i) == null) gui.setItem(i, border);
        }
        
        // Add close button
        ItemStack close = createItem(Material.BARRIER, "§c§l✖ CLOSE", "§7Click to close");
        gui.setItem(49, close);
        
        player.openInventory(gui);
        activeGUIs.put(player.getUniqueId(), gui);
        
        // Auto-close after 30 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                if(player.getOpenInventory() != null && player.getOpenInventory().getTitle().equals("§8§l🌑 SHADOW STEAL")) {
                    player.closeInventory();
                    player.sendMessage("§cShadow steal window expired!");
                }
                activeGUIs.remove(player.getUniqueId());
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 600L);
    }
    
    public static void handleGUIClick(Player thief, Player target) {
        if(target == null) return;
        
        // Set cooldown for thief
        String cooldownKey = "shadowweaver_steal";
        FruitsPlugin.getInstance().getCooldownManager().setCooldown(thief, cooldownKey, 120, "§8§l🌑 Shadow Steal");
        
        // Get target's ability
        String targetFruitId = FruitsPlugin.getInstance().getPlayerManager().getPlayerFruit(target);
        Fruit targetFruit = targetFruitId != null ? FruitsPlugin.getInstance().getFruitRegistry().getFruit(targetFruitId) : null;
        
        if(targetFruit == null || targetFruit.getAbilities().isEmpty()) {
            thief.sendMessage("§c❌ " + target.getName() + " has no ability to steal!");
            return;
        }
        
        // Store stolen ability for thief
        StolenAbility stolen = new StolenAbility(
            targetFruit,
            targetFruit.getAbilities().get(0),
            target.getName(),
            System.currentTimeMillis() + 20000 // 20 seconds
        );
        stolenAbilities.put(thief.getUniqueId(), stolen);
        
        // Apply freeze effect to all nearby players (30 blocks)
        freezeNearbyPlayers(thief, target);
        
        // Epic effects
        performStealEffects(thief, target);
        
        // Notify players
        thief.sendMessage("§8§l🌑 §eYou stole §c" + target.getName() + "'s §eability!");
        thief.sendMessage("§7You can use their ability for §e20 seconds§7!");
        thief.sendMessage("§7Ability: §f" + stolen.ability.getName());
        
        target.sendMessage("§c§l⚠️ YOUR ABILITY WAS STOLEN BY " + thief.getName().toUpperCase() + "!");
        target.sendMessage("§7You are frozen for §e20 seconds§7!");
        target.playSound(target.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);
        
        // Close GUI
        thief.closeInventory();
        activeGUIs.remove(thief.getUniqueId());
        
        // Schedule removal of stolen ability after 20 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                StolenAbility removed = stolenAbilities.remove(thief.getUniqueId());
                if(removed != null && thief.isOnline()) {
                    thief.sendMessage("§c🌑 The stolen ability has faded away!");
                    thief.playSound(thief.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
                }
            }
        }.runTaskLater(FruitsPlugin.getInstance(), 400L); // 20 seconds * 20 ticks
    }
    
    private static void freezeNearbyPlayers(Player thief, Player target) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p == thief) continue;
            if(p == target) continue;
            if(p.getLocation().distance(thief.getLocation()) > 30) continue;
            
            // Freeze the player
            freezePlayer(p, thief);
        }
    }
    
    private static void freezePlayer(Player player, Player thief) {
        // Store original values
        float originalWalkSpeed = player.getWalkSpeed();
        float originalFlySpeed = player.getFlySpeed();
        
        // Freeze
        player.setWalkSpeed(0);
        player.setFlySpeed(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 255, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 400, 128, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 1, false, false, false));
        
        frozenPlayers.put(player.getUniqueId(), thief.getUniqueId());
        
        // Ice particle effect around frozen player
        BukkitRunnable iceTask = new BukkitRunnable() {
            int duration = 0;
            @Override
            public void run() {
                if(duration >= 400 || !player.isOnline() || frozenPlayers.get(player.getUniqueId()) == null) {
                    // Unfreeze
                    player.setWalkSpeed(originalWalkSpeed);
                    player.setFlySpeed(originalFlySpeed);
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
                    player.removePotionEffect(PotionEffectType.JUMP_BOOST);
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                    frozenPlayers.remove(player.getUniqueId());
                    this.cancel();
                    return;
                }
                
                // Ice particles
                player.getWorld().spawnParticle(Particle.SNOWFLAKE, player.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5);
                player.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, player.getLocation(), 10, 0.3, 0.3, 0.3);
                
                // Ice block effect around feet
                for(int i = 0; i < 8; i++) {
                    double angle = Math.toRadians(duration * 10 + i * 45);
                    double x = Math.cos(angle) * 0.8;
                    double z = Math.sin(angle) * 0.8;
                    player.getWorld().spawnParticle(Particle.ITEM_SNOWBALL, player.getLocation().add(x, 0.1, z), 1, 0, 0, 0);
                }
                
                duration++;
            }
        };
        
        int taskId = iceTask.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L).getTaskId();
        freezeTasks.put(player.getUniqueId(), taskId);
        
        player.sendMessage("§b❄️ You are frozen by §e" + thief.getName() + "§b's shadow magic!");
        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.5f);
    }
    
    private static void performStealEffects(Player thief, Player target) {
        // Dark portal effect
        for(int i = 0; i < 360; i += 5) {
            double rad = Math.toRadians(i);
            double x = Math.cos(rad) * 2;
            double z = Math.sin(rad) * 2;
            thief.getWorld().spawnParticle(Particle.PORTAL, thief.getLocation().add(x, 0.5, z), 1, 0, 0, 0);
        }
        
        // Lightning effect on target
        target.getWorld().strikeLightningEffect(target.getLocation());
        
        // Dark beam from thief to target
        new BukkitRunnable() {
            int t = 0;
            @Override
            public void run() {
                if(t >= 20) {
                    this.cancel();
                    return;
                }
                
                double progress = t / 20.0;
                Location start = thief.getEyeLocation();
                Location end = target.getEyeLocation();
                
                double x = start.getX() + (end.getX() - start.getX()) * progress;
                double y = start.getY() + (end.getY() - start.getY()) * progress;
                double z = start.getZ() + (end.getZ() - start.getZ()) * progress;
                
                Location beamLoc = new Location(thief.getWorld(), x, y, z);
                thief.getWorld().spawnParticle(Particle.SPELL_WITCH, beamLoc, 10, 0.1, 0.1, 0.1);
                thief.getWorld().spawnParticle(Particle.DRAGON_BREATH, beamLoc, 5, 0.2, 0.2, 0.2);
                
                t++;
            }
        }.runTaskTimer(FruitsPlugin.getInstance(), 0L, 1L);
        
        // Sound effects
        thief.getWorld().playSound(thief.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1.0f, 0.8f);
        thief.getWorld().playSound(target.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 0.5f);
        
        // Particle explosion on target
        target.getWorld().spawnParticle(Particle.EXPLOSION, target.getLocation(), 3);
        target.getWorld().spawnParticle(Particle.SPELL_WITCH, target.getLocation(), 50, 0.5, 0.5, 0.5);
    }
    
    // ==================== USE STOLEN ABILITY ====================
    
    public static boolean useStolenAbility(Player player, Entity target) {
        StolenAbility stolen = stolenAbilities.get(player.getUniqueId());
        if(stolen == null) return false;
        
        // Check if still valid
        if(System.currentTimeMillis() > stolen.expiryTime) {
            stolenAbilities.remove(player.getUniqueId());
            return false;
        }
        
        // Use the stolen ability
        player.sendMessage("§8§l🌑 Using stolen ability: §f" + stolen.ability.getName());
        player.sendMessage("§7Stolen from: §c" + stolen.ownerName);
        
        stolen.ability.getExecutor().execute(player, target);
        
        // Dark particles when using stolen ability
        player.getWorld().spawnParticle(Particle.SPELL_WITCH, player.getLocation(), 30, 0.5, 0.5, 0.5);
        player.playSound(player.getLocation(), Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1.0f, 1.2f);
        
        return true;
    }
    
    public static boolean hasStolenAbility(Player player) {
        StolenAbility stolen = stolenAbilities.get(player.getUniqueId());
        if(stolen == null) return false;
        return System.currentTimeMillis() <= stolen.expiryTime;
    }
    
    public static String getStolenAbilityName(Player player) {
        StolenAbility stolen = stolenAbilities.get(player.getUniqueId());
        if(stolen == null) return null;
        if(System.currentTimeMillis() > stolen.expiryTime) {
            stolenAbilities.remove(player.getUniqueId());
            return null;
        }
        return stolen.ability.getName();
    }
    
    public static void cleanup() {
        stolenAbilities.clear();
        frozenPlayers.clear();
        for(Integer taskId : freezeTasks.values()) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        freezeTasks.clear();
        activeGUIs.clear();
    }
    
    // ==================== HELPER CLASS ====================
    
    private static class StolenAbility {
        final Fruit fruit;
        final com.example.fruits.models.Ability ability;
        final String ownerName;
        final long expiryTime;
        
        StolenAbility(Fruit fruit, com.example.fruits.models.Ability ability, String ownerName, long expiryTime) {
            this.fruit = fruit;
            this.ability = ability;
            this.ownerName = ownerName;
            this.expiryTime = expiryTime;
        }
    }
    
    private static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }
          }
