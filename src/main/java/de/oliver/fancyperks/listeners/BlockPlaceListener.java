package de.oliver.fancyperks.listeners;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockPlaceListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Material placedType = event.getBlockPlaced().getType();

        if (placedType == Material.SPAWNER) {

            NamespacedKey SPAWNER_TYPE_KEY = new NamespacedKey("fancyperks", "spawner_type");
            
            ItemMeta meta = event.getItemInHand().getItemMeta();
            if (meta == null) {
                return;
            }

            PersistentDataContainer data = meta.getPersistentDataContainer();
            if (!data.has(SPAWNER_TYPE_KEY, PersistentDataType.STRING)) {
                return;
            }

            String entityTypeName = data.get(SPAWNER_TYPE_KEY, PersistentDataType.STRING);
            EntityType entityType;
            try {
                entityType = EntityType.valueOf(entityTypeName);
            } catch (IllegalArgumentException e) {
                return;
            }

            Block block = event.getBlockPlaced();
            BlockState state = block.getState();
            if (state instanceof CreatureSpawner spawner) {
                spawner.setSpawnedType(entityType);
                spawner.update(true);
            }
        }
        
    }

}
