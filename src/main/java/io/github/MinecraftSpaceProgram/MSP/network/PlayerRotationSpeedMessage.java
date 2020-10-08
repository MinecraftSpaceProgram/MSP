package io.github.MinecraftSpaceProgram.MSP.network;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.util.MSPNBTUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PlayerRotationSpeedMessage {
  private final Quaternion PLAYER_ROTATION_SPEED;

  public PlayerRotationSpeedMessage(Quaternion quaternion) {
    this.PLAYER_ROTATION_SPEED = quaternion;
  }

  public static void encode(PlayerRotationSpeedMessage message, PacketBuffer buf) {
    buf.writeCompoundTag(MSPNBTUtils.writeQuaternion(message.PLAYER_ROTATION_SPEED));
  }

  public static PlayerRotationSpeedMessage decode(PacketBuffer buf) {
    Quaternion quaternion = MSPNBTUtils.readQuaternion(Objects.requireNonNull(buf.readCompoundTag()));
    return new PlayerRotationSpeedMessage(quaternion);
  }

  public static void handle(PlayerRotationSpeedMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    context.enqueueWork(() -> {
      MSP.LOGGER.debug("Received a Packet");
      MSP.proxy.setPlayerRotationSpeed(message.PLAYER_ROTATION_SPEED, context.getSender());
    });
    context.setPacketHandled(true);
  }
}
