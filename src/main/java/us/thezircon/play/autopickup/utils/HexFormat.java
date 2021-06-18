package us.thezircon.play.autopickup.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexFormat {

    private static final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");

    public static String format(String msg) {
        if (Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.18")) {
            Matcher match = pattern.matcher(msg);
            while (match.find()) {
                String color = msg.substring(match.start(), match.end());
                msg = msg.replace(color, ChatColor.of(color.replace("&#","#")) + "");
                match = pattern.matcher(msg);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
