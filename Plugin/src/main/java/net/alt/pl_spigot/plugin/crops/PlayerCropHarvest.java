package net.alt.pl_spigot.plugin.crops;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings("unused")
public class PlayerCropHarvest extends Event {

    private static final HandlerList handlers = new HandlerList();

    Crop harvestedCrop;
    Player player;

    public PlayerCropHarvest(Crop harvestedCrop, Player player) {
        this.harvestedCrop = harvestedCrop;
        this.player = player;
    }

    /* GETTER & SETTER */
    public Crop getHarvestedCrop() {
        return harvestedCrop;
    }

    public void setHarvestedCrop(Crop harvestedCrop) {
        this.harvestedCrop = harvestedCrop;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
