package com.example.fruits.listeners;

import com.example.fruits.FruitsPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    
    private final FruitsPlugin plugin;
    
    public JoinListener(FruitsPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Add to active players
        plugin.getPlayerManager().addActivePlayer(player);
        plugin.getPlayerManager().loadPlayerStats(player);
        
        // Start join protection
        plugin.getGracePeriodManager().startProtectionOnJoin(player);
        
        // Check if player has a fruit in hand
        plugin.getPlayerManager().updatePlayerFruit(player);
        
        if (plugin.getConfigManager().isDebugMode()) {
            plugin.getLogger().info(player.getName() + " joined the game!");
        }
    }
}
