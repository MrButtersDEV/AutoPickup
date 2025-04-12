package us.thezircon.play.autopickup;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import us.thezircon.play.autopickup.commands.AutoDrops;
import us.thezircon.play.autopickup.commands.AutoFishingDrops;
import us.thezircon.play.autopickup.commands.AutoPickup.Auto;
import us.thezircon.play.autopickup.commands.AutoSmelt;
import us.thezircon.play.autopickup.listeners.*;
import us.thezircon.play.autopickup.papi.AutoPickupExpansion;
import us.thezircon.play.autopickup.utils.*;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public final class AutoPickup extends JavaPlugin {

    public HashSet<Player> autopickup_list = new HashSet<>(); // Blocks
    public HashSet<Player> autopickup_list_mobs = new HashSet<>(); // Mobs
    public HashSet<Player> autopickup_list_fishing = new HashSet<>(); // Fish
    public HashSet<Player> auto_smelt_blocks = new HashSet<>(); // AutoSmelt - Blocks
    public Messages messages = null;
    public boolean UP2Date = true;
    public TallCrops crops;

    public static boolean usingUpgradableHoppers = false; // UpgradableHoppers Patch
    public static boolean usingLocketteProByBrunyman = false; // LockettePro Patch
    public static boolean usingBentoBox = false; // BentoBox - AOneBlock Patch
    public static boolean usingQuickShop = false; //QuickShop - Ghost_chu (reremake)
    public static boolean usingEpicFurnaces = false; //EpicFurnaces - Songoda
    public static boolean usingWildChests = false; // WildChests - BG Development
    public static boolean usingMythicMobs = false; // MythicMobs

    //public static boolean usingPFHoppers = false; // Play.PeacefulFarms.Net
    //public static boolean usingPFMoreHoppers = false; // Patch for PF

    public static boolean usingPlaceholderAPI = false; // Papi - clip

    public static ArrayList<String> worldsBlacklist = null;

    // Custom Items Patch
    public static HashMap<String, PickupObjective> customItemPatch = new HashMap<>();
    public static HashSet<UUID> droppedItems = new HashSet<>();

    // Notification Cooldown
    public static HashMap<UUID, Long> lastInvFullNotification = new HashMap<>();

    private static AutoPickup instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // Load Configuration Files
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        createBlacklist();
        createPlayerDataDir();

        // PAPI Check
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            createPAPI();
            new AutoPickupExpansion().register();
        }

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
        // QuickShop - QuickShop Patch
        if ((getServer().getPluginManager().getPlugin("QuickShop") != null)) {
            usingQuickShop = true;
        }

        // EpicFurnaces - EpicFurnaces Patch
        if ((getServer().getPluginManager().getPlugin("EpicFurnaces") != null)) {
            usingEpicFurnaces = true;
        }

        // WildChests - WildChests Patch
        if ((getServer().getPluginManager().getPlugin("WildChests") != null)) {
            usingWildChests = true;
        }

        // For placeholders
        if ((getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)) {
            usingPlaceholderAPI = true;
        }

        // MythicMobs
        if ((getServer().getPluginManager().getPlugin("MythicMobs") != null)) {
            usingMythicMobs = true;
        }

        // Peaceful Farms - Hoppers Patch
        // PFHoppers
        /*if ((getServer().getPluginManager().getPlugin("PFHoppers") != null)) {
            usingPFHoppers = true;
        }*/
        // PFMoreHoppers
        /*if ((getServer().getPluginManager().getPlugin("PFMoreHoppers") != null)) {
            usingPFMoreHoppers = true;
        }*/

        messages = new Messages();

        // Listeners
        getServer().getPluginManager().registerEvents(new BlockDropItemEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakEventListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDeathEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDropItemEventListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerFishEventListener(), this);
        getServer().getPluginManager().registerEvents(new ItemSpawnEventListener(), this);
        getServer().getPluginManager().registerEvents(new EntityDropItemEventListener(), this);


        if (usingMythicMobs) {
            getServer().getPluginManager().registerEvents(new MythicMobListener(), this);
        }

        // Commands
        getCommand("autopickup").setExecutor(new Auto());
        getCommand("autodrops").setExecutor(new AutoDrops());
        getCommand("autofishingdrops").setExecutor(new AutoFishingDrops());
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
        new BukkitRunnable() {
            @Override
            public void run() {
                customItemPatch.keySet().removeIf(key -> (Duration.between(Instant.now(), customItemPatch.get(key).getCreatedAt()).getSeconds() < -15));
            }
        }.runTaskTimerAsynchronously(this, 300L, 300L); // 15 sec

        // Dropped items cleaner ****
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    droppedItems.removeIf(uuid -> (Bukkit.getEntity(uuid))==null);
                    droppedItems.removeIf(uuid -> (Bukkit.getEntity(uuid)).isDead()); ///////
                } catch (NullPointerException ignored) {}
            }
        }.runTaskTimer(this, 6000L, 6000L); // 5 min

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

    //papi.yml
    private File filePAPI;
    private FileConfiguration confPAPI;

    public FileConfiguration getPAPIConf() {
        return this.confPAPI;
    }

    private void createPAPI() {
        filePAPI = new File(getDataFolder(), "papi.yml");
        if (!filePAPI.exists()) {
            filePAPI.getParentFile().mkdirs();
            saveResource("papi.yml", false);
        }

        confPAPI= new YamlConfiguration();
        try {
            confPAPI.load(filePAPI);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void PAPIReload(){
        confPAPI = YamlConfiguration.loadConfiguration(filePAPI);
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

    public Messages getMsg() {
        return messages;
    }

    public static AutoPickup getInstance() {
        return instance;
    }
}
