package io.github.MinecraftSpaceProgram.MSP.rocket;

public interface IRocketTank { // extends IFluidTank {
  /** the accepted fuelTypes */
  enum fuelTypes {HYDRAZINE}

  /** @return the fuel capacity of the tank */
  float getCapacity();

  /** @return the amount of fuel in the tank */
  float getFuelLevel();

  /** @return the fuel type of the tank */
  fuelTypes getFuelType();
}
