package frostbird347.wetslop;

import frostbird347.wetslop.fluid.FluidManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public class MainModClient implements ClientModInitializer {
	public static final Identifier SLOP_HEART_TEXTURE = new Identifier("bucket-of-wet-slop:textures/gui/slop_heart.png");
	public static int SLOP_AGE = -100;

	@Override
	public void onInitializeClient() {
		//Setup slop rendering
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(new Identifier("bucket-of-wet-slop:block/wet_slop_still"));
			registry.register(new Identifier("bucket-of-wet-slop:block/wet_slop_flowing"));
			registry.register(new Identifier("bucket-of-wet-slop:block/wet_slop_overlay"));
		});
		FluidRenderHandlerRegistry.INSTANCE.register(FluidManager.WET_SLOP, FluidManager.WET_SLOP_FLOWING, new SimpleFluidRenderHandler(
				new Identifier("bucket-of-wet-slop:block/wet_slop_still"),
				new Identifier("bucket-of-wet-slop:block/wet_slop_flowing"),
				new Identifier("bucket-of-wet-slop:block/wet_slop_overlay")
		));
		BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidManager.WET_SLOP, FluidManager.WET_SLOP_FLOWING);
	}
}