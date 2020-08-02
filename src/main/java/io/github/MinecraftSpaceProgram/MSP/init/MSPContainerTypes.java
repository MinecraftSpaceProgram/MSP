package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.container.LaunchpadControllerContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MSPContainerTypes {
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, MSP.MOD_ID);

    public static final RegistryObject<ContainerType<LaunchpadControllerContainer>> LAUNCHPAD_CONTROLLER = CONTAINER_TYPES.register(
            "launchpad_controller",
            () -> IForgeContainerType.create(LaunchpadControllerContainer::new)
    );
}
