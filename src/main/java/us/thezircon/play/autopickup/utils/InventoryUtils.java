package us.thezircon.play.autopickup.utils;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import us.thezircon.play.autopickup.AutoPickup;

import java.util.HashMap;

public class InventoryUtils {
    private static final long cooldown = 15000; // 15 sec
    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    public static void handleItemOverflow(Location loc, Player player, boolean doFullInvMSG, HashMap<Integer, ItemStack> leftOver, AutoPickup plugin) {
        for (ItemStack item : leftOver.values()) {
            player.getWorld().dropItemNaturally(loc, item);

        }
        if (doFullInvMSG) {
            long secondsLeft;
            if (AutoPickup.lastInvFullNotification.containsKey(player.getUniqueId())) {
                secondsLeft = (AutoPickup.lastInvFullNotification.get(player.getUniqueId()) / 1000) + cooldown / 1000 - (System.currentTimeMillis() / 1000);
            } else {
                secondsLeft = 0;
            }
            if (secondsLeft <= 0) {
                player.sendMessage(plugin.getMsg().getPrefix() + " " + plugin.getMsg().getFullInventory());
                AutoPickup.lastInvFullNotification.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    public static int mend(ItemStack item, int xp) {

        if (item.containsEnchantment(Enchantment.MENDING)) {
            ItemMeta meta = item.getItemMeta();
            Mendable mend[] = Mendable.values();
            for (Mendable m : mend) {

                if (item == null) {
                    continue;
                }

                if (item.getType().toString().equals(m.toString())) {
                    Damageable damage = (Damageable) meta;
                    int min = Math.min(xp, damage.getDamage());
                    if ((damage.getDamage() - min == 0) && damage.hasDamage()) {
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

    public static void applyMending(Player player, int xp) {
        player.giveExp(xp); // Give player XP

        // Mend
        InventoryUtils.mend(player.getInventory().getItemInMainHand(), xp);
        InventoryUtils.mend(player.getInventory().getItemInOffHand(), xp);
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack i : armor) {
            try {
                mend(i, xp);
            } catch (NullPointerException ignored) {
            }
        }
    }
}
