package io.github.MinecraftSpaceProgram.MSP.item;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.tileentity.ExampleTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class ExampleItem extends Item {
    public ExampleItem() {
        super(new Properties().group(Main.MSPItemGroup.instance));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getPos();
        TileEntity tileEntity = world.getTileEntity(blockPos);
        PlayerEntity player = context.getPlayer();

        if (!world.isRemote() && tileEntity instanceof ExampleTileEntity) {
            if (player.isSneaking()) {
                ((ExampleTileEntity) tileEntity).setActivated();
            }
            else {
                player.sendMessage(new StringTextComponent("Block " + (((ExampleTileEntity) tileEntity).getActivated()? "activated": "disabled")));
            }
        }

        return ActionResultType.FAIL;
    }
}
