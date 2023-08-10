package frostbird347.wetslop;

import frostbird347.wetslop.fluid.FluidManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class MainModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		//Reuse water texture, tint it green
		FluidRenderHandlerRegistry.INSTANCE.register(FluidManager.WET_SLOP, FluidManager.WET_SLOP_FLOWING, new SimpleFluidRenderHandler(
				new Identifier("minecraft:block/water_still"),
				new Identifier("minecraft:block/water_flow"),
				0x4AB464
		));
 
		//BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), FluidManager.WET_SLOP, FluidManager.WET_SLOP_FLOWING);
	}
}