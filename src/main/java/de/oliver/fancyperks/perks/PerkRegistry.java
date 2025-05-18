package de.oliver.fancyperks.perks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import de.oliver.fancyperks.perks.impl.AutoRepairPerk;
import de.oliver.fancyperks.perks.impl.DoubleDropsPerk;
import de.oliver.fancyperks.perks.impl.EffectPerk;
import de.oliver.fancyperks.perks.impl.FlyPerk;
import de.oliver.fancyperks.perks.impl.LavaRunnerPerk;
import de.oliver.fancyperks.perks.impl.NoHungerPerk;
import de.oliver.fancyperks.perks.impl.SimplePerk;
import de.oliver.fancyperks.perks.impl.VanishPerk;

public class PerkRegistry {

    public static final Perk FAST_DIGGING = new EffectPerk("fast_digging", "Fast Digging", "Gives you the haste effect", new ItemStack(Material.DIAMOND_PICKAXE), PotionEffectType.FAST_DIGGING, 1);
    public static final Perk INSTABREAK = new EffectPerk("instabreak", "Instant Break", "Enables you to instantly break (nearly) every block.", new ItemStack(Material.NETHERITE_PICKAXE), PotionEffectType.FAST_DIGGING, 250);
    public static final Perk NIGHT_VISION = new EffectPerk("night_vision", "Night Vision", "Gives you the night vision effect", new ItemStack(Material.SPYGLASS), PotionEffectType.NIGHT_VISION, 0);
    public static final Perk WATER_BREATHING = new EffectPerk("water_breathing", "Water Breathing", "Gives you the water breathing effect", new ItemStack(Material.WATER_BUCKET), PotionEffectType.WATER_BREATHING, 0);
    public static final Perk SPEED = new EffectPerk("speed", "Speed", "Gives you the speed effect", new ItemStack(Material.DIAMOND_BOOTS), PotionEffectType.SPEED, 1);
    public static final Perk SLOWNESS = new EffectPerk("slowness", "Slowness", "Gives you the slowness effect", new ItemStack(Material.LEATHER_BOOTS), PotionEffectType.SLOW,0);
    public static final Perk GLOWING = new EffectPerk("glowing", "Glowing", "Makes you glow", new ItemStack(Material.BEACON), PotionEffectType.GLOWING, 0);
    public static final Perk STRENGTH = new EffectPerk("strength", "Strength", "Gives you the strength effect", new ItemStack(Material.DIAMOND_SWORD), PotionEffectType.INCREASE_DAMAGE, 1);
    public static final Perk JUMP_BOOST = new EffectPerk("jumpboost", "Jump Boost", "Gives you the jump boost effect", new ItemStack(Material.RABBIT_FOOT), PotionEffectType.JUMP, 1);
    public static final Perk HEALTH_BOOST = new EffectPerk("health_boost", "Health Boost", "Gives you extra hearts", new ItemStack(Material.APPLE), PotionEffectType.HEALTH_BOOST, 0);
    public static final Perk REGENERATION = new EffectPerk("regeneration", "Regeneration", "Gives you the regeneration effect", new ItemStack(Material.HONEY_BOTTLE), PotionEffectType.REGENERATION, 0);
    public static final Perk FIRE_RESISTANCE = new EffectPerk("fire_resistance", "Fire Resistance", "Makes you fire-proof", new ItemStack(Material.CAMPFIRE), PotionEffectType.FIRE_RESISTANCE, 0);
    public static final Perk RESISTANCE = new EffectPerk("resistance", "Resistance", "Gives you the resistance effect", new ItemStack(Material.BEDROCK), PotionEffectType.DAMAGE_RESISTANCE, 2);
    public static final Perk INVISIBILITY = new EffectPerk("invisibility", "Invisibility", "Makes you invisible", new ItemStack(Material.ENDER_EYE), PotionEffectType.INVISIBILITY, 0);
    public static final Perk LEVITATION = new EffectPerk("levitation", "Levitation", "Gives you the levitation effect", new ItemStack(Material.ELYTRA), PotionEffectType.LEVITATION, 0);
    public static final Perk LUCK = new EffectPerk("luck", "Luck", "Increases your luck when fishing.", new ItemStack(Material.FISHING_ROD), PotionEffectType.LUCK, 1);
    public static final Perk SLOW_FALLING = new EffectPerk("slow_falling", "Slow falling", "Decreases damage when falling.", new ItemStack(Material.FEATHER), PotionEffectType.SLOW_FALLING, 0);
    public static final Perk BAD_OMEN = new EffectPerk("bad_omen", "Bad Omen", "Gives you the bad omen effect.", new ItemStack(Material.CROSSBOW), PotionEffectType.BAD_OMEN, 2);
    public static final Perk HERO_OF_THE_VILLAGE = new EffectPerk("hero_of_the_village", "Hero of the village", "Gives you the hero of the village effect.", new ItemStack(Material.EMERALD), PotionEffectType.HERO_OF_THE_VILLAGE, 2);
    public static final Perk KEEP_EXP = new SimplePerk("keep_exp", "Keep EXP", "Don't lose your exp after dying", new ItemStack(Material.EXPERIENCE_BOTTLE));
    public static final Perk KEEP_INVENTORY = new SimplePerk("keep_inventory", "Keep Inventory", "Don't lose your items after dying", new ItemStack(Material.CHEST));
    public static final Perk FLY = new FlyPerk("fly", "Fly", "Gives you the ability to fly", new ItemStack(Material.ELYTRA));
    public static final Perk NO_HUNGER = new NoHungerPerk("no_hunger", "No Hunger", "You don't need to eat again", new ItemStack(Material.COOKED_CHICKEN));
    public static final Perk NO_FIRE_DAMAGE = new SimplePerk("no_fire_damage", "No Fire Damage", "Don't get hurt by fire", new ItemStack(Material.FIRE_CHARGE));
    public static final Perk NO_FALL_DAMAGE = new SimplePerk("no_fall_damage", "No Fall Damage", "Don't get hurt by fall damage", new ItemStack(Material.SLIME_BLOCK));
    public static final Perk NO_EXPLOSION_DAMAGE = new SimplePerk("no_explosion_damage", "No Explosion Damage", "Don't get hurt by explosions", new ItemStack(Material.TNT));
    public static final Perk NO_POISON_DAMAGE = new SimplePerk("no_poison_damage", "No Poison Damage", "Don't damage from poisoning", new ItemStack(Material.SPIDER_EYE));
    public static final Perk NO_PROJECTILE_DAMAGE = new SimplePerk("no_projectile_damage", "No Projectile Damage", "Don't get damage from projectiles like arrows.", new ItemStack(Material.ARROW));
    public static final Perk NO_FROST_DAMAGE = new SimplePerk("no_frost_damage", "No Frost Damage", "Don't get damage if you're frozen.", new ItemStack(Material.SNOWBALL));
    public static final Perk NO_BLOCK_DAMAGE = new SimplePerk("no_block_damage", "No Block Damage", "Don't get (most) block-related damage.", new ItemStack(Material.BRICKS));
    public static final Perk HALF_DAMAGE = new SimplePerk("half_damage", "Half Damage", "Reduce the damage you get to the half.", new ItemStack(Material.SHIELD));
    public static final Perk GOD = new SimplePerk("god", "God", "Don't get any damage", new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
    public static final Perk MOBS_IGNORE = new SimplePerk("mobs_ignore", "Mobs ignore", "Mobs don't notice you anymore", new ItemStack(Material.ZOMBIE_SPAWN_EGG));
    public static final Perk DOUBLE_EXP = new SimplePerk("double_exp", "Double Exp", "Receive double exp for killing monsters", new ItemStack(Material.EXPERIENCE_BOTTLE));
    public static final Perk TRIPLE_EXP = new SimplePerk("triple_exp", "Triple Exp", "Receive 3x exp for killing monsters", new ItemStack(Material.EXPERIENCE_BOTTLE));
    public static final Perk DOUBLE_DROPS = new DoubleDropsPerk("double_mob_drops", "Double Mob Drops", "Receive double drops for killing Mobs", new ItemStack(Material.ROTTEN_FLESH));
    public static final Perk VANISH = new VanishPerk("vanish", "Vanish", "Hide from all players", new ItemStack(Material.ENDER_PEARL));
    public static final Perk TELEKINESIS = new SimplePerk("telekinesis", "Telekinesis", "Automatically pick ups items", new ItemStack(Material.GOLDEN_PICKAXE));
    public static final Perk INSTANT_SMELT = new SimplePerk("instant_smelt", "Instant smelt", "Automatically smelts ores", new ItemStack(Material.IRON_ORE));
    public static final Perk AUTO_REPAIR = new AutoRepairPerk("auto_repair", "Auto repair", "Your tools and armor won't lose durability", new ItemStack(Material.ANVIL));
    public static final Perk AUTO_PLANTING = new SimplePerk("auto_planting", "Auto planting", "Automatically replants crops when harvested", new ItemStack(Material.WHEAT_SEEDS));
    public static final Perk LAVA_RUNNER = new LavaRunnerPerk("lava_runner", "Lava Runner", "Allows you to walk on lava", new ItemStack(Material.MAGMA_BLOCK));
    public static final Perk DROP_MORE_BLOCKS = new SimplePerk("drop_more_blocks", "Drop more blocks", "Some blocks will drop that usually wouldn't.", new ItemStack(Material.BUDDING_AMETHYST));
    public static final Perk DROP_SPAWNERS = new SimplePerk("drop_spawners", "Drop mob spawners", "Will drop mob spawners (but not keep mob!)", new ItemStack(Material.SPAWNER));

    public static final List<Perk> ALL_PERKS = new ArrayList<>();

    static {
        registerPerk(FAST_DIGGING);
        registerPerk(INSTABREAK);
        registerPerk(NIGHT_VISION);
        registerPerk(WATER_BREATHING);
        registerPerk(SPEED);
        registerPerk(SLOWNESS);
        registerPerk(GLOWING);
        registerPerk(STRENGTH);
        registerPerk(JUMP_BOOST);
        registerPerk(HEALTH_BOOST);
        registerPerk(REGENERATION);
        registerPerk(FIRE_RESISTANCE);
        registerPerk(RESISTANCE);
        registerPerk(INVISIBILITY);
        registerPerk(LEVITATION);
        registerPerk(LUCK);
        registerPerk(SLOW_FALLING);
        registerPerk(BAD_OMEN);
        registerPerk(HERO_OF_THE_VILLAGE);
        registerPerk(KEEP_EXP);
        registerPerk(KEEP_INVENTORY);
        registerPerk(FLY);
        registerPerk(VANISH);
        registerPerk(GOD);
        registerPerk(NO_HUNGER);
        registerPerk(NO_FIRE_DAMAGE);
        registerPerk(NO_FALL_DAMAGE);
        registerPerk(NO_EXPLOSION_DAMAGE);
        registerPerk(NO_POISON_DAMAGE);
        registerPerk(NO_PROJECTILE_DAMAGE);
        registerPerk(NO_FROST_DAMAGE);
        registerPerk(NO_BLOCK_DAMAGE);
        registerPerk(HALF_DAMAGE);
        registerPerk(MOBS_IGNORE);
        registerPerk(DOUBLE_EXP);
        registerPerk(TRIPLE_EXP);
        registerPerk(DOUBLE_DROPS);
        registerPerk(TELEKINESIS);
        registerPerk(INSTANT_SMELT);
        registerPerk(AUTO_REPAIR);
        registerPerk(AUTO_PLANTING);
        registerPerk(LAVA_RUNNER);
        registerPerk(DROP_MORE_BLOCKS);
        registerPerk(DROP_SPAWNERS);
    }

    public static Perk getPerkByName(String name) {
        for (Perk perk : ALL_PERKS) {
            if (perk.getDisplayName().equalsIgnoreCase(name.replaceAll("_", " ")) || perk.getSystemName().equalsIgnoreCase(name)) {
                return perk;
            }
        }

        return null;
    }

    public static void registerPerk(Perk perk) {
        ALL_PERKS.add(perk);
    }

}
