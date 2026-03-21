package com.example.fruits.models;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class Fruit {
    private final String id;
    private final String name;
    private final Material material;
    private final int customModelData;
    private final List<String> lore;
    private final List<Ability> abilities;

    public Fruit(String id, String name, Material material, int customModelData, List<String> lore, List<Ability> abilities) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.customModelData = customModelData;
        this.lore = lore;
        this.abilities = abilities;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDisplayName() { return name; }
    public Material getMaterial() { return material; }
    public int getCustomModelData() { return customModelData; }
    public List<String> getLore() { return lore; }
    public List<Ability> getAbilities() { return abilities; }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setCustomModelData(customModelData);
            
            List<String> finalLore = new ArrayList<>();
            finalLore.addAll(lore);
            meta.setLore(finalLore);
            
            item.setItemMeta(meta);
        }
        return item;
    }

    public static String getFruitId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasCustomModelData()) return null;
        
        int cmd = meta.getCustomModelData();
        switch (cmd) {
            case 1001: return "vine_weaver";
            case 1002: return "dragonfruit";
            case 1003: return "starfruit";
            case 1004: return "moonberry";
            case 1005: return "voidberry";
            case 1006: return "stormberry";
            case 1007: return "frostberry";
            case 1008: return "flameberry";
            case 1009: return "shadowberry";
            case 1010: return "soulberry";
            case 1011: return "mysticberry";
            case 1012: return "shadowweaver";
            case 1013: return "golden_aegis";
            case 1014: return "voidweaver";
            case 1015: return "dracula_bites";
            default: return null;
        }
    }
}
