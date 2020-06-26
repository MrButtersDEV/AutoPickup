package us.thezircon.play.autopickup.utils;

import org.bukkit.ChatColor;
import us.thezircon.play.autopickup.AutoPickup;

public class Messages {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    private String prefix;
    private String autoPickupEnable;

    public String getPrefix() {
        return prefix;
    }

    public String getAutoPickupEnable() {
        return autoPickupEnable;
    }

    public String getAutoPickupDisable() {
        return autoPickupDisable;
    }

    public String getAutoEnabled() {
        return autoEnabled;
    }

    public String getAutoReenabled() {
        return autoReenabled;
    }

    public String getReload() {
        return reload;
    }

    public String getFullInventory() {
        return fullInventory;
    }

    public String getNoPerms() {
        return noPerms;
    }

    private String autoPickupDisable;
    private String autoEnabled;
    private String autoReenabled;
    private String reload;
    private String fullInventory;
    private String noPerms;

    public Messages() {
        this.prefix = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgPrefix"));
        this.autoPickupEnable = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgAutoPickupEnable"));
        this.autoPickupDisable = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgAutoPickupDisable"));
        this.autoEnabled = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgAutoEnable"));
        this.autoReenabled = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgEnabledJoinMSG"));
        this.reload = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgReload"));
        this.fullInventory = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgFullInv"));
        this.noPerms = ChatColor.translateAlternateColorCodes('&', PLUGIN.getConfig().getString("msgNoperms"));
    }

}
