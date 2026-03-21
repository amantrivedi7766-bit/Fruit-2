package com.example.fruits.commands;

import com.example.fruits.FruitsPlugin;
import com.example.fruits.models.Fruit;
import com.example.fruits.models.Ability;
import com.example.fruits.gui.FruitGUI;
import com.example.fruits.gui.AdminMenu;
import com.example.fruits.utils.CinematicSpinWheel;
import com.example.fruits.utils.SpinWheel;
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
    
    public FruitCommand(FruitsPlugin plugin) {
        this.plugin = plugin;
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
        
        // ========== PLAYER COMMANDS ==========
        if(subCmd.equals("gui")) {
            return handleGUI(sender);
        }
        else if(subCmd.equals("spin")) {
            return handleSpin(sender, args);
        }
        else if(subCmd.equals("trade")) {
            return handleTrade(sender, args);
        }
        else if(subCmd.equals("steal")) {
            return handleSteal(sender, args);
        }
        else if(subCmd.equals("list")) {
            return handleList(sender);
        }
        else if(subCmd.equals("info")) {
            return handleInfo(sender, args);
        }
        else if(subCmd.equals("cooldown")) {
            return handleCooldown(sender, args);
        }
        else if(subCmd.equals("stats")) {
            return handleStats(sender, args);
        }
        else if(subCmd.equals("top")) {
            return handleTop(sender);
        }
        
        // ========== ADMIN COMMANDS ==========
        else if(subCmd.equals("admin")) {
            return handleAdmin(sender);
        }
        else if(subCmd.equals("give")) {
            return handleGive(sender, args);
        }
        else if(subCmd.equals("remove")) {
            return handleRemove(sender, args);
        }
        else if(subCmd.equals("giveall")) {
            return handleGiveAll(sender, args);
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
        else if(subCmd.equals("autogive")) {
            return handleAutoGive(sender, args);
        }
        else if(subCmd.equals("setjoinfruit")) {
            return handleSetJoinFruit(sender, args);
        }
        else if(subCmd.equals("togglejoinfruit")) {
            return handleToggleJoinFruit(sender);
        }
        else if(subCmd.equals("reload")) {
            return handleReload(sender);
        }
        
        else {
            sender.sendMessage("§cUnknown subcommand! Use /fruit help");
            sendHelp(sender);
        }
        
        return true;
    }
    
    // ==================== PLAYER COMMAND HANDLERS ====================
    
    private boolean handleGUI(CommandSender sender) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use GUI!");
            return true;
        }
        Player player = (Player) sender;
        new FruitGUI(plugin, player).open();
        return true;
    }
    
    private boolean handleSpin(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can spin!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check permission
        if(!player.hasPermission("fruit.spin") && !player.isOp()) {
            player.sendMessage("§cYou don't have permission to spin!");
            return true;
        }
        
        // Check cooldown
        if(plugin.getCooldownManager().hasCooldown(player, "spin")) {
            long remaining = plugin.getCooldownManager().getRemaining(player, "spin");
            player.sendMessage("§cSpin on cooldown! §7" + remaining + " seconds remaining");
            return true;
        }
        
        // Get spin count
        int spins = 1;
        if(args.length >= 2) {
            try {
                spins = Integer.parseInt(args[1]);
                if(spins < 1) spins = 1;
                if(spins > 5) spins = 5;
            } catch(NumberFormatException e) {
                player.sendMessage("§cInvalid number! Using 1 spin.");
            }
        }
        
        // Start cinematic spin
        player.sendMessage("§a✨ Starting cinematic spin! Get ready! ✨");
        CinematicSpinWheel spinWheel = new CinematicSpinWheel(plugin, player);
        spinWheel.startSpin();
        
        // Set cooldown (60 seconds)
        plugin.getCooldownManager().setCooldown(player, "spin", 60, "Spin");
        
        return true;
    }
    
    private boolean handleTrade(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can trade!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(args.length < 2) {
            player.sendMessage("§cUsage: /fruit trade <player>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if(target == null) {
            player.sendMessage("§cPlayer not found!");
            return true;
        }
        
        if(target == player) {
            player.sendMessage("§cYou cannot trade with yourself!");
            return true;
        }
        
        // Open trade GUI
        player.sendMessage("§aOpening trade with §e" + target.getName() + "§a...");
        target.sendMessage("§e" + player.getName() + " §awants to trade with you!");
        
        // TODO: Implement trade GUI
        player.sendMessage("§cTrade system coming soon!");
        
        return true;
    }
    
    private boolean handleSteal(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can steal!");
            return true;
        }
        
        Player thief = (Player) sender;
        
        if(args.length < 2) {
            thief.sendMessage("§cUsage: /fruit steal <player>");
            return true;
        }
        
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
        
        // Find fruits to steal
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
        Random random = new Random();
        ItemStack stolen = fruits.get(random.nextInt(fruits.size()));
        int amount = Math.min(stolen.getAmount(), random.nextInt(3) + 1);
        
        stolen.setAmount(stolen.getAmount() - amount);
        ItemStack stolenStack = stolen.clone();
        stolenStack.setAmount(amount);
        
        thief.getInventory().addItem(stolenStack);
        
        // Set cooldown
        plugin.getCooldownManager().setCooldown(thief, "steal", 60, "Steal");
        
        // Effects
        thief.getWorld().playSound(thief.getLocation(), org.bukkit.Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1.0f, 1.5f);
        victim.getWorld().playSound(victim.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_HURT, 1.0f, 0.5f);
        
        String fruitName = Fruit.getFruitName(stolenStack);
        thief.sendMessage("§a✓ Stole §6" + amount + "x " + fruitName + "§a from §e" + victim.getName());
        victim.sendMessage("§c✗ " + thief.getName() + " stole §6" + amount + "x " + fruitName + "§c from you!");
        
        return true;
    }
    
    private boolean handleList(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§6╔══════════════════════════════════╗");
        sender.sendMessage("§6║ §e§l🍎 AVAILABLE FRUITS §6║");
        sender.sendMessage("§6╚══════════════════════════════════╝");
        
        for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
            int cd1 = fruit.getAbilities().get(0).getCooldown();
            int cd2 = fruit.getAbilities().size() > 1 ? fruit.getAbilities().get(1).getCooldown() : 0;
            sender.sendMessage(" §a" + fruit.getName() + " §7(" + fruit.getId() + ")");
            sender.sendMessage("   §8└─ §7CD: §f" + cd1 + "s§7/§f" + cd2 + "s");
        }
        
        sender.sendMessage("§7Use §e/fruit info <fruit> §7for details");
        return true;
    }
    
    private boolean handleInfo(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage("§cUsage: /fruit info <fruit_id>");
            return true;
        }
        
        String fruitId = args[1].toLowerCase();
        Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
        
        if(fruit == null) {
            sender.sendMessage("§cFruit not found! Use /fruit list to see all fruits.");
            return true;
        }
        
        sender.sendMessage("");
        sender.sendMessage("§6╔══════════════════════════════════╗");
        sender.sendMessage("§6║ §e" + fruit.getName() + " §6║");
        sender.sendMessage("§6╚══════════════════════════════════╝");
        sender.sendMessage(" §7ID: §f" + fruit.getId());
        sender.sendMessage(" §7Material: §f" + fruit.getMaterial().name());
        sender.sendMessage(" §7Abilities:");
        
        List<Ability> abilities = fruit.getAbilities();
        for(int i = 0; i < abilities.size(); i++) {
            Ability ab = abilities.get(i);
            sender.sendMessage("   §" + (i == 0 ? "a" : "b") + "Ability " + (i+1) + ": §f" + ab.getName());
            sender.sendMessage("      §8└─ §7Cooldown: §f" + ab.getCooldown() + "s");
        }
        
        return true;
    }
    
    private boolean handleCooldown(CommandSender sender, String[] args) {
        Player player;
        
        if(args.length >= 2) {
            if(!hasPermission(sender, "fruit.cooldown.others", true)) return true;
            player = Bukkit.getPlayer(args[1]);
            if(player == null) {
                sender.sendMessage("§cPlayer not found!");
                return true;
            }
        } else {
            if(!(sender instanceof Player)) {
                sender.sendMessage("§cConsole must specify a player!");
                return true;
            }
            player = (Player) sender;
        }
        
        sender.sendMessage("");
        sender.sendMessage("§6╔══════════════════════════════════╗");
        sender.sendMessage("§6║ §eCooldowns for §f" + player.getName() + " §6║");
        sender.sendMessage("§6╚══════════════════════════════════╝");
        
        Map<String, Long> cooldowns = plugin.getCooldownManager().getPlayerCooldowns(player);
        
        if(cooldowns.isEmpty()) {
            sender.sendMessage(" §7No active cooldowns!");
        } else {
            for(Map.Entry<String, Long> entry : cooldowns.entrySet()) {
                long remaining = (entry.getValue() - System.currentTimeMillis()) / 1000;
                if(remaining > 0) {
                    sender.sendMessage(" §a" + entry.getKey() + " §7- §f" + remaining + "s remaining");
                }
            }
        }
        
        return true;
    }
    
    private boolean handleStats(CommandSender sender, String[] args) {
        Player player;
        
        if(args.length >= 2) {
            player = Bukkit.getPlayer(args[1]);
            if(player == null) {
                sender.sendMessage("§cPlayer not found!");
                return true;
            }
        } else {
            if(!(sender instanceof Player)) {
                sender.sendMessage("§cConsole must specify a player!");
                return true;
            }
            player = (Player) sender;
        }
        
        int totalFruits = 0;
        Map<String, Integer> fruitCounts = new HashMap<>();
        
        for(ItemStack item : player.getInventory().getContents()) {
            if(item != null) {
                String fruitId = Fruit.getFruitId(item);
                if(fruitId != null) {
                    totalFruits += item.getAmount();
                    fruitCounts.put(fruitId, fruitCounts.getOrDefault(fruitId, 0) + item.getAmount());
                }
            }
        }
        
        sender.sendMessage("");
        sender.sendMessage("§6╔══════════════════════════════════╗");
        sender.sendMessage("§6║ §eStats for §f" + player.getName() + " §6║");
        sender.sendMessage("§6╚══════════════════════════════════╝");
        sender.sendMessage(" §7Total Fruits: §f" + totalFruits);
        
        if(!fruitCounts.isEmpty()) {
            sender.sendMessage(" §7Breakdown:");
            for(Map.Entry<String, Integer> entry : fruitCounts.entrySet()) {
                Fruit fruit = plugin.getFruitRegistry().getFruit(entry.getKey());
                sender.sendMessage("   §a" + (fruit != null ? fruit.getName() : entry.getKey()) + " §7x§f " + entry.getValue());
            }
        }
        
        return true;
    }
    
    private boolean handleTop(CommandSender sender) {
        Map<Player, Integer> fruitCounts = new HashMap<>();
        
        for(Player player : Bukkit.getOnlinePlayers()) {
            int count = 0;
            for(ItemStack item : player.getInventory().getContents()) {
                if(item != null && Fruit.getFruitId(item) != null) {
                    count += item.getAmount();
                }
            }
            if(count > 0) {
                fruitCounts.put(player, count);
            }
        }
        
        List<Map.Entry<Player, Integer>> sorted = new ArrayList<>(fruitCounts.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        sender.sendMessage("");
        sender.sendMessage("§6╔══════════════════════════════════╗");
        sender.sendMessage("§6║ §eTop Fruit Collectors §6║");
        sender.sendMessage("§6╚══════════════════════════════════╝");
        
        int rank = 1;
        for(Map.Entry<Player, Integer> entry : sorted.stream().limit(10).collect(Collectors.toList())) {
            String rankColor = rank <= 3 ? "6" : "7";
            sender.sendMessage(" §" + rankColor + rank + ". §f" + entry.getKey().getName() + " §7- §e" + entry.getValue() + " fruits");
            rank++;
        }
        
        if(sorted.isEmpty()) {
            sender.sendMessage(" §7No players have fruits yet!");
        }
        
        return true;
    }
    
    // ==================== ADMIN COMMAND HANDLERS ====================
    
    private boolean handleAdmin(CommandSender sender) {
        if(!hasPermission(sender, "fruit.admin", true)) return true;
        
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can open admin GUI!");
            return true;
        }
        
        Player admin = (Player) sender;
        new AdminMenu(plugin).open(admin);
        return true;
    }
    
    private boolean handleGive(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.give", true)) return true;
        
        if(args.length < 3) {
            sender.sendMessage("§cUsage: /fruit give <player> <fruit_id> [amount]");
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
            sender.sendMessage("§cInvalid fruit! Use /fruit list to see all fruits.");
            return true;
        }
        
        int amount = 1;
        if(args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
                if(amount < 1) amount = 1;
                if(amount > 64) amount = 64;
            } catch(NumberFormatException e) {
                sender.sendMessage("§cInvalid amount! Using 1.");
            }
        }
        
        ItemStack fruitItem = fruit.createItemStack(amount);
        target.getInventory().addItem(fruitItem);
        
        sender.sendMessage("§a✓ Gave §6" + amount + "x " + fruit.getName() + "§a to §e" + target.getName());
        target.sendMessage("§a✓ You received §6" + amount + "x " + fruit.getName() + "§a!");
        
        return true;
    }
    
    private boolean handleRemove(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.remove", true)) return true;
        
        if(args.length < 3) {
            sender.sendMessage("§cUsage: /fruit remove <player> <fruit_id> [amount]");
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
        
        int amount = 1;
        if(args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
            } catch(NumberFormatException e) {
                sender.sendMessage("§cInvalid amount! Removing 1.");
            }
        }
        
        int removed = 0;
        ItemStack[] contents = target.getInventory().getContents();
        
        for(int i = 0; i < contents.length; i++) {
            if(contents[i] != null) {
                String id = Fruit.getFruitId(contents[i]);
                if(id != null && id.equals(fruitId)) {
                    int toRemove = Math.min(amount - removed, contents[i].getAmount());
                    contents[i].setAmount(contents[i].getAmount() - toRemove);
                    removed += toRemove;
                    if(removed >= amount) break;
                }
            }
        }
        
        target.getInventory().setContents(contents);
        
        sender.sendMessage("§a✓ Removed §6" + removed + "x " + fruit.getName() + "§a from §e" + target.getName());
        if(removed > 0) {
            target.sendMessage("§c✗ " + removed + "x " + fruit.getName() + " removed from your inventory by admin!");
        }
        
        return true;
    }
    
    private boolean handleGiveAll(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.giveall", true)) return true;
        
        if(args.length < 2) {
            sender.sendMessage("§cUsage: /fruit giveall <fruit_id> [amount]");
            return true;
        }
        
        String fruitId = args[1].toLowerCase();
        Fruit fruit = plugin.getFruitRegistry().getFruit(fruitId);
        
        if(fruit == null) {
            sender.sendMessage("§cInvalid fruit!");
            return true;
        }
        
        int amount = 1;
        if(args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
            } catch(NumberFormatException e) {
                sender.sendMessage("§cInvalid amount! Using 1.");
            }
        }
        
        int given = 0;
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(fruit.createItemStack(amount));
            player.sendMessage("§a✓ You received §6" + amount + "x " + fruit.getName() + "§a from admin!");
            given++;
        }
        
        sender.sendMessage("§a✓ Gave §6" + amount + "x " + fruit.getName() + "§a to §e" + given + " players!");
        
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
        
        int spins = 1;
        if(args.length >= 3) {
            try {
                spins = Integer.parseInt(args[2]);
                if(spins < 1) spins = 1;
                if(spins > 10) spins = 10;
            } catch(NumberFormatException e) {
                sender.sendMessage("§cInvalid number! Using 1 spin.");
            }
        }
        
        target.sendMessage("§a✨ Admin started a spin for you! Get ready! ✨");
        CinematicSpinWheel spinWheel = new CinematicSpinWheel(plugin, target);
        spinWheel.startSpin();
        
        sender.sendMessage("§a✓ Started spin for §e" + target.getName() + "§a with §6" + spins + "§a spins!");
        
        return true;
    }
    
    private boolean handleSpinAll(CommandSender sender, String[] args) {
        if(!hasPermission(sender, "fruit.spin.all", true)) return true;
        
        int spins = 1;
        if(args.length >= 2) {
            try {
                spins = Integer.parseInt(args[1]);
            } catch(NumberFormatException e) {
                sender.sendMessage("§cInvalid number! Using 1 spin.");
            }
        }
        
        int count = 0;
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage("§a✨ Admin started a spin for everyone! Get ready! ✨");
            CinematicSpinWheel spinWheel = new CinematicSpinWheel(plugin, player);
            spinWheel.startSpin();
            count++;
        }
        
        sender.sendMessage("§a✓ Started spin for §e" + count + "§a players!");
        
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
            SpinWheel.stopSpin(target);
            sender.sendMessage("§a✓ Stopped spin for §e" + target.getName());
        } else {
            SpinWheel.stopAllSpins();
            sender.sendMessage("§a✓ Stopped all active spins!");
        }
        
        return true;
    }
    
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
        
        String fruitId = args[1].toLowerCase();
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
    
    private boolean handleReload(CommandSender sender) {
        if(!hasPermission(sender, "fruit.reload", true)) return true;
        
        plugin.reloadConfig();
        sender.sendMessage("§a✓ Plugin reloaded successfully!");
        
        return true;
    }
    
    // ==================== HELP MENU ====================
    
    private void sendHelp(CommandSender sender) {
        boolean isAdmin = sender.hasPermission("fruit.admin") || sender.isOp();
        
        sender.sendMessage("");
        sender.sendMessage("§6╔══════════════════════════════════════════════════════╗");
        sender.sendMessage("§6║ §e§l🍎 FRUIT PLUGIN - COMPLETE COMMANDS §6║");
        sender.sendMessage("§6╠══════════════════════════════════════════════════════╣");
        sender.sendMessage("§6║ §e/fruit gui §7- Open main GUI menu");
        sender.sendMessage("§6║ §e/fruit spin [spins] §7- Spin for random fruits (60s CD)");
        sender.sendMessage("§6║ §e/fruit trade <player> §7- Trade with another player");
        sender.sendMessage("§6║ §e/fruit steal <player> §7- Steal a fruit from a player (60s CD)");
        sender.sendMessage("§6║ §e/fruit list §7- List all available fruits");
        sender.sendMessage("§6║ §e/fruit info <fruit> §7- View detailed fruit information");
        sender.sendMessage("§6║ §e/fruit cooldown [player] §7- View active cooldowns");
        sender.sendMessage("§6║ §e/fruit stats [player] §7- View fruit statistics");
        sender.sendMessage("§6║ §e/fruit top §7- View top fruit collectors");
        
        if(isAdmin) {
            sender.sendMessage("§6╠══════════════════════════════════════════════════════╣");
            sender.sendMessage("§c║ §6ADMIN COMMANDS §c║");
            sender.sendMessage("§6║ §c/fruit admin §7- Open admin control panel GUI");
            sender.sendMessage("§6║ §c/fruit give <player> <fruit> [amount] §7- Give fruit to player");
            sender.sendMessage("§6║ §c/fruit remove <player> <fruit> [amount] §7- Remove fruit from player");
            sender.sendMessage("§6║ §c/fruit giveall <fruit> [amount] §7- Give fruit to all online players");
            sender.sendMessage("§6║ §c/fruit spinplayer <player> [spins] §7- Start spin for a player");
            sender.sendMessage("§6║ §c/fruit spinall [spins] §7- Start spin for all online players");
            sender.sendMessage("§6║ §c/fruit stopspin [player] §7- Stop active spins");
            sender.sendMessage("§6║ §c/fruit autogive <on/off> [fruit] [amount] §7- Configure auto-give on join");
            sender.sendMessage("§6║ §c/fruit setjoinfruit <fruit> [amount] §7- Set first-join gift fruit");
            sender.sendMessage("§6║ §c/fruit togglejoinfruit §7- Toggle first-join gift on/off");
            sender.sendMessage("§6║ §c/fruit reload §7- Reload plugin configuration");
        }
        
        sender.sendMessage("§6╚══════════════════════════════════════════════════════╝");
        sender.sendMessage("§7Tip: Use §e/fruit gui §7for the visual menu!");
    }
    
    // ==================== PERMISSION CHECKER ====================
    
    private boolean hasPermission(CommandSender sender, String perm, boolean requireAdmin) {
        if(sender.isOp()) return true;
        if(sender.hasPermission(perm)) return true;
        if(requireAdmin && sender.hasPermission("fruit.admin")) return true;
        
        sender.sendMessage("§cYou don't have permission: " + perm);
        return false;
    }
    
    // ==================== TAB COMPLETER ====================
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        
        if(args.length == 1) {
            List<String> cmds = new ArrayList<>(Arrays.asList(
                "gui", "spin", "trade", "steal", "list", "info", "cooldown", "stats", "top"
            ));
            
            if(sender.hasPermission("fruit.admin") || sender.isOp()) {
                cmds.addAll(Arrays.asList(
                    "admin", "give", "remove", "giveall", "spinplayer", "spinall", 
                    "stopspin", "autogive", "setjoinfruit", "togglejoinfruit", "reload"
                ));
            }
            
            for(String c : cmds) {
                if(c.startsWith(args[0].toLowerCase())) {
                    suggestions.add(c);
                }
            }
        }
        
        else if(args.length == 2) {
            String subCmd = args[0].toLowerCase();
            
            if(subCmd.equals("give") || subCmd.equals("remove") || subCmd.equals("trade") || 
               subCmd.equals("steal") || subCmd.equals("stats") || subCmd.equals("cooldown") || 
               subCmd.equals("spinplayer") || subCmd.equals("stopspin")) {
                // Player names
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        suggestions.add(p.getName());
                    }
                }
            }
            else if(subCmd.equals("info") || subCmd.equals("giveall") || subCmd.equals("setjoinfruit")) {
                // Fruit IDs
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
            else if(subCmd.equals("spin") || subCmd.equals("spinall") || subCmd.equals("spinplayer")) {
                suggestions.add("1");
                suggestions.add("3");
                suggestions.add("5");
            }
        }
        
        else if(args.length == 3) {
            String subCmd = args[0].toLowerCase();
            
            if(subCmd.equals("give") || subCmd.equals("remove") || subCmd.equals("setjoinfruit")) {
                // Fruit IDs
                for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
                    if(fruit.getId().startsWith(args[2].toLowerCase())) {
                        suggestions.add(fruit.getId());
                    }
                }
            }
            else if(subCmd.equals("autogive") && args[1].equalsIgnoreCase("on")) {
                // Fruit IDs for auto give
                for(Fruit fruit : plugin.getFruitRegistry().getAllFruits()) {
                    if(fruit.getId().startsWith(args[2].toLowerCase())) {
                        suggestions.add(fruit.getId());
                    }
                }
            }
            else if(subCmd.equals("give") || subCmd.equals("remove") || subCmd.equals("giveall")) {
                suggestions.add("1");
                suggestions.add("5");
                suggestions.add("10");
                suggestions.add("16");
                suggestions.add("32");
                suggestions.add("64");
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
