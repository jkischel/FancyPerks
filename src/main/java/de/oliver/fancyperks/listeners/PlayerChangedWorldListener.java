package de.oliver.fancyperks.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.LanguageHelper;
import de.oliver.fancyperks.PerkManager;
import de.oliver.fancyperks.perks.Perk;

public class PlayerChangedWorldListener implements Listener {

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event){
        Player p = event.getPlayer();

        PerkManager perkManager = FancyPerks.getInstance().getPerkManager();
        List<Perk> perks = perkManager.getEnabledPerks(p);

        for (Perk perk : new ArrayList<>(perks)) {
            if(perk.getDisabledWorlds().contains(p.getWorld().getName())){
                perk.revoke(p);
                Map<String, String> replacements = new HashMap<>();
                replacements.put("perkname", perk.getSystemName());
                String message = LanguageHelper.getLocalizedMessage("perk_disabled_in_this_world", replacements);
                MessageHelper.warning(p, message);
            }
        }
    }

}
