package us.thezircon.play.autopickup.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import us.thezircon.play.autopickup.AutoPickup;

public class EntityDeathEventListener implements Listener {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler
    public void onDeath(EntityDeathEvent e) {

        Player player = e.getEntity().getKiller();
        boolean doFullInvMSG = PLUGIN.getConfig().getBoolean("doFullInvMSG");

        if (PLUGIN.autopickup_list.contains(player)) {

            for (ItemStack drops : e.getDrops()) {
                if (player.getInventory().firstEmpty() != -1) { // has space
                    player.getInventory().addItem(drops);
                } else { // inv full
                    if (doFullInvMSG) {
                        player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getFullInventory());
                    }
                    return;
                }
            }
            e.getDrops().clear();
        }
    }
}
