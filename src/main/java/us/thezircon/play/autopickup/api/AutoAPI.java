package us.thezircon.play.autopickup.api;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.PickupObjective;

import java.time.Instant;
import java.util.UUID;

public class AutoAPI {

    /**
     * @deprecated Experimental API; This api has not been tested & officially supported / may be removed.
     * @param itemEntity The Item Entity that should be ignored by AutoPickup
     */
    public static void addIgnoredDrop(Item itemEntity) {
        UUID uuid = itemEntity.getUniqueId();
        addIgnoredDrop(uuid);
    }

    /**
     * @deprecated Experimental API; This api has not been tested & officially supported / may be removed.
     * @param itemEntityUUID UUID of an item entity that should be ignored by AutoPickup
     */
    public static void addIgnoredDrop(UUID itemEntityUUID) {
        AutoPickup.droppedItems.add(itemEntityUUID);
    }

    /**
     * @param location The location that should be watched for possible custom drops.
     * @param player The player who broke said block.
     * @deprecated Experimental API; This api has not been tested & officially supported / may be removed.
     *
     * Blocks broken by players are already tagged for custom drops; this should be used for adjacent blocks that should be watched for additional custom drops.
     */
    public static void tagCustomDropLocation(Location location, Player player) {
        String key = location.getBlockX()+";"+location.getBlockY()+";"+location.getBlockZ()+";"+location.getWorld();
        AutoPickup.customItemPatch.put(key, new PickupObjective(location, player, Instant.now()));
    }

}
