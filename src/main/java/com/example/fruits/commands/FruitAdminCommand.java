package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.gui.AdminGUI;
import com.example.fruits.models.Fruit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.Random;

public class FruitAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("fruits.admin")) {
            sender.sendMessage("§cNo permission!");
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage("§cUsage: /fruitadmin <give|spin|reload|gui>");
            return true;
        }

        switch(args[0].toLowerCase()) {
            case "give":
                if(args.length < 3) {
                    sender.sendMessage("§cUsage: /fruitadmin give <player> <fruit>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null) {
                    sender.sendMessage("§cPlayer not found!");
                    return true;
                }
                Fruit fruit = FruitsPlugin.getInstance().getFruitRegistry().getFruit(args[2]);
                if(fruit == null) {
                    sender.sendMessage("§cFruit not found!");
                    return true;
                }
                target.getInventory().addItem(fruit.createItem());
                sender.sendMessage("§aGave " + fruit.getDisplayName() + " to " + target.getName());
                break;

            case "spin":
                if(args.length < 2) {
                    sender.sendMessage("§cUsage: /fruitadmin spin <player>");
                    return true;
                }
                Player spinTarget = Bukkit.getPlayer(args[1]);
                if(spinTarget == null) {
                    sender.sendMessage("§cPlayer not found!");
                    return true;
                }
                Fruit[] fruits = FruitsPlugin.getInstance().getFruitRegistry().getAllFruits().toArray(new Fruit[0]);
                Fruit random = fruits[new Random().nextInt(fruits.length)];
                spinTarget.getInventory().addItem(random.createItem());
                sender.sendMessage("§aGave random fruit to " + spinTarget.getName());
                break;

            case "reload":
                FruitsPlugin.getInstance().reloadConfig();
                sender.sendMessage("§aConfig reloaded!");
                break;

            case "gui":
                if(!(sender instanceof Player)) {
                    sender.sendMessage("§cOnly players can use GUI!");
                    return true;
                }
                AdminGUI.open((Player) sender);
                break;

            default:
                sender.sendMessage("§cUnknown command!");
        }
        return true;
    }
}
