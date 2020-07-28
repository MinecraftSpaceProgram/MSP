package io.github.MinecraftSpaceProgram.MSP.api;

import io.github.MinecraftSpaceProgram.MSP.rocket.Hangar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IHangarController {

    /**
     * Link hangar, record to tag metadata
     * @param itemStack item
     * @param blockPos main hangar corner position
     */
    void linkHangar(ItemStack itemStack, BlockPos blockPos);

    /**
     * Get linked hangar from tag metadata
     * @param itemStack item
     * @param world world
     * @return HangarCorners or null
     */
    Hangar getHangar(ItemStack itemStack, World world);

    /**
     * Unlink hangar, remove from tag metadata
     */
    void unlinkHangar(Hand hand, PlayerEntity player);
}
