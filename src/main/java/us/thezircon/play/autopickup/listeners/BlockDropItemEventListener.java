package us.thezircon.play.autopickup.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;

import java.util.List;

public class BlockDropItemEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler
    public void onDrop(BlockDropItemEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlock();
        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");
        boolean doBlacklist = PLUGIN.getBlacklistConf().getBoolean("doBlacklisted");
        List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("Blacklisted");

        if (block.getState() instanceof Container) {
            return; // Containers are handled in block break event
        }

        if (PLUGIN.autopickup_list.contains(player)) { // Player has auto enabled
            for (Entity en : e.getItems()) {

                if (player.getInventory().firstEmpty() == -1) { // Checks for inventory space
                    //Player has no space
                    if (doFullInvMSG) {player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());}
                    return;
                }

                Item i = (Item) en;
                ItemStack drop = i.getItemStack();

                if (doBlacklist) { // Checks if blacklist is enabled
                    if (blacklist.contains(drop.getType().toString())) { // Stops resets the loop skipping the item & not removing it
                        continue;
                    }
                }

                player.getInventory().addItem(drop);
                i.remove();
            }

        }

    }
}
