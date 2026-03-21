package com.example.fruits.manager;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    
    public boolean checkCooldown(Player player, String abilityId) {
        if(!cooldowns.containsKey(player.getUniqueId())) {
            return true;
        }
        
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if(!playerCooldowns.containsKey(abilityId)) {
            return true;
        }
        
        long remaining = playerCooldowns.get(abilityId) - System.currentTimeMillis();
        if(remaining <= 0) {
            playerCooldowns.remove(abilityId);
            return true;
        }
        
        player.sendMessage("§c⏰ This ability is on cooldown for " + (remaining / 1000) + " seconds!");
        return false;
    }
    
    public void setCooldown(Player player, String abilityId, int cooldownSeconds, String abilityName) {
        long expiry = System.currentTimeMillis() + (cooldownSeconds * 1000L);
        
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                 .put(abilityId, expiry);
        
        player.sendMessage("§e⏰ " + abilityName + " §7is now on cooldown for §e" + cooldownSeconds + " §7seconds!");
    }
    
    public long getRemainingCooldown(Player player, String abilityId) {
        if(!cooldowns.containsKey(player.getUniqueId())) {
            return 0;
        }
        
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if(!playerCooldowns.containsKey(abilityId)) {
            return 0;
        }
        
        long remaining = playerCooldowns.get(abilityId) - System.currentTimeMillis();
        return Math.max(0, remaining);
    }
    
    public void clearCooldowns(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
