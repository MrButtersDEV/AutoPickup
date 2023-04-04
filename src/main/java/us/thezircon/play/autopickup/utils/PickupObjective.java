package us.thezircon.play.autopickup.utils;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashSet;

public class PickupObjective {

    private Location location;
    private Player player;
    private Instant createdAt;
    private HashSet<Item> processed;

    public PickupObjective(Location location, Player player, Instant instant, HashSet<Item> processed) {
        this.location = location;
        this.player = player;
        this.createdAt = instant;
        this.processed = processed;
    }

    public PickupObjective(Location location, Player player, Instant instant) {
        this.location = location;
        this.player = player;
        this.createdAt = instant;
        this.processed = null;
    }

    public Location getLocation() {
        return location;
    }

    public Player getPlayer() {
        return player;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public HashSet<Item> getProcessed() {
        return processed;
    }

}
