package us.thezircon.play.autopickup.commands.AutoPickup.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.commands.AutoSmelt;
import us.thezircon.play.autopickup.commands.CMDManager;
import us.thezircon.play.autopickup.utils.Messages;

import java.util.List;

public class smelt extends CMDManager {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @Override
    public String getName() {
        return "smelt";
    }

    @Override
    public String getDescription() {
        return "Smelts mined blocks";
    }

    @Override
    public String getSyntax() {
        return "/auto smelt";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        boolean requirePermsAUTO = PLUGIN.getConfig().getBoolean("requirePerms.autopickup");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("autopickup.pickup.mined.autosmelt") || !requirePermsAUTO) {
                AutoSmelt.toggle(player);
            } else {
                sender.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getNoPerms());
            }
        }
    }

    @Override
    public List<String> arg1(Player player, String[] args) {
        return null;
    }

    @Override
    public List<String> arg2(Player player, String[] args) {
        return null;
    }

    @Override
    public List<String> arg3(Player player, String[] args) {
        return null;
    }
}
