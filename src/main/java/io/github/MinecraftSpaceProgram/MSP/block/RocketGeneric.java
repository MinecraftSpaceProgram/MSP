package io.github.MinecraftSpaceProgram.MSP.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class RocketGeneric extends Block {
    public static final boolean isRocketBlock = true;

    public RocketGeneric() {
        super(Properties.create(Material.IRON));
        this.setRegistryName("rocket_generic");
    }
}
