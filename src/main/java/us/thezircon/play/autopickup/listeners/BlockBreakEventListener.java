package us.thezircon.play.autopickup.listeners;

import org.bukkit.Location;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        Location loc = e.getBlock().getLocation();
        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");
        boolean doBlacklist = PLUGIN.getBlacklistConf().getBoolean("doBlacklisted");
        List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("Blacklisted");

        if (block.getState() instanceof ShulkerBox) {
            return;
        }

        if (block.getState() instanceof Container) {

            e.setDropItems(false); // Cancel drops

            if (((Container) block.getState()).getInventory() instanceof DoubleChestInventory) {
                Chest chest = (Chest) block.getState();
                org.bukkit.block.data.type.Chest chestType = (org.bukkit.block.data.type.Chest) chest.getBlockData();
                ArrayList<ItemStack> chestDrops = new ArrayList<>();
                if (chestType.getType().equals(org.bukkit.block.data.type.Chest.Type.RIGHT)) { // Right
                    for (int x=0; x<27; x++) {
                        chestDrops.add(chest.getInventory().getItem(x));
                        chest.getInventory().setItem(x, null);
                    }
                } else if (chestType.getType().equals(org.bukkit.block.data.type.Chest.Type.LEFT)) {
                    for (int x=27; x<54; x++) {
                        chestDrops.add(chest.getInventory().getItem(x));
                        chest.getInventory().setItem(x, null);
                    }
                }

                for (ItemStack items : chestDrops) {
                    if (items!=null) {
                        if (player.getInventory().firstEmpty()!=-1) {
                            player.getInventory().addItem(items);
                        } else {
                            player.getWorld().dropItemNaturally(loc, items);
                        }
                    }
                }

            } else {
                for (ItemStack items : ((Container) e.getBlock().getState()).getInventory().getContents()) {

                    if (items!=null) {
                        if (player.getInventory().firstEmpty()!=-1) {
                            player.getInventory().addItem(items);
                        } else {
                            player.getWorld().dropItemNaturally(loc, items);
                        }
                    }

                    ((Container) e.getBlock().getState()).getInventory().clear();
                }
            }

            ItemStack drop = new ItemStack(e.getBlock().getType());
            if (player.getInventory().firstEmpty()!=-1) {
                player.getInventory().addItem(drop);
            } else {
                player.getWorld().dropItemNaturally(loc, drop);
            }

        }
    }

}
