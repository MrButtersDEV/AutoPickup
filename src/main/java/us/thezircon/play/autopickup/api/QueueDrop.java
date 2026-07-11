package us.thezircon.play.autopickup.api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;

import java.util.HashMap;

public class QueueDrop {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);
    private static final long COOLDOWN = 15000; // 15 seconds

    public static void queuePickup(ItemStack itemStack, Player player, Location location, boolean isDrops) {

        if (player == null) {
            dropItemSafely(location, itemStack);
            return;
        }

        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");

        if (AutoPickup.worldsBlacklist != null && AutoPickup.worldsBlacklist.contains(player.getWorld().getName())) {
            dropItemSafely(location, itemStack);
            return;
        }

        if (isDrops) {
            if (!PLUGIN.autopickup_list_mobs.contains(player)) {
                dropItemSafely(location, itemStack);
                return;
            }
        } else {
            if (!PLUGIN.autopickup_list.contains(player)) {
                dropItemSafely(location, itemStack);
                return;
            }
        }

        // Drops
        HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(itemStack);
        if (leftOver.keySet().size() > 0) {
            for (ItemStack item : leftOver.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
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
    }

    private static void dropItemSafely(Location location, ItemStack itemStack) {
        if (location != null && location.getWorld() != null) {
            location.getWorld().dropItemNaturally(location, itemStack);
        }
    }
}
