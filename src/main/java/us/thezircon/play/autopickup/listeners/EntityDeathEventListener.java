package us.thezircon.play.autopickup.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static us.thezircon.play.autopickup.listeners.BlockBreakEventListener.mend;

public class EntityDeathEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    //@EventHandler potential fix for WarnD skyblock
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeath(EntityDeathEvent e) {

        if (e.getEntity().getKiller()==null || !(e.getEntity().getKiller().getType().equals(EntityType.PLAYER))) {
            return;
        }

        Player player = e.getEntity().getKiller();

        if (!PLUGIN.autopickup_list_mobs.contains(player)) return;

        Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, new Runnable() {
            @Override
            public void run() {
                boolean requirePermsAUTO = PLUGIN.getConfig().getBoolean("requirePerms.autopickup");
                if (!requirePermsAUTO) {
                    return;
                }
                if (!player.hasPermission("autopickup.pickup.entities")) {
                    PLUGIN.autopickup_list_mobs.remove(player);
                }
            }
        });

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



        // Mend Items & Give Player XP
        int xp = e.getDroppedExp();
        player.giveExp(xp); // Give player XP

        // Mend
        mend(player.getInventory().getItemInMainHand(), xp);
        mend(player.getInventory().getItemInOffHand(), xp);
        ItemStack armor[] = player.getInventory().getArmorContents();
        for (ItemStack i : armor)
        {
            try {
                mend(i, xp);
            } catch (NullPointerException ignored) {}
        }
        e.setDroppedExp(0); // Remove default XP

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


//            if (player.getInventory().firstEmpty() != -1) { // has space
//                player.getInventory().addItem(drops);
//                iter.remove();
//            } else { // inv full
//                if (doFullInvMSG) {
//                    player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());
//                }
//                return;
//            }
        }
        e.getDrops().clear();

    }
}
