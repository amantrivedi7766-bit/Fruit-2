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
        if(!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if(args.length != 2 || !args[0].equals("use")) {
            p.sendMessage("§c/fruit use <1|2|3>");
            return true;
        }
        PlayerFruitData data = FruitsPlugin.getInstance().getActivePlayers().get(p.getUniqueId());
        if(data == null || data.getFruit() == null) {
            p.sendMessage("§cEat a fruit first!");
            return true;
        }
        int index;
        try { index = Integer.parseInt(args[1]) - 1; } catch(Exception e) {
            p.sendMessage("§cUse 1, 2, or 3");
            return true;
        }
        Fruit fruit = data.getFruit();
        if(index < 0 || index >= fruit.getAbilities().size()) {
            p.sendMessage("§cInvalid ability");
            return true;
        }
        Ability ability = fruit.getAbilities().get(index);
        if(!FruitsPlugin.getInstance().getCooldownManager().checkCooldown(p, fruit.getId()+"_"+index)) return true;
        ability.getExecutor().execute(p);
        FruitsPlugin.getInstance().getCooldownManager().setCooldown(p, fruit.getId()+"_"+index, ability.getCooldown(), ability.getName());
        data.incrementUsed();
        p.sendMessage("§aUsed " + ability.getName());
        if(data.getFruit() == null) p.sendMessage("§aFruit returned!");
        return true;
    }
}
