package us.thezircon.play.autopickup.listeners;

import me.crafter.mc.lockettepro.LocketteProAPI;
import org.bukkit.Bukkit;
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
import us.thezircon.play.autopickup.utils.HexFormat;
import us.thezircon.play.autopickup.utils.Mendable;
import us.thezircon.play.autopickup.utils.PickupObjective;
import us.thezircon.play.autopickup.utils.TallCrops;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockBreakEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {

        Player player = e.getPlayer();

        if (!PLUGIN.autopickup_list.contains(player)) {
            return;
        }

        Block block = e.getBlock();
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

        // QuickShop chest patch
        if (AutoPickup.usingQuickShop) {
            if(e.toString().startsWith("org.maxgamer.quickshop.util.PermissionChecker")) {
                return;
            }
        }

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

                        if (!bb.getIslands().getIslandAt(loc).isPresent()) { return; }

                        Island island = bb.getIslands().getIslandAt(loc).get();
                        if (island.getCenter().equals(block.getLocation())) {
                            for (Entity ent : loc.getWorld().getNearbyEntities(block.getLocation().add(0, 1, 0), 1, 1, 1)) {
                                if (ent instanceof Item) {

                                    HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(((Item) ent).getItemStack());
                                    ent.remove();
                                    if (leftOver.keySet().size()>0) {
                                        for (ItemStack item : leftOver.values()) {
                                            player.getWorld().dropItemNaturally(loc, item);
                                        }
                                        if (doFullInvMSG) {
                                            player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());
                                        }
                                    }

//                                    if (player.getInventory().firstEmpty() == -1) { // Checks for inventory space
//                                        //Player has no space
//                                        if (doFullInvMSG) {
//                                            player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());
//                                        }
//                                        return;
//                                    } else {
//                                        player.getInventory().addItem(((Item) ent).getItemStack());
//                                        ent.remove();
//                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskLater(PLUGIN, 1);

        ///////////////////////////////////// Custom items \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        String key = loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ()+";"+loc.getWorld();
        AutoPickup.customItemPatch.put(key, new PickupObjective(loc, player, Instant.now()));
        ///////////////////////////////////////////////////////////////////////////////////////

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
            if (block.getState() instanceof Hopper && AutoPickup.usingPFHoppers) {
                Hopper hopper = (Hopper) block.getState();
                try {
                    if (hopper.getCustomName().contains("PF Hopper"))
                        return;
                } catch (NullPointerException ignored) {}
            }

            // Peaceful Farms - PFMoreHoppers Patch
            if (block.getState() instanceof Hopper && AutoPickup.usingPFMoreHoppers) {
                NamespacedKey morePFHoppers = new NamespacedKey(PLUGIN.getServer().getPluginManager().getPlugin("PFMoreHoppers"), "PFHopper-Variant");
                Container con = (Container) block.getState();
                if (con.getPersistentDataContainer().getKeys().contains(morePFHoppers)) {
                    return;
                }
            }

            // WildChests patch
            if (AutoPickup.usingWildChests) {
                if (block.getType()==Material.CHEST) {
                    return;
                }
            }

            //e.setDropItems(false); // Cancel drops

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
//                        if (player.getInventory().firstEmpty()!=-1) {
//                            player.getInventory().add----Item(items);
//                        } else {
//                            player.getWorld().dropItemNaturally(loc, items);
//                        }
                        HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(items);
                        if (leftOver.keySet().size()>0) {
                            for (ItemStack item : leftOver.values()) {
                                player.getWorld().dropItemNaturally(loc, item);
                            }
                        }
                    }
                }

            } else {
                for (ItemStack items : ((Container) e.getBlock().getState()).getInventory().getContents()) {

                    if (items!=null) {
//                        if (player.getInventory().firstEmpty()!=-1) {
//                            player.getInventory().add----Item(items);
//                        } else {
//                            player.getWorld().dropItemNaturally(loc, items);
//                        }
                        HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(items);
                        if (leftOver.keySet().size()>0) {
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
                if (block.getType()==Material.FURNACE || block.getType()==Material.BLAST_FURNACE || block.getType()==Material.SMOKER) {
                    return;
                }
            }

//            if (player.getInventory().firstEmpty()!=-1) {
//                player.getInventory().add---Item(drop);
//            } else {
//                player.getWorld().dropItemNaturally(loc, drop);
//            }
//            ItemStack drop = new ItemStack(e.getBlock().getType());
//            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(drop);
//            if (leftOver.keySet().size()>0) {
//                for (ItemStack item : leftOver.values()) {
//                    player.getWorld().dropItemNaturally(loc, item);
//                }
//            }

        }

        TallCrops crops = PLUGIN.getCrops();
        ArrayList<Material> verticalReq = crops.getVerticalReq();
        ArrayList<Material> verticalReqDown = crops.getVerticalReqDown();

        // TEST START
        Location l = e.getBlock().getLocation();
        //Deal with kelp

        if(e.getBlock().getType() == Material.KELP_PLANT || e.getBlock().getType().equals(Material.KELP) || e.getBlock().getType() == Material.BAMBOO) {
            Location lnew = l.clone();
            do {
                lnew.setY(lnew.getY()+1);
                if(lnew.getBlock().getType() == Material.KELP_PLANT || lnew.getBlock().getType().equals(Material.KELP) || lnew.getBlock().getType() == Material.BAMBOO) {
                    addLocation(lnew, e.getPlayer());
                }
                else {
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
        if (e.getBlock().getType() == Material.SUGAR_CANE || e.getBlock().getType() == Material.GRASS || e.getBlock().getType() == Material.SAND) {
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

        // TEST END

        if (verticalReq.contains(e.getBlock().getType()) || verticalReqDown.contains(e.getBlock().getType())) {
            e.setDropItems(false);
            vertBreak(player, e.getBlock().getLocation());
        }

    }

    public static int mend(ItemStack item, int xp) {

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

        //type = loc.getBlock().getType();
        type = TallCrops.checkAltType(loc.getBlock().getType());
        loc.getBlock().setType(Material.AIR, true);

        //Bukkit.getPluginManager().callEvent(new BlockBreakEvent(loc.getBlock().getWorld().getBlockAt(loc), player)); //////

        //System.out.println(loc.clone().add(0,1,0).getBlock().getType() + " | " + loc.toString());
        if (verticalReq.contains(loc.add(0,1,0).getBlock().getType())) {
            amt++;
            vertBreak(player, loc);
        } else if (verticalReqDown.contains(loc.subtract(0,2,0).getBlock().getType())) {
            amt++;
            vertBreak(player, loc);
        } else {
            ItemStack drop = new ItemStack(type, amt);
            HashMap<Integer, ItemStack> leftOver = player.getInventory().addItem(drop);
            if (leftOver.keySet().size()>0) {
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
            loc.add(0,1,0);
            String key = loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ()+";"+loc.getWorld();
            AutoPickup.customItemPatch.put(key, new PickupObjective(loc, player, Instant.now()));
            ///////////////////////////////////////////////////////////////////////////////////////
        }

    }

    private void addLocation(Location loc, Player player) {
        String key = loc.getBlockX()+";"+loc.getBlockY()+";"+loc.getBlockZ()+";"+loc.getWorld();
        AutoPickup.customItemPatch.put(key, new PickupObjective(loc, player, Instant.now()));
    }

}
