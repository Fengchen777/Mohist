package org.bukkit.craftbukkit.block;

import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.Inventory;

public class CraftShulkerBox extends CraftLootable<TileShulkerEntityBox> implements ShulkerBox {

    public CraftShulkerBox(final Block block) {
        super(block, TileShulkerEntityBox.class);
    }

    public CraftShulkerBox(final Material material, final TileShulkerEntityBox te) {
        super(material, te);
    }

    @Override
    public Inventory getSnapshotInventory() {
        return new CraftInventory(this.getSnapshot());
    }

    @Override
    public Inventory getInventory() {
        if (!this.isPlaced()) {
            return this.getSnapshotInventory();
        }

        return new CraftInventory(this.getTileEntity());
    }

    @Override
    public DyeColor getColor() {
        net.minecraft.block.Block block = CraftMagicNumbers.getBlock(this.getType());

        return DyeColor.getByWoolData((byte) ((ShulkerBoxBlock) block).color.getColorIndex());
    }
}