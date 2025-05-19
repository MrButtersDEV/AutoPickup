package us.thezircon.play.autopickup.commands.AutoPickup.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.commands.AutoFishingDrops;
import us.thezircon.play.autopickup.commands.CMDManager;

import java.util.List;

public class fishingdrops extends CMDManager {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @Override
    public String getName() {
        return "fishingdrops";
    }

    @Override
    public String getDescription() {
        return "Picks up fishing drops";
    }

    @Override
    public String getSyntax() {
        return "/auto fishingdrops";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        boolean requirePermsAUTO = PLUGIN.getConfig().getBoolean("requirePerms.autopickup");

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("autopickup.pickup.fishing") || !requirePermsAUTO) {
                AutoFishingDrops.toggle(player);
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
