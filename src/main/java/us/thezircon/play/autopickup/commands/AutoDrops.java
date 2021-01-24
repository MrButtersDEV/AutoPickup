package us.thezircon.play.autopickup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.PickupPlayer;

public class AutoDrops implements CommandExecutor {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean requirePermsAUTO = PLUGIN.getConfig().getBoolean("requirePerms.autopickup");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("autopickup.pickup.entities") || (!requirePermsAUTO)) {
                toggle(player);
            } else {
                player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getNoPerms());
            }
        }

        return false;

    }

    public static void toggle(Player player) {
        PickupPlayer PP = new PickupPlayer(player);
        if (PLUGIN.autopickup_list_mobs.contains(player)) {
            PLUGIN.autopickup_list_mobs.remove(player);
            player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getMsgAutoDropsDisable());
            PP.setEnabledEntities(false);
        } else if (!PLUGIN.autopickup_list_mobs.contains(player)) {
            PLUGIN.autopickup_list_mobs.add(player);
            player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getMsgAutoDropsEnable());
            PP.setEnabledEntities(true);
        }
    }
}
