package com.example.fruits.registry;

import com.example.fruits.models.Ability;
import com.example.fruits.models.Fruit;
import com.example.fruits.abilities.NatureAbilities;
import com.example.fruits.abilities.ThiefAbilities;
import com.example.fruits.abilities.ThroneAbilities;
import com.example.fruits.abilities.PortalAbilities;
import com.example.fruits.abilities.VampireAbilities;
import com.example.fruits.abilities.CycloneAbilities;
import com.example.fruits.abilities.StormAbilities;
import com.example.fruits.abilities.WaterAbilities;
import org.bukkit.Material;
import java.util.*;

public class FruitRegistry {
    private final Map<String, Fruit> fruits = new HashMap<>();

    public FruitRegistry() {
        registerFruits();
    }

    private void registerFruits() {
        // ==================== 1. VINE WEAVER (Nature) ====================
        List<Ability> vineWeaverAbilities = Arrays.asList(
            new Ability("§a🌿 Vine Attach", 25, NatureAbilities::vineAttach),
            new Ability("§a🔨 Oak Hammer", 35, NatureAbilities::oakHammer)
        );
        
        List<String> vineWeaverLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Attach to a player - move together!",
            "§7  Left click within 15s to launch them!",
            "§f🔧 Right + Crouch:",
            "§7  Summon a massive oak hammer!",
            "§7  Smashes enemies with epic force!",
            "§7=================================",
            "§a§l✦ Vine Weaver ✦"
        );
        
        fruits.put("vine_weaver", new Fruit("vine_weaver", "§a§l🌿 Vine Weaver", Material.GREEN_DYE, 1001, vineWeaverLore, vineWeaverAbilities));
        
        // ==================== 2. SHADOWWEAVER (Thief) ====================
        List<Ability> shadowweaverAbilities = Arrays.asList(
            new Ability("§8§l🌑 Shadow Steal", 120, (p, target) -> {
                ThiefAbilities.shadowSteal(p);
            })
        );
        
        List<String> shadowweaverLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MYSTICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Open the Shadow Steal GUI",
            "§7  Choose a player to steal from!",
            "§7  All nearby players freeze for 20s!",
            "§7  Stolen ability lasts 20 seconds!",
            "§f⏰ Cooldown: §e2 minutes",
            "§7=================================",
            "§8§l✦ Shadowweaver ✦"
        );
        
        fruits.put("shadowweaver", new Fruit("shadowweaver", "§8§l🌑 Shadowweaver", Material.BLACK_DYE, 1012, shadowweaverLore, shadowweaverAbilities));
        
        // ==================== 3. GOLDEN AEGIS (Throne) ====================
        List<Ability> goldenAegisAbilities = Arrays.asList(
            new Ability("§6🛡️ Royal Aegis", 20, (p, target) -> {
                ThroneAbilities.royalAegis(p);
            }),
            new Ability("§6🏰 Golden Wall", 25, (p, target) -> {
                ThroneAbilities.goldenWall(p);
            })
        );
        
        List<String> goldenAegisLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MYSTICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Summon Royal Aegis shield",
            "§7  Reflect 75% damage back to attacker",
            "§7  Take 50% less damage",
            "§7  Lasts 15 seconds",
            "§f🔧 Right + Crouch:",
            "§7  Summon Golden Wall",
            "§7  Creates a 3x3 wall of gold",
            "§7  Enemies take damage + knockback",
            "§7  Wall lasts 15 seconds",
            "§f⏰ Cooldowns: §e20s (Shield), 25s (Wall)",
            "§7=================================",
            "§6§l✦ Golden Aegis ✦"
        );
        
        fruits.put("golden_aegis", new Fruit("golden_aegis", "§6§l🛡️ Golden Aegis", Material.GOLD_INGOT, 1013, goldenAegisLore, goldenAegisAbilities));
        
        // ==================== 4. VOIDWEAVER (Portal) ====================
        List<Ability> voidweaverAbilities = Arrays.asList(
            new Ability("§5🌀 Portal Link", 120, (p, target) -> {
                PortalAbilities.portalLink(p);
            }),
            new Ability("§5🌌 Portal Summon", 1200, (p, target) -> {
                PortalAbilities.portalSummon(p);
            })
        );
        
        List<String> voidweaverLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MYSTICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Place first portal (20s to place second)",
            "§7  Second click links portals together",
            "§7  Step through to teleport between them!",
            "§7  Portals last 60 seconds",
            "§f🔧 Right + Crouch:",
            "§7  Create a summon portal at cursor",
            "§7  Left-click to summon any online player!",
            "§7  Summoned player teleports to you",
            "§f⏰ Cooldowns: §e2 min (Link), 20 min (Summon)",
            "§7=================================",
            "§5§l✦ Voidweaver ✦"
        );
        
        fruits.put("voidweaver", new Fruit("voidweaver", "§5§l🌀 Voidweaver", Material.PURPLE_DYE, 1014, voidweaverLore, voidweaverAbilities));
        
        // ==================== 5. DRACULA BITES (Vampire) ====================
        List<Ability> draculaAbilities = Arrays.asList(
            new Ability("§c🩸 Bloodlust Phase", 30, (p, target) -> {
                VampireAbilities.bloodlustPhase(p);
            }),
            new Ability("§c🦇 Bat Ride", 45, (p, target) -> {
                VampireAbilities.batRide(p);
            })
        );
        
        List<String> draculaLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MYSTICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Enter Bloodlust Phase (15 seconds)",
            "§7  Every 3 hits heals 1 heart!",
            "§7  Blood particles surround you",
            "§f🔧 Right + Crouch:",
            "§7  Summon a rideable bat (20 seconds)",
            "§7  Control with W/A/S/D and mouse",
            "§7  Left-click for Blood Bite attack!",
            "§7  Bite deals 1 heart damage + heals you",
            "§7  Blood Bite has 3 second cooldown",
            "§f⏰ Cooldowns: §e30s (Phase), 45s (Ride)",
            "§7=================================",
            "§c§l✦ Dracula Bites ✦"
        );
        
        fruits.put("dracula_bites", new Fruit("dracula_bites", "§c§l🦇 Dracula Bites", Material.RED_DYE, 1015, draculaLore, draculaAbilities));
        
        // ==================== 6. CYCLONE FURY (Storm) ====================
        List<Ability> cycloneAbilities = Arrays.asList(
            new Ability("§3🌀 Speed Tornado", 30, (p, target) -> {
                CycloneAbilities.speedTornado(p);
            }),
            new Ability("§3🌪️ Block Tornado", 40, (p, target) -> {
                CycloneAbilities.blockTornado(p);
            })
        );
        
        List<String> cycloneLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MYSTICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Summon a deadly speed tornado",
            "§7  You gain 10x speed!",
            "§7  Enemies launched 25 blocks up!",
            "§7  Rotating vortex pulls enemies!",
            "§f🔧 Right + Crouch:",
            "§7  Summon a block tornado",
            "§7  Blocks rise and rotate around you",
            "§7  Left-click to launch all blocks!",
            "§7  Blocks crush enemies on impact",
            "§7  Lasts 10 seconds",
            "§f⏰ Cooldowns: §e30s (Speed), 40s (Block)",
            "§7=================================",
            "§3§l✦ Cyclone Fury ✦"
        );
        
        fruits.put("cyclone_fury", new Fruit("cyclone_fury", "§3§l🌀 Cyclone Fury", Material.CYAN_DYE, 1016, cycloneLore, cycloneAbilities));
        
        // ==================== 7. STORM MONSTER (Wind/Storm) ====================
        List<Ability> stormAbilities = Arrays.asList(
            new Ability("§b🌬️ Wind Monster", 60, (p, target) -> {
                StormAbilities.windMonster(p);
            }),
            new Ability("§9⚡ Storm Monster", 60, (p, target) -> {
                StormAbilities.stormMonster(p);
            })
        );
        
        List<String> stormLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MYSTICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Transform into Wind Monster",
            "§7  Rise 6 blocks into the air",
            "§7  Giant wind fist slams every 1.5s",
            "§7  5x5 area damage below you",
            "§7  Cloud particles from hands/back",
            "§f⚡ Right + Crouch:",
            "§7  Transform into Storm Monster",
            "§7  Villain look with electric aura",
            "§7  Lightning strikes with each slam",
            "§7  Electric damage + slowness",
            "§f⏰ Duration: §e10 seconds",
            "§f⏰ Cooldown: §e1 minute",
            "§7=================================",
            "§9§l✦ Storm Monster ✦"
        );
        
        fruits.put("storm_monster", new Fruit("storm_monster", "§9§l⚡ Storm Monster", Material.LIGHT_BLUE_DYE, 1017, stormLore, stormAbilities));
        
        // ==================== 8. TIDAL WEAVER (Water) ====================
        List<Ability> tidalAbilities = Arrays.asList(
            new Ability("§b💧 Water Geyser", 25, (p, target) -> {
                WaterAbilities.waterGeyser(p);
            }),
            new Ability("§b🌊 Tidal Wave", 35, (p, target) -> {
                WaterAbilities.tidalWave(p);
            })
        );
        
        List<String> tidalLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MYSTICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Summon water geysers under all nearby entities!",
            "§7  Cartoon-style launch into the air!",
            "§7  Water particles and bubble effects",
            "§7  Spinning launch effect",
            "§f🔧 Right + Crouch:",
            "§7  Transform into a powerful tidal wave!",
            "§7  Become invisible like water",
            "§7  Crash forward pushing all enemies!",
            "§7  Damage and knockback enemies",
            "§7  Cinematic water wave particles",
            "§f⏰ Cooldowns: §e25s (Geyser), 35s (Wave)",
            "§7=================================",
            "§b§l✦ Tidal Weaver ✦"
        );
        
        fruits.put("tidal_weaver", new Fruit("tidal_weaver", "§b§l💧 Tidal Weaver", Material.LIGHT_BLUE_DYE, 1018, tidalLore, tidalAbilities));
        
        // ==================== 9. OTHER MAGICAL FRUITS (Placeholder) ====================
        List<String> defaultLore = Arrays.asList(
            "§7=================================",
            "§e§l🔮 MAGICAL FRUIT",
            "§7=================================",
            "§f⚡ Right Click:",
            "§7  Ability coming soon!",
            "§f🔧 Right + Crouch:",
            "§7  Ability coming soon!",
            "§7=================================",
            "§d§l✦ Magical Fruit ✦"
        );
        
        fruits.put("dragonfruit", new Fruit("dragonfruit", "§c§l🐉 Dragonfruit", Material.RED_DYE, 1002, defaultLore, new ArrayList<>()));
        fruits.put("starfruit", new Fruit("starfruit", "§e§l⭐ Starfruit", Material.YELLOW_DYE, 1003, defaultLore, new ArrayList<>()));
        fruits.put("moonberry", new Fruit("moonberry", "§b§l🌙 Moonberry", Material.LIGHT_BLUE_DYE, 1004, defaultLore, new ArrayList<>()));
        fruits.put("voidberry", new Fruit("voidberry", "§8§l🕳️ Voidberry", Material.BLACK_DYE, 1005, defaultLore, new ArrayList<>()));
        fruits.put("stormberry", new Fruit("stormberry", "§3§l⚡ Stormberry", Material.CYAN_DYE, 1006, defaultLore, new ArrayList<>()));
        fruits.put("frostberry", new Fruit("frostberry", "§b§l❄️ Frostberry", Material.LIGHT_BLUE_DYE, 1007, defaultLore, new ArrayList<>()));
        fruits.put("flameberry", new Fruit("flameberry", "§c§l🔥 Flameberry", Material.RED_DYE, 1008, defaultLore, new ArrayList<>()));
        fruits.put("shadowberry", new Fruit("shadowberry", "§7§l🌑 Shadowberry", Material.GRAY_DYE, 1009, defaultLore, new ArrayList<>()));
        fruits.put("soulberry", new Fruit("soulberry", "§5§l👻 Soulberry", Material.PURPLE_DYE, 1010, defaultLore, new ArrayList<>()));
        fruits.put("mysticberry", new Fruit("mysticberry", "§5§l🔮 Mysticberry", Material.PURPLE_DYE, 1011, defaultLore, new ArrayList<>()));
    }

    public Fruit getFruit(String id) { 
        return fruits.get(id); 
    }
    
    public Collection<Fruit> getAllFruits() { 
        return fruits.values(); 
    }
}
