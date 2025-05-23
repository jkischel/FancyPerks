package de.oliver.fancyperks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import de.oliver.fancylib.ConfigHelper;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;
import de.oliver.fancyperks.perks.impl.DoubleDropsPerk;
import de.oliver.fancyperks.perks.impl.EffectPerk;
import de.oliver.fancyperks.perks.impl.LavaRunnerPerk;

public class FancyPerksConfig {

    private boolean muteVersionNotification;
    private boolean activatePerkOnPermissionSet;

    public void reload() {
        FancyPerks.getInstance().reloadConfig();
        FileConfiguration config = FancyPerks.getInstance().getConfig();

        muteVersionNotification = (boolean) ConfigHelper.getOrDefault(config, "mute_version_notification", false);
        activatePerkOnPermissionSet = (boolean) ConfigHelper.getOrDefault(config, "activate_perk_on_permission_set", false);

        for (Perk perk : PerkRegistry.ALL_PERKS) {
            String displayName = (String) ConfigHelper.getOrDefault(config, "perks." + perk.getSystemName() + ".name", perk.getDisplayName());
            perk.setDisplayName(displayName);

            String description = (String) ConfigHelper.getOrDefault(config, "perks." + perk.getSystemName() + ".description", perk.getDescription());
            perk.setDescription(description);

            boolean isEnabled = (boolean) ConfigHelper.getOrDefault(config, "perks." + perk.getSystemName() + ".enabled", true);
            perk.setEnabled(isEnabled);

            List<String> disabledWorlds = (List<String>) ConfigHelper.getOrDefault(config, "perks." + perk.getSystemName() + ".disabled_worlds", new ArrayList<>());
            perk.setDisabledWorlds(disabledWorlds);

            boolean buyable = (boolean) ConfigHelper.getOrDefault(config, "perks." + perk.getSystemName() + ".buyable", true);
            perk.setBuyable(buyable);

            double price = (double) ConfigHelper.getOrDefault(config, "perks." + perk.getSystemName() + ".price", 1000d);
            perk.setPrice(price);

            if (perk instanceof LavaRunnerPerk lavaRunnerPerk) {
                int radius = (int) ConfigHelper.getOrDefault(config, "perks." + perk.getSystemName() + ".radius", 4);
                int dissolutionTime = (int) ConfigHelper.getOrDefault(config, "perks." + perk.getSystemName() + ".dissolution_time", 3000);

                lavaRunnerPerk.setRadius(radius);
                lavaRunnerPerk.setDissolutionTime(dissolutionTime);
            }

            if(perk instanceof DoubleDropsPerk doubleDropsPerk) {
                if(!config.isList("perks." + perk.getSystemName() + ".blacklist")) {
                    doubleDropsPerk.addToBlacklist(EntityType.PLAYER);
                    config.set("perks." + perk.getSystemName() + ".blacklist", List.of("PLAYER"));
                } else {
                    List<String> blacklist = config.getStringList("perks." + perk.getSystemName() + ".blacklist");
                    for (String s : blacklist) {
                        EntityType entityType = EntityType.valueOf(s.toUpperCase());
                        doubleDropsPerk.addToBlacklist(entityType);
                    }
                }
            }

            if (perk instanceof EffectPerk effectPerk) {
                String key = perk.getSystemName();
                int defaultEffectStrength = EffectPerk.defaultEffectStrengths.getOrDefault(key, 0);

                int strength = (int) ConfigHelper.getOrDefault(config, "perks." + perk.getSystemName() + ".effectStrength", defaultEffectStrength);
                effectPerk.setEffectStrength(strength);
            }

        }

        FancyPerks.getInstance().saveConfig();
    }

    public boolean isMuteVersionNotification() {
        return muteVersionNotification;
    }

    public boolean isActivatePerkOnPermissionSet() {
        return activatePerkOnPermissionSet;
    }
}
