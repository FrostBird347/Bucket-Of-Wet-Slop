package frostbird347.wetslop.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import frostbird347.wetslop.MainMod;

@Mixin(MinecraftClient.class)
public class MainClientMixin {
	@Inject(at = @At("HEAD"), method = "setWorld")
	private void changeWorld(CallbackInfo info) {
		//Update the stored UUID when joining a world instead of on game launch because there are mods that allow users to switch accounts ingame
		MainMod.CLIENT_UUID = ((MinecraftClient)(Object)this).player.getUuid();
	}
}