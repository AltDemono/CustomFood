package net.alt.pl_spigot.plugin;

// In-plugin files
import net.alt.pl_spigot.plugin.api.NMS;
import net.alt.pl_spigot.plugin.crops.Crop;
import net.alt.pl_spigot.plugin.events.BlockBreakEvent;
import net.alt.pl_spigot.plugin.events.BlockPlace;

// Bukkit
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

// Java
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")

// Main file
public class MyPlugin extends JavaPlugin implements CommandExecutor, Listener {

    /* -===================================================================- */
    // nms handler
    private NMS nmsHandler;
    private HashMap<Block, Crop> crops = new HashMap<>();
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
            this.getLogger().severe("Could not find support for this Spigot version.");
            this.getLogger().info("Check for updates at URL HERE");
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
            switch (args[0])
            {
                // Give command
                case "give":
                    /* -===================================================================- */
                    // Choosing a food material
                    @Nullable
                    String mat = this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getString("material");
                    Material material = Material.getMaterial(mat);
                    /* -===================================================================- */


                    /* -===================================================================- */
                    // Checking Food item
                    if(this.getConfig().getConfigurationSection("food").isConfigurationSection(args[2]) && material != null && args.length == 4 || args.length == 3) {

                        /* -===================================================================- */
                        // Creating a new food (item)
                        ItemStack item = new ItemStack(material);
                        ItemMeta i_meta = item.getItemMeta();

                        String name = this.nmsHandler.color(this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getString("name"));
                        int food_level = Integer.parseInt(this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getString("food_level"));

                        String type = this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getString("type");
                        String pType = this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getConfigurationSection("place").getString("type");

                        // getting a player
                        Player p = Bukkit.getPlayer(args[1]);
                        /* -===================================================================- */


                        /* -===================================================================- */
                        i_meta.setDisplayName(this.nmsHandler.color("&r" + name));
                        i_meta.setLore(getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getStringList("description"));
                        i_meta.setCustomModelData(Integer.parseInt(this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getString("custom_model_data")));
                        // Set item meta
                        item.setItemMeta(i_meta);
                        // Setting count
                        int count = (args.length==3) ? 1 : Integer.parseInt(args[3]);
                        item.setAmount(count);
                        /* -===================================================================- */


                        /* -===================================================================- */
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("food_level"), PersistentDataType.INTEGER, food_level);
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("ptype"), PersistentDataType.STRING, pType);
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("type"), PersistentDataType.STRING, type);
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("id"), PersistentDataType.STRING, args[2]);
                        /* -===================================================================- */


                        /* -===================================================================- */
                        // Give item & send message
                        this.nmsHandler.giveItem(p, item);
                        this.nmsHandler.sendMessage(player, this.nmsHandler.color(String.format("CustomFood > gave [%s&f] %s to %s", name, count, p.getName())));
                        /* -===================================================================- */
                    }
                    else
                    {
                        this.nmsHandler.sendMessage(player, this.nmsHandler.color("CustomFood > &cERROR"));
                    }
                    break;
                /* -===================================================================- */

                // Help command
                case "help":
                    this.nmsHandler.sendMessage((Player) sender, "CustomFood > Help[1]");
                    this.nmsHandler.sendMessage((Player) sender, "======------COMMANDS--------========");
                    this.nmsHandler.sendMessage((Player) sender, "/cf give <player> <food-id> <count>");
                    this.nmsHandler.sendMessage((Player) sender, "/cf reload");
                    this.nmsHandler.sendMessage((Player) sender, "Plugin created by __AltDemono__");
                    this.nmsHandler.sendMessage((Player) sender, "CustomFood > Help[2]");
                    break;
                case "reload":
                    this.reloadConfig();
                    this.nmsHandler.sendMessage((Player) sender, "CustomFood > Plugin is reloaded");
                    break;
            }
        }else
        {
            this.nmsHandler.sendMessage((Player) sender, this.nmsHandler.color("CustomFood > &cERROR"));
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
