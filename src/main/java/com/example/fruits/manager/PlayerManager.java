package com.example.fruits.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.*;

public class PlayerManager {
    private final Set<UUID> activePlayers = new HashSet<>();
    private final Map<UUID, String> playerFruits = new HashMap<>();
    
    public void addActivePlayer(Player player) {
        activePlayers.add(player.getUniqueId());
    }
    
    public void removeActivePlayer(Player player) {
        activePlayers.remove(player.getUniqueId());
        playerFruits.remove(player.getUniqueId());
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
        playerFruits.put(player.getUniqueId(), fruitId);
    }
    
    public String getPlayerFruit(Player player) {
        return playerFruits.get(player.getUniqueId());
    }
    
    public boolean hasFruit(Player player) {
        return playerFruits.containsKey(player.getUniqueId());
    }
    
    public void clear() {
        activePlayers.clear();
        playerFruits.clear();
    }
}
