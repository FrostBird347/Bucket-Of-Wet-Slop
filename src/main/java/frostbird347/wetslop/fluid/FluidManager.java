package frostbird347.wetslop.fluid;

import net.minecraft.util.Identifier;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.registry.Registry;

public class FluidManager {
 
	public static final FlowableFluid WET_SLOP = new WetSlop.Still();
	public static final FlowableFluid WET_SLOP_FLOWING = new WetSlop.Flowing();

	public static void registerFluids() {
		Registry.register(Registry.FLUID, new Identifier("bucket-of-wet-slop", "wet_slop"), WET_SLOP);
		Registry.register(Registry.FLUID, new Identifier("bucket-of-wet-slop", "wet_slop_flowing"), WET_SLOP_FLOWING);
	}
	
}