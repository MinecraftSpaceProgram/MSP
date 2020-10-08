package io.github.MinecraftSpaceProgram.MSP.network;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
  private static final String version = "1.0";

  public static final SimpleChannel channel =
      NetworkRegistry.newSimpleChannel(
          new ResourceLocation(MSP.MOD_ID, "network"),
          () -> version,
          it -> it.equals(version),
          it -> it.equals(version));

  public static void init() {
    channel.registerMessage(
        0,
        PlayerPositionMessage.class,
        PlayerPositionMessage::encode,
        PlayerPositionMessage::decode,
        PlayerPositionMessage::handle);
    channel.registerMessage(
        1,
        PlayerRotationSpeedMessage.class,
        PlayerRotationSpeedMessage::encode,
        PlayerRotationSpeedMessage::decode,
        PlayerRotationSpeedMessage::handle);
  }

  public static void sendTo(Object message, PlayerEntity player) {
    channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), message);
  }
}
