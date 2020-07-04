package io.github.MinecraftSpaceProgram.MSP.item;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.init.BlockLoader;
import io.github.MinecraftSpaceProgram.MSP.util.HangarBuilder;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class SolderingIron extends Item {
    public SolderingIron() {
        super(new Properties().maxStackSize(1).group(Main.mspItemGroup));
        this.setRegistryName("soldering_iron");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getPos();
        Block block = world.getBlockState(blockPos).getBlock();
        PlayerEntity player = context.getPlayer();

        if (!world.isRemote()) {

            if (block == BlockLoader.hangar_corner) {
                buildHangar(world, blockPos, player);
                return ActionResultType.SUCCESS;
            }
            return ActionResultType.FAIL;
        }

        return ActionResultType.PASS;
    }

    public void buildHangar(World world, BlockPos blockPos, PlayerEntity player) {
        HangarBuilder hangarBuilder = new HangarBuilder(world, blockPos);

        if (hangarBuilder.getCorners() != null) {
            player.sendMessage(new TranslationTextComponent("event.msp.soldering_icon.hangar_found"));
        }
        else {
            player.sendMessage(new TranslationTextComponent("event.msp.soldering_iron.hangar_fail"));
        }
    }
}
