package us.thezircon.play.autopickup.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Objects;

public class TallCrops {

    public ArrayList<Material> verticalReq = new ArrayList<>();
    public ArrayList<Material> verticalReqDown = new ArrayList<>();

    public TallCrops () {
         if (
                Bukkit.getVersion().contains("1.14") ||
                Bukkit.getVersion().contains("1.15") ||
                Bukkit.getVersion().contains("1.16") ||
                Bukkit.getVersion().contains("1.17") ||
                Bukkit.getVersion().contains("1.18") ||
                Bukkit.getVersion().contains("1.19") ||
                Bukkit.getVersion().contains("1.20") ||
                Bukkit.getVersion().contains("1.21")
        ) {
            verticalReq.add(Material.KELP);
            verticalReq.add(Material.KELP_PLANT);
            verticalReq.add(Material.BAMBOO);
            verticalReq.add(Material.BAMBOO_SAPLING);
        }else if(Bukkit.getVersion().contains("1.13")) {
            verticalReq.add(Material.KELP);
            verticalReq.add(Material.KELP_PLANT);
        }
    }

    public ArrayList<Material> getVerticalReq() {
        return verticalReq;
    }

    public ArrayList<Material> getVerticalReqDown() {
        return verticalReqDown;
    }

    public static Material checkAltType(Material material) {
        if (
                Bukkit.getVersion().contains("1.16") ||
                        Bukkit.getVersion().contains("1.17") ||
                        Bukkit.getVersion().contains("1.18") ||
                        Bukkit.getVersion().contains("1.19") ||
                        Bukkit.getVersion().contains("1.20") ||
                        Bukkit.getVersion().contains("1.21")
        ) {
            if (material == Material.BAMBOO_SAPLING) {
                return Material.BAMBOO;
            }
        }
        return material;
    }
}
