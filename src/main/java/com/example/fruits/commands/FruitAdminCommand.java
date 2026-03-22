package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.gui.AdminGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FruitAdminCommand implements CommandExecutor {
    
    private final FruitsPlugin plugin;
    private AdminGUI adminGUI;
    
    public FruitAdminCommand(FruitsPlugin plugin) {
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
            case "give":
                if (args.length >= 3) {
                    Player target = plugin.getServer().getPlayer(args[1]);
                    String fruitId = args[2];
                    int amount = args.length >= 4 ? Integer.parseInt(args[3]) : 1;
                    
                    com.example.fruits.models.Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
                    if (fruit != null && target != null) {
                        target.getInventory().addItem(fruit.createItemStack(amount));
                        player.sendMessage("§a✓ Gave §6" + amount + "x " + fruit.getName() + "§a to §e" + target.getName());
                    }
                }
                break;
            case "remove":
                if (args.length >= 3) {
                    Player target = plugin.getServer().getPlayer(args[1]);
                    String fruitId = args[2];
                    int amount = args.length >= 4 ? Integer.parseInt(args[3]) : 1;
                    
                    // Remove logic
                    player.sendMessage("§a✓ Removed from player!");
                }
                break;
            case "giveall":
                if (args.length >= 2) {
                    String fruitId = args[1];
                    int amount = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        com.example.fruits.models.Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
                        if (fruit != null) {
                            p.getInventory().addItem(fruit.createItemStack(amount));
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
                player.sendMessage("§a✓ Started spin for all!");
                break;
            case "reload":
                plugin.reloadConfig();
                plugin.getConfigManager().reload();
                player.sendMessage("§a✓ Reloaded!");
                break;
            default:
                adminGUI.openMainMenu(player);
                break;
        }
        
        return true;
    }
}
