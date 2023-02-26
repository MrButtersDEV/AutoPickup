package us.thezircon.play.autopickup.listeners;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static us.thezircon.play.autopickup.listeners.BlockBreakEventListener.mend;

public class MythicMobListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler
    public void onDeath(MythicMobDeathEvent e) {

        if (e.getKiller()==null) {
            return;
        }

        Player player;
        if (e.getKiller() instanceof Player) {
            player = (Player) e.getKiller();
        } else {
            return;
        }

        if (!PLUGIN.autopickup_list_mobs.contains(player)) return;

        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");

        Location loc = e.getKiller().getLocation();
        if (AutoPickup.worldsBlacklist!=null && AutoPickup.worldsBlacklist.contains(loc.getWorld().getName())) {
            return;
        }

        if (PLUGIN.getBlacklistConf().contains("BlacklistedEntities", true)) {
            boolean doBlacklist = PLUGIN.getBlacklistConf().getBoolean("doBlacklistedEntities");
            List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("BlacklistedEntities");

            if (doBlacklist && blacklist.contains(e.getMobType().toString())) {
                return;
            }
        }


        // Drops
        Iterator<ItemStack> iter = e.getDrops().iterator();
        while (iter.hasNext()) {
            ItemStack drops = iter.next();
            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(drops);
            iter.remove();
            if (leftOver.keySet().size()>0) {
                for (ItemStack item : leftOver.values()) {
                    player.getWorld().dropItemNaturally(loc, item);
                }
                if (doFullInvMSG) {
                    player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());
                }
            }
        }
        e.getDrops().clear();

    }

}
