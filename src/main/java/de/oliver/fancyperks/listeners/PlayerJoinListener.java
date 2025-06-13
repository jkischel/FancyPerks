package de.oliver.fancyperks.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.LanguageHelper;
import de.oliver.fancyperks.PerkManager;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;
import de.oliver.fancyperks.perks.impl.EffectPerk;
import de.oliver.fancyperks.perks.impl.FlyPerk;
import de.oliver.fancyperks.perks.impl.VanishPerk;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        if (!p.hasPermission("FancyPerks.seevanished")) {
            FancyPerks.getInstance().getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                if (!onlinePlayer.getMetadata("vanished").isEmpty() && onlinePlayer.getMetadata("vanished").get(0).asBoolean()) {
                    p.hidePlayer(FancyPerks.getInstance(), onlinePlayer);
                }
            });
        }

        PerkManager perkManager = FancyPerks.getInstance().getPerkManager();
        List<Perk> perks = perkManager.getEnabledPerks(event.getPlayer());

        // disable non-active effect perks (e.g. if deactivated while player offline)
        List<EffectPerk> allEffectPerks = PerkRegistry.ALL_PERKS.stream()
            .filter(EffectPerk.class::isInstance)
            .map(EffectPerk.class::cast)
            .collect(Collectors.toList());

        for (EffectPerk availablePerk : allEffectPerks) {
            String perkSystemName = availablePerk.getSystemName();
            
            boolean isActivated = perks.stream()
                .anyMatch(activePerk -> activePerk.getSystemName().equals(perkSystemName));

            if (!isActivated) {
                p.removePotionEffect(availablePerk.getEffectType());
            }
        }

        // give effect for activated perks, if allowed in that world
        perks = perkManager.getEnabledPerks(event.getPlayer());
        for (Perk perk : perks) {
            if(perk.getDisabledWorlds().contains(p.getWorld().getName())){
                Map<String, String> replacements = new HashMap<>();
                replacements.put("perkname", perk.getSystemName());
                String message = LanguageHelper.getLocalizedMessage("perk_disabled_in_this_world", replacements);
                MessageHelper.warning(p, message);
                continue;
            }

            if (perk instanceof EffectPerk effectPerk) {
                p.addPotionEffect(new PotionEffect(effectPerk.getEffectType(), -1, effectPerk.getEffectStrength(), true, false, false));
            } else if (perk instanceof FlyPerk) {
                p.setAllowFlight(true);
            } else if (perk instanceof VanishPerk vanishPerk) {
                vanishPerk.grant(p);
            }
        }

    }

}
