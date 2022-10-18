package us.thezircon.play.autopickup.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.AutoSmelt;

import java.util.HashMap;
import java.util.List;

public class BlockDropItemEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrop(BlockDropItemEvent e) {
        Player player = e.getPlayer();
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

        if (block.getState() instanceof Container) {
            return; // Containers are handled in block break event
        }

        if (PLUGIN.autopickup_list.contains(player)) { // Player has auto enabled
            for (Entity en : e.getItems()) {

                if (en==null || en.isDead() || !en.isValid()) {
                    continue; // TEST
                }

                Item i = (Item) en;
                ItemStack drop = i.getItemStack();

                if (player.getInventory().firstEmpty() == -1) { // Checks for inventory space
                    //Player has no space
                    if (doFullInvMSG) {player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());}

                    if (voidOnFullInv) {
                        i.remove();
                    }

                    return;
                }

                if (doBlacklist) { // Checks if blacklist is enabled
                    if (blacklist.contains(drop.getType().toString())) { // Stops resets the loop skipping the item & not removing it
                        continue;
                    }
                }

                if (doSmelt) {
                    drop = AutoSmelt.smelt(drop, player);
                }

                HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(drop);
                if (leftOver.keySet().size()>0) {
                    for (ItemStack item : leftOver.values()) {
                        player.getWorld().dropItemNaturally(loc, item);
                    }
                }

//                if (doSmelt) {
//                    player.getInventory().addItem(AutoSmelt.smelt(drop, player));
//                } else {
//                    player.getInventory().addItem(drop);
//                }
                i.remove();
            }

        }

    }
}
