package io.github.MinecraftSpaceProgram.MSP.rocket;

public interface IRocketEngine {
  /** @return the fuel type used by the engine */
  IRocketTank.fuelTypes getFuelType();

  /** @return the maximum thrust of the engine */
  float getThrust();

  /** @return the ISP of the engine */
  float getFlowRate();
}
