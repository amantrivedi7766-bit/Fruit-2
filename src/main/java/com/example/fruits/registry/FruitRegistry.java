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
        fruits.put("dragonfruit", new Fruit("dragonfruit", "§c§l🐉 Dragonfruit", Material.RED_DYE, 1001, new ArrayList<>()));
        
        // ==================== 2. STARFRUIT ====================
        fruits.put("starfruit", new Fruit("starfruit", "§e§l⭐ Starfruit", Material.YELLOW_DYE, 1002, new ArrayList<>()));
        
        // ==================== 3. MOONBERRY ====================
        fruits.put("moonberry", new Fruit("moonberry", "§b§l🌙 Moonberry", Material.LIGHT_BLUE_DYE, 1003, new ArrayList<>()));
        
        // ==================== 4. VOIDBERRY ====================
        fruits.put("voidberry", new Fruit("voidberry", "§8§l🕳️ Voidberry", Material.BLACK_DYE, 1004, new ArrayList<>()));
        
        // ==================== 5. STORMBERRY ====================
        fruits.put("stormberry", new Fruit("stormberry", "§3§l⚡ Stormberry", Material.CYAN_DYE, 1005, new ArrayList<>()));
        
        // ==================== 6. FROSTBERRY ====================
        fruits.put("frostberry", new Fruit("frostberry", "§b§l❄️ Frostberry", Material.LIGHT_BLUE_DYE, 1006, new ArrayList<>()));
        
        // ==================== 7. FLAMEBERRY ====================
        fruits.put("flameberry", new Fruit("flameberry", "§c§l🔥 Flameberry", Material.RED_DYE, 1007, new ArrayList<>()));
        
        // ==================== 8. SHADOWBERRY ====================
        fruits.put("shadowberry", new Fruit("shadowberry", "§7§l🌑 Shadowberry", Material.GRAY_DYE, 1008, new ArrayList<>()));
        
        // ==================== 9. SOULBERRY ====================
        fruits.put("soulberry", new Fruit("soulberry", "§5§l👻 Soulberry", Material.PURPLE_DYE, 1009, new ArrayList<>()));
        
        // ==================== 10. MYSTICBERRY ====================
        fruits.put("mysticberry", new Fruit("mysticberry", "§5§l🔮 Mysticberry", Material.PURPLE_DYE, 1010, new ArrayList<>()));
    }

    public Fruit getFruit(String id) { 
        return fruits.get(id); 
    }
    
    public Collection<Fruit> getAllFruits() { 
        return fruits.values(); 
    }
}
