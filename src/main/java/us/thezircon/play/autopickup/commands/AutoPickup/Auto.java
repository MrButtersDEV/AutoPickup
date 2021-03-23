package us.thezircon.play.autopickup.commands.AutoPickup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.commands.AutoPickup.subcommands.drops;
import us.thezircon.play.autopickup.commands.AutoPickup.subcommands.reload;
import us.thezircon.play.autopickup.commands.AutoPickup.subcommands.smelt;
import us.thezircon.play.autopickup.commands.CMDManager;
import us.thezircon.play.autopickup.utils.PickupPlayer;

import java.util.ArrayList;
import java.util.List;

public class Auto implements TabExecutor{

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    private ArrayList<CMDManager> subcommands = new ArrayList<>();

    public Auto(){
        subcommands.add(new reload());
        subcommands.add(new drops());
        subcommands.add(new smelt());
    }

    public ArrayList<CMDManager> getSubCommands(){
        return subcommands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean requirePermsAUTO = PLUGIN.getConfig().getBoolean("requirePerms.autopickup");

        if (args.length > 0){
            for (int i = 0; i < getSubCommands().size(); i++){
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                    getSubCommands().get(i).perform(sender, args);
                }
            }
        } else {
            // Toggle Auto
            if (sender instanceof Player) {
                Player player = (Player) sender;
                PickupPlayer PP = new PickupPlayer(player);
                if ((player.hasPermission("autopickup.pickup.mined")) || (!requirePermsAUTO)) {
                    //Does have perm
                    if (PLUGIN.autopickup_list.contains(player)) {
                        PLUGIN.autopickup_list.remove(player);
                        player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getAutoPickupDisable());
                        PP.setEnabled(false);
                    } else if (!PLUGIN.autopickup_list.contains(player)) {
                        PLUGIN.autopickup_list.add(player);
                        player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getAutoPickupEnable());
                        PP.setEnabled(true);
                    }
                } else {
                    player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getNoPerms());
                }
            }
        }

        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (sender instanceof ConsoleCommandSender) {
            return null;
        }

        if (args.length == 1) {
            ArrayList<String> subcommandsArguments = new ArrayList<>();

            for (int i = 0; i < getSubCommands().size(); i++) {
                subcommandsArguments.add(getSubCommands().get(i).getName());
            }

            return subcommandsArguments;
        } else if (args.length == 2) {
            for (int i = 0; i < getSubCommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                    return getSubCommands().get(i).arg1((Player) sender, args);
                }
            }
        } else if (args.length == 3) {
            for (int i = 0; i < getSubCommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                    return getSubCommands().get(i).arg2((Player) sender, args);
                }
            }
        }
        return null;
    }

}
