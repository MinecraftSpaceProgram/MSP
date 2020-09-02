package io.github.MinecraftSpaceProgram.MSP.util;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.rocket.Rocket;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockStorage {
    BlockState[][][] blockStates;
    //FluidState[][][] fluidStates;
    //ArrayList<TileEntity> tileEntities = new ArrayList<>();

    public final int x, y, z, sizeX, sizeY, sizeZ, numberOfBlocks;


    public BlockStorage(Rocket rocket) {
        World world = rocket.getWorld();
        List<BlockPos> blocksPos = rocket.getRocketBlocksPos();
        this.numberOfBlocks = blocksPos.size();
        MSP.LOGGER.debug(this.numberOfBlocks);

        int minx = blocksPos.get(0).getX();
        int miny = blocksPos.get(0).getY();
        int minz = blocksPos.get(0).getZ();
        int maxx = minx;
        int maxy = miny;
        int maxz = minz;
        for (BlockPos pos : blocksPos) {
            if (pos.getX() < minx) minx = pos.getX();
            if (pos.getY() < miny) miny = pos.getY();
            if (pos.getZ() < minz) minz = pos.getZ();
            if (pos.getX() > maxx) maxx = pos.getX();
            if (pos.getY() > maxy) maxy = pos.getY();
            if (pos.getZ() > maxz) maxz = pos.getZ();
        }
        this.x = minx;
        this.y = miny;
        this.z = minz;
        this.sizeX = maxx - minx + 1;
        this.sizeY = maxy - miny + 1;
        this.sizeZ = maxz - minz + 1;

        this.blockStates = new BlockState[sizeX][sizeY][sizeZ];
        //this.fluidStates = new FluidState[sizeX][sizeY][sizeZ];

        for (BlockPos pos : blocksPos) {
            int dx = pos.getX() - minx;
            int dy = pos.getY() - miny;
            int dz = pos.getZ() - minz;
            blockStates[dx][dy][dz] = world.getBlockState(pos);
            //fluidStates[dx][dy][dz] = world.getFluidState(pos);
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                tile.setPos(new BlockPos(dx, dy, dz));
                //tileEntities.add(tile);
            }
        }
    }

    public BlockStorage(BlockState[][][] blockStates, int x, int y, int z, int sizeX, int sizeY, int sizeZ, int numberOfBlocks) {
        this.blockStates = blockStates;
        this.x = x;
        this.y = y;
        this.z = z;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.numberOfBlocks = numberOfBlocks;
    }

    public BlockStorage(BlockStorage blockStorage) {
        this(
                blockStorage.blockStates,
                blockStorage.x,
                blockStorage.y,
                blockStorage.z,
                blockStorage.sizeX,
                blockStorage.sizeY,
                blockStorage.sizeZ,
                blockStorage.numberOfBlocks);
    }

    @Nullable
    public BlockState getBlockState(BlockPos pos) {
        if (pos.getX() < 0 || pos.getX() > sizeX || pos.getY() < 0 || pos.getY() > sizeY || pos.getZ() < 0 || pos.getZ() > sizeZ)
            return null;
        return blockStates[pos.getX()][pos.getY()][pos.getZ()];
    }

    //public ArrayList<TileEntity> getTileEntityList() {
    //    return tileEntities;
    //}


    public CompoundNBT toNBT() {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putInt("x", this.x);
        compoundnbt.putInt("y", this.y);
        compoundnbt.putInt("z", this.z);
        compoundnbt.putInt("sizeX", this.sizeX);
        compoundnbt.putInt("sizeY", this.sizeY);
        compoundnbt.putInt("sizeZ", this.sizeZ);
        compoundnbt.putInt("numberOfBlocks", this.numberOfBlocks);

        for (int x = 0; x < this.sizeX; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeZ; z++) {
                    if (this.blockStates[x][y][z] != null) {
                        compoundnbt.put("BlockState[" + x + "][" + y + "][" + z + "]", NBTUtil.writeBlockState(this.blockStates[x][y][z]));
                        MSP.LOGGER.debug("key: " + (int) compoundnbt.getTagId("BlockState[" + x + "][" + y + "][" + z + "]"));
                    }
                }
            }
        }
        return compoundnbt;
    }

    public BlockStorage(CompoundNBT compoundNBT) {
        MSP.LOGGER.debug("from nbt");
        this.x = compoundNBT.getInt("x");
        this.y = compoundNBT.getInt("y");
        this.z = compoundNBT.getInt("z");
        this.sizeX = compoundNBT.getInt("sizeX");
        this.sizeY = compoundNBT.getInt("sizeY");
        this.sizeZ = compoundNBT.getInt("sizeZ");
        this.numberOfBlocks = compoundNBT.getInt("numberOfBlocks");

        this.blockStates = new BlockState[sizeX][sizeY][sizeZ];

        int numberOfBlocksActual = 0;
        for (int x = 0; x < this.sizeX; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeZ; z++) {
                    if (compoundNBT.contains("BlockState[" + x + "][" + y + "][" + z + "]", 10)) {
                        this.blockStates[x][y][z] = NBTUtil.readBlockState(compoundNBT.getCompound("BlockState[" + x + "][" + y + "][" + z + "]"));
                        numberOfBlocksActual++;
                    } else {
                        this.blockStates[x][y][z] = null;
                    }
                }
            }
        }

        if (numberOfBlocksActual != this.numberOfBlocks) {
            MSP.LOGGER.error("----- NBT TRANSLATION FAILED -----");
        }
    }
}
