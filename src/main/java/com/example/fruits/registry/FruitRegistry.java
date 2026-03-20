package com.example.fruits.registry;

import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.*;

public class FruitRegistry {
    private final Map<String, Fruit> fruits = new HashMap<>();
    public FruitRegistry() {
        fruits.put("crimson_star", new Fruit("crimson_star", "§cCrimson Star", Material.APPLE,
            List.of(new Ability("Vine Grab", 20, p -> p.sendMessage("§aUsed Vine Grab!")))));
        fruits.put("moon_crescent", new Fruit("moon_crescent", "§eMoon Crescent", Material.GOLDEN_CARROT,
            List.of(new Ability("Lunar Slip", 15, p -> p.sendMessage("§aUsed Lunar Slip!")))));
        fruits.put("primordial_essence", new Fruit("primordial_essence", "§5Primordial Essence", Material.ENCHANTED_GOLDEN_APPLE,
            List.of(new Ability("One Shot", 120, p -> {
                if(p.getLevel()>=30){ p.setLevel(p.getLevel()-30); p.sendMessage("§cONE SHOT!"); }
            }))));
    }
    public Fruit getFruit(String id) { return fruits.get(id); }
    public Collection<Fruit> getAllFruits() { return fruits.values(); }
}
