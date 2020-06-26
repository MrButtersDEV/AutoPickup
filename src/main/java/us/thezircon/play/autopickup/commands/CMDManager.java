package us.thezircon.play.autopickup.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class CMDManager {

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract void perform(CommandSender sender, String args[]);

    public abstract List<String> arg1 (Player player, String args[]);

    public abstract List<String> arg2 (Player player, String args[]);

    public abstract List<String> arg3 (Player player, String args[]);


}
