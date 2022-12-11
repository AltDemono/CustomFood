/**
 * Custom Food Plugin
 *
 * @author Alt Demono
 * @version 1.0.0
 */

package net.alt.pl_spigot.plugin;

// In-plugin files
import net.alt.pl_spigot.plugin.api.NMS;
import net.alt.pl_spigot.plugin.crops.Crop;
import net.alt.pl_spigot.plugin.events.BlockBreakEvent;
import net.alt.pl_spigot.plugin.events.BlockPlace;

// Bukkit
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

// Java
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")

// Main file
public class MyPlugin extends JavaPlugin implements CommandExecutor, Listener {

    /* -===================================================================- */
    // nms handler
    private NMS nmsHandler;
    private final HashMap<Block, Crop> crops = new HashMap<>();
    public NMS getNmsHandler() {
        return nmsHandler;
    }
    /* -===================================================================- */


    /* -===================================================================- */
    // On Enable method
    @Override
    public void onEnable() {
        String packageName = this.getServer().getClass().getPackage().getName();
        // Get full package string of Spigot.
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        // Get the last element of the package

        try {
            final Class<?> clazz = Class.forName("net.alt.pl_spigot.plugin.nms." + version + ".NMSHandler");

            // Check if we have a NMSHandler class at that location.
            if (NMS.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
                this.nmsHandler = (NMS) clazz.getConstructor().newInstance(); // Set our handler
            }
        } catch (final Exception e) {
            e.printStackTrace();

            // Logger messages
            this.getLogger().severe("Could not find support for this Spigot version.");
            this.getLogger().info("Check for updates at https://github.com/AltDemono/CustomFood");
            this.getLogger().info(version);
            this.setEnabled(false);
            return;
        }
        /* -===================================================================- */



        /* -===================================================================- */
        // Holographic Displays
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }
        /* -===================================================================- */
        this.getLogger().info("Loading support for " + version + " ...");
        /* -===================================================================- */

        /* -===================================================================- */
        // config & command
        saveDefaultConfig();
        saveConfig();

        this.getCommand("cf").setExecutor(this);
        /* -===================================================================- */

        /* -===================================================================- */
        // register events
        this.getServer().getPluginManager().registerEvents(new BlockPlace(this, nmsHandler, crops), this);
        this.getServer().getPluginManager().registerEvents(new BlockBreakEvent(this, nmsHandler, crops), this);
        this.getServer().getPluginManager().registerEvents(this, this);
        /* -===================================================================- */
    }

    /* -===================================================================- */
    // Commands
    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (sender instanceof Player) {
            // Getting a player param
            Player player = (Player) sender;

            switch (args[0]) {

                // Give command
                case "give" -> {
                    /* -===================================================================- */
                    // Choosing a food material
                    @Nullable
                    String mat = this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getString("material");
                    assert mat != null;
                    Material material = Material.getMaterial(mat);
                    /* -===================================================================- */


                    /* -===================================================================- */
                    // Checking Food item
                    if (this.getConfig().getConfigurationSection("food").isConfigurationSection(args[2]) && material != null && args.length == 4 || args.length == 3) {

                        /* -===================================================================- */
                        // Creating a new food (item)
                        assert material != null;
                        ItemStack item = new ItemStack(material);
                        ItemMeta i_meta = item.getItemMeta();

                        ConfigurationSection foodObject = this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]);

                        String name = this.nmsHandler.color(foodObject.getString("name"));
                        int food_level = Integer.parseInt(foodObject.getString("food_level"));

                        String type = foodObject.getString("type");
                        String pType = foodObject.getConfigurationSection("place").getString("type");
                        String particle = foodObject.getConfigurationSection("particle").getConfigurationSection("on_place").getString("name");
                        String particleH = foodObject.getConfigurationSection("particle").getConfigurationSection("on_harvest").getString("name");
                        String particleE = foodObject.getConfigurationSection("particle").getConfigurationSection("on_eat").getString("name");

                        int particle_amount = Integer.parseInt(foodObject.getConfigurationSection("particle").getConfigurationSection("on_place").getString("amount"));
                        int particleH_amount = Integer.parseInt(foodObject.getConfigurationSection("particle").getConfigurationSection("on_harvest").getString("amount"));
                        int particleE_amount = Integer.parseInt(foodObject.getConfigurationSection("particle").getConfigurationSection("on_eat").getString("amount"));

                        // getting a player
                        Player p = Bukkit.getPlayer(args[1]);
                        /* -===================================================================- */


                        /* -===================================================================- */
                        i_meta.setDisplayName(this.nmsHandler.color("&r" + name));
                        List<String> deskList = this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getStringList("description");
                        for (int i = 1; i <= deskList.size(); i++)
                        {
                            deskList.set(i-1, this.nmsHandler.color(deskList.get(i)));
                        }
                        i_meta.setLore(deskList);
                        i_meta.setCustomModelData(Integer.parseInt(this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getString("custom_model_data")));
                        // Setting count
                        int count = (args.length == 3) ? 1 : Integer.parseInt(args[3]);
                        item.setAmount(count);
                        /* -===================================================================- */

                        /*                        -= PersistentDataContainers =-                 */

                        /* -===================================================================- */
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("food_level"), PersistentDataType.INTEGER, food_level);
                        assert pType != null;
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("ptype"), PersistentDataType.STRING, pType);
                        assert type != null;
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("type"), PersistentDataType.STRING, type);
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("id"), PersistentDataType.STRING, args[2]);
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("particle"), PersistentDataType.STRING, particle);
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("particle_amount"), PersistentDataType.INTEGER, particle_amount);
                        assert particleE != null;
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("particlee"), PersistentDataType.STRING, particleE);
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("particlee_amount"), PersistentDataType.INTEGER, particleE_amount);
                        assert particleH != null;
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("particleh"), PersistentDataType.STRING, particleH);
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("particleh_amount"), PersistentDataType.INTEGER, particleH_amount);

                        item.setItemMeta(i_meta);
                        /* -===================================================================-

                         */

                        /* -===================================================================- */
                        // Give item & send message
                        this.nmsHandler.giveItem(p, item);
                        this.nmsHandler.sendMessage(player, this.nmsHandler.color(String.format("CustomFood > gave [%s&f] %s to %s.", name, count, p.getName())));
                        /* -===================================================================- */
                    } else {
                        this.nmsHandler.sendMessage(player, this.nmsHandler.color("CustomFood > &cERROR!"));
                    }
                }
                /* -===================================================================- */

                // Help command
                case "help" -> {
                    this.nmsHandler.sendMessage((Player) sender, "CustomFood > Help[1]");
                    this.nmsHandler.sendMessage((Player) sender, "======------COMMANDS--------========");
                    this.nmsHandler.sendMessage((Player) sender, "/cf give <player> <food-id> <count>");
                    this.nmsHandler.sendMessage((Player) sender, "/cf reload");
                    this.nmsHandler.sendMessage((Player) sender, "Plugin created by __AltDemono__");
                    this.nmsHandler.sendMessage((Player) sender, "CustomFood > Help[2]");
                }

                // reload command
                case "reload" -> {
                    this.reloadConfig();
                    this.nmsHandler.sendMessage((Player) sender, this.nmsHandler.color("CustomFood > &aReload complete!"));
                }
            }
        }else
        {
            this.nmsHandler.sendMessage((Player) sender, this.nmsHandler.color("CustomFood > &cERROR!"));
        }
        return true;
    }
    /* -===================================================================- */


    /* -===================================================================- */
    // Creating a new event
    @EventHandler
    public void onPlayerFoodEat(PlayerItemConsumeEvent e)
    {
            Player p = e.getPlayer();
            if (!p.getInventory().getItemInMainHand().getType().isAir() && !e.getItem().getItemMeta().getPersistentDataContainer().isEmpty() && p.getFoodLevel() <= 20) {
                if (e.getItem().getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("type"), PersistentDataType.STRING).equals("seed"))
                {
                    nmsHandler.sendMessage(p, this.nmsHandler.color("CustomFood > &cYou can't use this item!"));
                    e.setCancelled(true);
                } else {
                    e.setCancelled(true);

                    Location location = e.getPlayer().getLocation();

                    Location location_ = e.getPlayer().getLocation();
                    location_.setY(location.getY()+1);

                    PersistentDataContainer item = e.getItem().getItemMeta().getPersistentDataContainer();

                    Particle particle = Particle.valueOf(item.get(NamespacedKey.fromString("particlee"), PersistentDataType.STRING));
                    int particleAmount = item.get(NamespacedKey.fromString("particlee_amount"), PersistentDataType.INTEGER);
                    location_.getWorld().spawnParticle(particle, location_, particleAmount);

                    p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
                    e.getPlayer().setFoodLevel(p.getFoodLevel() + e.getItem().getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("food_level"), PersistentDataType.INTEGER));
                }
            }
    }


    /* -===================================================================- */

    /*                      CUSTOM FOOD PLUGIN
                             BY ALT DEMONO
    */

    /* -===================================================================- */


    /* -===================================================================- */
    // Version SETTER
    private String combineSplit(String[] split) {
        if (split.length == 0) {
            return "";
        } else if (split.length == 1) {
            return split[0];
        }
        StringBuilder builder = new StringBuilder();
        for (String s : split) {
            builder.append(s).append(' ');
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
    /* -===================================================================- */
}
