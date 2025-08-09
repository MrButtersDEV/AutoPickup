package us.thezircon.play.autopickup.listeners;


import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.PickupObjective;
import us.thezircon.play.autopickup.utils.TallCrops;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import us.thezircon.play.autopickup.utils.InventoryUtils;

public class BlockBreakEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {

        Player player = e.getPlayer();

        if (!PLUGIN.autopickup_list.contains(player)) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(PLUGIN, new Runnable() {
            @Override
            public void run() {
                boolean requirePermsAUTO = PLUGIN.getConfig().getBoolean("requirePerms.autopickup");
                if (!requirePermsAUTO) {
                    return;
                }
                if (!player.hasPermission("autopickup.pickup.mined") && !player.hasPermission("autopickup.pickup.mined.autoenabled")) {
                    PLUGIN.autopickup_list.remove(player);
                }
                if (!player.hasPermission("autopickup.pickup.mined.autosmelt") && !player.hasPermission("autopickup.pickup.mined.autosmelt.autoenabled")) {
                    PLUGIN.auto_smelt_blocks.remove(player);
                }
            }
        });

        Block block = e.getBlock();
        Location loc = e.getBlock().getLocation();
        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");
        boolean doBlacklist = PLUGIN.getBlacklistConf().getBoolean("doBlacklisted");

        List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("Blacklisted");

        if (AutoPickup.worldsBlacklist != null && AutoPickup.worldsBlacklist.contains(loc.getWorld().getName())) {
            return;
        }

        if (doBlacklist) { // Checks if blacklist is enabled
            if (blacklist.contains(block.getType().toString())) { // Stops resets the loop skipping the item & not removing it
                return;
            }
        }

        // QuickShop chest patch
        if (AutoPickup.usingQuickShop) {
            if (e.toString().startsWith("org.maxgamer.quickshop.util.PermissionChecker")) {
                return;
            }
        }

        // LockettePro Patch
//        if (AutoPickup.usingLocketteProByBrunyman) {
//            if (LocketteProAPI.isLocked(block)) {
//                return;
//            }
//        }

        // AOneBlock Patch
        new BukkitRunnable() {
            @Override
            public void run() {
                if (AutoPickup.usingBentoBox) {
                    BentoBox bb = BentoBox.getInstance();
                    if (BentoBox.getInstance().getAddonsManager().getAddonByName("AOneBlock").isPresent()) {

                        if (!bb.getIslands().getIslandAt(loc).isPresent()) {
                            return;
                        }

                        Island island = bb.getIslands().getIslandAt(loc).get();
                        if (island.getCenter().equals(block.getLocation())) {
                            oneBlockAutoPickup(loc, block, player, doFullInvMSG);
                        }
                    }
                }
            }
        }.runTaskLater(PLUGIN, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!AutoPickup.usingSSB2OneBlock) return;

                com.bgsoftware.superiorskyblock.api.island.Island island = SuperiorSkyblockAPI.getIslandAt(player.getLocation());

                if(island == null) return;

                Location oneBlockLocation = island.getCenter(SuperiorSkyblockAPI.getSettings().getWorlds().getDefaultWorldDimension()).subtract(0.5F,1.0F,0.5F);

                if (!oneBlockLocation.equals(block.getLocation())) return;

                oneBlockAutoPickup(loc, block, player, doFullInvMSG);
            }
        }.runTaskLater(PLUGIN, 1);

        // Mend Items & Give Player XP
        boolean usingSilkSpawner = PLUGIN.getConfig().getBoolean("usingSilkSpawnerPlugin");
        if (!usingSilkSpawner || !(block.getType() == Material.SPAWNER)) {
            int xp = e.getExpToDrop();

            InventoryUtils.applyMending(player, xp);

            e.setExpToDrop(0); // Remove default XP
        }

        // Deal with Containers
        if (block.getState() instanceof Container) {

            if (block.getState() instanceof ShulkerBox) {
                return;
            }

            // Upgradable Hoppers Patch
            if (block.getState() instanceof Hopper && AutoPickup.usingUpgradableHoppers) {
                NamespacedKey upgHoppers = new NamespacedKey(PLUGIN.getServer().getPluginManager().getPlugin("UpgradeableHoppers"), "o");
                Container con = (Container) block.getState();
                if (con.getPersistentDataContainer().getKeys().contains(upgHoppers)) {
                    return;
                }
            }

            // Peaceful Farms - PFHoppers Patch
            /*if (block.getState() instanceof Hopper && AutoPickup.usingPFHoppers) {
                Hopper hopper = (Hopper) block.getState();
                try {
                    if (hopper.getCustomName().contains("PF Hopper"))
                        return;
                } catch (NullPointerException ignored) {}
            }*/

            // Peaceful Farms - PFMoreHoppers Patch
            /*if (block.getState() instanceof Hopper && AutoPickup.usingPFMoreHoppers) {
                NamespacedKey morePFHoppers = new NamespacedKey(PLUGIN.getServer().getPluginManager().getPlugin("PFMoreHoppers"), "PFHopper-Variant");
                Container con = (Container) block.getState();
                if (con.getPersistentDataContainer().getKeys().contains(morePFHoppers)) {
                    return;
                }
            }*/

            // WildChests patch
            if (AutoPickup.usingWildChests) {
                if (block.getType() == Material.CHEST) {
                    return;
                }
            }

            //e.setDropItems(false); // Cancel drops

            if (((Container) block.getState()).getInventory() instanceof DoubleChestInventory) {
                Chest chest = (Chest) block.getState();
                org.bukkit.block.data.type.Chest chestType = (org.bukkit.block.data.type.Chest) chest.getBlockData();
                ArrayList<ItemStack> chestDrops = new ArrayList<>();
                if (chestType.getType().equals(org.bukkit.block.data.type.Chest.Type.RIGHT)) { // Right
                    for (int x = 0; x < 27; x++) {
                        chestDrops.add(chest.getInventory().getItem(x));
                        chest.getInventory().setItem(x, null);
                    }
                } else if (chestType.getType().equals(org.bukkit.block.data.type.Chest.Type.LEFT)) {
                    for (int x = 27; x < 54; x++) {
                        chestDrops.add(chest.getInventory().getItem(x));
                        chest.getInventory().setItem(x, null);
                    }
                }

                for (ItemStack items : chestDrops) {
                    if (items != null) {
//                        if (player.getInventory().firstEmpty()!=-1) {
//                            player.getInventory().add----Item(items);
//                        } else {
//                            player.getWorld().dropItemNaturally(loc, items);
//                        }
                        HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(items);
                        if (leftOver.keySet().size() > 0) {
                            for (ItemStack item : leftOver.values()) {
                                player.getWorld().dropItemNaturally(loc, item);
                            }
                        }
                    }
                }

            } else {
                for (ItemStack items : ((Container) e.getBlock().getState()).getInventory().getContents()) {

                    if (items != null) {
                        HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(items);
                        if (leftOver.keySet().size() > 0) {
                            for (ItemStack item : leftOver.values()) {
                                player.getWorld().dropItemNaturally(loc, item);
                            }
                        }
                    }

                    ((Container) e.getBlock().getState()).getInventory().clear();
                }
            }

            // EpicFurnaces patch
            if (AutoPickup.usingEpicFurnaces) {
                if (block.getType() == Material.FURNACE || block.getType() == Material.BLAST_FURNACE || block.getType() == Material.SMOKER) {
                    return;
                }
            }

            return;
        }

        ///////////////////////////////////// Custom items \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        String key = loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";" + loc.getWorld();
        AutoPickup.customItemPatch.put(key, new PickupObjective(loc, player, Instant.now()));
        ///////////////////////////////////////////////////////////////////////////////////////

        TallCrops crops = PLUGIN.getCrops();
        ArrayList<Material> verticalReq = crops.getVerticalReq();
        ArrayList<Material> verticalReqDown = crops.getVerticalReqDown();

        // TEST START
        Location l = e.getBlock().getLocation();
        //Deal with kelp

        if (e.getBlock().getType() == Material.KELP_PLANT || e.getBlock().getType().equals(Material.KELP) || e.getBlock().getType() == Material.BAMBOO) {
            Location lnew = l.clone();
            do {
                lnew.setY(lnew.getY() + 1);
                if (lnew.getBlock().getType() == Material.KELP_PLANT || lnew.getBlock().getType().equals(Material.KELP) || lnew.getBlock().getType() == Material.BAMBOO) {
                    addLocation(lnew, e.getPlayer());
                } else {
                    break;
                }
            } while (true);
            addLocation(lnew, e.getPlayer());
        }
        //deal with cactus
        if (e.getBlock().getType() == Material.CACTUS || e.getBlock().getType() == Material.SAND) {
            Location lnew = l.clone();
            do {
                lnew.setY(lnew.getY() + 1);
                if (lnew.getBlock().getType() == Material.CACTUS) {
                    addLocation(lnew, e.getPlayer());
                } else {
                    break;
                }
            } while (true);
            addLocation(lnew, e.getPlayer());
        }

        //deal with sugarcane
        if (e.getBlock().getType() == Material.SUGAR_CANE || e.getBlock().getType() == Material.GRASS_BLOCK || e.getBlock().getType() == Material.SAND) {
            Location lnew = l.clone();
            do {
                lnew.setY(lnew.getY() + 1);
                if (lnew.getBlock().getType() == Material.SUGAR_CANE) {
                    addLocation(lnew, e.getPlayer());
                } else {
                    break;
                }
            } while (true);
            addLocation(lnew, e.getPlayer());
        }

        if (
                Bukkit.getVersion().contains("1.16") ||
                        Bukkit.getVersion().contains("1.17") ||
                        Bukkit.getVersion().contains("1.18") ||
                        Bukkit.getVersion().contains("1.19") ||
                        Bukkit.getVersion().contains("1.20") ||
                        Bukkit.getVersion().contains("1.21")
        ) {
            //deal with weeping vines
            if (e.getBlock().getType() == Material.WEEPING_VINES_PLANT || e.getBlock().getRelative(BlockFace.DOWN).getType() == Material.WEEPING_VINES_PLANT) {
                Location lnew = l.clone();
                do {
                    lnew.setY(lnew.getY() - 1);
                    if (lnew.getBlock().getType() == Material.WEEPING_VINES_PLANT) {
                        addLocation(lnew, e.getPlayer());
                    } else {
                        break;
                    }
                } while (true);
                addLocation(lnew, e.getPlayer());
            } else if (e.getBlock().getType() == Material.WEEPING_VINES || e.getBlock().getRelative(BlockFace.DOWN).getType() == Material.WEEPING_VINES) {
                Location lnew = l.clone();
                do {
                    lnew.setY(lnew.getY() - 1);
                    if (lnew.getBlock().getType() == Material.WEEPING_VINES) {
                        addLocation(lnew, e.getPlayer());
                    } else {
                        break;
                    }
                } while (true);
                addLocation(lnew, e.getPlayer());
            }

            //deal with twisting vines
            if (e.getBlock().getType() == Material.TWISTING_VINES_PLANT || e.getBlock().getRelative(BlockFace.UP).getType() == Material.TWISTING_VINES_PLANT) {
                Location lnew = l.clone();
                do {
                    lnew.setY(lnew.getY() + 1);
                    if (lnew.getBlock().getType() == Material.TWISTING_VINES_PLANT) {
                        addLocation(lnew, e.getPlayer());
                    } else {
                        break;
                    }
                } while (true);
                addLocation(lnew, e.getPlayer());
            } else if (e.getBlock().getType() == Material.TWISTING_VINES || e.getBlock().getRelative(BlockFace.UP).getType() == Material.TWISTING_VINES) {
                Location lnew = l.clone();
                do {
                    lnew.setY(lnew.getY() + 1);
                    if (lnew.getBlock().getType() == Material.TWISTING_VINES) {
                        addLocation(lnew, e.getPlayer());
                    } else {
                        break;
                    }
                } while (true);
                addLocation(lnew, e.getPlayer());
            }

            if (!Bukkit.getVersion().contains("1.16")) {
                //deal with glow berries
                if (e.getBlock().getType() == Material.CAVE_VINES_PLANT || e.getBlock().getRelative(BlockFace.DOWN).getType() == Material.CAVE_VINES_PLANT) {
                    Location lnew = l.clone();
                    do {
                        lnew.setY(lnew.getY() - 1);
                        if (lnew.getBlock().getType() == Material.CAVE_VINES_PLANT) {
                            addLocation(lnew, e.getPlayer());
                        } else {
                            break;
                        }
                    } while (true);
                    addLocation(lnew, e.getPlayer());
                } else if (e.getBlock().getType() == Material.CAVE_VINES || e.getBlock().getRelative(BlockFace.DOWN).getType() == Material.CAVE_VINES) {
                    Location lnew = l.clone();
                    do {
                        lnew.setY(lnew.getY() - 1);
                        if (lnew.getBlock().getType() == Material.CAVE_VINES) {
                            addLocation(lnew, e.getPlayer());
                        } else {
                            break;
                        }
                    } while (true);
                    addLocation(lnew, e.getPlayer());
                }

                //deal with dripleafs
                if (e.getBlock().getType() == Material.BIG_DRIPLEAF_STEM || e.getBlock().getRelative(BlockFace.UP).getType() == Material.BIG_DRIPLEAF_STEM) {
                    Location lnew = l.clone();
                    double y = lnew.getY();
                    do {
                        lnew.setY(lnew.getY() + 1);
                        if (lnew.getBlock().getType() == Material.BIG_DRIPLEAF_STEM) {
                            addLocation(lnew, e.getPlayer());
                        } else if (lnew.getBlock().getType() == Material.BIG_DRIPLEAF) {
                            addLocation(lnew, e.getPlayer());
                        } else {
                            y--;
                            lnew.setY(y);
                            if (lnew.getBlock().getType() == Material.BIG_DRIPLEAF_STEM) {
                                addLocation(lnew, e.getPlayer());
                            } else {
                                break;
                            }
                        }
                    } while (true);
                    addLocation(lnew, e.getPlayer());
                } else if (e.getBlock().getType() == Material.BIG_DRIPLEAF || e.getBlock().getRelative(BlockFace.UP).getType() == Material.BIG_DRIPLEAF) {
                    Location lnew = l.clone();
                    do {
                        lnew.setY(lnew.getY() - 1);
                        if (lnew.getBlock().getType() == Material.BIG_DRIPLEAF) {
                            addLocation(lnew, e.getPlayer());
                        } else {
                            break;
                        }
                    } while (true);
                    addLocation(lnew, e.getPlayer());
                }
            }
        }

        // TEST END

        if (verticalReq.contains(e.getBlock().getType()) || verticalReqDown.contains(e.getBlock().getType())) {
            e.setDropItems(false);
            vertBreak(player, e.getBlock().getLocation());
        }

    }

    private void oneBlockAutoPickup(Location loc, Block block, Player player, boolean doFullInvMSG) {
        for (Entity ent : loc.getWorld().getNearbyEntities(block.getLocation().add(0, 1, 0), 1, 1, 1)) {
            if (ent instanceof Item) {
                HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(((Item) ent).getItemStack());
                ent.remove();
                if (!leftOver.isEmpty()) {
                    InventoryUtils.handleItemOverflow(loc, player, doFullInvMSG, leftOver, PLUGIN);
                }
            }
        }
    }

    private static int amt = 1;
    private static Material type;

    private static void vertBreak(Player player, Location loc) {
        TallCrops crops = PLUGIN.getCrops();
        ArrayList<Material> verticalReq = crops.getVerticalReq();
        ArrayList<Material> verticalReqDown = crops.getVerticalReqDown();

        //type = loc.getBlock().getType();
        type = TallCrops.checkAltType(loc.getBlock().getType());
        loc.getBlock().setType(Material.AIR, true);

        //Bukkit.getPluginManager().callEvent(new BlockBreakEvent(loc.getBlock().getWorld().getBlockAt(loc), player)); //////

        //System.out.println(loc.clone().add(0,1,0).getBlock().getType() + " | " + loc.toString());
        if (verticalReq.contains(loc.add(0, 1, 0).getBlock().getType())) {
            amt++;
            vertBreak(player, loc);
        } else if (verticalReqDown.contains(loc.subtract(0, 2, 0).getBlock().getType())) {
            amt++;
            vertBreak(player, loc);
        } else {
            ItemStack drop = new ItemStack(type, amt);
            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(drop);
            if (leftOver.keySet().size() > 0) {
                for (ItemStack item : leftOver.values()) {
                    player.getWorld().dropItemNaturally(loc, item);
                }
            }
//            if (player.getInventory().firstEmpty()!=-1) {
//                player.getInventory().add---Item(new ItemStack(type, amt));
//            } else {
//                player.getWorld().dropItemNaturally(loc, new ItemStack(type, amt));
//            }
            type = null;
            amt = 1;
            ///////////////////////////////////// Custom items \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
            loc.add(0, 1, 0);
            String key = loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";" + loc.getWorld();
            AutoPickup.customItemPatch.put(key, new PickupObjective(loc, player, Instant.now()));
            ///////////////////////////////////////////////////////////////////////////////////////
        }

    }

    private void addLocation(Location loc, Player player) {
        String key = loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";" + loc.getWorld();
        AutoPickup.customItemPatch.put(key, new PickupObjective(loc, player, Instant.now()));
    }

}
