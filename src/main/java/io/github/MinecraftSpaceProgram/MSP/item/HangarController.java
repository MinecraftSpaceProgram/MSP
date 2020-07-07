package io.github.MinecraftSpaceProgram.MSP.item;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.tileentity.HangarCornerTileEntity;
import io.github.MinecraftSpaceProgram.MSP.util.Hangar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class HangarController extends Item implements IHangarController {
    private static final Logger LOGGER = LogManager.getLogger();

    public HangarController(Properties properties) {
        super(properties);
    }

    public HangarController() {
        super(new Properties().group(MSP.ITEM_GROUP).maxStackSize(1));
    }

    @Override
    public void linkHangar(final ItemStack itemStack, final BlockPos blockPos) {
        final CompoundNBT MSPtag = itemStack.getOrCreateChildTag("MSP");
        final CompoundNBT c = new CompoundNBT();
        c.putInt("x", blockPos.getX());
        c.putInt("y", blockPos.getY());
        c.putInt("z", blockPos.getZ());

        MSPtag.put("hangar", c);
        MSPtag.putBoolean("linked", true);
        itemStack.getOrCreateTag().put("MSP", MSPtag);
    }

    @Override
    @Nullable
    public Hangar getHangar(final ItemStack itemStack, final World world) {
        final CompoundNBT tag = itemStack.getOrCreateTag();
        if (!tag.contains("MSP")) return null;
        final CompoundNBT MSPtag = tag.getCompound("MSP");
        if (!MSPtag.contains("hangar")) return null;
        final CompoundNBT c = MSPtag.getCompound("hangar");
        int x = c.getInt("x");
        int y = c.getInt("y");
        int z = c.getInt("z");
        LOGGER.debug(String.format("Trying to find hangar at (%d,%d,%d)", x, y, z));

        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if (tileEntity instanceof HangarCornerTileEntity) {
            Hangar hangar = ((HangarCornerTileEntity) tileEntity).getAssociatedCorners();
            if (hangar == null) {
                LOGGER.debug("Could not find hangar");
                MSPtag.putBoolean("linked", false);
            }
            else {
                LOGGER.debug("Found " + hangar.toString());
                MSPtag.putBoolean("linked", true);
            }
            tag.put("MSP", MSPtag);
            return hangar;
        }

        MSPtag.putBoolean("linked", false);
        tag.put("MSP", MSPtag);
        return null;
    }

    @Override
    public void unlinkHangar(final Hand hand, final PlayerEntity player) {
        if (this.getHangar(player.getHeldItem(hand), player.world) == null)
            player.sendMessage(new TranslationTextComponent("event.msp.hangar_controller.not_linked"));
        else {
            player.sendMessage(new TranslationTextComponent("event.msp.hangar_controller.cleared"));
            ItemStack itemStack = player.getHeldItem(hand);
            CompoundNBT tag = itemStack.getOrCreateTag();
            CompoundNBT MSPtag = tag.getCompound("MSP");
            if (!MSPtag.isEmpty()) {
                if (MSPtag.contains("hangar")) MSPtag.remove("hangar");
                MSPtag.putBoolean("linked", false);
                tag.put("MSP", MSPtag);
            }
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (!context.getPlayer().world.isRemote) {
            if (context.getPlayer().isSneaking()) {
                this.unlinkHangar(context.getHand(), context.getPlayer());
            }
            else {
                Hangar hangar = this.getHangar(context.getItem(), context.getPlayer().world);
                if (hangar == null) {
                    context.getPlayer().sendMessage(new TranslationTextComponent("event.msp.hangar_controller.not_linked"));
                }
                else {
                    final BlockPos[] extremeBlocks = hangar.getExtremeCorners();
                    final String pos1 = String.format("(%d,%d,%d)", extremeBlocks[0].getX(), extremeBlocks[0].getY(), extremeBlocks[0].getZ());
                    final String pos2 = String.format("(%d,%d,%d)", extremeBlocks[1].getX(), extremeBlocks[1].getY(), extremeBlocks[1].getZ());
                    context.getPlayer().sendMessage(new TranslationTextComponent("event.msp.hangar_controller.found_again", pos1, pos2));
                }
            }
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote) {
            if (player.isSneaking()) {
                this.unlinkHangar(hand, player);
            }
            else {
                Hangar hangar = this.getHangar(player.getHeldItem(hand), world);
                if (hangar == null) {
                    player.sendMessage(new TranslationTextComponent("event.msp.hangar_controller.not_linked"));
                }
                else {
                    final BlockPos[] extremeBlocks = hangar.getExtremeCorners();
                    final String pos1 = String.format("(%d,%d,%d)", extremeBlocks[0].getX(), extremeBlocks[0].getY(), extremeBlocks[0].getZ());
                    final String pos2 = String.format("(%d,%d,%d)", extremeBlocks[1].getX(), extremeBlocks[1].getY(), extremeBlocks[1].getZ());
                    player.sendMessage(new TranslationTextComponent("event.msp.hangar_controller.found_again", pos1, pos2));
                }
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }

    public static int getItemColor(ItemStack itemStack, int tintIndex) {
        if (tintIndex == 0)
            return 0xFFFFFF;

        if (itemStack.getOrCreateTag().getCompound("MSP").getBoolean("linked"))
            return 0x32D314;
        else
            return 0xF02525;
    }
}
