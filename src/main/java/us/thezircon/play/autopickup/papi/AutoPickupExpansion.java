package us.thezircon.play.autopickup.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import us.thezircon.play.autopickup.AutoPickup;
import us.thezircon.play.autopickup.utils.HexFormat;
import us.thezircon.play.autopickup.utils.PickupPlayer;

public class AutoPickupExpansion extends PlaceholderExpansion {

    /**
     * This method should always return true unless we
     * have a dependency we need to make sure is on the server
     * for our placeholders to work!
     *
     * @return always true since we do not have any dependencies.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return "BUTTERFIELD8";
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "autopickup";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return "1.0.0";
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return Possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier){

        String sTrue = HexFormat.format(AutoPickup.getInstance().getPAPIConf().getString("papi.enabled.true"));
        String sFalse = HexFormat.format(AutoPickup.getInstance().getPAPIConf().getString("papi.enabled.false"));

        // %autopickup_autoenabled%
        if(identifier.equals("autoenabled")){
            if (new PickupPlayer((Player) player).getToggle()) {
                return sTrue;
            }
            return sFalse;
        }

        // %autopickup_dropsenabled%
        if(identifier.equals("dropsenabled")){
            if (new PickupPlayer((Player) player).getMobDropsToggle()) {
                return sTrue;
            }
            return sFalse;
        }

        // %autopickup_autosmeltenabled%
        if(identifier.equals("autosmeltenabled")){
            if (new PickupPlayer((Player) player).getAutoSmeltToggle()) {
                return sTrue;
            }
            return sFalse;
        }

        // We return null if an invalid placeholder (f.e. %example_placeholder3%)
        // was provided
        return null;
    }
}