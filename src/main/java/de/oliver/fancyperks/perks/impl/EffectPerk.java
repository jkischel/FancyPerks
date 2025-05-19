package de.oliver.fancyperks.perks.impl;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.oliver.fancyperks.perks.Perk;

public class EffectPerk extends Perk {

    private final PotionEffectType effectType;
    private int effectStrength;

    public EffectPerk(String systemName, String name, String description, ItemStack displayName, PotionEffectType effectType) {
        super(systemName, name, description, displayName);
        this.effectType = effectType;
    }

    public int getEffectStrength() {
        return effectStrength;
    }

    public void setEffectStrength(int strength) {
        this.effectStrength = strength;
    }    

    @Override
    public boolean grant(Player player) {
        if(!super.grant(player)) return false;

        player.addPotionEffect(new PotionEffect(effectType, -1, effectStrength, true, false, false));
        return true;
    }

    @Override
    public void revoke(Player player) {
        super.revoke(player);
        player.removePotionEffect(effectType);
    }

    public PotionEffectType getEffectType() {
        return effectType;
    }

    public static final Map<String, Integer> defaultEffectStrengths;
    static {
        defaultEffectStrengths = new HashMap<>();
        defaultEffectStrengths.put("fast_digging", 1);
        defaultEffectStrengths.put("instabreak", 250);
        defaultEffectStrengths.put("night_vision", 0);
        defaultEffectStrengths.put("water_breathing", 0);
        defaultEffectStrengths.put("speed", 1);
        defaultEffectStrengths.put("slowness", 0);
        defaultEffectStrengths.put("glowing", 0);
        defaultEffectStrengths.put("strength", 1);
        defaultEffectStrengths.put("jump_boost", 1);
        defaultEffectStrengths.put("health_boost", 0);
        defaultEffectStrengths.put("regeneration", 0);
        defaultEffectStrengths.put("fire_resistance", 0);
        defaultEffectStrengths.put("resistance", 2);
        defaultEffectStrengths.put("invisibility", 0);
        defaultEffectStrengths.put("levitation", 0);
        defaultEffectStrengths.put("luck", 1);
        defaultEffectStrengths.put("slow_falling", 0);
        defaultEffectStrengths.put("bad_omen", 2);
        defaultEffectStrengths.put("hero_of_the_village", 2);
        defaultEffectStrengths.put("dolphins_grace", 0);
    }

}
