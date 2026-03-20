package com.example.fruits.models;

import com.example.fruits.FruitsPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.List;

public class Fruit {
    private final String id;
    private final String displayName;
    private final Material material;
    private final List<Ability> abilities;

    public Fruit(String id, String displayName, Material material, List<Ability> abilities) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.abilities = abilities;
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        
        NamespacedKey key = new NamespacedKey(FruitsPlugin.getInstance(), "fruit_id");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);
        item.setItemMeta(meta);
        return item;
    }

    public static String getFruitId(ItemStack item) {
        if(item == null || !item.hasItemMeta()) return null;
        NamespacedKey key = new NamespacedKey(FruitsPlugin.getInstance(), "fruit_id");
        return item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public List<Ability> getAbilities() { return abilities; }
}
