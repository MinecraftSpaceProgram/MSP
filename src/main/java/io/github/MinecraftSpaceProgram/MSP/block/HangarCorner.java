package io.github.MinecraftSpaceProgram.MSP.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class HangarCorner extends Block {
    public HangarCorner() {
        super(Properties.create(Material.IRON));
        this.setRegistryName("hangar_corner");
    }
}
