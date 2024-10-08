package us.thezircon.play.autopickup.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import us.thezircon.play.autopickup.AutoPickup;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;

import static com.google.common.net.HttpHeaders.USER_AGENT;

public class VersionChk {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    private static final Logger log = Logger.getLogger("Minecraft");

    public static String[] changelog;

    public static void checkVersion(String name, int id) throws Exception { //https://api.spigotmc.org/legacy/update.php?resource=76103"

        //https://api.github.com/repos/MrButtersDEV/AutoPickup/releases/latest
        String url = "https://api.spigotmc.org/legacy/update.php?resource="+id;
        String url2 = "https://raw.githubusercontent.com/MrButtersDEV/AutoPickup/master/update-info.txt";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        PLUGIN.getServer().getConsoleSender().sendMessage(PLUGIN.getMsg().getPrefix() + ChatColor.RESET +" Checking for new verison...");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        URL obj2 = new URL(url2);
        HttpURLConnection con2 = (HttpURLConnection) obj2.openConnection();

        // optional default is GET
        con2.setRequestMethod("GET");

        //add request header
        con2.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode2 = con2.getResponseCode();

        BufferedReader in2 = new BufferedReader(new InputStreamReader(con2.getInputStream()));
        String inputLine2;
        StringBuffer response2 = new StringBuffer();

        while ((inputLine2 = in2.readLine()) != null) {
            response2.append(inputLine2);
        }
        in2.close();

        changelog = response2.toString().split("- ");

        //print result
        String spigotVerison = response.toString();
        String ver = Bukkit.getServer().getPluginManager().getPlugin(name).getDescription().getVersion();
        if (spigotVerison.equals(ver)) {
            PLUGIN.getServer().getConsoleSender().sendMessage(PLUGIN.getMsg().getPrefix() + " " + ChatColor.DARK_GREEN + "Plugin is up-to-date.");
        } else {
            PLUGIN.getServer().getConsoleSender().sendMessage(PLUGIN.getMsg().getPrefix() + ChatColor.RED + " UPDATE FOUND: " + ChatColor.GREEN + "https://www.spigotmc.org/resources/"+id+"/");
            PLUGIN.getServer().getConsoleSender().sendMessage(PLUGIN.getMsg().getPrefix() + ChatColor.GOLD + " Version: " + ChatColor.GREEN + response.toString() + ChatColor.AQUA + " Using Version: " + ChatColor.DARK_AQUA + ver);
            PLUGIN.UP2Date = false;
            for (String s : changelog) {
                if (s.isEmpty())
                    continue;
                PLUGIN.getServer().getConsoleSender().sendMessage(PLUGIN.getMsg().getPrefix() + ChatColor.YELLOW + "  - " + s);
            }
        }

        // Config Version:
        double configVersion = PLUGIN.getConfig().getDouble("ConfigVersion");
        if (configVersion<=1.2) {
            PLUGIN.getConfig().set("ConfigVersion", 1.3);
            PLUGIN.getBlacklistConf().set("doAutoSmeltBlacklist", false);
            PLUGIN.getBlacklistConf().set("AutoSmeltBlacklist", Arrays.asList("OAK_LOG"));

            File conf = new File(PLUGIN.getDataFolder(), "config.yml");
            File fileBlacklist = new File(PLUGIN.getDataFolder(), "blacklist.yml");

            PLUGIN.getConfig().save(conf);
            PLUGIN.getBlacklistConf().save(fileBlacklist);
        }
        if (configVersion<=1.3) {
            PLUGIN.getConfig().set("ConfigVersion", 1.4);
            PLUGIN.getBlacklistConf().set("doAutoSmeltBlacklist", false);
            PLUGIN.getBlacklistConf().set("AutoSmeltBlacklist", Arrays.asList("OAK_LOG"));
            PLUGIN.getConfig().set("usingSilkSpawnerPlugin", true);
            PLUGIN.getConfig().set("ignoreMobXPDrops", false);

            File conf = new File(PLUGIN.getDataFolder(), "config.yml");
            File fileBlacklist = new File(PLUGIN.getDataFolder(), "blacklist.yml");

            PLUGIN.getConfig().save(conf);
            PLUGIN.getBlacklistConf().save(fileBlacklist);
        }


    }

    public static void noConnection(){
        PLUGIN.getServer().getConsoleSender().sendMessage(PLUGIN.getMsg().getPrefix() + " " + ChatColor.LIGHT_PURPLE + "Cannot check for update's - No internet connection!");
    }

}
