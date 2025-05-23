package de.oliver.fancyperks.listeners;

import de.oliver.fancyperks.FancyPerks;
import de.oliver.fancyperks.PerkManager;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;
import de.oliver.fancyperks.perks.impl.DoubleDropsPerk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class EntityDeathListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        Player p = event.getEntity().getKiller();

        PerkManager perkManager = FancyPerks.getInstance().getPerkManager();
        List<Perk> perks = perkManager.getEnabledPerks(p);

        int expMultiplier = 1;

        boolean hasDoubleExp = perks.contains(PerkRegistry.DOUBLE_EXP);
        if (hasDoubleExp) {
            expMultiplier = 2;
        }

        boolean hasTripleExp = perks.contains(PerkRegistry.TRIPLE_EXP);
        if (hasTripleExp) {
            expMultiplier = 3;
        }

        event.setDroppedExp(event.getDroppedExp() * expMultiplier);

        boolean hasDoubleDrops = perks.contains(PerkRegistry.DOUBLE_DROPS);
        if (hasDoubleDrops && !((DoubleDropsPerk) PerkRegistry.DOUBLE_DROPS).getBlacklist().contains(event.getEntityType())) {
            event.getDrops().forEach(itemStack -> itemStack.setAmount(itemStack.getAmount() * 2));
        }

        boolean hasTelekinesis = perks.contains(PerkRegistry.TELEKINESIS);
        if (hasTelekinesis) {
            for (ItemStack drop : event.getDrops()) {
                HashMap<Integer, ItemStack> couldNotFit = p.getInventory().addItem(drop);
                for (ItemStack item : couldNotFit.values()) {
                    event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), item);
                }
            }
            event.getDrops().clear();
        }
    }

}
