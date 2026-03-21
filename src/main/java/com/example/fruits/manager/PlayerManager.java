package com.example.fruits.manager;

import com.example.fruits.models.PlayerFruitData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;

public class PlayerManager {
    private final Set<UUID> activePlayers = new HashSet<>();
    private final Map<UUID, PlayerFruitData> playerData = new HashMap<>();
    
    public void addActivePlayer(Player player) {
        activePlayers.add(player.getUniqueId());
        if(!playerData.containsKey(player.getUniqueId())) {
            playerData.put(player.getUniqueId(), new PlayerFruitData(player.getUniqueId(), null));
        }
    }
    
    public void removeActivePlayer(Player player) {
        activePlayers.remove(player.getUniqueId());
    }
    
    public boolean isActivePlayer(Player player) {
        return activePlayers.contains(player.getUniqueId());
    }
    
    public List<Player> getActivePlayers() {
        List<Player> players = new ArrayList<>();
        for(UUID uuid : activePlayers) {
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) {
                players.add(p);
            }
        }
        return players;
    }
    
    public void setPlayerFruit(Player player, String fruitId) {
        UUID uuid = player.getUniqueId();
        if(!playerData.containsKey(uuid)) {
            addActivePlayer(player);
        }
        playerData.get(uuid).setCurrentFruit(fruitId);
        playerData.get(uuid).updateLastUsed();
    }
    
    public String getPlayerFruit(Player player) {
        UUID uuid = player.getUniqueId();
        if(!playerData.containsKey(uuid)) return null;
        return playerData.get(uuid).getCurrentFruit();
    }
    
    public String getFruit(Player player) {
        return getPlayerFruit(player);
    }
    
    public boolean hasFruit(Player player) {
        return getPlayerFruit(player) != null;
    }
    
    public int getUsedAbilities(Player player) {
        UUID uuid = player.getUniqueId();
        if(!playerData.containsKey(uuid)) return 0;
        return playerData.get(uuid).getUsedCount();
    }
    
    public void incrementUsed(Player player) {
        UUID uuid = player.getUniqueId();
        if(!playerData.containsKey(uuid)) {
            addActivePlayer(player);
        }
        playerData.get(uuid).incrementUsed();
    }
    
    public void incrementUsedAbilities(Player player) {
        incrementUsed(player);
    }
    
    public Map<String, Integer> getAbilityUsage(Player player) {
        UUID uuid = player.getUniqueId();
        if(!playerData.containsKey(uuid)) return new HashMap<>();
        return playerData.get(uuid).getAbilityUsage();
    }
    
    public void recordAbilityUse(Player player, String abilityId) {
        UUID uuid = player.getUniqueId();
        if(!playerData.containsKey(uuid)) {
            addActivePlayer(player);
        }
        playerData.get(uuid).recordAbilityUse(abilityId);
    }
    
    public PlayerFruitData getPlayerData(Player player) {
        return playerData.get(player.getUniqueId());
    }
    
    public PlayerFruitData get(UUID uuid) {
        return playerData.get(uuid);
    }
    
    public boolean containsKey(UUID uuid) {
        return playerData.containsKey(uuid);
    }
    
    // ==================== FOR COMPATIBILITY ====================
    
    public Map<UUID, Map<String, Object>> getPlayerDataMap() {
        Map<UUID, Map<String, Object>> result = new HashMap<>();
        for(Map.Entry<UUID, PlayerFruitData> entry : playerData.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("fruit", entry.getValue().getCurrentFruit());
            data.put("usedAbilities", entry.getValue().getUsedCount());
            data.put("abilityUsage", entry.getValue().getAbilityUsage());
            result.put(entry.getKey(), data);
        }
        return result;
    }
    
    public void clear() {
        activePlayers.clear();
        playerData.clear();
    }
}
