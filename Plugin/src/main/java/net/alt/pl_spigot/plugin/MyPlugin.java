package net.alt.pl_spigot.plugin;

import net.alt.pl_spigot.plugin.api.NMS;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

@SuppressWarnings("unused")

public class MyPlugin extends JavaPlugin implements CommandExecutor, Listener {

    private NMS nmsHandler;

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
        this.getLogger().info("Loading support for " + version + " ...");
        saveDefaultConfig();
        saveConfig();
        this.getCommand("cf").setExecutor(this);
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    // Commands
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            // Getting a player param
            Player player = (Player) sender;
            switch ((String) args[0])
            {
                // Give command
                case "give":
                    // cf give <player> <food-id> <count> ( -1 0 1 2 3) -1 is cf
                    this.getLogger().info(args[2]);
                    // Choosing a food material
                    Material material = switch (this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getString("material")) {
                        case "minecraft:apple" -> Material.APPLE;
                        case "minecraft:mushroom_stew" -> Material.MUSHROOM_STEW;
                        case "minecraft:bread" -> Material.BREAD;
                        case "minecraft:porkchop" -> Material.PORKCHOP;
                        case "minecraft:cooked_porkchop" -> Material.COOKED_PORKCHOP;
                        case "minecraft:cod" -> Material.COD;
                        case "minecraft:cookie" -> Material.COOKIE;
                        default -> null;
                    };
                    // Checking Food item
                    if(this.getConfig().getConfigurationSection("food").isConfigurationSection(args[2]) && material != null) {
                        // Creating a new food (item)
                        ItemStack item = new ItemStack(material);
                        ItemMeta i_meta = item.getItemMeta();

                        Player p = Bukkit.getPlayer(args[1]);

                        assert p != null;
                        this.getLogger().info(p.getDisplayName());

                        String name = this.nmsHandler.color(this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getString("name"));
                        int food_level = Integer.parseInt(this.getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getString("food_level"));

                        i_meta.setDisplayName(this.nmsHandler.color("&r" + name));
                        i_meta.getPersistentDataContainer().set(NamespacedKey.fromString("food_level"), PersistentDataType.INTEGER, food_level);
                        i_meta.setLore(getConfig().getConfigurationSection("food").getConfigurationSection(args[2]).getStringList("description"));
                        item.setItemMeta(i_meta);

                        int count = (args.length==3) ? 1 : Integer.parseInt(args[3]);
                        item.setAmount(count);

                        this.nmsHandler.giveItem(p, item);
                        this.nmsHandler.sendMessage(player, this.nmsHandler.color(String.format("CustomFood > gave [%s] %s to %s", name, count, p.getName())));
                        break;
                    }

                // Help command
                case "help":
                    this.nmsHandler.sendMessage((Player) sender, "CustomFood > Help[1]");
                    this.nmsHandler.sendMessage((Player) sender, "======------COMMANDS--------========");
                    this.nmsHandler.sendMessage((Player) sender, "/cf give <player> <food-id> <count>");
                    this.nmsHandler.sendMessage((Player) sender, "Plugin created by __AltDemono__");
                    this.nmsHandler.sendMessage((Player) sender, "CustomFood > Help[2]");
                    break;
            }
        }else
        {
            this.nmsHandler.sendMessage((Player) sender, "CustomFood > Error 01 > ");
        }
        return true;
    }

    // Creating a new event
    @EventHandler
    public void onPlayerFoodEat(PlayerItemConsumeEvent e)
    {
            Player p = e.getPlayer();
            if(!p.getInventory().getItemInMainHand().getType().isAir() && !e.getItem().getItemMeta().getPersistentDataContainer().isEmpty() && p.getFoodLevel() <= 20) {
                e.setCancelled(true);
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
                e.getPlayer().setFoodLevel(p.getFoodLevel() + e.getItem().getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("food_level"), PersistentDataType.INTEGER));
            }
    }

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

}
