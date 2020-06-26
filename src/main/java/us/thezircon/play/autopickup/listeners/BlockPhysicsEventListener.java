package us.thezircon.play.autopickup.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class BlockPhysicsEventListener implements Listener {

    @EventHandler
    public void onPhysics(BlockPhysicsEvent e) {
     //   System.out.println("Source: " + e.getSourceBlock().getType() + " - " + e.getSourceBlock().getLocation());
     //   System.out.println("Change Type: " + e.getChangedType());

    }

}
