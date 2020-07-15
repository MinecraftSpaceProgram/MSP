package io.github.MinecraftSpaceProgram.MSP.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class RocketGenericBlock extends Block {
    public static final boolean isRocketBlock = true;

    public RocketGenericBlock() {
        super(Properties.create(Material.IRON));
    }
}
