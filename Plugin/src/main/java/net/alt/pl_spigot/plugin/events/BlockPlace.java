package net.alt.pl_spigot.plugin.events;

import net.alt.pl_spigot.plugin.api.NMS;
import net.alt.pl_spigot.plugin.crops.Crop;
import net.alt.pl_spigot.plugin.crops.CropPlaceEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;

public class BlockPlace implements Listener {
    private final Plugin plugin;
    private final NMS nmsHandler;
    private final HashMap<Block, Crop> crops;

    public BlockPlace(Plugin plugin, NMS nms, HashMap<Block, Crop> crops) {
        this.plugin = plugin;
        this.nmsHandler = nms;
        this.crops = crops;
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("id"), PersistentDataType.STRING)) {
            e.getBlockPlaced().setType(Material.AIR);
            ItemStack item = e.getItemInHand();
            String item_id = item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("id"), PersistentDataType.STRING);
            ConfigurationSection csF = plugin.getConfig().getConfigurationSection("food").getConfigurationSection(item_id);
            assert csF != null;
            Crop crop = new Crop(Material.getMaterial(csF.getConfigurationSection("place").getString("material")), item, e.getBlockPlaced(), plugin, nmsHandler);
            crop.setName(csF.getString("name"));
            List<String> materials = (List<String>) csF.getConfigurationSection("place").getList("allowedBlocks");
            for (int i = 0; i <= materials.size() - 1; i++) {
                crop.addAllowedBlock(Material.getMaterial(materials.get(i)));
            }
            if(plugin.getConfig().getBoolean("hologramsEnabled")) {
                crop.setHologram(e.getBlockPlaced());
            }
            crop.placeCrop(e.getBlockPlaced(), item);
            crops.put(e.getBlockPlaced(), crop);
            this.plugin.getServer().getPluginManager().callEvent(new CropPlaceEvent(crop, e.getPlayer(), e.getBlock(), e.getBlockAgainst()));
        }
    }

}
