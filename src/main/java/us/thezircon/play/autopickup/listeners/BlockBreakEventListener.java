package us.thezircon.play.autopickup.listeners;

import me.crafter.mc.lockettepro.LocketteProAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.Mendable;
import us.thezircon.play.autopickup.utils.PickupObjective;
import us.thezircon.play.autopickup.utils.TallCrops;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BlockBreakEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        Location loc = e.getBlock().getLocation();
        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");
        boolean doBlacklist = PLUGIN.getBlacklistConf().getBoolean("doBlacklisted");

        List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("Blacklisted");

        if (AutoPickup.worldsBlacklist!=null && AutoPickup.worldsBlacklist.contains(loc.getWorld().getName())) {
            return;
        }

        if (doBlacklist) { // Checks if blacklist is enabled
            if (blacklist.contains(block.getType().toString())) { // Stops resets the loop skipping the item & not removing it
                return;
            }
        }

        if (!PLUGIN.autopickup_list.contains(player)) {
            return;
        }

        // Check if inv is full title
        if (PLUGIN.getConfig().contains("titlebar")) {
            boolean doFullInvMSGTitleBar = PLUGIN.getConfig().getBoolean("titlebar.doTitleBar");
            String titleLine1 = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("titlebar.line1"));
            String titleLine2 = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("titlebar.line2"));
            if (player.getInventory().firstEmpty() == -1) { // Checks for inventory space
                //Player has no space
                if (doFullInvMSGTitleBar) {
                    player.sendTitle(titleLine1, titleLine2, 1, 20, 1);
                }
            }
        }

        // LockettePro Patch
        if (AutoPickup.usingLocketteProByBrunyman) {
            if (LocketteProAPI.isLocked(block)) {
                return;
            }
        }

        // AOneBlock Patch
        new BukkitRunnable() {
            @Override
            public void run() {
                if (AutoPickup.usingBentoBox) {
                    BentoBox bb = BentoBox.getInstance();
                    if (BentoBox.getInstance().getAddonsManager().getAddonByName("AOneBlock").isPresent()) {
                        Island island = bb.getIslands().getIslandAt(loc).get();
                        if (island.getCenter().equals(block.getLocation())) {
                            for (Entity ent : loc.getWorld().getNearbyEntities(block.getLocation().add(0, 1, 0), 1, 1, 1)) {
                                if (ent instanceof Item) {
                                    if (player.getInventory().firstEmpty() == -1) { // Checks for inventory space
                                        //Player has no space
                                        if (doFullInvMSG) {
                                            player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());
                                        }
                                        return;
                                    } else {
                                        player.getInventory().addItem(((Item) ent).getItemStack());
                                        ent.remove();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskLater(PLUGIN, 1);

        // Mend Items & Give Player XP
        int xp = e.getExpToDrop();
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
        e.setExpToDrop(0); // Remove default XP

        ///////////////////////////////////// Custom items \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        String key = loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ()+";"+loc.getWorld();
        AutoPickup.customItemPatch.put(key, new PickupObjective(loc, player, Instant.now()));
        ///////////////////////////////////////////////////////////////////////////////////////


        // Deal with Containers
        if (block.getState() instanceof Container) {

            if (block.getState() instanceof ShulkerBox) {
                return;
            }

            // Upgradable Hoppers Patch
            if (block.getState() instanceof Hopper && AutoPickup.usingUpgradableHoppers) {
                NamespacedKey upgHoppers = new NamespacedKey(PLUGIN.getServer().getPluginManager().getPlugin("UpgradeableHoppers"), "upgradeablehoppers:o");
                Container con = (Container) block.getState();
                if (con.getPersistentDataContainer().getKeys().contains(upgHoppers)) {
                    return;
                }
            }

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

        TallCrops crops = PLUGIN.getCrops();
        ArrayList<Material> verticalReq = crops.getVerticalReq();
        ArrayList<Material> verticalReqDown = crops.getVerticalReqDown();

        if (verticalReq.contains(e.getBlock().getType()) || verticalReqDown.contains(e.getBlock().getType())) {
            //e.setDropItems(false);
            //vertBreak(player, e.getBlock().getLocation());
            Location tempLoc = loc.clone();
            while(true) {
                tempLoc.setY(tempLoc.getY() + 1.0D);
                String key2 = tempLoc.getBlockX()+";"+tempLoc.getBlockY()+";"+tempLoc.getBlockZ()+";"+tempLoc.getWorld();
                if (!(verticalReq.contains(tempLoc.getBlock().getType()))) {
                    //this.addLocation(tempLoc, e.getPlayer());
                    AutoPickup.customItemPatch.put(key2, new PickupObjective(tempLoc, player, Instant.now()));
                    break;
                }
                AutoPickup.customItemPatch.put(key2, new PickupObjective(tempLoc, player, Instant.now()));
                //this.addLocation(tempLoc, e.getPlayer());
            }
        }

    }

    private static int mend(ItemStack item, int xp) {

        if (item.containsEnchantment(Enchantment.MENDING)) {
            ItemMeta meta = item.getItemMeta();
            Mendable mend[] = Mendable.values();
            for (Mendable m : mend) {

                if (item.equals(null)) {
                    continue;
                }

                if (item.getType().toString().equals(m.toString())) {
                    Damageable damage = (Damageable) meta;
                    int min = Math.min(xp, damage.getDamage());
                    if ((damage.getDamage() - min==0) && damage.hasDamage()) {
                        fix(item);
                    } else {
                        damage.setDamage(damage.getDamage() - min);
                    }
                    xp -= min;
                    item.setItemMeta(meta);
                }
            }
        }
        return xp;
    }

    private static void fix(ItemStack item) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemMeta meta = item.getItemMeta();
                Damageable damage = (Damageable) meta;
                damage.setDamage(0);
                item.setItemMeta(meta);
            }
        }.runTaskLater(PLUGIN, 1);
    }

    private static int amt = 1;
    private static Material type;
    private static void vertBreak(Player player, Location loc) {
        TallCrops crops = PLUGIN.getCrops();
        ArrayList<Material> verticalReq = crops.getVerticalReq();
        ArrayList<Material> verticalReqDown = crops.getVerticalReqDown();

        type = loc.getBlock().getType();
        loc.getBlock().setType(Material.AIR);

        if (verticalReq.contains(loc.add(0,1,0).getBlock().getType())) {
            amt++;
            vertBreak(player, loc);
        } else if (verticalReqDown.contains(loc.subtract(0,2,0).getBlock().getType())) {
            amt++;
            vertBreak(player, loc);
        } else {

            if (player.getInventory().firstEmpty()!=-1) {
                player.getInventory().addItem(new ItemStack(type, amt));
            } else {
                player.getWorld().dropItemNaturally(loc, new ItemStack(type, amt));
            }
            type = null;
            amt = 1;
        }

    }

}
