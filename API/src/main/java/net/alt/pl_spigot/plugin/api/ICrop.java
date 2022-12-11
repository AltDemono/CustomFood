package net.alt.pl_spigot.plugin.api;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public interface ICrop {
    Material getSeedMaterial();
    Material getMaterial();
    void setSeedMaterial(Material m);
    void setMaterial(Material m);
    void addDropItem(ItemStack item, int chance);
    void removeDropItem(ItemStack item);
    void setBlock(Block block);
    Block getBlock();
    void setHologram(Block block);
    void removeHologram();
    void setName(String name);
    String getName();
    void setItem(ItemStack item);
    ItemStack getItem();
    HashMap getDropItems();
    void addAllowedBlock(Material material);
    void removeAllowedBlock(Material material);
    void dropItem();
    ArrayList getAllowedBlocks();

    // set a hologram

    void placeCrop(Block target, ItemStack itemInHand);
}
