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

    /**
     * Create ItemStack with specific amount
     */
    public ItemStack createItemStack(int amount) {
        ItemStack item = createItem();
        item.setAmount(amount);
        return item;
    }

    /**
     * Get fruit ID from ItemStack using custom model data
     */
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
            case 1016: return "cyclone_fury";
            case 1017: return "storm_monster";
            case 1018: return "tidal_weaver";
            case 1019: return "starfall";
            case 1020: return "frostbite";
            default: return null;
        }
    }

    /**
     * Get fruit name from ItemStack
     */
    public static String getFruitName(ItemStack item) {
        String id = getFruitId(item);
        if (id == null) return "Unknown Fruit";
        
        // Return formatted name based on ID
        switch (id) {
            case "vine_weaver": return "§aVine Weaver";
            case "dragonfruit": return "§cDragonfruit";
            case "starfruit": return "§eStarfruit";
            case "moonberry": return "§7Moonberry";
            case "voidberry": return "§5Voidberry";
            case "stormberry": return "§3Stormberry";
            case "frostberry": return "§bFrostberry";
            case "flameberry": return "§6Flameberry";
            case "shadowberry": return "§8Shadowberry";
            case "soulberry": return "§dSoulberry";
            case "mysticberry": return "§5Mysticberry";
            case "shadowweaver": return "§8Shadow Weaver";
            case "golden_aegis": return "§6Golden Aegis";
            case "voidweaver": return "§5Void Weaver";
            case "dracula_bites": return "§cDracula's Bites";
            case "cyclone_fury": return "§3Cyclone Fury";
            case "storm_monster": return "§bStorm Monster";
            case "tidal_weaver": return "§3Tidal Weaver";
            case "starfall": return "§eStarfall";
            case "frostbite": return "§bFrostbite";
            default: return id;
        }
    }

    /**
     * Get display name from fruit ID
     */
    public static String getDisplayNameById(String id) {
        switch (id) {
            case "vine_weaver": return "§aVine Weaver";
            case "dragonfruit": return "§cDragonfruit";
            case "starfruit": return "§eStarfruit";
            case "moonberry": return "§7Moonberry";
            case "voidberry": return "§5Voidberry";
            case "stormberry": return "§3Stormberry";
            case "frostberry": return "§bFrostberry";
            case "flameberry": return "§6Flameberry";
            case "shadowberry": return "§8Shadowberry";
            case "soulberry": return "§dSoulberry";
            case "mysticberry": return "§5Mysticberry";
            case "shadowweaver": return "§8Shadow Weaver";
            case "golden_aegis": return "§6Golden Aegis";
            case "voidweaver": return "§5Void Weaver";
            case "dracula_bites": return "§cDracula's Bites";
            case "cyclone_fury": return "§3Cyclone Fury";
            case "storm_monster": return "§bStorm Monster";
            case "tidal_weaver": return "§3Tidal Weaver";
            case "starfall": return "§eStarfall";
            case "frostbite": return "§bFrostbite";
            default: return id;
        }
    }

    /**
     * Get custom model data from fruit ID
     */
    public static int getCustomModelData(String id) {
        switch (id) {
            case "vine_weaver": return 1001;
            case "dragonfruit": return 1002;
            case "starfruit": return 1003;
            case "moonberry": return 1004;
            case "voidberry": return 1005;
            case "stormberry": return 1006;
            case "frostberry": return 1007;
            case "flameberry": return 1008;
            case "shadowberry": return 1009;
            case "soulberry": return 1010;
            case "mysticberry": return 1011;
            case "shadowweaver": return 1012;
            case "golden_aegis": return 1013;
            case "voidweaver": return 1014;
            case "dracula_bites": return 1015;
            case "cyclone_fury": return 1016;
            case "storm_monster": return 1017;
            case "tidal_weaver": return 1018;
            case "starfall": return 1019;
            case "frostbite": return 1020;
            default: return 0;
        }
    }
}
