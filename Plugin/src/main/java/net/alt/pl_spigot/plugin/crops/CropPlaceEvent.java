package net.alt.pl_spigot.plugin.crops;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings("unused")
public class CropPlaceEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();

    public CropPlaceEvent(Crop placedCrop, Player player, Block placedBlock, Block blockAgainst) {
        this.placedCrop = placedCrop;
        this.player = player;
        this.placedBlock = placedBlock;
        this.blockAgainst = blockAgainst;
    }

    Crop placedCrop;
    Player player;
    Block placedBlock;
    Block blockAgainst;

    public Crop getPlacedCrop() {
        return placedCrop;
    }

    /* GETTER & SETTER */
    public void setPlacedCrop(Crop placedCrop) {
        this.placedCrop = placedCrop;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Block getPlacedBlock() {
        return placedBlock;
    }

    public void setPlacedBlock(Block placedBlock) {
        this.placedBlock = placedBlock;
    }

    public Block getBlockAgainst() {
        return blockAgainst;
    }

    public void setBlockAgainst(Block blockAgainst) {
        this.blockAgainst = blockAgainst;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
