package us.thezircon.play.autopickup.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.SchedulerUtils;

import java.util.HashMap;
import java.util.List;

public class PlayerFishEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        Player player = e.getPlayer();
        if(!(e.getState().equals(PlayerFishEvent.State.CAUGHT_FISH))) return;
        if(!(e.getCaught() instanceof Item)) return;

        if (!PLUGIN.autopickup_list_fishing.contains(player)) return;

        SchedulerUtils.runTaskAsynchronously(new Runnable() {
            @Override
            public void run() {
                boolean requirePermsAUTO = PLUGIN.getConfig().getBoolean("requirePerms.autopickup");
                if (!requirePermsAUTO) {
                    return;
                }
                if (!player.hasPermission("autopickup.pickup.fishing") && !player.hasPermission("autopickup.pickup.fishing.autoenabled")) {
                    PLUGIN.autopickup_list_fishing.remove(player);
                }
            }
        });

        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");

        Location loc = e.getPlayer().getLocation();
        if (AutoPickup.worldsBlacklist!=null && AutoPickup.worldsBlacklist.contains(loc.getWorld().getName())) {
            return;
        }

        SchedulerUtils.runTask(e.getPlayer().getLocation(), () -> {
            Item caught = (Item) e.getCaught();

            if (PLUGIN.getBlacklistConf().contains("BlacklistedFishing", true)) {
                boolean doBlacklist = PLUGIN.getBlacklistConf().getBoolean("BlacklistedFishing");
                List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("BlacklistedFishing");

                if (doBlacklist && blacklist.contains(caught.getItemStack().getType().toString())) {
                    return;
                }
            }

            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(caught.getItemStack());
            caught.remove();
            if (!leftOver.isEmpty()) {
                for (ItemStack item : leftOver.values()) {
                    player.getWorld().dropItemNaturally(loc, item);
                }
                if (doFullInvMSG) {
                    long secondsLeft;
                    long cooldown = 15000; // 15 sec
                    if (AutoPickup.lastInvFullNotification.containsKey(player.getUniqueId())) {
                        secondsLeft = (AutoPickup.lastInvFullNotification.get(player.getUniqueId()) / 1000) + cooldown / 1000 - (System.currentTimeMillis() / 1000);
                    } else {
                        secondsLeft = 0;
                    }
                    if (secondsLeft <= 0) {
                        player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());
                        AutoPickup.lastInvFullNotification.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                }
            }
        });
    }
}