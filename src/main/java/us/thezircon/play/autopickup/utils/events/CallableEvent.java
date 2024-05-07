package us.thezircon.play.autopickup.utils.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CallableEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private boolean isCalled = false;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public CallableEvent call() {
        if (!this.isCalled) {
            Bukkit.getPluginManager().callEvent(this);
            this.isCalled = true;
        }
        return this;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
