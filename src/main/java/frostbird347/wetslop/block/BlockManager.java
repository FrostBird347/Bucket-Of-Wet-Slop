package frostbird347.wetslop.block;

import frostbird347.wetslop.fluid.FluidManager;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.util.Identifier;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.registry.Registry;

public class BlockManager {
 
	public static final Block WET_SLOP_BLOCK = new FluidBlock(FluidManager.WET_SLOP, FabricBlockSettings.copy(Blocks.WATER));

	public static void registerBlocks() {
		Registry.register(Registry.BLOCK, new Identifier("bucket-of-wet-slop", "wet_slop"), WET_SLOP_BLOCK);
	}
	
}