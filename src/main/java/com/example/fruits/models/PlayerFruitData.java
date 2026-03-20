package com.example.fruits.models;

import org.bukkit.entity.Player;

public class PlayerFruitData {
    private final Player player;
    private Fruit fruit;
    private int usedAbilities;

    public PlayerFruitData(Player player, Fruit fruit) {
        this.player = player;
        this.fruit = fruit;
        this.usedAbilities = 0;
    }

    public void incrementUsed() {
        usedAbilities++;
        if(usedAbilities >= 3) {
            // Give back ONE fruit after all abilities used
            player.getInventory().addItem(fruit.createItem());
            fruit = null;
            usedAbilities = 0;
        }
    }

    public Fruit getFruit() { return fruit; }
    public int getUsedAbilities() { return usedAbilities; }
}
