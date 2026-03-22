package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.gui.AdminGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {
    
    private final FruitsPlugin plugin;
    private AdminGUI adminGUI;
    
    public AdminCommand(FruitsPlugin plugin) {
        this.plugin = plugin;
        this.adminGUI = new AdminGUI(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("fruit.admin") && !player.isOp()) {
            player.sendMessage("§cYou don't have permission!");
            return true;
        }
        
        if (args.length == 0) {
            adminGUI.openMainMenu(player);
            return true;
        }
        
        String subCmd = args[0].toLowerCase();
        
        switch (subCmd) {
            case "players":
                adminGUI.openPlayerManagement(player);
                break;
            case "fruits":
                adminGUI.openFruitManagement(player);
                break;
            case "rewards":
                adminGUI.openRewardSettings(player);
                break;
            case "grace":
                adminGUI.openGracePeriod(player);
                break;
            case "giveall":
                if (args.length >= 2) {
                    String fruitId = args[1];
                    int amount = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        com.example.fruits.models.Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
                        if (fruit != null) {
                            p.getInventory().addItem(fruit.createItemStack(amount));
                            p.sendMessage("§aYou received §6" + amount + "x " + fruit.getName() + "§a from admin!");
                        }
                    }
                    player.sendMessage("§a✓ Gave to all players!");
                }
                break;
            case "spinall":
                int spins = args.length >= 2 ? Integer.parseInt(args[1]) : 1;
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    plugin.getSpinManager().startSpin(p, spins);
                }
                player.sendMessage("§a✓ Started spin for all players!");
                break;
            case "stopspin":
                plugin.getSpinManager().stopAllSpins();
                player.sendMessage("§a✓ Stopped all spins!");
                break;
            case "reload":
                plugin.reloadConfig();
                plugin.getConfigManager().reload();
                player.sendMessage("§a✓ Plugin reloaded!");
                break;
            default:
                adminGUI.openMainMenu(player);
                break;
        }
        
        return true;
    }
}
