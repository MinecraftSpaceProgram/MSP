package io.github.MinecraftSpaceProgram.MSP.util;

import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public final class VoxelShapesUtil {

    /**
     * Rotate any VoxelShape in cube [[0,0,0],[1,1,1]] along axis X
     * @param voxelShape VoxelShape to be rotated
     * @param times Rotation, positive (negative) for positive (negative) rotation angle
     * @return rotated VoxelShape
     */
    public static VoxelShape rotateX(VoxelShape voxelShape, int times) {
        final VoxelShape[] rotatedShape = {voxelShape, VoxelShapes.empty()};

        if (times == 0) return voxelShape;
        if (times > 0) {
            for (int i = 0; i < times; i++) {
                rotatedShape[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> rotatedShape[1] = VoxelShapes.or(
                        rotatedShape[1],
                        VoxelShapes.create(
                                minX, minZ, 1 - maxY,
                                maxX, maxZ, 1 - minY
                        )
                ));
                rotatedShape[0] = rotatedShape[1];
                rotatedShape[1] = VoxelShapes.empty();
            }
        }
        if (times < 0) {
            for (int i = 0; i < - times; i++) {
                rotatedShape[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> rotatedShape[1] = VoxelShapes.or(
                        rotatedShape[1],
                        VoxelShapes.create(
                                minX, 1 - maxZ, minY,
                                maxX, 1 - minZ, maxY
                        )
                ));
                rotatedShape[0] = rotatedShape[1];
                rotatedShape[1] = VoxelShapes.empty();
            }
        }

        return rotatedShape[0];
    }

    /**
     * Rotate any VoxelShape in cube [[0,0,0],[1,1,1]] along axis Y
     * @param voxelShape VoxelShape to be rotated
     * @param times Number of rotations, positive (negative) for positive (negative) rotation angle
     * @return rotated VoxelShape
     */
    public static VoxelShape rotateY(VoxelShape voxelShape, int times) {
        final VoxelShape[] rotatedShape = {voxelShape, VoxelShapes.empty()};

        if (times == 0) return voxelShape;
        if (times > 0) {
            for (int i = 0; i < times; i++) {
                rotatedShape[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> rotatedShape[1] = VoxelShapes.or(
                        rotatedShape[1],
                        VoxelShapes.create(
                                1 - maxZ, minY, minX,
                                1 - minZ, maxY, maxX
                        )
                ));
                rotatedShape[0] = rotatedShape[1];
                rotatedShape[1] = VoxelShapes.empty();
            }
        }
        if (times < 0) {
            for (int i = 0; i < - times; i++) {
                rotatedShape[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> rotatedShape[1] = VoxelShapes.or(
                        rotatedShape[1],
                        VoxelShapes.create(
                                minZ, minY, 1 - maxX,
                                maxZ, maxY, 1 - minX
                        )
                ));
                rotatedShape[0] = rotatedShape[1];
                rotatedShape[1] = VoxelShapes.empty();
            }
        }

        return rotatedShape[0];
    }
}
