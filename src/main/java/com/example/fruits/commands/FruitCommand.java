package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FruitCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(args.length == 0) {
            showInfo(player);
            return true;
        }
        
        if(args[0].equalsIgnoreCase("info")) {
            showInfo(player);
        }
        else if(args[0].equalsIgnoreCase("stats")) {
            showStats(player);
        }
        else {
            player.sendMessage("§e§l🍎 Fruit Command");
            player.sendMessage("§7/fruit info §8- §7Show your fruit info");
            player.sendMessage("§7/fruit stats §8- §7Show your usage stats");
        }
        
        return true;
    }
    
    private void showInfo(Player player) {
        String fruitId = FruitsPlugin.getInstance().getPlayerManager().getPlayerFruit(player);
        
        if(fruitId == null) {
            player.sendMessage("§c❌ You don't have a magical fruit yet!");
            player.sendMessage("§eJoin the server for a free fruit spin!");
            return;
        }
        
        Fruit fruit = FruitsPlugin.getInstance().getFruitRegistry().getFruit(fruitId);
        if(fruit != null) {
            player.sendMessage("§6§l========== [YOUR FRUIT] ==========");
            player.sendMessage(fruit.getName());
            player.sendMessage("§7Right-click to use abilities!");
            player.sendMessage("§7Sneak + Right-click for second ability!");
            player.sendMessage("§6§l=================================");
        }
    }
    
    private void showStats(Player player) {
        String fruitId = FruitsPlugin.getInstance().getPlayerManager().getPlayerFruit(player);
        
        if(fruitId == null) {
            player.sendMessage("§c❌ You don't have a magical fruit yet!");
            return;
        }
        
        int used = FruitsPlugin.getInstance().getPlayerManager().getUsedAbilities(player);
        
        player.sendMessage("§6§l========== [FRUIT STATS] ==========");
        player.sendMessage("§7Fruit: §e" + fruitId);
        player.sendMessage("§7Abilities Used: §e" + used);
        player.sendMessage("§6§l=================================");
    }
}
