package frostbird347.wetslop.mixin.client;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import frostbird347.wetslop.MainMod;
import frostbird347.wetslop.MainModClient;
import frostbird347.wetslop.effect.EffectManager;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	boolean SHOULD_CHANGE_HEARTS = false;
	
	//Set this flag only when rendering the player's health bar
	@Inject(at = @At("HEAD"), method = "renderHealthBar")
	private void beginHealthBarRender(MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo info) {
		//If the saved age is somehow significantly larger than the player's real age then they might have respawned or something else weird might have happened.
		//It should be reset in this scenario to prevent weird behaviour
		if (player.age + 10 < MainModClient.SLOP_AGE) {
			MainMod.LOGGER.warn("CLIENT_SLOP_AGE was larger than the player's own age! (You should only see this message when respawning or changing dimensions/servers)");
			MainModClient.SLOP_AGE = -1;
		}
		SHOULD_CHANGE_HEARTS = player.hasStatusEffect(EffectManager.SLOPPIFIED) && (!player.isWet() || player.age - 10 < MainModClient.SLOP_AGE);
	}
	
	//Clear this flag so anything else calling drawHeart() won't be affected by the player's slop effect
	@Inject(at = @At("TAIL"), method = "renderHealthBar")
	private void endHealthBarRender(MatrixStack matrices, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo info) {
		SHOULD_CHANGE_HEARTS = false;
	}

	//Render custom heart graphics when the player has the slop effect and isn't in water
	@Inject(at = @At("HEAD"), method = "drawHeart", cancellable = true)
	private void drawSlopHeart(MatrixStack matrices, InGameHud.HeartType type, int x, int y, int v, boolean blinking, boolean halfHeart, CallbackInfo callback) {
		
		//If other mods didn't exist, I would have just used !(type == InGameHud.HeartType.CONTAINER)
		if (SHOULD_CHANGE_HEARTS && (type == InGameHud.HeartType.NORMAL || type == InGameHud.HeartType.POISIONED || type == InGameHud.HeartType.ABSORBING || type == InGameHud.HeartType.WITHERED || type == InGameHud.HeartType.FROZEN)) {

			//Store the current texture and temporarilly replace it when rendering the custom hearts
			int oldTexture = RenderSystem.getShaderTexture(0);
			RenderSystem.setShaderTexture(0, MainModClient.SLOP_HEART_TEXTURE);
			//I am too lazy to change the texture coordinates, the slop heart texture has the hearts at the same position as the regular hearts
			((InGameHud)(Object)this).drawTexture(matrices, x, y, type.getU(halfHeart, blinking), v, 9, 9);
			RenderSystem.setShaderTexture(0, oldTexture);
			callback.cancel();
		}
	}

}