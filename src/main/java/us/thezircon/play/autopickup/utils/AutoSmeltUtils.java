package us.thezircon.play.autopickup.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import us.thezircon.play.autopickup.AutoPickup;

import java.util.*;

import static us.thezircon.play.autopickup.AutoPickup.smeltRecipeCache;

public class AutoSmeltUtils {

    private static final AutoPickup PLUGIN = AutoPickup.getPlugin(AutoPickup.class);

    public boolean isAutoSmeltEnabled = false;
    public static Material[] ignore = {Material.COAL_ORE, Material.REDSTONE_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE, Material.NETHER_QUARTZ_ORE};
    public static List<Material> ignoreMaterials = Collections.unmodifiableList(Arrays.asList(ignore));

    public boolean isEnabled() {
        return isAutoSmeltEnabled;
    }

    public static void loadFurnaceRecipes(Map<Material, FurnaceRecipe> smeltRecipeCache) {
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()) {
            Recipe recipe = iter.next();
            if (recipe instanceof FurnaceRecipe) {
                FurnaceRecipe furnaceRecipe = (FurnaceRecipe) recipe;
                Material inputType = furnaceRecipe.getInput().getType();
                // Only cache the first found recipe per material
                smeltRecipeCache.putIfAbsent(inputType, furnaceRecipe);
            }
        }
    }


    public static ItemStack smelt(ItemStack itemStack, Player player) {
        List<String> blacklist = PLUGIN.getBlacklistConf().getStringList("AutoSmeltBlacklist");

        if (ignoreMaterials.contains(itemStack.getType()) || blacklist.contains(itemStack.getType().toString())) {
            return itemStack;
        }

        // Special case: logs into charcoal
        if (Tag.LOGS_THAT_BURN.isTagged(itemStack.getType())) {
            player.giveExp(0); // Adjust XP if needed
            return new ItemStack(Material.CHARCOAL, itemStack.getAmount());
        }

        FurnaceRecipe recipe = smeltRecipeCache.get(itemStack.getType());
        if (recipe != null) {
            ItemStack result = recipe.getResult().clone();
            result.setAmount(itemStack.getAmount());
            player.giveExp((int) (recipe.getExperience() * itemStack.getAmount()));
            return result;
        }

        return itemStack;
    }

}
