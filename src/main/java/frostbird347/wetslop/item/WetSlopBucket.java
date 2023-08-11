package frostbird347.wetslop.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WetSlopBucket extends BucketItem {
	private FlowableFluid fluidProxy;
	
	public WetSlopBucket(FlowableFluid fluid, Settings settings) {
		super(fluid, settings);
		fluidProxy = fluid;
	}

    //Reimplement vanilla water bucket place code to place a slime block if in the nether
	@Override
    public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult) {
        BlockState targetBlockState = world.getBlockState(pos);
        Block targetBlock = targetBlockState.getBlock();
        Material placeMaterial = targetBlockState.getMaterial();

        boolean placeCheckSimple = targetBlockState.canBucketPlace(fluidProxy);;
        boolean placeCheck = targetBlockState.isAir() || placeCheckSimple || targetBlock instanceof FluidFillable && ((FluidFillable)((Object)targetBlock)).canFillWithFluid(world, pos, targetBlockState, fluidProxy);
        if (!placeCheck) {
            return hitResult != null && this.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null);
        }

        if (!world.isClient && placeCheckSimple && !placeMaterial.isLiquid()) {
            world.breakBlock(pos, true);
        }

        if (world.getDimension().ultrawarm()) {
			ShapeContext placeContext = player == null ? ShapeContext.absent() : ShapeContext.of(player);
			if (!world.canPlace(Blocks.SLIME_BLOCK.getDefaultState(), pos, placeContext)) {
				return false;
			}
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
            world.playSound(player, pos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.random.nextFloat() - world.random.nextFloat()) * 0.8f);
            for (int l = 0; l < 8; ++l) {
                world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
            }
			world.setBlockState(pos, Blocks.SLIME_BLOCK.getDefaultState());

            return true;
        }

        if (world.setBlockState(pos, fluidProxy.getDefaultState().getBlockState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD) || targetBlockState.getFluidState().isStill()) {
            this.playEmptyingSound(player, world, pos);
            return true;
        }

        return false;
    }
}