package io.github.MinecraftSpaceProgram.MSP.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ExampleBlock extends Block {
    public ExampleBlock(Properties properties) {
        super(properties);
    }

    public ExampleBlock() {
        super(Properties.create(Material.IRON));
    }
}
