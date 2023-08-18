package us.thezircon.play.autopickup.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import us.thezircon.play.autopickup.AutoPickup;

import java.util.*;

public class AutoSmelt {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    public boolean isAutoSmeltEnabled = false;
    public static Material[] ignore = {Material.COAL_ORE, Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE, Material.NETHER_QUARTZ_ORE};
    public static List<Material> ignoreMaterials = Arrays.asList(ignore);

    public boolean isEnabled() {
        return isAutoSmeltEnabled;
    }

    public static ItemStack smelt(ItemStack itemStack, Player player) {
        List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("AutoSmeltBlacklist");

        if (ignoreMaterials.contains(itemStack.getType())) {
            return itemStack;
        }

        if (blacklist.contains(itemStack.getType().toString())) {
            return itemStack;
        }

        ItemStack result = itemStack;
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()) {
            Recipe recipe = iter.next();
            if (!(recipe instanceof FurnaceRecipe)) continue;

            RecipeChoice.MaterialChoice materialChoice = new RecipeChoice.MaterialChoice(Tag.LOGS_THAT_BURN);

            if ((materialChoice.getChoices().contains(itemStack.getType()))) {
                return new ItemStack(Material.CHARCOAL, itemStack.getAmount());
            }

            if (((FurnaceRecipe) recipe).getInput().getType() != itemStack.getType()) continue;
            result = recipe.getResult();
            player.giveExp(((int) ((FurnaceRecipe) recipe).getExperience())*itemStack.getAmount());
            break;
        }

        result.setAmount(itemStack.getAmount());

        return result;
    }

}
