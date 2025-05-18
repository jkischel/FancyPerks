package de.oliver.fancyperks.listeners;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.PerkManager;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) {
            return;
        }

        PerkManager perkManager = FancyPerks.getInstance().getPerkManager();
        List<Perk> perks = perkManager.getEnabledPerks(p);
        Boolean applyNoDamage = false;
        String damageReason = event.getCause().name().toLowerCase();

        Boolean hasGod = perks.contains(PerkRegistry.GOD);
        if (hasGod) {
            applyNoDamage = true;
        }

        Boolean hasNoFireDamage = perks.contains(PerkRegistry.NO_FIRE_DAMAGE);
        if (hasNoFireDamage && damageReason.contains("fire") 
            || damageReason.equals("lava")
            || damageReason.equals("hot_floor"))
        {
            applyNoDamage = true;
        }

        Boolean hasNoFallDamage = perks.contains(PerkRegistry.NO_FALL_DAMAGE);
        if (hasNoFallDamage && damageReason.equals("fall")) {
            applyNoDamage = true;
        }

        Boolean hasNoExplosion = perks.contains(PerkRegistry.NO_EXPLOSION_DAMAGE);
        if (hasNoExplosion && damageReason.contains("explosion")) {
            applyNoDamage = true;
        }

        Boolean hasNoPoison = perks.contains(PerkRegistry.NO_POISON_DAMAGE);
        if (hasNoPoison && damageReason.equals("poison")) {
            applyNoDamage = true;
        }

        Boolean hasNoProjectile = perks.contains(PerkRegistry.NO_PROJECTILE_DAMAGE);
        if (hasNoProjectile && damageReason.equals("projectile")) {
            applyNoDamage = true;
        }

        boolean hasNoFrost = perks.contains(PerkRegistry.NO_FROST_DAMAGE);
        if (hasNoFrost && (damageReason.equals("freeze"))) {
            applyNoDamage = true;
        }

        Boolean hasNoBlockDamage = perks.contains(PerkRegistry.NO_BLOCK_DAMAGE);
        if (hasNoBlockDamage && damageReason.equals("block_explosion")
        || damageReason.equals("contact")
        || damageReason.equals("falling_block")
        || damageReason.equals("fly_into_wall")
        || damageReason.equals("hot_floor")
        || damageReason.equals("suffocation")
        ) {
            applyNoDamage = true;
        }

        if (applyNoDamage) {
            event.setDamage(0);
            event.setCancelled(true);
            return;
        }

        Boolean hasHalfDamage = perks.contains(PerkRegistry.HALF_DAMAGE);
        if (hasHalfDamage) {
            double originalDamage = event.getDamage();
            event.setDamage(originalDamage / 2);
        }
    }

}
