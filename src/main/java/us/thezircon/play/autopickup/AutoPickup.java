package us.thezircon.play.autopickup;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import us.thezircon.play.autopickup.commands.AutoPickup.Auto;
import us.thezircon.play.autopickup.listeners.*;
import us.thezircon.play.autopickup.utils.Messages;
import us.thezircon.play.autopickup.utils.Metrics;
import us.thezircon.play.autopickup.utils.VersionChk;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

public final class AutoPickup extends JavaPlugin {

    public ArrayList<Player> autopickup_list = new ArrayList<>();
    public Messages messages = null;
    public boolean UP2Date = true;

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Load Configuration Files
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        createBlacklist();

        messages = new Messages();

        // Listeners
        getServer().getPluginManager().registerEvents(new BlockDropItemEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPhysicsEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakEventListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDeathEventListener(), this);

        // Commands
        getCommand("autopickup").setExecutor(new Auto());

        //bStats
        Metrics metrics = new Metrics(this, 5914);

        // Version Check
        String pluginName = this.getName();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    VersionChk.checkVersion(pluginName, 70157);
                } catch (UnknownHostException e) {
                    VersionChk.noConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    //blacklist.yml
    private File fileBlacklist;
    private FileConfiguration confBlacklist;

    public FileConfiguration getBlacklistConf() {
        return this.confBlacklist;
    }

    private void createBlacklist() {
        fileBlacklist = new File(getDataFolder(), "blacklist.yml");
        if (!fileBlacklist.exists()) {
            fileBlacklist.getParentFile().mkdirs();
            saveResource("blacklist.yml", false);
        }

        confBlacklist= new YamlConfiguration();
        try {
            confBlacklist.load(fileBlacklist);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void blacklistReload(){
        confBlacklist = YamlConfiguration.loadConfiguration(fileBlacklist);
    }

    public Messages getMsg() {
        return messages;
    }
}
