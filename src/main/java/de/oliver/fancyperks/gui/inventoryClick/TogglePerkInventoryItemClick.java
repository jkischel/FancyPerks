package de.oliver.fancyperks.gui.inventoryClick;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.gui.inventoryClick.InventoryItemClick;
import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.LanguageHelper;
import de.oliver.fancyperks.gui.customInventories.PerksInventory;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;

public class TogglePerkInventoryItemClick implements InventoryItemClick {

    public static final TogglePerkInventoryItemClick INSTANCE = new TogglePerkInventoryItemClick();

    private final static List<NamespacedKey> REQUIRED_KEYS = Collections.singletonList(
            Perk.PERK_KEY
    );

    private TogglePerkInventoryItemClick() {
    }

    @Override
    public String getId() {
        return "togglePerk";
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        Player p = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item != null && InventoryItemClick.hasKeys(item, REQUIRED_KEYS)) {
            event.setCancelled(true);

            String perkName = item.getItemMeta().getPersistentDataContainer().get(Perk.PERK_KEY, PersistentDataType.STRING);
            Perk perk = PerkRegistry.getPerkByName(perkName);

            if (perk == null) {
                return;
            }

            boolean hasPerk = FancyPerks.getInstance().getPerkManager().hasPerkEnabled(p, perk);

            if (hasPerk) {
                perk.revoke(p);
                event.setCurrentItem(PerksInventory.getDisabledPerkItem(perk));
            } else {
                if (!perk.hasPermission(player)) {
                    String message = LanguageHelper.getLocalizedMessage("no_perk_permission", null);
                    MessageHelper.error(p, message);
                    return;
                }

                if(!perk.grant(p)){
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkname", perk.getSystemName());
                    String message = LanguageHelper.getLocalizedMessage("perk_disabled_in_this_world", replacements);
                    MessageHelper.warning(player, message);
                    return;
                }

                event.setCurrentItem(PerksInventory.getEnabledPerkItem(perk));
            }
        }
    }
}
