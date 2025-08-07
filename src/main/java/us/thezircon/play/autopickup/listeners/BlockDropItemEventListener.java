package us.thezircon.play.autopickup.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.AutoSmeltUtils;
import us.thezircon.play.autopickup.utils.HexFormat;

import java.util.HashMap;
import java.util.List;

public class BlockDropItemEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(BlockDropItemEvent e) {

        Player player = e.getPlayer();

        if (!PLUGIN.autopickup_list.contains(player)) return; // Player has auto enabled

        Block block = e.getBlock();
        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");
        boolean doBlacklist = PLUGIN.getBlacklistConf().getBoolean("doBlacklisted");
        boolean voidOnFullInv = false;
        boolean doSmelt = PLUGIN.auto_smelt_blocks.contains(player);

        if (PLUGIN.getConfig().contains("voidOnFullInv")) {
            voidOnFullInv = PLUGIN.getConfig().getBoolean("voidOnFullInv");
        }

        List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("Blacklisted");

        Location loc = block.getLocation();
        if (AutoPickup.worldsBlacklist!=null && AutoPickup.worldsBlacklist.contains(loc.getWorld().getName())) {
            return;
        }

//        if (block.getState() instanceof Container) {
//            return; // Containers are handled in block break event
//        }

        for (Item i : e.getItems()) {
//            i.setThrower(new UUID(0,0));
//                if (i==null || i.isDead() || !i.isValid()) {
//                    System.out.println("RAR " + i.getItemStack().getType() + " " + (i==null) + " "+ (i.isDead()) + " " + (!i.isValid()));
//                    continue; // TEST
//                }

            ItemStack drop = i.getItemStack();

            if (doBlacklist) { // Checks if blacklist is enabled
                if (blacklist.contains(drop.getType().toString())) { // Stops resets the loop skipping the item & not removing it
                    continue;
                }
            }

            if (doSmelt) {
                drop = AutoSmeltUtils.smelt(drop, player);
            }

            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(drop);
            if (leftOver.keySet().size()>0) {

                // Check if inv is full title
                if (PLUGIN.getConfig().contains("titlebar")) {
                    boolean doFullInvMSGTitleBar = PLUGIN.getConfig().getBoolean("titlebar.doTitleBar");
                    String titleLine1 = HexFormat.format(PLUGIN.getConfig().getString("titlebar.line1"));
                    String titleLine2 = HexFormat.format(PLUGIN.getConfig().getString("titlebar.line2"));
                    if (player.getInventory().firstEmpty() == -1) { // Checks for inventory space
                        //Player has no space
                        if (doFullInvMSGTitleBar) {
                            player.sendTitle(titleLine1, titleLine2, 1, 20, 1);
                        }
                    }
                }

                //Player has no space
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

                if (voidOnFullInv) {
                    i.remove();
                    return;
                }

                for (ItemStack item : leftOver.values()) {
                    player.getWorld().dropItemNaturally(loc, item);
                }
            }

//            if (doSmelt) {
//                player.getInventory().addItem(AutoSmelt.smelt(drop, player));
//            } else {
//                player.getInventory().addItem(drop);
//            }
            i.remove();
        }



    }
}
