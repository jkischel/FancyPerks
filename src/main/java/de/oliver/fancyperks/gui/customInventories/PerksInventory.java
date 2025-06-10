package de.oliver.fancyperks.gui.customInventories;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.gui.customInventories.CustomPlayerInventory;
import de.oliver.fancylib.gui.customInventories.PageInventory;
import de.oliver.fancylib.gui.inventoryClick.InventoryItemClick;
import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class PerksInventory extends CustomPlayerInventory implements PageInventory {

    private final static LanguageConfig lang = FancyPerks.getInstance().getLanguageConfig();
    public static String perkDisabledItemName;
    public static String perkEnabledItemName;
    public static String perkNotOwnedItemName;
    public static String nextPageItem;
    public static String previousPageItem;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public PerksInventory(Player player) {
        super(player, 6, MiniMessage.miniMessage().deserialize("Perks"));

        loadPage(1);
    }

    public static ItemStack getEnabledPerkItem(Perk perk) {
        ItemStack item;

        if (perkEnabledItemName.startsWith("PLAYER_HEAD")) {

            item = FancyPerks.getInstance().createCustomSkull(perkEnabledItemName);

            item.editMeta(SkullMeta.class, skullMeta -> {
                skullMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_perk_is_enabled")), TextDecoration.ITALIC));
                skullMeta.lore(Arrays.asList(
                        Component.empty(),
                        MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_click_to_toggle")), TextDecoration.ITALIC)
                ));
                skullMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "togglePerk");
                skullMeta.getPersistentDataContainer().set(Perk.PERK_KEY, PersistentDataType.STRING, perk.getSystemName());
            });
        } else {
            item = new ItemStack(Material.valueOf(perkEnabledItemName));
            item.editMeta(itemMeta -> {
                itemMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_perk_is_enabled")), TextDecoration.ITALIC));
                itemMeta.lore(Arrays.asList(
                        Component.empty(),
                        MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_click_to_toggle")), TextDecoration.ITALIC)
                ));

                itemMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "togglePerk");
                itemMeta.getPersistentDataContainer().set(Perk.PERK_KEY, PersistentDataType.STRING, perk.getSystemName());
            });
        }

        return item;
    }

    public static ItemStack getDisabledPerkItem(Perk perk) {
        ItemStack item;

        if (perkDisabledItemName.startsWith("PLAYER_HEAD")) {
            item = FancyPerks.getInstance().createCustomSkull(perkDisabledItemName);
            item.editMeta(SkullMeta.class, skullMeta -> {
                skullMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_perk_is_disabled")), TextDecoration.ITALIC));
                skullMeta.lore(Arrays.asList(
                        Component.empty(),
                        MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_click_to_toggle")), TextDecoration.ITALIC)
                ));
                skullMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "togglePerk");
                skullMeta.getPersistentDataContainer().set(Perk.PERK_KEY, PersistentDataType.STRING, perk.getSystemName());
            });
        } else {
            item = new ItemStack(Material.valueOf(perkDisabledItemName));
            item.editMeta(itemMeta -> {
                itemMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_perk_is_disabled")), TextDecoration.ITALIC));
                itemMeta.lore(Arrays.asList(
                        Component.empty(),
                        MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_click_to_toggle")), TextDecoration.ITALIC)
                ));

                itemMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "togglePerk");
                itemMeta.getPersistentDataContainer().set(Perk.PERK_KEY, PersistentDataType.STRING, perk.getSystemName());
            });
        }

        return item;
    }

    public static ItemStack getBuyPerkItem(Perk perk) {
        ItemStack item;

        if (perkNotOwnedItemName.startsWith("PLAYER_HEAD")) {

            item = FancyPerks.getInstance().createCustomSkull(perkNotOwnedItemName);

            item.editMeta(SkullMeta.class, skullMeta -> {
                skullMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_you_dont_own_this_perk")), TextDecoration.ITALIC));
                skullMeta.lore(Arrays.asList(
                        Component.empty(),
                        MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_price", "price", String.valueOf(perk.getPrice()))), TextDecoration.ITALIC),
                        Component.empty(),
                        MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_click_to_buy")), TextDecoration.ITALIC)
                ));

                skullMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "buyPerk");
                skullMeta.getPersistentDataContainer().set(Perk.PERK_KEY, PersistentDataType.STRING, perk.getSystemName());
            });
        } else {
            item = new ItemStack(Material.valueOf(perkNotOwnedItemName));
            item.editMeta(itemMeta -> {
                itemMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_you_dont_own_this_perk")), TextDecoration.ITALIC));
                itemMeta.lore(Arrays.asList(
                        Component.empty(),
                        MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_price", "price", String.valueOf(perk.getPrice()))), TextDecoration.ITALIC),
                        Component.empty(),
                        MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_click_to_buy")), TextDecoration.ITALIC)
                ));

                itemMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "buyPerk");
                itemMeta.getPersistentDataContainer().set(Perk.PERK_KEY, PersistentDataType.STRING, perk.getSystemName());
            });
        }

        return item;
    }

    @Override
    public void loadPage(int page) {
        inventory.clear();

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, CustomPlayerInventory.getPlaceholder());
        }

        List<Perk> perks = PerkRegistry.ALL_PERKS.stream().filter(Perk::isEnabled).toList();
        final int perksPerPage = 2 * 9;
        int perkIndex = perksPerPage * Math.max(0, page - 1);
        boolean isLastPage = false;

        rowLoop:
        for (int row = 0; row < 4; row += 2) {
            for (int col = 0; col < 9; col++) {
                int topIndex = row * 9 + col;
                int bottomIndex = topIndex + 9;

                Perk perk = perks.get(perkIndex);
                perkIndex++;

                boolean enabled = FancyPerks.getInstance().getPerkManager().hasPerkEnabled(player, perk);
                boolean hasPermissions = player.hasPermission("FancyPerks.perk." + perk.getSystemName());

                inventory.setItem(topIndex, perk.getDisplayItem());

                if (FancyPerks.getInstance().isUsingVault() && !hasPermissions && perk.isBuyable()) {
                    inventory.setItem(bottomIndex, getBuyPerkItem(perk));
                } else if (enabled) {
                    inventory.setItem(bottomIndex, getEnabledPerkItem(perk));
                } else {
                    inventory.setItem(bottomIndex, getDisabledPerkItem(perk));
                }

                if (perkIndex >= perks.size()) {
                    isLastPage = true;
                    break rowLoop;
                }
            }
        }

        if (page > 1) {
            ItemStack previousPage;
            if (previousPageItem.startsWith("PLAYER_HEAD")) {
                previousPage = FancyPerks.getInstance().createCustomSkull(previousPageItem);
                previousPage.editMeta(SkullMeta.class, skullMeta -> {
                    skullMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_previous_page")), TextDecoration.ITALIC));
                    skullMeta.getPersistentDataContainer().set(PageInventory.PAGE_KEY, PersistentDataType.INTEGER, page - 1);
                    skullMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "changePage");
                });
            } else {
                previousPage = new ItemStack(Material.valueOf(previousPageItem));
                previousPage.editMeta(itemMeta -> {
                    itemMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_previous_page")), TextDecoration.ITALIC));
                    itemMeta.getPersistentDataContainer().set(PageInventory.PAGE_KEY, PersistentDataType.INTEGER, page - 1);
                    itemMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "changePage");
                });
            }

            inventory.setItem(47, previousPage);
        }

        if (!isLastPage) {
            ItemStack nextPage;
            if (nextPageItem.startsWith("PLAYER_HEAD")) {
                nextPage = FancyPerks.getInstance().createCustomSkull(nextPageItem);
                nextPage.editMeta(SkullMeta.class, skullMeta -> {
                    skullMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_next_page")), TextDecoration.ITALIC));
                    skullMeta.getPersistentDataContainer().set(PageInventory.PAGE_KEY, PersistentDataType.INTEGER, page + 1);
                    skullMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "changePage");
                });
            } else {
                nextPage = new ItemStack(Material.valueOf(nextPageItem));
                nextPage.editMeta(itemMeta -> {
                    itemMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize(lang.get("gui_next_page")), TextDecoration.ITALIC));
                    itemMeta.getPersistentDataContainer().set(PageInventory.PAGE_KEY, PersistentDataType.INTEGER, page + 1);
                    itemMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "changePage");
                });
            }

            inventory.setItem(51, nextPage);
        }
    }
}
