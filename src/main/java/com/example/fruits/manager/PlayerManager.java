package com.example.fruits.managers;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    
    private final FruitsPlugin plugin;
    private final Set<UUID> activePlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, PlayerStats> playerStats = new HashMap<>();
    private final Map<UUID, Long> lastActiveTime = new HashMap<>();
    private final Map<UUID, String> playerFruits = new HashMap<>();
    
    public PlayerManager(FruitsPlugin plugin) {
        this.plugin = plugin;
        startInactiveCleanupTask();
    }
    
    public void addActivePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        activePlayers.add(uuid);
        lastActiveTime.put(uuid, System.currentTimeMillis());
        
        if (!playerStats.containsKey(uuid)) {
            playerStats.put(uuid, new PlayerStats(player.getName()));
        }
        
        // Load player's held fruit
        updatePlayerFruit(player);
    }
    
    public void removeActivePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        activePlayers.remove(uuid);
        savePlayerStats(player);
    }
    
    public Set<Player> getActivePlayers() {
        Set<Player> players = new HashSet<>();
        for (UUID uuid : activePlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                players.add(player);
            }
        }
        return players;
    }
    
    public boolean isActive(Player player) {
        return activePlayers.contains(player.getUniqueId());
    }
    
    public void updateLastActive(Player player) {
        lastActiveTime.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    public long getInactiveTime(Player player) {
        Long lastActive = lastActiveTime.get(player.getUniqueId());
        if (lastActive == null) return 0;
        return System.currentTimeMillis() - lastActive;
    }
    
    public PlayerStats getPlayerStats(Player player) {
        return playerStats.computeIfAbsent(player.getUniqueId(), 
            k -> new PlayerStats(player.getName()));
    }
    
    public void savePlayerStats(Player player) {
        PlayerStats stats = playerStats.get(player.getUniqueId());
        if (stats != null) {
            stats.updateFromPlayer(player);
            plugin.getConfigManager().setPlayerData(player.getName(), "stats", stats.serialize());
        }
    }
    
    public void loadPlayerStats(Player player) {
        Map<String, Object> data = plugin.getConfigManager().getPlayerData(player.getName());
        if (data.containsKey("stats")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> statsData = (Map<String, Object>) data.get("stats");
            PlayerStats stats = PlayerStats.deserialize(statsData);
            playerStats.put(player.getUniqueId(), stats);
        } else {
            playerStats.put(player.getUniqueId(), new PlayerStats(player.getName()));
        }
    }
    
    /**
     * Get the fruit ID of the fruit the player is holding
     */
    public String getPlayerFruit(Player player) {
        UUID uuid = player.getUniqueId();
        
        // Check cached value first
        if (playerFruits.containsKey(uuid)) {
            return playerFruits.get(uuid);
        }
        
        // Otherwise get from inventory
        String fruitId = updatePlayerFruit(player);
        return fruitId;
    }
    
    /**
     * Update the player's held fruit in cache
     */
    public String updatePlayerFruit(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && !item.getType().isAir()) {
            String fruitId = Fruit.getFruitId(item);
            if (fruitId != null) {
                playerFruits.put(player.getUniqueId(), fruitId);
                return fruitId;
            }
        }
        
        // Check off-hand
        item = player.getInventory().getItemInOffHand();
        if (item != null && !item.getType().isAir()) {
            String fruitId = Fruit.getFruitId(item);
            if (fruitId != null) {
                playerFruits.put(player.getUniqueId(), fruitId);
                return fruitId;
            }
        }
        
        playerFruits.remove(player.getUniqueId());
        return null;
    }
    
    /**
     * Set a fruit for the player (puts in main hand)
     */
    public void setPlayerFruit(Player player, String fruitId) {
        Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
        if (fruit != null) {
            player.getInventory().setItemInMainHand(fruit.createItemStack(1));
            playerFruits.put(player.getUniqueId(), fruitId);
        }
    }
    
    /**
     * Clear player's held fruit cache
     */
    public void clearPlayerFruitCache(Player player) {
        playerFruits.remove(player.getUniqueId());
    }
    
    public int getTotalFruits(Player player) {
        int total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && Fruit.getFruitId(item) != null) {
                total += item.getAmount();
            }
        }
        return total;
    }
    
    public Map<String, Integer> getFruitBreakdown(Player player) {
        Map<String, Integer> breakdown = new HashMap<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                String fruitId = Fruit.getFruitId(item);
                if (fruitId != null) {
                    breakdown.put(fruitId, breakdown.getOrDefault(fruitId, 0) + item.getAmount());
                }
            }
        }
        return breakdown;
    }
    
    private void startInactiveCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long inactiveThreshold = 300000; // 5 minutes
                
                for (UUID uuid : new ArrayList<>(activePlayers)) {
                    Long lastActive = lastActiveTime.get(uuid);
                    if (lastActive != null && (now - lastActive) > inactiveThreshold) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            savePlayerStats(player);
                        }
                        activePlayers.remove(uuid);
                        playerFruits.remove(uuid);
                    }
                }
            }
        }.runTaskTimer(plugin, 6000L, 6000L);
    }
    
    // ==================== INNER CLASS ====================
    
    public static class PlayerStats {
        private String playerName;
        private int totalSpins;
        private int totalSteals;
        private int totalFruitsCollected;
        private int totalAbilitiesUsed;
        private long firstJoinTime;
        private long lastPlayTime;
        
        public PlayerStats(String playerName) {
            this.playerName = playerName;
            this.totalSpins = 0;
            this.totalSteals = 0;
            this.totalFruitsCollected = 0;
            this.totalAbilitiesUsed = 0;
            this.firstJoinTime = System.currentTimeMillis();
            this.lastPlayTime = System.currentTimeMillis();
        }
        
        public void incrementSpins() { totalSpins++; }
        public void incrementSteals() { totalSteals++; }
        public void incrementFruits(int amount) { totalFruitsCollected += amount; }
        public void incrementAbilitiesUsed() { totalAbilitiesUsed++; }
        public void updatePlayTime() { lastPlayTime = System.currentTimeMillis(); }
        
        public void updateFromPlayer(Player player) {
            lastPlayTime = System.currentTimeMillis();
        }
        
        public Map<String, Object> serialize() {
            Map<String, Object> data = new HashMap<>();
            data.put("playerName", playerName);
            data.put("totalSpins", totalSpins);
            data.put("totalSteals", totalSteals);
            data.put("totalFruitsCollected", totalFruitsCollected);
            data.put("totalAbilitiesUsed", totalAbilitiesUsed);
            data.put("firstJoinTime", firstJoinTime);
            data.put("lastPlayTime", lastPlayTime);
            return data;
        }
        
        @SuppressWarnings("unchecked")
        public static PlayerStats deserialize(Map<String, Object> data) {
            PlayerStats stats = new PlayerStats((String) data.get("playerName"));
            stats.totalSpins = ((Number) data.getOrDefault("totalSpins", 0)).intValue();
            stats.totalSteals = ((Number) data.getOrDefault("totalSteals", 0)).intValue();
            stats.totalFruitsCollected = ((Number) data.getOrDefault("totalFruitsCollected", 0)).intValue();
            stats.totalAbilitiesUsed = ((Number) data.getOrDefault("totalAbilitiesUsed", 0)).intValue();
            stats.firstJoinTime = ((Number) data.getOrDefault("firstJoinTime", System.currentTimeMillis())).longValue();
            stats.lastPlayTime = ((Number) data.getOrDefault("lastPlayTime", System.currentTimeMillis())).longValue();
            return stats;
        }
        
        public String getPlayerName() { return playerName; }
        public int getTotalSpins() { return totalSpins; }
        public int getTotalSteals() { return totalSteals; }
        public int getTotalFruitsCollected() { return totalFruitsCollected; }
        public int getTotalAbilitiesUsed() { return totalAbilitiesUsed; }
    }
}
