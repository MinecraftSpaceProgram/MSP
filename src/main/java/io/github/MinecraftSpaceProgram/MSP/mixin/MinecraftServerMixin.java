package io.github.MinecraftSpaceProgram.MSP.mixin;

import io.github.MinecraftSpaceProgram.MSP.MSP;
import io.github.MinecraftSpaceProgram.MSP.init.MSPDimensions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.IServerConfiguration;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
  @Final
  @Shadow protected DynamicRegistries.Impl field_240767_f_;
  @Final
  @Shadow protected IServerConfiguration field_240768_i_;

  /*
   * MinecraftServer#func_240800_l__
   */
  @Inject(at = @At("HEAD"), method = "func_240800_l__()V")
  private void initServer(CallbackInfo callback) {
    MSP.LOGGER.debug("MIXIN IN BUISNESS");
    MSPDimensions.init(
        this.field_240768_i_.func_230418_z_().func_236224_e_(), // getDimensionGeneratorSettings
        this.field_240767_f_.func_243612_b(Registry.DIMENSION_TYPE_KEY),
        this.field_240767_f_.func_243612_b(Registry.BIOME_KEY),
        this.field_240767_f_.func_243612_b(Registry.field_243549_ar),
        this.field_240768_i_.func_230418_z_().func_236221_b_()); // seeds
  }
}
