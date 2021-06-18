package us.thezircon.play.autopickup.utils;

import us.thezircon.play.autopickup.AutoPickup;

public class Messages {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    private String prefix;
    private String autoPickupEnable;
    private String autoPickupDisable;
    private String autoEnabled;
    private String autoReenabled;
    private String reload;
    private String fullInventory;
    private String noPerms;

    public String getMsgAutoDropsEnable() {
        return msgAutoDropsEnable;
    }

    public String getMsgAutoDropsDisable() {
        return msgAutoDropsDisable;
    }

    public String getMsgAutoSmeltEnable() {
        return msgAutoSmeltEnable;
    }

    public String getMsgAutoSmeltDisable() {
        return msgAutoSmeltDisable;
    }

    private String msgAutoDropsEnable;
    private String msgAutoDropsDisable;
    private String msgAutoSmeltEnable;
    private String msgAutoSmeltDisable;

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

    public Messages() {
        this.prefix = HexFormat.format(PLUGIN.getConfig().getString("msgPrefix"));
        this.autoPickupEnable = HexFormat.format(PLUGIN.getConfig().getString("msgAutoPickupEnable"));
        this.autoPickupDisable = HexFormat.format(PLUGIN.getConfig().getString("msgAutoPickupDisable"));
        this.autoEnabled = HexFormat.format(PLUGIN.getConfig().getString("msgAutoEnable"));
        this.autoReenabled = HexFormat.format(PLUGIN.getConfig().getString("msgEnabledJoinMSG"));
        this.reload = HexFormat.format(PLUGIN.getConfig().getString("msgReload"));
        this.fullInventory = HexFormat.format(PLUGIN.getConfig().getString("msgFullInv"));
        this.noPerms = HexFormat.format(PLUGIN.getConfig().getString("msgNoperms"));

        try {
            this.msgAutoDropsEnable = HexFormat.format(PLUGIN.getConfig().getString("msgAutoMobDropsEnable"));
            this.msgAutoDropsDisable = HexFormat.format(PLUGIN.getConfig().getString("msgAutoMobDropsDisable"));
            this.msgAutoSmeltEnable = HexFormat.format(PLUGIN.getConfig().getString("msgAutoSmeltEnable"));
            this.msgAutoSmeltDisable = HexFormat.format(PLUGIN.getConfig().getString("msgAutoSmeltDisable"));
        } catch (NullPointerException e) {
            double ver = PLUGIN.getConfig().getDouble("ConfigVersion");
            if (ver==1.1) {
                System.out.println("----------------------------------");
                System.out.println("Outdated Config! V1.1 Latest: 1.2");
                System.out.println("----------------------------------");
                System.out.println("Check the default config on spigot!");
            }
            e.printStackTrace();
        }
    }

}
