package io.github.MinecraftSpaceProgram.MSP.util;

import io.github.MinecraftSpaceProgram.MSP.rocket.Rocket;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockStorage {
    BlockState[][][] blockStates;
    FluidState[][][] fluidStates;
    ArrayList<TileEntity> tileEntities = new ArrayList<>();

    public final int x,y,z,sizeX,sizeY,sizeZ,numberOfBlocks;

    public BlockStorage(Rocket rocket) {
        World world = rocket.getWorld();
        List<BlockPos> blocksPos = rocket.getRocketBlocksPos();
        this.numberOfBlocks = blocksPos.size();

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
        this.fluidStates = new FluidState[sizeX][sizeY][sizeZ];

        for (BlockPos pos : blocksPos) {
            int dx = pos.getX() - minx;
            int dy = pos.getY() - miny;
            int dz = pos.getZ() - minz;
            blockStates[dx][dy][dz] = world.getBlockState(pos);
            fluidStates[dx][dy][dz] = world.getFluidState(pos);
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                tile.setPos(new BlockPos(dx,dy,dz));
                tileEntities.add(tile);
            }
        }
    }

    @Nullable
    public BlockState getBlockState(BlockPos pos) {
        if (pos.getX() < 0 || pos.getX() > sizeX || pos.getY() < 0 || pos.getY() > sizeY || pos.getZ() < 0 || pos.getZ() > sizeZ)
            return null;
        return blockStates[pos.getX()][pos.getY()][pos.getZ()];
    }

    public ArrayList<TileEntity> getTileEntityList() {
        return tileEntities;
    }
}
