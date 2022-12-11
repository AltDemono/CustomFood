package net.alt.pl_spigot.plugin.events;

import net.alt.pl_spigot.plugin.api.NMS;
import net.alt.pl_spigot.plugin.crops.Crop;
import net.alt.pl_spigot.plugin.crops.PlayerCropHarvest;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

@SuppressWarnings("unused")
public class BlockBreakEvent implements Listener {
    private final Plugin plugin;
    private final NMS nmsHandler;
    private final HashMap<Block, Crop> crops;

    public BlockBreakEvent(Plugin plugin, NMS nms, HashMap<Block, Crop> crops) {
        this.plugin = plugin;
        this.nmsHandler = nms;
        this.crops = crops;
    }

    @EventHandler
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent e)
    {
        if(crops.containsKey(e.getBlock()))
        {
            e.setDropItems(false);

            Location location = e.getBlock().getLocation();
            location.setY(location.getY()+.5);
            location.setZ(location.getZ()+.5);
            location.setX(location.getX()+.5);

            crops.get(e.getBlock()).dropItem();

            plugin.getLogger().info("Dropped!");


            crops.get(e.getBlock()).removeHologram();
            crops.remove(e.getBlock());

            this.plugin.getServer().getPluginManager().callEvent(new PlayerCropHarvest(crops.get(e.getBlock()), e.getPlayer()));
        }
    }
}
