package us.thezircon.play.autopickup.listeners;

import me.clip.placeholderapi.expansion.Version;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.PickupPlayer;
import us.thezircon.play.autopickup.utils.VersionChk;

public class PlayerJoinEventListener implements Listener{

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        // Update notifications for admins
        if (player.hasPermission("autopickup.admin.notifyupdate") && !PLUGIN.UP2Date){
            String msgUpdate = ChatColor.translateAlternateColorCodes('&', "&6➤ &eClick &6&lHERE&e to view the latest version.");
            String ver = Bukkit.getServer().getPluginManager().getPlugin("AutoPickup").getDescription().getVersion();
            player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + ChatColor.YELLOW + "Version: " + ChatColor.RED + ver + ChatColor.YELLOW + " is not up to date. Please check your console on next startup or reload.");

            TextComponent message = new TextComponent(msgUpdate);
            message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/autopickup-1-16-support.70157/" ) );
            message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Click to open on spigot!" ).create() ) );
            player.spigot().sendMessage( message );
            for (String s : VersionChk.changelog) {
                if (s.isEmpty())
                    continue;
                player.sendMessage(PLUGIN.getMsg().getPrefix() + ChatColor.YELLOW + "  - " + s);
            }
        }

        boolean doAutoEnableMSG = PLUGIN.getConfig().getBoolean("doAutoEnableMSG");
        boolean doEnabledOnJoinMSG = PLUGIN.getConfig().getBoolean("doEnabledOnJoinMSG");

        // Auto Enable - Automatically turns auto on for players
        if (player.hasPermission("autopickup.pickup.mined.autoenabled")) {
            if (!PLUGIN.autopickup_list.contains(player)) {
                PLUGIN.autopickup_list.add(player);
                if (doAutoEnableMSG) {player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getAutoEnabled());}
            }
        }

        if (player.hasPermission("autopickup.pickup.entities.autoenabled")) {
            if (!PLUGIN.autopickup_list_mobs.contains(player)) {
                PLUGIN.autopickup_list_mobs.add(player);
                //if (doAutoEnableMSG) {player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getAutoEnabled());}
            }
        }

        if (player.hasPermission("autopickup.pickup.mined.autosmelt.autoenabled")) {
            if (!PLUGIN.auto_smelt_blocks.contains(player)) {
                PLUGIN.auto_smelt_blocks.add(player);
            }
        }

        // Auto Re-Enable - Turns auto on if they left the server with it on.
        PickupPlayer PP = new PickupPlayer(player);
        if (PP.getToggle() && player.hasPermission("autopickup.pickup.mined")) {
            if (!PLUGIN.autopickup_list.contains(player)) {
                PLUGIN.autopickup_list.add(player);
                if (doEnabledOnJoinMSG) { player.sendMessage(PLUGIN.getMsg().getPrefix() + " " + PLUGIN.getMsg().getAutoReenabled());}
            }
        }
        if (PP.getAutoSmeltToggle() && player.hasPermission("autopickup.pickup.mined.autosmelt")) {
            if (!PLUGIN.auto_smelt_blocks.contains(player)) {
                PLUGIN.auto_smelt_blocks.add(player);
            }
        }
        if (PP.getMobDropsToggle() && player.hasPermission("autopickup.pickup.entities")) {
            if (!PLUGIN.autopickup_list_mobs.contains(player)) {
                PLUGIN.autopickup_list_mobs.add(player);
            }
        }
    }

}
