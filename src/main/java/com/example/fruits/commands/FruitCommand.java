package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Ability;
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
        
        if(args.length != 2 || !args[0].equalsIgnoreCase("use")) {
            p.sendMessage("§cUsage: §e/fruit use <1|2|3>");
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
        
        Ability ability = fruit.getAbilities().get(index);
        String cooldownKey = fruit.getId() + "_" + index;
        
        if(!FruitsPlugin.getInstance().getCooldownManager().checkCooldown(p, cooldownKey)) return true;
        
        ability.getExecutor().execute(p);
        FruitsPlugin.getInstance().getCooldownManager().setCooldown(p, cooldownKey, ability.getCooldown(), ability.getName());
        
        data.incrementUsed();
        
        p.sendMessage("§a⚡ Used §6" + ability.getName() + "§a! (" + data.getUsedAbilities() + "/3)");
        
        int remaining = 3 - data.getUsedAbilities();
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
            TextComponent.fromLegacyText("§a✓ " + remaining + " §7uses remaining"));
        
        if(data.getFruit() == null) {
            p.sendMessage("§a🔄 Fruit returned! Eat again to reuse abilities!");
        }
        
        return true;
    }
}
