package com.example.fruits.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;

public class PlayerManager {
    private final Set<UUID> activePlayers = new HashSet<>();
    private final Map<UUID, Map<String, Object>> playerData = new HashMap<>();
    
    public void addActivePlayer(Player player) {
        activePlayers.add(player.getUniqueId());
        if(!playerData.containsKey(player.getUniqueId())) {
            Map<String, Object> data = new HashMap<>();
            data.put("fruit", null);
            data.put("usedAbilities", 0);
            data.put("abilityUsage", new HashMap<String, Integer>());
            playerData.put(player.getUniqueId(), data);
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
        playerData.get(uuid).put("fruit", fruitId);
    }
    
    public String getPlayerFruit(Player player) {
        UUID uuid = player.getUniqueId();
        if(!playerData.containsKey(uuid)) return null;
        return (String) playerData.get(uuid).get("fruit");
    }
    
    public String getFruit(Player player) {
        return getPlayerFruit(player);
    }
    
    public boolean hasFruit(Player player) {
        return getPlayerFruit(player) != null;
    }
    
    @SuppressWarnings("unchecked")
    public int getUsedAbilities(Player player) {
        UUID uuid = player.getUniqueId();
        if(!playerData.containsKey(uuid)) return 0;
        return (int) playerData.get(uuid).getOrDefault("usedAbilities", 0);
    }
    
    public void incrementUsed(Player player) {
        UUID uuid = player.getUniqueId();
        if(!playerData.containsKey(uuid)) {
            addActivePlayer(player);
        }
        int current = getUsedAbilities(player);
        playerData.get(uuid).put("usedAbilities", current + 1);
    }
    
    public void incrementUsedAbilities(Player player) {
        incrementUsed(player);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Integer> getAbilityUsage(Player player) {
        UUID uuid = player.getUniqueId();
        if(!playerData.containsKey(uuid)) return new HashMap<>();
        return (Map<String, Integer>) playerData.get(uuid).getOrDefault("abilityUsage", new HashMap<>());
    }
    
    @SuppressWarnings("unchecked")
    public void recordAbilityUse(Player player, String abilityId) {
        UUID uuid = player.getUniqueId();
        if(!playerData.containsKey(uuid)) {
            addActivePlayer(player);
        }
        Map<String, Integer> usage = (Map<String, Integer>) playerData.get(uuid).get("abilityUsage");
        if(usage == null) {
            usage = new HashMap<>();
            playerData.get(uuid).put("abilityUsage", usage);
        }
        usage.put(abilityId, usage.getOrDefault(abilityId, 0) + 1);
        incrementUsed(player);
    }
    
    public Map<String, Object> getPlayerData(Player player) {
        return playerData.get(player.getUniqueId());
    }
    
    public Map<String, Object> get(UUID uuid) {
        return playerData.get(uuid);
    }
    
    public boolean containsKey(UUID uuid) {
        return playerData.containsKey(uuid);
    }
    
    public Map<UUID, Map<String, Object>> getPlayerDataMap() {
        return playerData;
    }
    
    public void clear() {
        activePlayers.clear();
        playerData.clear();
    }
}
