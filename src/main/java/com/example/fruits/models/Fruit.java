package com.example.fruits.models;

import com.example.fruits.FruitsPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import java.util.List;
import java.util.ArrayList;

public class Fruit {
    private final String id;
    private final String displayName;
    private final Material material;
    private final int customModelData;
    private final List<Ability> abilities;

    public Fruit(String id, String displayName, Material material, int customModelData, List<Ability> abilities) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.customModelData = customModelData;
        this.abilities = abilities;
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setCustomModelData(customModelData);
        
        List<String> lore = new ArrayList<>();
        lore.add("§7§m-------------------");
        lore.add("§6✨ Magical Fruit");
        lore.add("§7§m-------------------");
        lore.add("§eAbilities:");
        for(int i = 0; i < abilities.size(); i++) {
            Ability a = abilities.get(i);
            lore.add("§7 " + (i+1) + ". §f" + a.getName() + " §7(§b" + a.getCooldown() + "s§7)");
        }
        lore.add("§7§m-------------------");
        lore.add("§a🍎 Right-click to eat!");
        lore.add("§7Use §e/fruit withdraw §7to cancel power");
        meta.setLore(lore);
        
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
