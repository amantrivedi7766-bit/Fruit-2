package com.example.fruits.models;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Ability {
    private final String name;
    private final int cooldown;
    private final AbilityExecutor executor;

    public Ability(String name, int cooldown, AbilityExecutor executor) {
        this.name = name;
        this.cooldown = cooldown;
        this.executor = executor;
    }

    public String getName() { return name; }
    public int getCooldown() { return cooldown; }
    public AbilityExecutor getExecutor() { return executor; }

    @FunctionalInterface
    public interface AbilityExecutor {
        void execute(Player player, Entity target);
    }
}
