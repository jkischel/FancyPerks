package de.oliver.fancyperks.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.LanguageHelper;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.model.user.User;

public class LuckPermsListener {

    public LuckPermsListener() {
        EventBus eventBus = FancyPerks.getInstance().getLuckPerms().getEventBus();
        eventBus.subscribe(FancyPerks.getInstance(), NodeAddEvent.class, this::onNodeAdd);
    }

    private void onNodeAdd(NodeAddEvent event) {
        String permission = event.getNode().getKey().toLowerCase();
        if (!permission.startsWith("fancyperks.perk.")) {
            return;
        }

        String perkStr = permission.substring(permission.lastIndexOf('.') + 1, permission.length());
        Perk perk = PerkRegistry.getPerkByName(perkStr);
        if (perk == null) {
            return;
        }

        if (event.isUser()) {
            User user = (User) event.getTarget();
            Player p = Bukkit.getPlayer(user.getUniqueId());
            if (p == null) {
                return;
            }

            if (event.getNode().getValue() && perk.hasPermission(p)) {
                if(!perk.grant(p)){
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkname", perk.getSystemName());
                    String message = LanguageHelper.getLocalizedMessage("perk_disabled_in_this_world", replacements);                    
                    MessageHelper.warning(p, message);
                    return;
                }
                
                Map<String, String> replacements = new HashMap<>();
                replacements.put("perkname", perk.getSystemName());
                String message = LanguageHelper.getLocalizedMessage("perk_automatically_enabled", replacements);
                MessageHelper.success(p, message);
            } else if (!perk.hasPermission(p)) {
                perk.revoke(p);
                Map<String, String> replacements = new HashMap<>();
                replacements.put("perkname", perk.getSystemName());
                String message = LanguageHelper.getLocalizedMessage("perk_automatically_disabled", replacements);
                MessageHelper.success(p, message);
            }
        }

    }

}
