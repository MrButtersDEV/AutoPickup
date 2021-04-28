package us.thezircon.play.autopickup;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import us.thezircon.play.autopickup.commands.AutoDrops;
import us.thezircon.play.autopickup.commands.AutoPickup.Auto;
import us.thezircon.play.autopickup.commands.AutoSmelt;
import us.thezircon.play.autopickup.listeners.*;
import us.thezircon.play.autopickup.utils.*;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public final class AutoPickup extends JavaPlugin {

    public ArrayList<Player> autopickup_list = new ArrayList<>(); // Blocks
    public ArrayList<Player> autopickup_list_mobs = new ArrayList<>(); // Mobs
    public ArrayList<Player> auto_smelt_blocks = new ArrayList<>(); // AutoSmelt - Blocks
    public Messages messages = null;
    public boolean UP2Date = true;
    public TallCrops crops;

    public static boolean usingUpgradableHoppers = false; // UpgradableHoppers Patch
    public static boolean usingLocketteProByBrunyman = false; // LockettePro Patch
    public static boolean usingBentoBox = false; // BentoBox - AOneBlock Patch
    public static ArrayList<String> worldsBlacklist = null;

    // Custom Items Patch
    public static HashMap<String, PickupObjective> customItemPatch = new HashMap<>();
    public static ArrayList<String> customItemPatchKeys = new ArrayList<>();
    public static HashSet<UUID> droppedItems = new HashSet<>();

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Load Configuration Files
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        createBlacklist();
        createPlayerDataDir();

        // UpgradableHoppers Patch
        if ((getServer().getPluginManager().getPlugin("UpgradeableHoppers") != null)) {
            usingUpgradableHoppers = true;
        }
        // LockettePro Patch
        if ((getServer().getPluginManager().getPlugin("LockettePro") != null)) {
            usingLocketteProByBrunyman = true;
        }
        // BentoBox - AOneBlock Patch
        if ((getServer().getPluginManager().getPlugin("BentoBox") != null)) {
            usingBentoBox = true;
        }

        messages = new Messages();

        // Listeners
        getServer().getPluginManager().registerEvents(new BlockDropItemEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakEventListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDeathEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemEventListener(), this);
        getServer().getPluginManager().registerEvents(new ItemSpawnEventListener(), this);

        // Commands
        getCommand("autopickup").setExecutor(new Auto());
        getCommand("autodrops").setExecutor(new AutoDrops());
        getCommand("autosmelt").setExecutor(new AutoSmelt());

        // Crops by version
        crops = new TallCrops();

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

        // Worlds blacklist
        if (getBlacklistConf().contains("BlacklistedWorlds")) {
            worldsBlacklist = (ArrayList<String>) getBlacklistConf().getList("BlacklistedWorlds");
        }

        // Pickup Objective Cleaner
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {

                for (String key : customItemPatchKeys) {
                    if (customItemPatch.containsKey(key)) {
                        PickupObjective po = customItemPatch.get(key);
                        if (Duration.between(Instant.now(), po.getCreatedAt()).getSeconds() < -15) {
                            customItemPatch.remove(key);
                            customItemPatchKeys.remove(key);
                        }
                    }
                }

            }
        }, 0L, 300L); // 15 sec

        // Dropped items cleaner ****
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    droppedItems.removeIf(uuid -> (Bukkit.getEntity(uuid))==null);
                    droppedItems.removeIf(uuid -> (Bukkit.getEntity(uuid)).isDead()); ///////
                } catch (NullPointerException ignored) {}
            }
        }, 0L, 6000L); // 5 min
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

    public void createPlayerDataDir() {
        File dir = new File(getDataFolder(), "PlayerData");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public TallCrops getCrops() {
        return crops;
    }

    public void blacklistReload(){
        confBlacklist = YamlConfiguration.loadConfiguration(fileBlacklist);
    }

    public Messages getMsg() {
        return messages;
    }
}
