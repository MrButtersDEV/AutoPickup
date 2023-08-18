package us.thezircon.play.autopickup.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class EntityDropItemEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    private static HashMap<UUID, UUID> player_sheep_map = new HashMap<>();

    @EventHandler
    public void onSheer(PlayerShearEntityEvent e) {
        player_sheep_map.put(e.getEntity().getUniqueId(), e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDrop(EntityDropItemEvent e) {

        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");

        if (AutoPickup.worldsBlacklist!=null && AutoPickup.worldsBlacklist.contains(e.getEntity().getWorld().getName())) {
            return;
        }

        UUID sheep = e.getEntity().getUniqueId();
        if (player_sheep_map.containsKey(sheep)) {
            Player player = Bukkit.getPlayer(player_sheep_map.get(sheep));
            if (!PLUGIN.autopickup_list.contains(player)) {
                return;
            }

            // Drops
            ItemStack drops = e.getItemDrop().getItemStack();
            e.getItemDrop().remove();
            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(drops);
            if (leftOver.keySet().size()>0) {
                for (ItemStack item : leftOver.values()) {
                    player.getWorld().dropItemNaturally(e.getItemDrop().getLocation(), item);
                }
                if (doFullInvMSG) {
                    long secondsLeft;
                    long cooldown = 15000; // 15 sec
                    if (AutoPickup.lastInvFullNotification.containsKey(player.getUniqueId())) {
                        secondsLeft = (AutoPickup.lastInvFullNotification.get(player.getUniqueId())/1000)+ cooldown/1000 - (System.currentTimeMillis()/1000);
                    } else {
                        secondsLeft = 0;
                    }
                    if (secondsLeft<=0) {
                        player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());
                        AutoPickup.lastInvFullNotification.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                }
            }

            Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, new Runnable() {
                @Override
                public void run() {
                    if (!player.hasPermission("autopickup.pickup.mined")) {
                        PLUGIN.autopickup_list.remove(player);
                    }
                }
            });
        }
    }

}
