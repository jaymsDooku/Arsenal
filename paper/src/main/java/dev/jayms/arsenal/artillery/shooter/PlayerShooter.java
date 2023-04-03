package dev.jayms.arsenal.artillery.shooter;

import org.bukkit.entity.Player;

public class PlayerShooter implements Shooter {

    private final Player player;

    public PlayerShooter(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }
}
