package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.gui.AdminGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FruitAdminCommand implements CommandExecutor {
    
    private final AdminGUI adminGUI = new AdminGUI();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(!player.hasPermission("fruits.admin")) {
            player.sendMessage("§c❌ You don't have permission!");
            return true;
        }
        
        if(args.length == 0) {
            adminGUI.openMainMenu(player);
        }
        else if(args[0].equalsIgnoreCase("toggle")) {
            boolean enabled = FruitsPlugin.getInstance().getConfigManager().isRewardEnabled();
            FruitsPlugin.getInstance().getConfigManager().setRewardEnabled(!enabled);
            player.sendMessage("§a✅ Join reward " + (!enabled ? "enabled" : "disabled") + "!");
        }
        else if(args[0].equalsIgnoreCase("status")) {
            player.sendMessage("§eJoin Reward: " + (FruitsPlugin.getInstance().getConfigManager().isRewardEnabled() ? "§aENABLED" : "§cDISABLED"));
            player.sendMessage("§eActive Players: §a" + FruitsPlugin.getInstance().getActivePlayers().size());
        }
        
        return true;
    }
}
