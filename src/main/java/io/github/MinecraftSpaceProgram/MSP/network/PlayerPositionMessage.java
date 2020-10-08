package io.github.MinecraftSpaceProgram.MSP.network;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.util.MSPNBTUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PlayerPositionMessage {
  private final Vector3d PLAYER_POSITION;
  private final Quaternion PLAYER_ROTATION;

  public PlayerPositionMessage(Vector3d pos, Quaternion quaternion) {
    this.PLAYER_POSITION = pos;
    this.PLAYER_ROTATION = quaternion;
  }

  public static void encode(PlayerPositionMessage message, PacketBuffer buf) {
    buf.writeCompoundTag(MSPNBTUtils.writeVector3d(message.PLAYER_POSITION));
    buf.writeCompoundTag(MSPNBTUtils.writeQuaternion(message.PLAYER_ROTATION));
  }

  public static PlayerPositionMessage decode(PacketBuffer buf) {
    Vector3d position = MSPNBTUtils.readVector3d(Objects.requireNonNull(buf.readCompoundTag()));
    Quaternion quaternion = MSPNBTUtils.readQuaternion(Objects.requireNonNull(buf.readCompoundTag()));
    return new PlayerPositionMessage(position, quaternion);
  }

  public static void handle(PlayerPositionMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    context.enqueueWork(() -> {
      MSP.proxy.setPlayerPosition(message.PLAYER_POSITION, message.PLAYER_ROTATION);
    });
    context.setPacketHandled(true);
  }
}
