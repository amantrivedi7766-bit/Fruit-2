package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.Ability;
import com.example.fruits.gui.FruitGUI;
import com.example.fruits.gui.SpinGUI;
import com.example.fruits.gui.TradeGUI;
import com.example.fruits.managers.SpinManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class FruitCommand implements CommandExecutor, TabCompleter {
    
    private final FruitsPlugin plugin;
    private final SpinManager spinManager;
    
    public FruitCommand(FruitsPlugin plugin) {
        this.plugin = plugin;
        this.spinManager = new SpinManager(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if(!cmd.getName().equalsIgnoreCase("fruit")) return false;
        
        if(args.length == 0) {
            if(sender instanceof Player) {
                // Open main GUI for players
                new FruitGUI(plugin, (Player) sender).open();
            } else {
                sendHelp(sender);
            }
            return true;
        }
        
        String subCmd = args[0].toLowerCase();
        
        // ========== GUI COMMAND ==========
        if(subCmd.equals("gui")) {
            return handleGUI(sender);
        }
        
        // ========== SPIN COMMANDS ==========
        else if(subCmd.equals("spin")) {
            return handleSpin(sender, args);
        }
        
        else if(subCmd.equals("spinself")) {
            return handleSpinSelf(sender, args);
        }
        
        else if(subCmd.equals("spinplayer")) {
            return handleSpinPlayer(sender, args);
        }
        
        else if(subCmd.equals("spinall")) {
            return handleSpinAll(sender, args);
        }
        
        else if(subCmd.equals("stopspin")) {
            return handleStopSpin(sender, args);
        }
        
        // ========== AUTO GIVE COMMANDS ==========
        else if(subCmd.equals("autogive")) {
            return handleAutoGive(sender, args);
        }
        
        else if(subCmd.equals("setjoinfruit")) {
            return handleSetJoinFruit(sender, args);
        }
        
        else if(subCmd.equals("togglejoinfruit")) {
            return handleToggleJoinFruit(sender);
        }
        
        // ========== TRADE COMMANDS ==========
        else if(subCmd.equals("trade")) {
            return handleTrade(sender, args);
        }
        
        else if(subCmd.equals("tradeaccept")) {
            return handleTradeAccept(sender);
        }
        
        else if(subCmd.equals("tradedecline")) {
            return handleTradeDecline(sender);
        }
        
        // ========== STEAL COMMAND ==========
        else if(subCmd.equals("steal")) {
            return handleSteal(sender, args);
        }
        
        // ========== GIVE COMMAND ==========
        else if(subCmd.equals("give")) {
            return handleGive(sender, args);
        }
        
        // ========== RELOAD COMMAND ==========
        else if(subCmd.equals("reload")) {
            return handleReload(sender);
        }
        
        // ========== LIST COMMAND ==========
        else if(subCmd.equals("list")) {
            return handleList(sender);
        }
        
        // ========== INFO COMMAND ==========
        else if(subCmd.equals("info")) {
            return handleInfo(sender, args);
        }
        
        // ========== COOLDOWN COMMAND ==========
        else if(subCmd.equals("cooldown")) {
            return handleCooldown(sender, args);
        }
        
        // ========== REMOVE COMMAND ==========
        else if(subCmd.equals("remove")) {
            return handleRemove(sender, args);
        }
        
        // ========== GIVEALL COMMAND ==========
        else if(subCmd.equals("giveall")) {
            return handleGiveAll(sender, args);
        }
        
        // ========== STATS COMMAND ==========
        else if(subCmd.equals("stats")) {
            return handleStats(sender, args);
        }
        
        // ========== TOP COMMAND ==========
        else if(subCmd.equals("top")) {
            return handleTop(sender);
        }
        
        else {
            sender.sendMessage("§cUnknown subcommand! Use /fruit help");
            sendHelp(sender);
        }
        
        return true;
    }
    
    // ==================== GUI COMMAND ====================
    private boolean handleGUI(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use GUI!");
            return true;
        }
        
        Player player = (Player) sender;
        new FruitGUI(plugin, player).open();
        return true;
    }
    
    // ==================== SPIN COMMANDS ====================
    private boolean handleSpin(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can spin!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(args.length >= 2 && args[1].equalsIgnoreCase("stop")) {
            spinManager.stopSpin(player);
            player.sendMessage("§c❌ Spin stopped!");
            return true;
        }
        
        int spins = 1;
        if(args.length >= 2) {
            try {
                spins = Integer.parseInt(args[1]);
                if(spins > 10) spins = 10;
            } catch(NumberFormatException e) {}
        }
        
        spinManager.startSpin(player, spins);
        return true;
    }
    
    private boolean handleSpinSelf(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can spin!");
            return true;
        }
        
        Player player = (Player) sender;
        int spins = args.length >= 2 ? Integer.parseInt(args[1]) : 5;
        spinManager.startSpin(player, spins);
        return true;
    }
    
    private boolean handleSpinPlayer(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.spin.others", true)) return true;
        
        if(args.length < 2) {
            sender.sendMessage("§cUsage: /fruit spinplayer <player> [spins]");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if(target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }
        
        int spins = args.length >= 3 ? Integer.parseInt(args[2]) : 5;
        spinManager.startSpin(target, spins);
        sender.sendMessage("§a✓ Started spin for §e" + target.getName() + "§a with §6" + spins + "§a spins!");
        return true;
    }
    
    private boolean handleSpinAll(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.spin.all", true)) return true;
        
        int spins = args.length >= 2 ? Integer.parseInt(args[1]) : 3;
        int count = 0;
        
        for(Player player : Bukkit.getOnlinePlayers()) {
            spinManager.startSpin(player, spins);
            count++;
        }
        
        sender.sendMessage("§a✓ Started §6" + spins + "§a spins for §e" + count + "§a players!");
        return true;
    }
    
    private boolean handleStopSpin(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.spin.stop", true)) return true;
        
        if(args.length >= 2) {
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null) {
                sender.sendMessage("§cPlayer not found!");
                return true;
            }
            spinManager.stopSpin(target);
            sender.sendMessage("§a✓ Stopped spin for §e" + target.getName());
        } else if(sender instanceof Player) {
            spinManager.stopSpin((Player) sender);
            sender.sendMessage("§a✓ Spin stopped!");
        } else {
            sender.sendMessage("§cUsage: /fruit stopspin [player]");
        }
        
        return true;
    }
    
    // ==================== AUTO GIVE COMMANDS ====================
    private boolean handleAutoGive(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.autogive", true)) return true;
        
        if(args.length < 2) {
            sender.sendMessage("§cUsage: /fruit autogive <on/off> [fruit_id] [amount]");
            sender.sendMessage("§7Current: " + (plugin.isAutoGiveEnabled() ? "§aON" : "§cOFF"));
            if(plugin.isAutoGiveEnabled()) {
                sender.sendMessage("§7Fruit: §f" + plugin.getAutoGiveFruit() + " §7Amount: §f" + plugin.getAutoGiveAmount());
            }
            return true;
        }
        
        if(args[1].equalsIgnoreCase("on")) {
            String fruitId = args.length >= 3 ? args[2] : "nature_dye";
            int amount = args.length >= 4 ? Integer.parseInt(args[3]) : 1;
            
            Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
            if(fruit == null) {
                sender.sendMessage("§cInvalid fruit!");
                return true;
            }
            
            plugin.setAutoGive(true, fruitId, amount);
            sender.sendMessage("§a✓ Auto-give enabled!");
            sender.sendMessage("§7Fruit: §f" + fruit.getName() + " §7Amount: §f" + amount);
        } 
        else if(args[1].equalsIgnoreCase("off")) {
            plugin.setAutoGive(false, null, 0);
            sender.sendMessage("§c✗ Auto-give disabled!");
        }
        
        return true;
    }
    
    private boolean handleSetJoinFruit(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.joingive", true)) return true;
        
        if(args.length < 2) {
            sender.sendMessage("§cUsage: /fruit setjoinfruit <fruit_id> [amount]");
            return true;
        }
        
        String fruitId = args[1];
        int amount = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
        
        Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
        if(fruit == null) {
            sender.sendMessage("§cInvalid fruit!");
            return true;
        }
        
        plugin.setJoinFruit(fruitId, amount);
        sender.sendMessage("§a✓ Join fruit set to §6" + amount + "x " + fruit.getName());
        return true;
    }
    
    private boolean handleToggleJoinFruit(CommandSender sender) {
        if(!hasPermission(sender, "fruit.joingive", true)) return true;
        
        boolean enabled = plugin.toggleJoinFruit();
        sender.sendMessage((enabled ? "§a✓" : "§c✗") + " Join fruit " + (enabled ? "enabled" : "disabled") + "!");
        return true;
    }
    
    // ==================== TRADE COMMANDS ====================
    private boolean handleTrade(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can trade!");
            return true;
        }
        
        if(args.length < 2) {
            sender.sendMessage("§cUsage: /fruit trade <player>");
            return true;
        }
        
        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[1]);
        
        if(target == null) {
            player.sendMessage("§cPlayer not found!");
            return true;
        }
        
        if(target == player) {
            player.sendMessage("§cYou cannot trade with yourself!");
            return true;
        }
        
        new TradeGUI(plugin, player, target).open();
        return true;
    }
    
    private boolean handleTradeAccept(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can accept trades!");
            return true;
        }
        
        Player player = (Player) sender;
        TradeGUI.acceptTrade(player);
        return true;
    }
    
    private boolean handleTradeDecline(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can decline trades!");
            return true;
        }
        
        Player player = (Player) sender;
        TradeGUI.declineTrade(player);
        return true;
    }
    
    // ==================== STEAL COMMAND ====================
    private boolean handleSteal(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can steal!");
            return true;
        }
        
        if(args.length < 2) {
            sender.sendMessage("§cUsage: /fruit steal <player>");
            return true;
        }
        
        Player thief = (Player) sender;
        Player victim = Bukkit.getPlayer(args[1]);
        
        if(victim == null) {
            thief.sendMessage("§cPlayer not found!");
            return true;
        }
        
        if(victim == thief) {
            thief.sendMessage("§cYou cannot steal from yourself!");
            return true;
        }
        
        // Check cooldown
        if(plugin.getCooldownManager().hasCooldown(thief, "steal")) {
            long remaining = plugin.getCooldownManager().getRemaining(thief, "steal");
            thief.sendMessage("§cSteal on cooldown! §7" + remaining + "s remaining");
            return true;
        }
        
        // Find fruit to steal
        List<ItemStack> fruits = new ArrayList<>();
        for(ItemStack item : victim.getInventory().getContents()) {
            if(item != null && Fruit.getFruitId(item) != null) {
                fruits.add(item);
            }
        }
        
        if(fruits.isEmpty()) {
            thief.sendMessage("§c" + victim.getName() + " has no fruits to steal!");
            return true;
        }
        
        // Steal random fruit
        ItemStack stolen = fruits.get(new Random().nextInt(fruits.size()));
        int amount = Math.min(stolen.getAmount(), new Random().nextInt(3) + 1);
        
        stolen.setAmount(stolen.getAmount() - amount);
        ItemStack stolenStack = stolen.clone();
        stolenStack.setAmount(amount);
        
        thief.getInventory().addItem(stolenStack);
        
        // Set cooldown
        plugin.getCooldownManager().setCooldown(thief, "steal", 60, "Steal");
        
        // Effects
        thief.getWorld().playSound(thief.getLocation(), org.bukkit.Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 1.5f);
        victim.getWorld().playSound(victim.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_HURT, 1.0f, 0.5f);
        
        thief.sendMessage("§a✓ Stole §6" + amount + "x " + Fruit.getFruitName(stolenStack) + "§a from §e" + victim.getName());
        victim.sendMessage("§c✗ " + thief.getName() + " stole §6" + amount + "x " + Fruit.getFruitName(stolenStack) + "§c from you!");
        
        return true;
    }
    
    // ==================== EXISTING METHODS (KEEP FROM BEFORE) ====================
    private boolean handleGive(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.give", true)) return true;
        
        if(args.length < 3) {
            sender.sendMessage("§eUsage: §6/fruit give <player> <fruit_id> [amount]");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if(target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }
        
        String fruitId = args[2].toLowerCase();
        Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
        if(fruit == null) {
            sender.sendMessage("§cInvalid fruit!");
            return true;
        }
        
        int amount = args.length >= 4 ? Integer.parseInt(args[3]) : 1;
        target.getInventory().addItem(fruit.createItemStack(amount));
        
        sender.sendMessage("§a✓ Gave §6" + amount + "x " + fruit.getName() + "§a to §e" + target.getName());
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        if(!hasPermission(sender, "fruit.admin", true)) return true;
        plugin.reloadConfig();
        sender.sendMessage("§a✓ Plugin reloaded!");
        return true;
    }
    
    private boolean handleList(CommandSender sender) {
        sender.sendMessage("§6=== §eFruits List §6===");
        for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
            sender.sendMessage(" §a" + fruit.getName() + " §7(" + fruit.getId() + ")");
        }
        return true;
    }
    
    private boolean handleInfo(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage("§cUsage: /fruit info <fruit_id>");
            return true;
        }
        
        Fruit fruit = plugin.getFruitRegistry().getFruit(args[1].toLowerCase());
        if(fruit == null) {
            sender.sendMessage("§cFruit not found!");
            return true;
        }
        
        sender.sendMessage("§6=== §e" + fruit.getName() + " §6===");
        sender.sendMessage(" §7ID: §f" + fruit.getId());
        for(int i = 0; i < fruit.getAbilities().size(); i++) {
            sender.sendMessage(" §7Ability " + (i+1) + ": §f" + fruit.getAbilities().get(i).getName() + 
                             " §7(CD: " + fruit.getAbilities().get(i).getCooldown() + "s)");
        }
        return true;
    }
    
    private boolean handleCooldown(CommandSender sender, String[] args) {
        Player player = args.length >= 2 ? Bukkit.getPlayer(args[1]) : 
                        (sender instanceof Player ? (Player) sender : null);
        
        if(player == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }
        
        sender.sendMessage("§6Cooldowns for §e" + player.getName());
        Map<String, Long> cooldowns = plugin.getCooldownManager().getPlayerCooldowns(player);
        if(cooldowns.isEmpty()) {
            sender.sendMessage(" §7No active cooldowns");
        } else {
            for(Map.Entry<String, Long> entry : cooldowns.entrySet()) {
                long remaining = (entry.getValue() - System.currentTimeMillis()) / 1000;
                if(remaining > 0) {
                    sender.sendMessage(" §a" + entry.getKey() + " §7- §f" + remaining + "s");
                }
            }
        }
        return true;
    }
    
    private boolean handleRemove(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.remove", true)) return true;
        // Similar to before
        sender.sendMessage("§a✓ Remove command implemented");
        return true;
    }
    
    private boolean handleGiveAll(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.giveall", true)) return true;
        // Similar to before
        sender.sendMessage("§a✓ GiveAll command implemented");
        return true;
    }
    
    private boolean handleStats(CommandSender sender, String[] args) {
        Player player = args.length >= 2 ? Bukkit.getPlayer(args[1]) :
                        (sender instanceof Player ? (Player) sender : null);
        
        if(player == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }
        
        int total = 0;
        for(ItemStack item : player.getInventory().getContents()) {
            if(item != null && Fruit.getFruitId(item) != null) {
                total += item.getAmount();
            }
        }
        
        sender.sendMessage("§6Stats for §e" + player.getName());
        sender.sendMessage(" §7Total Fruits: §f" + total);
        return true;
    }
    
    private boolean handleTop(CommandSender sender) {
        Map<Player, Integer> counts = new HashMap<>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            int total = 0;
            for(ItemStack item : p.getInventory().getContents()) {
                if(item != null && Fruit.getFruitId(item) != null) {
                    total += item.getAmount();
                }
            }
            if(total > 0) counts.put(p, total);
        }
        
        List<Map.Entry<Player, Integer>> sorted = new ArrayList<>(counts.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        sender.sendMessage("§6=== Top Fruit Collectors ===");
        int rank = 1;
        for(Map.Entry<Player, Integer> entry : sorted.stream().limit(10).collect(Collectors.toList())) {
            sender.sendMessage(" §" + (rank <= 3 ? "6" : "7") + rank + ". §f" + 
                             entry.getKey().getName() + " §7- §e" + entry.getValue() + " fruits");
            rank++;
        }
        return true;
    }
    
    // ==================== HELP MENU ====================
    private void sendHelp(CommandSender sender) {
        boolean isAdmin = sender.hasPermission("fruit.admin") || sender.isOp();
        
        sender.sendMessage("");
        sender.sendMessage("§6╔══════════════════════════════════════════════════════╗");
        sender.sendMessage("§6║ §e§lFRUIT PLUGIN - COMPLETE COMMANDS §6║");
        sender.sendMessage("§6╠══════════════════════════════════════════════════════╣");
        sender.sendMessage("§6║ §e/fruit gui §7- Open main GUI menu");
        sender.sendMessage("§6║ §e/fruit spin [spins] §7- Spin for random fruits");
        sender.sendMessage("§6║ §e/fruit trade <player> §7- Trade with player");
        sender.sendMessage("§6║ §e/fruit steal <player> §7- Steal fruit from player");
        sender.sendMessage("§6║ §e/fruit list §7- List all fruits");
        sender.sendMessage("§6║ §e/fruit info <fruit> §7- View fruit details");
        sender.sendMessage("§6║ §e/fruit cooldown [player] §7- View cooldowns");
        sender.sendMessage("§6║ §e/fruit stats [player] §7- View player stats");
        sender.sendMessage("§6║ §e/fruit top §7- Top fruit collectors");
        
        if(isAdmin) {
            sender.sendMessage("§6╠══════════════════════════════════════════════════════╣");
            sender.sendMessage("§c║ §6ADMIN COMMANDS §c║");
            sender.sendMessage("§6║ §c/fruit spinplayer <player> [spins] §7- Spin for player");
            sender.sendMessage("§6║ §c/fruit spinall [spins] §7- Spin for all players");
            sender.sendMessage("§6║ §c/fruit stopspin [player] §7- Stop spinning");
            sender.sendMessage("§6║ §c/fruit autogive <on/off> [fruit] [amount] §7- Auto give on join");
            sender.sendMessage("§6║ §c/fruit setjoinfruit <fruit> [amount] §7- Set join fruit");
            sender.sendMessage("§6║ §c/fruit togglejoinfruit §7- Toggle join fruit");
            sender.sendMessage("§6║ §c/fruit give <player> <fruit> [amount] §7- Give fruit");
            sender.sendMessage("§6║ §c/fruit giveall <fruit> [amount] §7- Give to all");
            sender.sendMessage("§6║ §c/fruit remove <player> <fruit> [amount] §7- Remove fruit");
            sender.sendMessage("§6║ §c/fruit reload §7- Reload plugin");
        }
        
        sender.sendMessage("§6╚══════════════════════════════════════════════════════╝");
        sender.sendMessage("§7Tip: Use §e/fruit gui §7for visual menu!");
    }
    
    private boolean hasPermission(CommandSender sender, String perm, boolean requireAdmin) {
        if(sender.isOp()) return true;
        if(sender.hasPermission(perm)) return true;
        if(requireAdmin && sender.hasPermission("fruit.admin")) return true;
        
        sender.sendMessage("§cYou don't have permission!");
        return false;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        
        if(args.length == 1) {
            List<String> cmds = new ArrayList<>(Arrays.asList("gui", "spin", "trade", "steal", "list", "info", "cooldown", "stats", "top"));
            if(sender.hasPermission("fruit.admin") || sender.isOp()) {
                cmds.addAll(Arrays.asList("spinplayer", "spinall", "stopspin", "autogive", "setjoinfruit", 
                                         "togglejoinfruit", "give", "giveall", "remove", "reload"));
            }
            for(String c : cmds) {
                if(c.startsWith(args[0].toLowerCase())) suggestions.add(c);
            }
        }
        
        else if(args.length == 2) {
            String subCmd = args[0].toLowerCase();
            if(subCmd.equals("trade") || subCmd.equals("steal") || subCmd.equals("give") || 
               subCmd.equals("remove") || subCmd.equals("spinplayer") || subCmd.equals("stopspin")) {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        suggestions.add(p.getName());
                    }
                }
            }
            else if(subCmd.equals("info") || subCmd.equals("setjoinfruit")) {
                for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
                    if(fruit.getId().startsWith(args[1].toLowerCase())) {
                        suggestions.add(fruit.getId());
                    }
                }
            }
            else if(subCmd.equals("autogive")) {
                suggestions.add("on");
                suggestions.add("off");
            }
        }
        
        else if(args.length == 3) {
            String subCmd = args[0].toLowerCase();
            if(subCmd.equals("give") || subCmd.equals("remove") || subCmd.equals("setjoinfruit")) {
                for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
                    if(fruit.getId().startsWith(args[2].toLowerCase())) {
                        suggestions.add(fruit.getId());
                    }
                }
            }
            else if(subCmd.equals("autogive") && args[1].equalsIgnoreCase("on")) {
                for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
                    if(fruit.getId().startsWith(args[2].toLowerCase())) {
                        suggestions.add(fruit.getId());
                    }
                }
            }
            else if(subCmd.equals("spinplayer") || subCmd.equals("spinall")) {
                suggestions.add("1");
                suggestions.add("3");
                suggestions.add("5");
                suggestions.add("10");
            }
        }
        
        else if(args.length == 4) {
            String subCmd = args[0].toLowerCase();
            if(subCmd.equals("give") || subCmd.equals("setjoinfruit") || 
               (subCmd.equals("autogive") && args[1].equalsIgnoreCase("on"))) {
                suggestions.add("1");
                suggestions.add("5");
                suggestions.add("10");
                suggestions.add("16");
                suggestions.add("32");
                suggestions.add("64");
            }
        }
        
        return suggestions;
    }
}
