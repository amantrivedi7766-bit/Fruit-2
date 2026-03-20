package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.PlayerFruitData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FruitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(args.length != 2 || !args[0].equalsIgnoreCase("use")) {
            player.sendMessage("§cUsage: /fruit use <1|2|3>");
            return true;
        }

        PlayerFruitData data = FruitsPlugin.getInstance().getActivePlayers().get(player.getUniqueId());
        if(data == null || data.getFruit() == null) {
            player.sendMessage(FruitsPlugin.getInstance().getConfig().getString("messages.no_fruit").replace('&', '§'));
            return true;
        }

        int index;
        try {
            index = Integer.parseInt(args[1]) - 1;
        } catch(NumberFormatException e) {
            player.sendMessage(FruitsPlugin.getInstance().getConfig().getString("messages.invalid_ability").replace('&', '§'));
            return true;
        }

        Fruit fruit = data.getFruit();
        if(index < 0 || index >= fruit.getAbilities().size()) {
            player.sendMessage(FruitsPlugin.getInstance().getConfig().getString("messages.invalid_ability").replace('&', '§'));
            return true;
        }

        Ability ability = fruit.getAbilities().get(index);
        String cooldownKey = fruit.getId() + "_" + index;

        if(!FruitsPlugin.getInstance().getCooldownManager().checkCooldown(player, cooldownKey)) {
            return true;
        }

        ability.getExecutor().execute(player);
        
        // ✅ FIXED: Added ability name as 4th parameter
        FruitsPlugin.getInstance().getCooldownManager().setCooldown(
            player, 
            cooldownKey, 
            ability.getCooldown(),
            ability.getName()  // ✅ 4th parameter added
        );

        data.incrementUsed();
        
        String msg = FruitsPlugin.getInstance().getConfig().getString("messages.ability_used")
            .replace("{ability}", ability.getName())
            .replace("{used}", String.valueOf(data.getUsedAbilities()))
            .replace('&', '§');
        player.sendMessage(msg);

        if(data.getFruit() == null) {
            player.sendMessage(FruitsPlugin.getInstance().getConfig().getString("messages.fruit_returned").replace('&', '§'));
        }

        return true;
    }
}
