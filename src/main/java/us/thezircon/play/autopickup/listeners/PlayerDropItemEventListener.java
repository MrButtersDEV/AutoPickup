package us.thezircon.play.autopickup.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import us.thezircon.play.autopickup.AutoPickup;

public class PlayerDropItemEventListener implements Listener {

    // Adds items dropped by players so they wont be sent back to the inventory.
    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        AutoPickup.droppedItems.add(e.getItemDrop().getUniqueId());
    }

}
