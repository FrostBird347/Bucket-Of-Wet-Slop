package frostbird347.wetslop.fluid;

import frostbird347.wetslop.block.BlockManager;
import frostbird347.wetslop.item.ItemManager;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public abstract class WetSlop extends AbstractFluid {

	//Allow it to be replaced by water and lava
	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		if (fluid.matchesType(Fluids.WATER.getDefaultState().getFluid()) | fluid.matchesType(Fluids.LAVA.getDefaultState().getFluid())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getTickRate(WorldView worldView) {
		return 15;
	}


	@Override
	public Fluid getStill() {
		return FluidManager.WET_SLOP;
	}
 
	@Override
	public Fluid getFlowing() {
		return FluidManager.WET_SLOP_FLOWING;
	}
 
	@Override
	public Item getBucketItem() {
		return ItemManager.WET_SLOP_BUCKET;
	}
 
	@Override
	protected BlockState toBlockState(FluidState fluidState) {
		return BlockManager.WET_SLOP_BLOCK.getDefaultState().with(Properties.LEVEL_15, getBlockStateLevel(fluidState));
	}
 
	public static class Flowing extends WetSlop {
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
		}
 
		@Override
		public int getLevel(FluidState fluidState) {
			return fluidState.get(LEVEL);
		}
 
		@Override
		public boolean isStill(FluidState fluidState) {
			return false;
		}
	}
 
	public static class Still extends WetSlop {
		@Override
		public int getLevel(FluidState fluidState) {
			return 8;
		}
 
		@Override
		public boolean isStill(FluidState fluidState) {
			return true;
		}
	}
}