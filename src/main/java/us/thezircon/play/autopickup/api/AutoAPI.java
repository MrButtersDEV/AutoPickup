package us.thezircon.play.autopickup.api;

import org.bukkit.entity.Item;
import us.thezircon.play.autopickup.AutoPickup;

import java.util.UUID;

public class AutoAPI {

    public static void addIgnoredDrop(Item itemEntity) {
        UUID uuid = itemEntity.getUniqueId();
        addIgnoredDrop(uuid);
    }

    public static void addIgnoredDrop(UUID itemEntityUUID) {
        AutoPickup.droppedItems.add(itemEntityUUID);
    }

}
