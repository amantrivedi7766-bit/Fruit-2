package com.example.fruits.manager;

import com.example.fruits.models.PlayerFruitData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;

public class PlayerManager {
    private final Set<UUID> activePlayers = new HashSet<>();
    private final Map<UUID, PlayerFruitData> playerFruits = new HashMap<>();
    
    public void addActivePlayer(Player player) {
        activePlayers.add(player.getUniqueId());
        if(!playerFruits.containsKey(player.getUniqueId())) {
            playerFruits.put(player.getUniqueId(), new PlayerFruitData(player.getUniqueId(), null));
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
        if(playerFruits.containsKey(uuid)) {
            playerFruits.get(uuid).setCurrentFruit(fruitId);
            playerFruits.get(uuid).updateLastUsed();
        } else {
            playerFruits.put(uuid, new PlayerFruitData(uuid, fruitId));
        }
    }
    
    public String getPlayerFruit(Player player) {
        PlayerFruitData data = playerFruits.get(player.getUniqueId());
        return data != null ? data.getCurrentFruit() : null;
    }
    
    public boolean hasFruit(Player player) {
        return playerFruits.containsKey(player.getUniqueId()) && 
               playerFruits.get(player.getUniqueId()).getCurrentFruit() != null;
    }
    
    public PlayerFruitData getPlayerData(Player player) {
        return playerFruits.get(player.getUniqueId());
    }
    
    public void clear() {
        activePlayers.clear();
        playerFruits.clear();
    }
}
