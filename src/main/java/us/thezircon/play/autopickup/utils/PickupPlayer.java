package us.thezircon.play.autopickup.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.thezircon.play.autopickup.AutoPickup;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

public class PickupPlayer {

    private static final AutoPickup plugin = AutoPickup.getPlugin(AutoPickup.class);
    private static final Logger log = Logger.getLogger("Minecraft");

    private Player player;
    private UUID uuid;
    private File playerData;

    public PickupPlayer(Player p){
        player = p;
        uuid = player.getUniqueId();
        playerData = new File(plugin.getDataFolder()+File.separator+"PlayerData"+File.separator+uuid+".yml");
    }

    public boolean fileExists() {
        return playerData.exists();
    }

    public void createFile() {
        if (!fileExists()) {
           try {
               playerData.createNewFile();
           } catch (IOException err) {
               log.warning("[AutoPickup] Unable to create playdata file for "+uuid);
               // Dev Build
               log.severe("[AutoPickup] Cuase: "+err.getCause());
               System.out.println("");
               err.printStackTrace();
               ////////////
           }
        }
    }

    public File getPlayerData() {
        if (!fileExists()) {createFile();}
        return playerData;
    }

    public void setEnabled(boolean e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!fileExists()) {createFile();}

                File playerFile = new File(plugin.getDataFolder()+File.separator+"PlayerData"+File.separator+uuid+".yml");
                FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);

                data.set("enabled", e);

                try {
                    data.save(playerFile);
                } catch (IOException err){
                    log.warning("[AutoPickup] Unable to update "+uuid+"'s data file.");
                }
            }
        }.runTaskLater(plugin, 1);
    }

    public boolean getToggle(){
        if (!fileExists()) {createFile();}
        FileConfiguration data = YamlConfiguration.loadConfiguration(playerData);
        return data.getBoolean("enabled");
    }

}