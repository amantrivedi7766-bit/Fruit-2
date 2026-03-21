package com.example.fruits.models;

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;

@FunctionalInterface
public interface AbilityExecutor {
    void execute(Player player, Entity target);
}
