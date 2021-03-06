package io.github.MinecraftSpaceProgram.MSP.block;

import io.github.MinecraftSpaceProgram.MSP.init.MSPTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class ExampleTileEntityBlock extends Block {
    public ExampleTileEntityBlock() {
        super(Properties.create(Material.ROCK));
    }

    @Override
    public boolean hasTileEntity(BlockState blockState) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState blockState, IBlockReader world) {
        return MSPTileEntityTypes.EXAMPLE_TILEENTITY.get().create();
    }
}
