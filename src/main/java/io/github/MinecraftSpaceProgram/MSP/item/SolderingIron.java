package io.github.MinecraftSpaceProgram.MSP.item;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.init.BlockLoader;
import io.github.MinecraftSpaceProgram.MSP.util.RocketBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class SolderingIron extends Item {
    public SolderingIron() {
        super(new Properties().maxStackSize(1).group(Main.mspItemGroup));
        this.setRegistryName("soldering_iron");
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        if (!context.getWorld().isRemote()) {

            if (context.getWorld().getBlockState(context.getPos()).getBlock() == BlockLoader.rocket_generic) {
                buildRocket(context);
                context.getPlayer().sendMessage(new TranslationTextComponent("event.msp.soldering_iron.success"));
                return ActionResultType.SUCCESS;
            }

            context.getPlayer().sendMessage(new TranslationTextComponent("event.msp.soldering_iron.fail"));
            return ActionResultType.FAIL;
        }
        return ActionResultType.PASS;
    }

    public void buildRocket(ItemUseContext context) {
        RocketBuilder rocketBuilder = new RocketBuilder(context.getWorld(), context.getPos());
    }
}
