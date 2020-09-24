package io.github.MinecraftSpaceProgram.MSP.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stage {
  public BlockStorage myStorage;
  public final List<Stage> children = new ArrayList<>();
  public final Stage parent;

  public Stage(Stage parent, BlockStorage myStorage) {
    this.myStorage = myStorage;
    if (parent != null) {
      parent.children.add(this);
      this.parent = parent;
    } else {
      this.parent = this;
    }
  }

  private Stage(Stage parent, BlockState[][][] storage, BlockPos origin){
    this(parent, null);
    List<BlockPos> myBlocks = new ArrayList<>();
    List<BlockPos> myInterfaces = new ArrayList<>();

    // if origin is a separator, finds the inside
    if(false){}
    // origin is now a block inside the other stage.

    List<BlockPos> pointers = new ArrayList<>(Collections.singletonList(origin));
    Vector3i[] directions = new Vector3i[]{
        new Vector3i(1,0,0),
        new Vector3i(0,0,-1),
        new Vector3i(0,0,1),
        new Vector3i(-1,0,0),
        new Vector3i(0,1,0),
        new Vector3i(0,-1,0)
    };

    // finds the shell of the stage and all the interfaces
    while(!pointers.isEmpty()){
      BlockPos pointer = pointers.get(0);
      pointers.remove(0);

      for (int i = 0; i < 6; i++) {
        BlockState inspected = storage[pointer.getX() + directions[i].getX()][pointer.getY() + directions[i].getY()][pointer.getZ() + directions[i].getZ()];
        if (inspected != null) {
          if (inspected.getBlock() != Blocks.IRON_BLOCK) { // TODO add a separator block
            // TODO check if separator is valid
            myInterfaces.add(pointer.add(directions[i]));
          } else if (inspected.getBlock() != Blocks.AIR) {
            pointers.add(pointer.add(directions[i]));
          }
        }
      }
      storage[pointer.getX()][pointer.getY()][pointer.getZ()] = null;
      myBlocks.add(pointer);
    }

    // Checks if horizontal separator is legal
    for (BlockPos hS: myInterfaces) {
      for (Vector3i direction : directions) {
        BlockState inspected = storage[hS.getX() + direction.getX()][hS.getY() + direction.getY()][hS.getZ() + direction.getZ()];
        if(inspected != null && !(inspected.getBlock() == Blocks.IRON_BLOCK || inspected.getBlock() == Blocks.AIR)){
          throw new IllegalStateException("YOU BUILT THE ROCKET LIKE A FUCKING DONKEY");
        }
      }
    }
  }

  public static void buildStagesFromStorage(Stage root){

  }
}
