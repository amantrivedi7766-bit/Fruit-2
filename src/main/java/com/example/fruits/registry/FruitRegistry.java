package com.example.fruits.registry;

import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import com.example.fruits.abilities.NatureAbilities;
import org.bukkit.Material;
import java.util.*;

public class FruitRegistry {
    private final Map<String, Fruit> fruits = new HashMap<>();

    public FruitRegistry() {
        registerFruits();
    }

    private void registerFruits() {
        // ==================== 1. NATURE DYE ====================
        List<Ability> natureAbilities = Arrays.asList(
            new Ability("§a🌿 Vine Attach", 25, NatureAbilities::vineAttach),
            new Ability("§a🔨 Oak Hammer", 35, NatureAbilities::oakHammer)
        );
        
        List<String> natureLore = Arrays.asList(
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
            "§d§l✦ Nature Fruit ✦"
        );
        
        fruits.put("nature_dye", new Fruit("nature_dye", "§a§l🌿 Nature Dye", Material.GREEN_DYE, 1001, natureLore, natureAbilities));
        
        // Other fruits will be added later with their own abilities
        // For now, just placeholder fruits
        fruits.put("dragonfruit", new Fruit("dragonfruit", "§c§l🐉 Dragonfruit", Material.RED_DYE, 1002, new ArrayList<>(), new ArrayList<>()));
        fruits.put("starfruit", new Fruit("starfruit", "§e§l⭐ Starfruit", Material.YELLOW_DYE, 1003, new ArrayList<>(), new ArrayList<>()));
        fruits.put("moonberry", new Fruit("moonberry", "§b§l🌙 Moonberry", Material.LIGHT_BLUE_DYE, 1004, new ArrayList<>(), new ArrayList<>()));
        fruits.put("voidberry", new Fruit("voidberry", "§8§l🕳️ Voidberry", Material.BLACK_DYE, 1005, new ArrayList<>(), new ArrayList<>()));
        fruits.put("stormberry", new Fruit("stormberry", "§3§l⚡ Stormberry", Material.CYAN_DYE, 1006, new ArrayList<>(), new ArrayList<>()));
        fruits.put("frostberry", new Fruit("frostberry", "§b§l❄️ Frostberry", Material.LIGHT_BLUE_DYE, 1007, new ArrayList<>(), new ArrayList<>()));
        fruits.put("flameberry", new Fruit("flameberry", "§c§l🔥 Flameberry", Material.RED_DYE, 1008, new ArrayList<>(), new ArrayList<>()));
        fruits.put("shadowberry", new Fruit("shadowberry", "§7§l🌑 Shadowberry", Material.GRAY_DYE, 1009, new ArrayList<>(), new ArrayList<>()));
        fruits.put("soulberry", new Fruit("soulberry", "§5§l👻 Soulberry", Material.PURPLE_DYE, 1010, new ArrayList<>(), new ArrayList<>()));
        fruits.put("mysticberry", new Fruit("mysticberry", "§5§l🔮 Mysticberry", Material.PURPLE_DYE, 1011, new ArrayList<>(), new ArrayList<>()));
    }

    public Fruit getFruit(String id) { 
        return fruits.get(id); 
    }
    
    public Collection<Fruit> getAllFruits() { 
        return fruits.values(); 
    }
}
