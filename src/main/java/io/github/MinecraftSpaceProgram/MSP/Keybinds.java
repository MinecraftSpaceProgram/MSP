package io.github.MinecraftSpaceProgram.MSP;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = MSP.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Keybinds {
  public static KeyBinding RotateXPlus= new KeyBinding("key.rotateXPlus", InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_I,"key.categories.MSP");
  public static KeyBinding RotateXMinus= new KeyBinding("key.rotateXMinus", InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_K,"key.categories.MSP");

  @SubscribeEvent
  public static void register(FMLCommonSetupEvent e)
  {
    ClientRegistry.registerKeyBinding(RotateXMinus);
    ClientRegistry.registerKeyBinding(RotateXPlus);
    MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
  }
}
