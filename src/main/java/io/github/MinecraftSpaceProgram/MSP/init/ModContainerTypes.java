package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.Main;
import io.github.MinecraftSpaceProgram.MSP.container.FlightComputerContainer;
import io.github.MinecraftSpaceProgram.MSP.container.GUIBlockContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, Main.MOD_ID);

    public static final RegistryObject<ContainerType<FlightComputerContainer>> FLIGHT_COMPUTER = CONTAINER_TYPES.register(
            "flight_computer",
            () -> IForgeContainerType.create(FlightComputerContainer::new));

    public static final RegistryObject<ContainerType<GUIBlockContainer>> GUI_BLOCK = CONTAINER_TYPES.register(
            "gui_block",
            () -> IForgeContainerType.create(GUIBlockContainer::new));

}
