package com.example.fruits.registry;

import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import com.example.fruits.abilities.NatureAbilities;
import com.example.fruits.abilities.ThiefAbilities;
import com.example.fruits.abilities.ThroneAbilities;
import org.bukkit.Material;
import java.util.*;

public class FruitRegistry {
    private final Map<String, Fruit> fruits = new HashMap<>();

    public FruitRegistry() {
        registerFruits();
    }

    private void registerFruits() {
        // ==================== 1. VINE WEAVER (Nature) ====================
        List<Ability> vineWeaverAbilities = Arrays.asList(
            new Ability("§a🌿 Vine Attach", 25, NatureAbilities::vineAttach),
            new Ability("§a🔨 Oak Hammer", 35, NatureAbilities::oakHammer)
        );
        
        List<String> vineWeaverLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Attach to a player - move together!",
            "§7  Left click within 15s to launch them!",
            "§f🔧 Right + Crouch:",
            "§7  Summon a massive oak hammer!",
            "§7  Smashes enemies with epic force!",
            "§7=================================",
            "§a§l✦ Vine Weaver ✦"
        );
        
        fruits.put("vine_weaver", new Fruit("vine_weaver", "§a§l🌿 Vine Weaver", Material.GREEN_DYE, 1001, vineWeaverLore, vineWeaverAbilities));
        
        // ==================== 2. SHADOWWEAVER (Thief) ====================
        List<Ability> shadowweaverAbilities = Arrays.asList(
            new Ability("§8§l🌑 Shadow Steal", 120, (p, target) -> {
                ThiefAbilities.shadowSteal(p);
            })
        );
        
        List<String> shadowweaverLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MYSTICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Open the Shadow Steal GUI",
            "§7  Choose a player to steal from!",
            "§7  All nearby players freeze for 20s!",
            "§7  Stolen ability lasts 20 seconds!",
            "§f⏰ Cooldown: §e2 minutes",
            "§7=================================",
            "§8§l✦ Shadowweaver ✦"
        );
        
        fruits.put("shadowweaver", new Fruit("shadowweaver", "§8§l🌑 Shadowweaver", Material.BLACK_DYE, 1012, shadowweaverLore, shadowweaverAbilities));
        
        // ==================== 3. GOLDEN AEGIS (Throne) ====================
        List<Ability> goldenAegisAbilities = Arrays.asList(
            new Ability("§6🛡️ Royal Aegis", 20, (p, target) -> {
                ThroneAbilities.royalAegis(p);
            }),
            new Ability("§6🏰 Golden Wall", 25, (p, target) -> {
                ThroneAbilities.goldenWall(p);
            })
        );
        
        List<String> goldenAegisLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MYSTICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Summon Royal Aegis shield",
            "§7  Reflect 75% damage back to attacker",
            "§7  Take 50% less damage",
            "§7  Lasts 15 seconds",
            "§f🔧 Right + Crouch:",
            "§7  Summon Golden Wall",
            "§7  Creates a 3x3 wall of gold",
            "§7  Enemies take damage + knockback",
            "§7  Wall lasts 15 seconds",
            "§f⏰ Cooldowns: §e20s (Shield), 25s (Wall)",
            "§7=================================",
            "§6§l✦ Golden Aegis ✦"
        );
        
        fruits.put("golden_aegis", new Fruit("golden_aegis", "§6§l🛡️ Golden Aegis", Material.GOLD_INGOT, 1013, goldenAegisLore, goldenAegisAbilities));
        
        // ==================== 4. OTHER MAGICAL FRUITS (Placeholder) ====================
        List<String> defaultLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Ability coming soon!",
            "§f🔧 Right + Crouch:",
            "§7  Ability coming soon!",
            "§7=================================",
            "§d§l✦ Magical Fruit ✦"
        );
        
        fruits.put("dragonfruit", new Fruit("dragonfruit", "§c§l🐉 Dragonfruit", Material.RED_DYE, 1002, defaultLore, new ArrayList<>()));
        fruits.put("starfruit", new Fruit("starfruit", "§e§l⭐ Starfruit", Material.YELLOW_DYE, 1003, defaultLore, new ArrayList<>()));
        fruits.put("moonberry", new Fruit("moonberry", "§b§l🌙 Moonberry", Material.LIGHT_BLUE_DYE, 1004, defaultLore, new ArrayList<>()));
        fruits.put("voidberry", new Fruit("voidberry", "§8§l🕳️ Voidberry", Material.BLACK_DYE, 1005, defaultLore, new ArrayList<>()));
        fruits.put("stormberry", new Fruit("stormberry", "§3§l⚡ Stormberry", Material.CYAN_DYE, 1006, defaultLore, new ArrayList<>()));
        fruits.put("frostberry", new Fruit("frostberry", "§b§l❄️ Frostberry", Material.LIGHT_BLUE_DYE, 1007, defaultLore, new ArrayList<>()));
        fruits.put("flameberry", new Fruit("flameberry", "§c§l🔥 Flameberry", Material.RED_DYE, 1008, defaultLore, new ArrayList<>()));
        fruits.put("shadowberry", new Fruit("shadowberry", "§7§l🌑 Shadowberry", Material.GRAY_DYE, 1009, defaultLore, new ArrayList<>()));
        fruits.put("soulberry", new Fruit("soulberry", "§5§l👻 Soulberry", Material.PURPLE_DYE, 1010, defaultLore, new ArrayList<>()));
        fruits.put("mysticberry", new Fruit("mysticberry", "§5§l🔮 Mysticberry", Material.PURPLE_DYE, 1011, defaultLore, new ArrayList<>()));
    }

    public Fruit getFruit(String id) { 
        return fruits.get(id); 
    }
    
    public Collection<Fruit> getAllFruits() { 
        return fruits.values(); 
    }
}
