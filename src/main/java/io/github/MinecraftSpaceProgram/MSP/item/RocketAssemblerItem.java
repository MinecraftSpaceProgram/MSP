package io.github.MinecraftSpaceProgram.MSP.item;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class RocketAssemblerItem extends Item {
    public RocketAssemblerItem() {
        super(new Properties().group(MSP.ITEM_GROUP).maxStackSize(1));
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }
}
