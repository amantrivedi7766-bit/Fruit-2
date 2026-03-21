package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.PlayerFruitData;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FruitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        
        if(args.length < 1) {
            p.sendMessage("§cUsage: /fruit <use|withdraw> [1|2|3]");
            return true;
        }
        
        // WITHDRAW COMMAND - Cancel fruit power
        if(args[0].equalsIgnoreCase("withdraw")) {
            PlayerFruitData data = FruitsPlugin.getInstance().getActivePlayers().get(p.getUniqueId());
            if(data == null || data.getFruit() == null) {
                p.sendMessage("§c❌ You don't have any active fruit power!");
                return true;
            }
            
            // Return fruit to inventory
            p.getInventory().addItem(data.getFruit().createItem());
            
            // Remove active fruit
            FruitsPlugin.getInstance().getActivePlayers().remove(p.getUniqueId());
            
            p.sendMessage("§a🔄 You withdrew your fruit power! The fruit has been returned.");
            p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            return true;
        }
        
        // USE COMMAND
        if(args.length != 2 || !args[0].equalsIgnoreCase("use")) {
            p.sendMessage("§cUsage: /fruit use <1|2|3> or /fruit withdraw");
            return true;
        }
        
        PlayerFruitData data = FruitsPlugin.getInstance().getActivePlayers().get(p.getUniqueId());
        if(data == null || data.getFruit() == null) {
            p.sendMessage("§c❌ Eat a fruit first!");
            return true;
        }
        
        int index;
        try {
            index = Integer.parseInt(args[1]) - 1;
        } catch(Exception e) {
            p.sendMessage("§c❌ Use 1, 2, or 3");
            return true;
        }
        
        Fruit fruit = data.getFruit();
        if(index < 0 || index >= fruit.getAbilities().size()) {
            p.sendMessage("§c❌ Invalid ability number");
            return true;
        }
        
        com.example.fruits.models.Ability ability = fruit.getAbilities().get(index);
        String cooldownKey = fruit.getId() + "_" + index;
        
        if(!FruitsPlugin.getInstance().getCooldownManager().checkCooldown(p, cooldownKey)) return true;
        
        // Get target (player or mob)
        org.bukkit.entity.Entity target = getTargetEntity(p, 20);
        
        // Execute ability with target
        ability.getExecutor().execute(p, target);
        FruitsPlugin.getInstance().getCooldownManager().setCooldown(p, cooldownKey, ability.getCooldown(), ability.getName());
        
        data.incrementUsed();
        
        // Send message with target info
        if(target != null) {
            p.sendMessage("§a⚡ Used §6" + ability.getName() + "§a on §e" + getEntityName(target) + "§a! (" + data.getUsedAbilities() + "/3)");
        } else {
            p.sendMessage("§a⚡ Used §6" + ability.getName() + "§a! (" + data.getUsedAbilities() + "/3)");
        }
        
        // Update action bar
        int remaining = 3 - data.getUsedAbilities();
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
            TextComponent.fromLegacyText("§a✓ " + remaining + " §7uses remaining"));
        
        if(data.getFruit() == null) {
            p.sendMessage("§a🔄 Fruit returned to inventory! Eat again to reuse!");
        }
        
        return true;
    }
    
    private org.bukkit.entity.Entity getTargetEntity(Player player, int range) {
        // Get the entity the player is looking at
        return player.getWorld().getNearbyEntities(player.getEyeLocation(), range, range, range)
            .stream()
            .filter(e -> e != player && e.getLocation().distance(player.getEyeLocation()) <= range)
            .min((e1, e2) -> {
                double d1 = e1.getLocation().distance(player.getEyeLocation());
                double d2 = e2.getLocation().distance(player.getEyeLocation());
                return Double.compare(d1, d2);
            })
            .orElse(null);
    }
    
    private String getEntityName(org.bukkit.entity.Entity e) {
        if(e instanceof Player) return ((Player) e).getName();
        return e.getType().name().toLowerCase().replace("_", " ");
    }
}
