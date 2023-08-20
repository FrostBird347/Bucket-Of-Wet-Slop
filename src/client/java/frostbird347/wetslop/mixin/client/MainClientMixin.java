package frostbird347.wetslop.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import frostbird347.wetslop.MainMod;

@Mixin(MinecraftClient.class)
public class MainClientMixin {
	@Inject(at = @At("HEAD"), method = "tick")
	private void updateUUID(CallbackInfo info) {
		//Update the stored UUID every 5 seconds instead of on game launch because there are mods that allow users to switch accounts ingame
		if (((MinecraftClient)(Object)this).player != null && ((MinecraftClient)(Object)this).player.age % (20*5) == 0) {
			MainMod.CLIENT_UUID = ((MinecraftClient)(Object)this).player.getUuid();
		}
	}
}