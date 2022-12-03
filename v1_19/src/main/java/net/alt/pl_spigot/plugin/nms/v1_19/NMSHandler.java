package net.alt.pl_spigot.plugin.nms.v1_19;

import net.alt.pl_spigot.plugin.api.NMS;
import net.minecraft.network.chat.IChatBaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class NMSHandler implements NMS {

    @Override
    public void sendMessage(Player player, String message) {
        for(IChatBaseComponent component : CraftChatMessage.fromString(message)) {
            CraftPlayer craftPlayer = (CraftPlayer) player;
            craftPlayer.sendMessage(message);
        }
    }
    @Override
    public void giveItem(Player player, ItemStack item) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getInventory().addItem(item);
    }

    @Override
    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
