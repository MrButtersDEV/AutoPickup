package us.thezircon.play.autopickup.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.FoliaRunnable;
import us.thezircon.play.autopickup.utils.SchedulerUtils;

import java.util.HashMap;

public class PlayerInteractEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler
    public void onClick(PlayerInteractEvent e) {

        if ((e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_AIR))) {
            return;
        }

        Player player = e.getPlayer();
        if (!PLUGIN.autopickup_list.contains(player)) {
            return;
        }

        Location loc = e.getClickedBlock().getLocation();

        if (AutoPickup.worldsBlacklist!=null && AutoPickup.worldsBlacklist.contains(loc.getWorld().getName())) {
            return;
        }

        if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(e.getClickedBlock().getType() == Material.SWEET_BERRY_BUSH) {
                SchedulerUtils.runTaskLater(loc, new FoliaRunnable() {
                    @Override
                    public void run() {
                        for (Entity entity : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
                            if (entity.getType().equals(EntityType.ITEM)) {
                                Item item = (Item) entity;
                                ItemStack items = item.getItemStack();
                                if (items.getType().equals(Material.SWEET_BERRIES)) {

                                    HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(items);
                                    item.remove();
                                    if (leftOver.keySet().size()>0) {
                                        for (ItemStack drops : leftOver.values()) {
                                            player.getWorld().dropItemNaturally(loc, drops);
                                        }
                                    }

//                                    if (player.getInventory().firstEmpty()!=-1) {
//                                        player.getInventory().addItem(items);
//                                        item.remove();
//                                    }
                                }

                            }
                        }
                    }
                }, 1);
            }
        }
    }

}
