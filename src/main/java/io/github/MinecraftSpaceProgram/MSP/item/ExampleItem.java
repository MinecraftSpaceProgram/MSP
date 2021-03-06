package io.github.MinecraftSpaceProgram.MSP.item;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.tileentity.ExampleTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ExampleItem extends Item {
    public ExampleItem() {
        super(new Properties().group(MSP.ITEM_GROUP));
    }

    @Override
    @Nonnull
    public ActionResultType onItemUse(final ItemUseContext context) {
        final World world = context.getWorld();
        final BlockPos blockPos = context.getPos();
        final TileEntity tileEntity = world.getTileEntity(blockPos);
        final PlayerEntity player = context.getPlayer();
        if (player == null) return super.onItemUse(context);

        if (!world.isRemote() && tileEntity instanceof ExampleTileEntity) {
            if (player.isCrouching()) {
                ((ExampleTileEntity) tileEntity).setActivated();
            }
            else {
                player.sendMessage(new StringTextComponent("Block " + (((ExampleTileEntity) tileEntity).getActivated()? "activated": "disabled")), player.getUniqueID());
            }
            return ActionResultType.SUCCESS;
        }

        return super.onItemUse(context);
    }
}
