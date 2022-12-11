package net.alt.pl_spigot.plugin.events;

// Plugin files
import net.alt.pl_spigot.plugin.api.NMS;
import net.alt.pl_spigot.plugin.crops.Crop;
import net.alt.pl_spigot.plugin.crops.CropPlaceEvent;

// Bukkit
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

// Java
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class BlockPlace implements Listener {
    private final Plugin plugin;
    private final NMS nmsHandler;
    private final HashMap<Block, Crop> crops;

    /* -===================================================================- */
    public BlockPlace(Plugin plugin, NMS nms, HashMap<Block, Crop> crops) {
        this.plugin = plugin;
        this.nmsHandler = nms;
        this.crops = crops;
    }
    /* -===================================================================- */


    /* -===================================================================- */
    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getItemMeta().getPersistentDataContainer().has(NamespacedKey.fromString("id"), PersistentDataType.STRING)) {
            e.getBlockPlaced().setType(Material.AIR);

            if (Objects.equals(e.getItemInHand().getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("ptype"), PersistentDataType.STRING), "food"))
            {
                nmsHandler.sendMessage(e.getPlayer(), "CustomFood > you can't place this item!");
                e.setCancelled(true);
            }

            ItemStack item = e.getItemInHand();

            String item_id = item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("id"), PersistentDataType.STRING);
            ConfigurationSection csF = plugin.getConfig().getConfigurationSection("food").getConfigurationSection(item_id);

            assert csF != null;
            Crop crop = new Crop(Material.getMaterial(csF.getConfigurationSection("place").getString("material")), item, e.getBlockPlaced(), item_id, plugin, nmsHandler);
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
            Particle particle = Particle.valueOf(e.getItemInHand().getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("particle"), PersistentDataType.STRING));
            int particleAmount = e.getItemInHand().getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("particle_amount"), PersistentDataType.INTEGER);
            e.getBlockPlaced().getWorld().spawnParticle(particle, e.getBlockPlaced().getLocation(), particleAmount);

            this.plugin.getServer().getPluginManager().callEvent(new CropPlaceEvent(crop, e.getPlayer(), e.getBlock(), e.getBlockAgainst()));
        }
    }
    /* -===================================================================- */
}
