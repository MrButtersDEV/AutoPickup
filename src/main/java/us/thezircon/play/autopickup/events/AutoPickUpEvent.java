package us.thezircon.play.autopickup.events;

import org.bukkit.entity.Player;
import us.thezircon.play.autopickup.utils.events.CancellableEvent;

import javax.annotation.Nullable;

public class AutoPickUpEvent extends CancellableEvent {

    private final @Nullable Player player;

    public AutoPickUpEvent(@Nullable Player player) {
        this.player = player;
    }

    public @Nullable Player getPlayer() {
        return player;
    }
}
