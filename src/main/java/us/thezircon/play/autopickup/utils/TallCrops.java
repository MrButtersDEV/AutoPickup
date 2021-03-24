package us.thezircon.play.autopickup.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;

public class TallCrops {

    public ArrayList<Material> verticalReq = new ArrayList<>();
    public ArrayList<Material> verticalReqDown = new ArrayList<>();

    public TallCrops () {
        verticalReq.add(Material.SUGAR_CANE);
        verticalReq.add(Material.CACTUS);
        verticalReq.add(Material.KELP);
        verticalReq.add(Material.KELP_PLANT);

        if (Bukkit.getVersion().contains("1.16")) {
            verticalReqDown.add(Material.WEEPING_VINES);
            verticalReqDown.add(Material.WEEPING_VINES_PLANT);
            verticalReq.add(Material.TWISTING_VINES_PLANT);
            verticalReq.add(Material.TWISTING_VINES);
            verticalReq.add(Material.BAMBOO);
            verticalReq.add(Material.BAMBOO_SAPLING);
        } else if (Bukkit.getVersion().contains("1.15")) {
            verticalReq.add(Material.BAMBOO);
            verticalReq.add(Material.BAMBOO_SAPLING);
        } else if (Bukkit.getVersion().contains("1.14")) {
            verticalReq.add(Material.BAMBOO);
            verticalReq.add(Material.BAMBOO_SAPLING);
        }
    }

    public ArrayList<Material> getVerticalReq() {
        return verticalReq;
    }

    public ArrayList<Material> getVerticalReqDown() {
        return verticalReqDown;
    }
}
