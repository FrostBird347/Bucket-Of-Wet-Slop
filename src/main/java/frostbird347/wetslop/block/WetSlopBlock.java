package frostbird347.wetslop.block;

import java.util.List;

import frostbird347.wetslop.MainMod;
import frostbird347.wetslop.damage.DamageManager;
import frostbird347.wetslop.fluid.FluidManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;

public class WetSlopBlock extends FluidBlock {
 
	public WetSlopBlock(FlowableFluid fluid, Settings settings) {
		super(fluid, settings);
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!(entity instanceof SlimeEntity)) {
			//MainMod.LOGGER.info(Double.toString(((0.5 + Math.abs(Math.sin(world.getTime()))) * 100 )));
			boolean isMoving = (entity.lastRenderX != entity.getX() || (entity.lastRenderY - entity.getY() <= 0.0024 && entity.lastRenderY != entity.getY()) || entity.lastRenderZ != entity.getZ() || (entity.isPlayer() && ((((PlayerEntity)entity).isDescending() && (entity.lastRenderY - entity.getY() != 0)) || ((PlayerEntity)entity).handSwinging))) && entity instanceof LivingEntity;
			if (world.isClient && isMoving && entity.age % 15 == 0) { 
				world.addParticle(ParticleTypes.ITEM_SLIME, true, entity.getX(), pos.getY() + 1, entity.getZ(), (Math.random() * 1.5f - 0.75f), (Math.random() * 0.75f), (Math.random() * 1.5f - 0.75f));
				entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 0.075f, 0.5f);
			}
			
			double slowMult = 1;
			double sinkSpeed = 0;
			if (entity.isSubmergedInWater() && !(entity instanceof WitherEntity)) {
				slowMult = 0.21;
				sinkSpeed = -0.025;
			}
			entity.slowMovement(state, new Vec3d(0.95 * slowMult, 0.95 * slowMult, 0.95 * slowMult));
			entity.addVelocity(0, sinkSpeed, 0);

			if (entity instanceof LivingEntity && !world.isClient && entity.isAlive()) {
				boolean isUnderWater = entity.isSubmergedInWater();
				boolean isWaterEntity = ((LivingEntity)entity).canBreatheInWater();
				boolean hasWaterBreathing = ((LivingEntity)entity).hasStatusEffect(StatusEffects.WATER_BREATHING);
				
				if ((isWaterEntity && isUnderWater) || (isUnderWater && hasWaterBreathing)) {
					if (entity.age % 20 == 0) {
						entity.damage(DamageSource.DROWN, 0f);
						entity.damage(DamageManager.SLOP_DRINK_DAMAGE, 2f);
					}
				}
				float healthPercent = ((LivingEntity)entity).getHealth() / ((LivingEntity)entity).getMaxHealth();

				if (isUnderWater) {
					healthPercent *= 0.80f;
					if (((LivingEntity)entity).getAir() == -19 && ((LivingEntity)entity).getHealth() <= 2f && world.getBlockState(new BlockPos((entity.getPos().add(0, entity.getHeight(), 0)))).getBlock().getTranslationKey() != "block.minecraft.water" ) {
						entity.damage(DamageManager.SLOP_DROWN_DAMAGE, 3);
					}

					boolean canHunger = (isMoving && (!((LivingEntity)entity).hasStatusEffect(StatusEffects.HUNGER) || !((LivingEntity)entity).hasStatusEffect(StatusEffects.WEAKNESS)));
					
					if (entity.isPlayer() && (!((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS) || !((LivingEntity)entity).hasStatusEffect(StatusEffects.MINING_FATIGUE)) || canHunger) {
						((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 50, 0, true, false, false));
						((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20, 1, true, false, false));
						if (canHunger) {
							((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 40, 0, true, false, false));
							((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 1, true, false, false));
						}
					}
				}
				
				if (healthPercent < 0.75f) {
					if (entity.age % Math.round((827 * healthPercent) + 20) == 0) {
						entity.damage(DamageManager.SLOP_DAMAGE, (-1.75f * healthPercent + 1.438f));
					}
				}
			}
		} else {
			LivingEntity target = ((SlimeEntity)entity).getTarget();
			boolean isUnderWater = entity.isSubmergedInWater();

			if (isUnderWater && entity.age % 20 == 0) {
				entity.setAir(entity.getMaxAir());
			}

			if (target != null) {
				Vec3d slimePos = entity.getPos();
				Vec3d targetPos = target.getPos();
				Vec3d targetPosSurface = new Vec3d(targetPos.getX(), slimePos.getY(), targetPos.getZ());

				if (!isUnderWater) {
					if (entity.age % 20 == 0 && targetPos.getY() < slimePos.getY() - 1 && slimePos.distanceTo(targetPosSurface) < 2) {
						entity.addVelocity(0, -0.05, 0);
					}
				} else {
					if (targetPos.getY() + (target.getHeight() / 2) < slimePos.getY()) {
						entity.addVelocity(0, -0.02, 0);
					} else {
						//entity.addVelocity(0, 0.001, 0);
					}
				}
			}
		}
	}
}