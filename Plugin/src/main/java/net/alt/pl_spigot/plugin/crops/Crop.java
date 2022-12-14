/**
 * Crop Java Class
 *
 * @author Alt Demono
 * @version 1.0.0
 */
package net.alt.pl_spigot.plugin.crops;

// HolographicDisplaysAPI
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;

import net.alt.pl_spigot.plugin.api.ICrop;
import net.alt.pl_spigot.plugin.api.NMS;

// Bukkit
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

// Java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class Crop implements ICrop {
    /* -===================================================================- */
    // params
    private Hologram hologram;
    private Material material;
    private ArrayList<Material> allowedBlocks = new ArrayList<>();
    private ItemStack item;
    private Material seedMaterial;
    private String name;
    private Block block;
    private final Plugin plugin;
    private final NMS nmsHandler;
    private final HolographicDisplaysAPI api;
    private final String itemId;
    private final HashMap<ItemStack, Integer> dropItems = new HashMap<>();
    /* -===================================================================- */


    /* -===================================================================- */
    // constructor
    public Crop(Material material, ItemStack item, Block block, String id, Plugin plugin, NMS nms) {
        this.material = material;
        this.block = block;
        this.itemId = id;
        this.item = item;
        this.plugin = plugin;
        this.nmsHandler = nms;
        this.api = HolographicDisplaysAPI.get((Plugin) plugin);
    }
    /* -===================================================================- */

    /* -===================================================================- */
    // methods

    // get a crop seed material
    @Override
    public Material getSeedMaterial() {
        return this.seedMaterial;
    }

    // get a crop material
    @Override
    public Material getMaterial() {
        return this.material;
    }

    // set a crop seed material
    @Override
    public void setSeedMaterial(Material m) {
        this.seedMaterial = m;
    }

    // set a crop material
    @Override
    public void setMaterial(Material m) {
        this.material = m;
    }

    // add a new drop item
    @Override
    public void addDropItem(ItemStack item, int chance) {
        this.dropItems.put(item, chance);
    }

    // remove a drop item
    @Override
    public void removeDropItem(ItemStack item) {
        this.dropItems.remove(item);
    }

    // set a block
    @Override
    public void setBlock(Block block) {
        this.block = block;
    }

    // get a block
    @Override
    public Block getBlock() {
        return this.block;
    }

    // remove hologram
    @Override
    public void removeHologram() {
        this.hologram.delete();
    }

    // set a new name
    @Override
    public void setName(String name) {
        this.name = name;
    }

    // get a name
    @Override
    public String getName() {
        return this.name;
    }

    // set item
    @Override
    public void setItem(ItemStack item) {
        this.item = item;
    }
    // get item
    @Override
    public ItemStack getItem() {
        return item;
    }

    // get drop items
    @Override
    public HashMap getDropItems() {
        return this.dropItems;
    }

    @Override
    public void addAllowedBlock(Material material) {
        this.allowedBlocks.add(material);
    }

    @Override
    public void removeAllowedBlock(Material material) {
        this.allowedBlocks.remove(material);
    }

    @Override
    public void dropItem() {
        ItemStack newItem = new ItemStack(item);
        Random rnd = new Random();
        ConfigurationSection configSect = plugin.getConfig().getConfigurationSection("food").getConfigurationSection(itemId).getConfigurationSection("place").getConfigurationSection("droppedItem");
        List<Integer> amounts = (List<Integer>) configSect.getList("amount");
        newItem.setAmount(rnd.nextInt(amounts.get(0),amounts.get(1)));
        block.getWorld().dropItemNaturally(block.getLocation(), newItem);
    }

    @Override
    public ArrayList getAllowedBlocks() {
        return this.allowedBlocks;
    }

    // set a hologram
    @Override
    public void setHologram(Block block) {
        // getting a location
        Location location = block.getLocation();
        location.setY(location.getY()+2.5);
        location.setX(location.getX()+.5);
        location.setZ(location.getBlockZ()+.5);

        // create a new item
        ItemStack newItem = new ItemStack(this.item);
        newItem.setAmount(1);

        this.hologram = api.createHologram(location);
        this.hologram.getLines().appendText(nmsHandler.color(this.getName()));
        this.hologram.getLines().appendItem(newItem);
        this.hologram.getLines().appendText(nmsHandler.color("&aCOMPLETED"));
    }

    // Place a crop
    @Override
    public void placeCrop(Block target, ItemStack itemInHand) {
        // checking if item in hand is item
        if(itemInHand.equals(this.item)) {
            target.setType(this.getMaterial());
        }
    }
    /* -===================================================================- */
}
