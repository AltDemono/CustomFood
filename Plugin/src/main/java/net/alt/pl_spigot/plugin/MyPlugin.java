package net.alt.pl_spigot.plugin;

import net.alt.pl_spigot.plugin.api.NMS;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("unused")

public class MyPlugin extends JavaPlugin implements CommandExecutor, Listener {

    private NMS nmsHandler;

    @Override
    public void onEnable() {
        String packageName = this.getServer().getClass().getPackage().getName();
        // Get full package string of CraftServer.
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
        this.getCommand("example").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            switch ((String) args[0])
            {
                case "give":
                    Objects.requireNonNull(this.getConfig().getConfigurationSection("food")).getConfigurationSection(args[1]);
                    Material material = switch (Objects.requireNonNull(Objects.requireNonNull(this.getConfig().getConfigurationSection("food").getConfigurationSection(args[1])).getString("material"))) {
                        case "minecraft:apple" -> Material.APPLE;
                        case "minecraft:mushroom_stew" -> Material.MUSHROOM_STEW;
                        case "minecraft:bread" -> Material.BREAD;
                        case "minecraft:porkchop" -> Material.PORKCHOP;
                        case "minecraft:cooked_porkchop" -> Material.COOKED_PORKCHOP;
                        case "minecraft:cod" -> Material.COD;
                        default -> null;
                    };
                    ItemStack item = new ItemStack(material);
                    ItemMeta i_meta = item.getItemMeta();
                    i_meta.setDisplayName(this.getConfig().getConfigurationSection("food").getConfigurationSection(args[1]).getString("name"));
                    item.setItemMeta(i_meta);
                    item.setAmount(Integer.parseInt(args[2]));
                    player.getInventory().setItemInMainHand(item);
                    this.nmsHandler.sendMessage(player, "CustomFood > You have taken " + this.getConfig().getConfigurationSection("food").getConfigurationSection(args[1]).getString("name"));
                    break;
            }
        }else
        {
            sender.sendMessage("ERROR!");
        }
        return true;
    }

    @EventHandler
    public void onPlayerFoodEat(PlayerInteractEvent e)
    {
     if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR))
        {
            Player p = e.getPlayer();
            if(!p.getInventory().getItemInMainHand().getType().isAir()) {
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
            }
        }
    }
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
