package io.github.MinecraftSpaceProgram.MSP.tileentity;

public interface IRocketTank { // extends IFluidTank {
  /** the accepted fuelTypes */
  String[] fuelTypes = new String[] {"HYDRAZINE"};

  /** @return the fuel capacity of the tank */
  float getCapacity();

  /** @return the amount of fuel in the tank */
  float getFuelLevel();

  /** @return the fuel type of the tank */
  String getFuelType();
}
