package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.managers.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RewardCommand implements CommandExecutor {
    
    private final FruitsPlugin plugin;
    
    public RewardCommand(FruitsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if (!sender.hasPermission("fruit.admin") && !sender.isOp()) {
            sender.sendMessage("§cYou don't have permission!");
            return true;
        }
        
        ConfigManager configManager = plugin.getConfigManager();
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCmd = args[0].toLowerCase();
        
        switch (subCmd) {
            case "toggle":
                boolean enabled = !configManager.isRewardEnabled();
                configManager.setRewardEnabled(enabled);
                sender.sendMessage((enabled ? "§a✓" : "§c✗") + " Rewards " + (enabled ? "enabled" : "disabled"));
                break;
                
            case "status":
                sender.sendMessage("§6=== Reward Status ===");
                sender.sendMessage("§7Enabled: " + (configManager.isRewardEnabled() ? "§aYes" : "§cNo"));
                sender.sendMessage("§7Spin Cooldown: §e" + configManager.getSpinCooldown() + "s");
                sender.sendMessage("§7Max Spins/Day: §e" + configManager.getMaxSpinsPerDay());
                break;
                
            case "reload":
                configManager.reload();
                sender.sendMessage("§a✓ Reward config reloaded!");
                break;
                
            case "test":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    plugin.getSpinManager().startSpin(player, 1);
                    sender.sendMessage("§a✓ Test spin started!");
                }
                break;
                
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== Reward Commands ===");
        sender.sendMessage("§e/reward toggle §7- Toggle rewards on/off");
        sender.sendMessage("§e/reward status §7- View reward status");
        sender.sendMessage("§e/reward reload §7- Reload reward config");
        sender.sendMessage("§e/reward test §7- Test reward system");
    }
}
