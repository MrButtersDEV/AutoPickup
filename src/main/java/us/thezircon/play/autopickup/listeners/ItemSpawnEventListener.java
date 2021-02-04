package us.thezircon.play.autopickup.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.AutoSmelt;
import us.thezircon.play.autopickup.utils.PickupObjective;

public class ItemSpawnEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    ///////////////////////////////////// Custom items \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    @EventHandler
    public void onSpawn(ItemSpawnEvent e) {
        Location loc = e.getLocation();
        String key = loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ()+";"+loc.getWorld();
        if (AutoPickup.customItemPatch.containsKey(key)) {
            PickupObjective po = AutoPickup.customItemPatch.get(key);
            ItemStack item = e.getEntity().getItemStack();
            Player player = po.getPlayer();
            boolean doSmelt = PLUGIN.auto_smelt_blocks.contains(player);
            if (player.getInventory().firstEmpty()!=-1) {
                e.getEntity().remove();
                if (doSmelt) {
                    player.getInventory().addItem(AutoSmelt.smelt(item, player));
                } else {
                    player.getInventory().addItem(item);
                }
            }
        }
    }

}
