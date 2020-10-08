package io.github.MinecraftSpaceProgram.MSP.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;

public final class MSPNBTUtils {

  /** Creates a new NBTTagCompound from a Vector3d. */
  public static CompoundNBT writeVector3d(Vector3d pos) {
    CompoundNBT compoundnbt = new CompoundNBT();
    compoundnbt.putDouble("X", pos.x);
    compoundnbt.putDouble("Y", pos.y);
    compoundnbt.putDouble("Z", pos.z);
    return compoundnbt;
  }

  /** Creates a Vector3d object from the data stored in the passed NBTTagCompound. */
  public static Vector3d readVector3d(CompoundNBT compoundNBT) {
    return new Vector3d(
        compoundNBT.getDouble("X"), compoundNBT.getDouble("Y"), compoundNBT.getDouble("Z"));
  }

  /** Creates a new NBTTagCompound from a Quaternion. */
  public static CompoundNBT writeQuaternion(Quaternion quaternion) {
    CompoundNBT compoundNBT = new CompoundNBT();
    compoundNBT.putFloat("X", quaternion.getX());
    compoundNBT.putFloat("Y", quaternion.getY());
    compoundNBT.putFloat("Z", quaternion.getZ());
    compoundNBT.putFloat("W", quaternion.getW());
    return compoundNBT;
  }

  /** Creates a Quaternion object from the data stored in the passed NBTTagCompound. */
  public static Quaternion readQuaternion(CompoundNBT compoundNBT) {
    return new Quaternion(
        compoundNBT.getFloat("X"),
        compoundNBT.getFloat("Y"),
        compoundNBT.getFloat("Z"),
        compoundNBT.getFloat("W"));
  }
}
