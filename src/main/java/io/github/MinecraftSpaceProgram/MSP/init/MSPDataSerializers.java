package io.github.MinecraftSpaceProgram.MSP.init;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.util.BlockStorage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
@ParametersAreNonnullByDefault
public class MSPDataSerializers {
  public static final DeferredRegister<DataSerializerEntry> DATA_SERIALIZERS =
      DeferredRegister.create(ForgeRegistries.DATA_SERIALIZERS, MSP.MOD_ID);

  public static final IDataSerializer<Quaternion> QUATERNION_SERIALIZER =
      new IDataSerializer<Quaternion>() {

        @Override
        public void write(PacketBuffer buf, Quaternion q) {
          buf.writeFloat(q.getX());
          buf.writeFloat(q.getY());
          buf.writeFloat(q.getZ());
          buf.writeFloat(q.getW());
        }

        @Override
        public Quaternion read(PacketBuffer buf) {
          try {
            return new Quaternion(
                buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
          } catch (IndexOutOfBoundsException e) {
            // This function would throw anyway, might as well wrap the error with more relevant
            // info
            throw new RuntimeException(
                "packet buffer does not contain enough data to construct plane's Quaternion", e);
          }
        }

        @Override
        public Quaternion copyValue(Quaternion q) {
          return new Quaternion(q);
        }
      };

  public static final IDataSerializer<List<BlockPos>> BLOCK_POS_LIST_SERIALIZER =
      new IDataSerializer<List<BlockPos>>() {

        @Override
        public void write(PacketBuffer buf, List<BlockPos> list) {
          CompoundNBT compound = new CompoundNBT();
          BlockPos[] array = list.toArray(new BlockPos[] {});
          int size = list.size();
          compound.putInt("size", size);
          for (int i = 0; i < size; i++) {
            compound.put("element" + i, NBTUtil.writeBlockPos(array[i]));
          }
          buf.writeCompoundTag(compound);
        }

        @Override
        public List<BlockPos> read(PacketBuffer buf) {
          CompoundNBT compound = buf.readCompoundTag();
          BlockPos[] array = new BlockPos[0];
          if (compound != null) {
            int size = compound.getInt("size");
            array = new BlockPos[size];
            for (int i = 0; i < size; i++) {
              array[i] = NBTUtil.readBlockPos(compound.getCompound("element" + i));
            }
          } else{
            MSP.LOGGER.debug("WTFFFFFFFFFFFFFFFFFFf");
          }
          return Arrays.asList(array.clone());
        }

        @Override
        public List<BlockPos> copyValue(List<BlockPos> list) {
          return new ArrayList<>(list);
        }
      };

  public static final IDataSerializer<BlockStorage> STORAGE_SERIALIZER =
      new IDataSerializer<BlockStorage>() {

        @Override
        public void write(PacketBuffer buf, BlockStorage storage) {
          buf.writeCompoundTag(storage.toNBT());
        }

        @Override
        public BlockStorage read(PacketBuffer buf) {
          CompoundNBT compound = buf.readCompoundTag();
          if (compound != null) {
            return new BlockStorage(compound);
          } else {
            throw new IllegalArgumentException("buffer does not contain a compoundNBT");
          }
        }

        @Override
        public BlockStorage copyValue(BlockStorage storage) {
          return new BlockStorage(storage);
        }
      };


  public static final RegistryObject<DataSerializerEntry> QUAT_SERIALIZER =
      DATA_SERIALIZERS.register("quaternion", () -> new DataSerializerEntry(QUATERNION_SERIALIZER));
  public static final RegistryObject<DataSerializerEntry> STORE_SERIALIZER =
      DATA_SERIALIZERS.register("storage", () -> new DataSerializerEntry(STORAGE_SERIALIZER));
  public static final RegistryObject<DataSerializerEntry> BLOCK_POS_SERIALIZER =
      DATA_SERIALIZERS.register("blockpos", () -> new DataSerializerEntry(BLOCK_POS_LIST_SERIALIZER));
}
