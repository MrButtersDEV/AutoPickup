package us.thezircon.play.autopickup.utils.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class CancellableEvent extends CallableEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean isCancelled = false;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
