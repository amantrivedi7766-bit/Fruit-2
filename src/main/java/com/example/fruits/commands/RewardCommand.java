package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RewardCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("fruits.admin")) {
            sender.sendMessage(ChatColor.RED + "❌ You don't have permission!");
            return true;
        }
        
        if(args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        if(args[0].equalsIgnoreCase("toggle")) {
            boolean current = FruitsPlugin.getInstance().getConfigManager().isRewardEnabled();
            FruitsPlugin.getInstance().getConfigManager().setRewardEnabled(!current);
            
            String status = !current ? "§aENABLED" : "§cDISABLED";
            sender.sendMessage("§e=================================");
            sender.sendMessage("§6⚙️ Join Reward System: " + status);
            sender.sendMessage("§7New players will " + (!current ? "now" : "no longer") + " receive a fruit on join!");
            sender.sendMessage("§e=================================");
        } 
        else if(args[0].equalsIgnoreCase("status")) {
            boolean enabled = FruitsPlugin.getInstance().getConfigManager().isRewardEnabled();
            sender.sendMessage("§e=================================");
            sender.sendMessage("§6⚙️ Join Reward Status: " + (enabled ? "§aENABLED" : "§cDISABLED"));
            sender.sendMessage("§e=================================");
        }
        else if(args[0].equalsIgnoreCase("reload")) {
            FruitsPlugin.getInstance().getConfigManager().reloadConfig();
            sender.sendMessage("§a✅ Config reloaded!");
        }
        else if(args[0].equalsIgnoreCase("test") && sender instanceof Player) {
            Player player = (Player) sender;
            FruitsPlugin.getInstance().getSpinManager().startSpin(player);
            sender.sendMessage("§a🎲 Test spin started!");
        }
        else {
            sendHelp(sender);
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l========== [FRUIT REWARD HELP] ==========");
        sender.sendMessage("§e/freward toggle §7- Toggle join reward on/off");
        sender.sendMessage("§e/freward status §7- Check reward status");
        sender.sendMessage("§e/freward reload §7- Reload config");
        sender.sendMessage("§e/freward test §7- Test spin animation");
        sender.sendMessage("§6§l========================================");
    }
}
