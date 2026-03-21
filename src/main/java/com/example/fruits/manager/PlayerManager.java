package com.example.fruits.manager;

import org.bukkit.entity.Player;
import java.util.*;

public class PlayerManager {
    private final Set<UUID> activePlayers = new HashSet<>();
    
    public void addActivePlayer(Player player) {
        activePlayers.add(player.getUniqueId());
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
            Player p = org.bukkit.Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) {
                players.add(p);
            }
        }
        return players;
    }
    
    public void clear() {
        activePlayers.clear();
    }
}
