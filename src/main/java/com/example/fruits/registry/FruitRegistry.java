package com.example.fruits.registry;

import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import org.bukkit.Material;
import java.util.*;

public class FruitRegistry {
    private final Map<String, Fruit> fruits = new HashMap<>();

    public FruitRegistry() {
        registerFruits();
    }

    private void registerFruits() {
        // ==================== 1. DRAGONFRUIT ====================
        List<String> dragonfruitLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Summons a dragon to fight for you",
            "§f🔧 Right + Crouch:",
            "§7  Breathes fire in a cone",
            "§7=================================",
            "§d§l✦ Legendary Fruit ✦"
        );
        fruits.put("dragonfruit", new Fruit("dragonfruit", "§c§l🐉 Dragonfruit", Material.RED_DYE, 1001, dragonfruitLore, new ArrayList<>()));
        
        // ==================== 2. STARFRUIT ====================
        List<String> starfruitLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Rains stars from the sky",
            "§f🔧 Right + Crouch:",
            "§7  Teleports you to the stars",
            "§7=================================",
            "§d§l✦ Legendary Fruit ✦"
        );
        fruits.put("starfruit", new Fruit("starfruit", "§e§l⭐ Starfruit", Material.YELLOW_DYE, 1002, starfruitLore, new ArrayList<>()));
        
        // ==================== 3. MOONBERRY ====================
        List<String> moonberryLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Grants night vision and speed",
            "§f🔧 Right + Crouch:",
            "§7  Creates a lunar shield",
            "§7=================================",
            "§d§l✦ Legendary Fruit ✦"
        );
        fruits.put("moonberry", new Fruit("moonberry", "§b§l🌙 Moonberry", Material.LIGHT_BLUE_DYE, 1003, moonberryLore, new ArrayList<>()));
        
        // ==================== 4. VOIDBERRY ====================
        List<String> voidberryLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Teleports you through the void",
            "§f🔧 Right + Crouch:",
            "§7  Pulls enemies into the void",
            "§7=================================",
            "§d§l✦ Legendary Fruit ✦"
        );
        fruits.put("voidberry", new Fruit("voidberry", "§8§l🕳️ Voidberry", Material.BLACK_DYE, 1004, voidberryLore, new ArrayList<>()));
        
        // ==================== 5. STORMBERRY ====================
        List<String> stormberryLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Strikes lightning on enemies",
            "§f🔧 Right + Crouch:",
            "§7  Creates a storm cloud",
            "§7=================================",
            "§d§l✦ Legendary Fruit ✦"
        );
        fruits.put("stormberry", new Fruit("stormberry", "§3§l⚡ Stormberry", Material.CYAN_DYE, 1005, stormberryLore, new ArrayList<>()));
        
        // ==================== 6. FROSTBERRY ====================
        List<String> frostberryLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Freezes enemies in ice",
            "§f🔧 Right + Crouch:",
            "§7  Creates an ice wall",
            "§7=================================",
            "§d§l✦ Legendary Fruit ✦"
        );
        fruits.put("frostberry", new Fruit("frostberry", "§b§l❄️ Frostberry", Material.LIGHT_BLUE_DYE, 1006, frostberryLore, new ArrayList<>()));
        
        // ==================== 7. FLAMEBERRY ====================
        List<String> flameberryLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Sets enemies on fire",
            "§f🔧 Right + Crouch:",
            "§7  Creates a fire tornado",
            "§7=================================",
            "§d§l✦ Legendary Fruit ✦"
        );
        fruits.put("flameberry", new Fruit("flameberry", "§c§l🔥 Flameberry", Material.RED_DYE, 1007, flameberryLore, new ArrayList<>()));
        
        // ==================== 8. SHADOWBERRY ====================
        List<String> shadowberryLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Turns you invisible",
            "§f🔧 Right + Crouch:",
            "§7  Summons shadow clones",
            "§7=================================",
            "§d§l✦ Legendary Fruit ✦"
        );
        fruits.put("shadowberry", new Fruit("shadowberry", "§7§l🌑 Shadowberry", Material.GRAY_DYE, 1008, shadowberryLore, new ArrayList<>()));
        
        // ==================== 9. SOULBERRY ====================
        List<String> soulberryLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Steals health from enemies",
            "§f🔧 Right + Crouch:",
            "§7  Resurrects fallen allies",
            "§7=================================",
            "§d§l✦ Legendary Fruit ✦"
        );
        fruits.put("soulberry", new Fruit("soulberry", "§5§l👻 Soulberry", Material.PURPLE_DYE, 1009, soulberryLore, new ArrayList<>()));
        
        // ==================== 10. MYSTICBERRY ====================
        List<String> mysticberryLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Random powerful effect",
            "§f🔧 Right + Crouch:",
            "§7  Reveals hidden treasures",
            "§7=================================",
            "§d§l✦ Legendary Fruit ✦"
        );
        fruits.put("mysticberry", new Fruit("mysticberry", "§5§l🔮 Mysticberry", Material.PURPLE_DYE, 1010, mysticberryLore, new ArrayList<>()));
    }

    public Fruit getFruit(String id) { 
        return fruits.get(id); 
    }
    
    public Collection<Fruit> getAllFruits() { 
        return fruits.values(); 
    }
}
