package de.oliver.fancyperks.perks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.gui.inventoryClick.InventoryItemClick;
import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.PerkManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public abstract class Perk {

    public static final NamespacedKey PERK_KEY = new NamespacedKey(FancyPerks.getInstance(), "perk");

    protected static final PerkManager perkManager = FancyPerks.getInstance().getPerkManager();

    protected final String systemName;
    protected ItemStack displayItem;
    protected String displayName;
    protected String description;
    protected boolean enabled;
    public List<String> disabledWorlds;
    protected boolean buyable;
    protected double price;

    public Perk(String systemName, String displayName, String description, ItemStack displayItem) {
        this.systemName = systemName;
        this.displayName = displayName;
        this.description = description;
        this.displayItem = displayItem;
        this.enabled = true;
        this.disabledWorlds = new ArrayList<>();
        this.buyable = false;
    }

    public boolean hasPermission(Player player) {
        if (player.hasPermission("fancyperks.perk.*")) {
            return true;
        }

        return player.hasPermission("fancyperks.perk." + systemName);
    }

    public boolean grant(Player player) {
        if(disabledWorlds.contains(player.getWorld().getName())){
            return false;
        }

        perkManager.enablePerk(player, this);

        return true;
    }

    public boolean forceGrant(Player player) {
        perkManager.enablePerk(player, this);
        return true;
    }

    public void revoke(Player player) {
        perkManager.disablePerk(player, this);
    }

    public ItemStack getDisplayItem() {
        ItemStack item = displayItem.clone();
        final String primaryColor = MessageHelper.getPrimaryColor();
        String[] lines = description.split("<newline>");
        List<Component> loreLines = new ArrayList<>();
        for (String line : lines) {
            Component lineComponent = MessageHelper.removeDecoration(
                MiniMessage.miniMessage().deserialize("<gray>" + line + "</gray>"),
                TextDecoration.ITALIC
            );
            loreLines.add(lineComponent);
        }

        if (item.getType().name().startsWith("PLAYER_HEAD")) {
            item.editMeta(SkullMeta.class, skullMeta -> {
                skullMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize("<color:" + primaryColor + ">" + displayName + "</color>"), TextDecoration.ITALIC));
                skullMeta.lore(loreLines);
                skullMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "cancelClick");
            });
        } else {
            item.editMeta(itemMeta -> {
                
                itemMeta.displayName(MessageHelper.removeDecoration(MiniMessage.miniMessage().deserialize("<color:" + primaryColor + ">" + displayName + "</color>"), TextDecoration.ITALIC));
                itemMeta.lore(loreLines);
                itemMeta.getPersistentDataContainer().set(InventoryItemClick.ON_CLICK_KEY, PersistentDataType.STRING, "cancelClick");
            });            
        }
        
        return item;
    }

    public void setDisplayItem(String itemName) {
        if (itemName.startsWith("PLAYER_HEAD")) {
            displayItem = FancyPerks.getInstance().createCustomSkull(itemName);
        } else {
            Material displayMaterial = Material.valueOf(itemName.toUpperCase());
            displayItem = new ItemStack(displayMaterial);
        }
    }

    public String getSystemName() {
        return systemName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public void setDisabledWorlds(List<String> disabledWorlds) {
        this.disabledWorlds = disabledWorlds;
    }

    public boolean isBuyable() {
        return buyable;
    }

    public void setBuyable(boolean buyable) {
        this.buyable = buyable;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
