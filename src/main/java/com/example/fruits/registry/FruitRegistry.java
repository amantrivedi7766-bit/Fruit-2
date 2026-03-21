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
        // ==================== 1. NATURE DYE ====================
        fruits.put("nature_dye", new Fruit("nature_dye", "§a§l🌿 Nature Dye", Material.GREEN_DYE, 1001, new ArrayList<>()));
        
        // ==================== 2. WATER DYE ====================
        fruits.put("water_dye", new Fruit("water_dye", "§b§l💧 Water Dye", Material.LIGHT_BLUE_DYE, 1002, new ArrayList<>()));
        
        // ==================== 3. CYCLONE DYE ====================
        fruits.put("cyclone_dye", new Fruit("cyclone_dye", "§3§l🌀 Cyclone Dye", Material.CYAN_DYE, 1003, new ArrayList<>()));
        
        // ==================== 4. DRACULA DYE ====================
        fruits.put("dracula_dye", new Fruit("dracula_dye", "§c§l🦇 Dracula Dye", Material.RED_DYE, 1004, new ArrayList<>()));
        
        // ==================== 5. PORTAL DYE ====================
        fruits.put("portal_dye", new Fruit("portal_dye", "§5§l🌀 Portal Dye", Material.PURPLE_DYE, 1005, new ArrayList<>()));
        
        // ==================== 6. THRONE DYE ====================
        fruits.put("throne_dye", new Fruit("throne_dye", "§6§l👑 Throne Dye", Material.YELLOW_DYE, 1006, new ArrayList<>()));
        
        // ==================== 7. THIEF DYE ====================
        fruits.put("thief_dye", new Fruit("thief_dye", "§8§l🗡️ Thief Dye", Material.BLACK_DYE, 1007, new ArrayList<>()));
        
        // ==================== 8. STAR DYE ====================
        fruits.put("star_dye", new Fruit("star_dye", "§e§l⭐ Star Dye", Material.ORANGE_DYE, 1008, new ArrayList<>()));
        
        // ==================== 9. SHADOW DYE ====================
        fruits.put("shadow_dye", new Fruit("shadow_dye", "§7§l🌑 Shadow Dye", Material.GRAY_DYE, 1009, new ArrayList<>()));
        
        // ==================== 10. PRIMORDIAL DYE ====================
        fruits.put("primordial_dye", new Fruit("primordial_dye", "§5§l✨ Primordial Dye", Material.MAGENTA_DYE, 1010, new ArrayList<>()));
    }

    public Fruit getFruit(String id) { 
        return fruits.get(id); 
    }
    
    public Collection<Fruit> getAllFruits() { 
        return fruits.values(); 
    }
}
