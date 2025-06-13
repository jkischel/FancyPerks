package de.oliver.fancyperks.gui.inventoryClick;

import java.util.Collections;
import java.util.List;

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
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

public class BuyPerkInventoryItemClick implements InventoryItemClick {

    public static final BuyPerkInventoryItemClick INSTANCE = new BuyPerkInventoryItemClick();

    private final static List<NamespacedKey> REQUIRED_KEYS = Collections.singletonList(
            Perk.PERK_KEY
    );

    private BuyPerkInventoryItemClick() {
    }

    @Override
    public String getId() {
        return "buyPerk";
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        Player p = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item != null && InventoryItemClick.hasKeys(item, REQUIRED_KEYS)) {
            event.setCancelled(true);
            String perkName = item.getItemMeta().getPersistentDataContainer().get(Perk.PERK_KEY, PersistentDataType.STRING);
            Perk perk = PerkRegistry.getPerkByName(perkName);

            if (perk == null || !perk.isBuyable() || !FancyPerks.getInstance().isUsingVault()) {
                return;
            }

            Economy economy = FancyPerks.getInstance().getVaultEconomy();
            Permission permission = FancyPerks.getInstance().getVaultPermission();

            if (!economy.has(player, perk.getPrice())) {
                String message = LanguageHelper.getLocalizedMessage("not_enough_money", null);
                MessageHelper.error(player, message);
                return;
            }

            EconomyResponse response = economy.withdrawPlayer(player, perk.getPrice());
            if (!response.transactionSuccess()) {
                String message = LanguageHelper.getLocalizedMessage("transaction_unsuccessful", null);
                MessageHelper.warning(player, message);
                return;
            }

            if (!permission.playerAdd(null, player, "fancyperks.perk." + perk.getSystemName())) {
                String message = LanguageHelper.getLocalizedMessage("permission_unsuccessful", null);
                MessageHelper.warning(player, message);
                return;
            }

            perk.grant(p);
            event.setCurrentItem(PerksInventory.getEnabledPerkItem(perk));
        }
    }
}
