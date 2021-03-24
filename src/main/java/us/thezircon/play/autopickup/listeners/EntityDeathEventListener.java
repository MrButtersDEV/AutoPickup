package us.thezircon.play.autopickup.listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;

import java.util.Iterator;
import java.util.List;

public class EntityDeathEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler
    public void onDeath(EntityDeathEvent e) {

        if (e.getEntity().getKiller()==null || !(e.getEntity().getKiller().getType().equals(EntityType.PLAYER))) {
            return;
        }

        Player player = e.getEntity().getKiller();
        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");

        Location loc = e.getEntity().getKiller().getLocation();
        if (AutoPickup.worldsBlacklist!=null && AutoPickup.worldsBlacklist.contains(loc.getWorld().getName())) {
            return;
        }

        if (PLUGIN.getBlacklistConf().contains("BlacklistedEntities", true)) {
            boolean doBlacklist = PLUGIN.getBlacklistConf().getBoolean("doBlacklistedEntities");
            List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("BlacklistedEntities");

            if (doBlacklist && blacklist.contains(e.getEntity().getType().toString())) {
                return;
            }
        }

        if (PLUGIN.autopickup_list_mobs.contains(player)) {

            // XP
            player.giveExp(e.getDroppedExp());
            e.setDroppedExp(0);

            // Drops
            Iterator<ItemStack> iter = e.getDrops().iterator();
            while (iter.hasNext()) {
                ItemStack drops = iter.next();
                if (player.getInventory().firstEmpty() != -1) { // has space
                    player.getInventory().addItem(drops);
                    iter.remove();
                } else { // inv full
                    if (doFullInvMSG) {
                        player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());
                    }
                    return;
                }
            }
            e.getDrops().clear();
        }
    }
}
