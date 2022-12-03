package net.alt.pl_spigot.plugin.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMS {
    public void sendMessage(Player player, String message);
    public void giveItem(Player player, ItemStack item);
    public String color(String text);
}
