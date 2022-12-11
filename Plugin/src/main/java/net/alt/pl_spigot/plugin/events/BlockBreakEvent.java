package net.alt.pl_spigot.plugin.events;

import net.alt.pl_spigot.plugin.api.NMS;
import net.alt.pl_spigot.plugin.crops.Crop;
import net.alt.pl_spigot.plugin.crops.PlayerCropHarvest;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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

        Location location_ = e.getBlock().getLocation();
        location_.setY(location_.getY()+1);

        if(crops.containsKey(e.getBlock()))
        {
            e.setDropItems(false);

            Location location = e.getBlock().getLocation();
            location.setY(location.getY()+.5);
            location.setZ(location.getZ()+.5);
            location.setX(location.getX()+.5);

            PersistentDataContainer item = crops.get(e.getBlock()).getItem().getItemMeta().getPersistentDataContainer();

            Particle particle = Particle.valueOf(item.get(NamespacedKey.fromString("particleh"), PersistentDataType.STRING));
            int particleAmount = item.get(NamespacedKey.fromString("particleh_amount"), PersistentDataType.INTEGER);
            e.getBlock().getWorld().spawnParticle(particle, e.getBlock().getLocation(), particleAmount);

            crops.get(e.getBlock()).dropItem();

            crops.get(e.getBlock()).removeHologram();
            crops.remove(e.getBlock());

            this.plugin.getServer().getPluginManager().callEvent(new PlayerCropHarvest(crops.get(e.getBlock()), e.getPlayer()));
        } else if (crops.containsKey(location_.getBlock())) {
            e.setDropItems(false);

            Location location = location_;
            location.setY(location.getY()+.5);
            location.setZ(location.getZ()+.5);
            location.setX(location.getX()+.5);

            crops.get(location_.getBlock()).dropItem();

            PersistentDataContainer item = crops.get(location_.getBlock()).getItem().getItemMeta().getPersistentDataContainer();

            Particle particle = Particle.valueOf(item.get(NamespacedKey.fromString("particleh"), PersistentDataType.STRING));
            int particleAmount = item.get(NamespacedKey.fromString("particleh_amount"), PersistentDataType.INTEGER);
            location_.getWorld().spawnParticle(particle, location_, particleAmount);

            crops.get(location_.getBlock()).removeHologram();
            crops.remove(location_.getBlock());

            this.plugin.getServer().getPluginManager().callEvent(new PlayerCropHarvest(crops.get(location_.getBlock()), e.getPlayer()));
        }
    }
}
