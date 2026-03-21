package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GracePeriodCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("fruits.admin")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }
        
        if(args.length < 1) {
            sender.sendMessage("§cUsage: /graceperiod <start|stop> [minutes]");
            return true;
        }
        
        if(args[0].equalsIgnoreCase("start")) {
            int minutes = args.length > 1 ? Integer.parseInt(args[1]) : 1;
            int seconds = minutes * 60;
            FruitsPlugin.getInstance().getGracePeriodManager().startGlobalGrace(seconds);
            sender.sendMessage("§aStarted grace period for " + minutes + " minutes!");
        } 
        else if(args[0].equalsIgnoreCase("stop")) {
            sender.sendMessage("§cGrace period stopped!");
        }
        else {
            sender.sendMessage("§cUsage: /graceperiod <start|stop> [minutes]");
        }
        
        return true;
    }
}
