package frostbird347.wetslop.fluid;

import java.util.Optional;

import frostbird347.wetslop.block.BlockManager;
import frostbird347.wetslop.item.ItemManager;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class WetSlop extends AbstractFluid {

	//Allow water to destroy wet slop fluids if the water is either coming from the side or if the wet slop fluid isn't a source block
	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockView blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		boolean isWater = fluid.matchesType(Fluids.WATER.getDefaultState().getFluid());
		if (isWater | fluid.matchesType(Fluids.LAVA.getDefaultState().getFluid())) {
			if (isWater && this.isStill(fluidState)) {
				return (direction.getName() != "down");
			}
			return true;
		} else {
			return false;
		}
	}

	//Rarely play lava bubble sounds and show popping bubbles at the surface
	//The rate is increased a when the block light level increases 
	@Override
	public void randomDisplayTick(World world, BlockPos pos, FluidState state, Random random) {
		BlockPos abovePos = pos.up();
		if (world.getBlockState(abovePos).isAir() && !world.getBlockState(abovePos).isOpaqueFullCube(world, abovePos)) {
			int bubbleRate = Math.max(1, 16 - world.getLightLevel(LightType.BLOCK, pos));
			if (random.nextInt(bubbleRate * 63) == 0) {
                double x = (double)pos.getX() + random.nextDouble();
                double y = (double)pos.getY() + random.nextDouble();
                double z = (double)pos.getZ() + random.nextDouble();
                world.addParticle(ParticleTypes.BUBBLE_POP, x, y + 0.125, z, 0.0, random.nextDouble() * 0.01f, 0.0);
                world.playSound(x, y, z, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.05f + random.nextFloat() * 0.25f, 0.001f + random.nextFloat() * 0.005f, true);
            } 
        }
    }

	//Use the fish bucket fill sound effect because that sounds the closest to a mix of slime and water
    @Override
    public Optional<SoundEvent> getBucketFillSound() {
        return Optional.of(SoundEvents.ITEM_BUCKET_FILL_FISH);
    }

	//Faster than overworld lava, but slower than nether lava
	@Override
	public int getTickRate(WorldView worldView) {
		return 15;
	}

	//Same as overworld lava
	@Override
	protected int getFlowSpeed(WorldView worldView) {
		return 2;
	}

	//Same as overworld lava
	@Override
	protected int getLevelDecreasePerBlock(WorldView worldView) {
		return 2;
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