package us.thezircon.play.autopickup.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.Instant;

public class PickupObjective {

    private Location location;
    private Player player;
    private Instant createdAt;

    public PickupObjective(Location location, Player player, Instant instant) {
        this.location = location;
        this.player = player;
        this.createdAt = instant;
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
}
