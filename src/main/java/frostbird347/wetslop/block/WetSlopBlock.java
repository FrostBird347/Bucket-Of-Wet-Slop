package frostbird347.wetslop.block;

import frostbird347.wetslop.damage.DamageManager;
import frostbird347.wetslop.effect.EffectManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;

public class WetSlopBlock extends FluidBlock {
 
	public WetSlopBlock(FlowableFluid fluid, Settings settings) {
		super(fluid, settings);
	}

	//This is where all the horrible mechanics take place!
	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {

		//Don't do anything below if we are on a singleplayer creative world: I don't want certain people to immediately find out that the slop is sinister :)
		//This will be removed in a future update
		//Until this code is removed, all releases will be set to alpha/pre-release
		if ((world.getServer() == null || world.getServer().isSingleplayer()) && world.getPlayers().size() == 1 && world.getPlayers().get(0).isCreative()) {
			return;
		}

		//Don't run on creative mode players
		if (entity.isPlayer() && ((PlayerEntity)entity).isCreative()) {
			return;
		}

		//Slimes are not debuffed by the slop
		if (!(entity instanceof SlimeEntity)) {

			//If moving, play occasional slop sounds and show slime particles
			boolean isMoving = entity instanceof LivingEntity && (entity.lastRenderX != entity.getX() || (entity.lastRenderY - entity.getY() <= 0.0024 && entity.lastRenderY != entity.getY()) || entity.lastRenderZ != entity.getZ() || (entity.isPlayer() && ((((PlayerEntity)entity).isDescending() && (entity.lastRenderY - entity.getY() != 0)) || ((PlayerEntity)entity).handSwinging)));
			if (world.isClient && isMoving && entity.age % 15 == 0) { 
				world.addParticle(ParticleTypes.ITEM_SLIME, true, entity.getX(), pos.getY() + 1, entity.getZ(), (Math.random() * 1.5f - 0.75f), (Math.random() * 0.75f), (Math.random() * 1.5f - 0.75f));
				entity.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 0.075f, 0.5f);
			}
			
			//Slow the movement when in slop and pull the entity down if it's underwater
			double slowMult = 1;
			double sinkSpeed = 0;
			if (entity.isSubmergedInWater() && !(entity instanceof WitherEntity)) {
				slowMult = 0.21;
				sinkSpeed = -0.025;
			}
			entity.slowMovement(state, new Vec3d(0.95 * slowMult, 0.95 * slowMult, 0.95 * slowMult));
			entity.addVelocity(0, sinkSpeed, 0);

			//Methods of directly killing living entities
			if (entity instanceof LivingEntity && !world.isClient && entity.isAlive()) {
				//Get the current state of the entity (Around once every 25 seconds an undead creature will be treated as a water breathing creature for a single tick, so some slop left out in the open won't get filled up with tons of trapped undead)
				boolean isUnderWater = (entity.isSubmergedInWater() && world.getBlockState(new BlockPos((entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0)))).getBlock().getTranslationKey().equals("block.bucket-of-wet-slop.wet_slop"));
				boolean isWaterEntity = ((LivingEntity)entity).canBreatheInWater() && (((LivingEntity)entity).getGroup() != EntityGroup.UNDEAD || Math.random() > 0.998);
				boolean hasWaterBreathing = ((LivingEntity)entity).hasStatusEffect(StatusEffects.WATER_BREATHING);
				boolean tryingToBreatheSlop = (isUnderWater && (isWaterEntity || hasWaterBreathing));
				float healthPercent = ((LivingEntity)entity).getHealth() / ((LivingEntity)entity).getMaxHealth();
				
				//Prevent water breathing from helping
				if (tryingToBreatheSlop) {
					if (entity.age % 20 == 0) {
						entity.damage(DamageManager.SLOP_DRINK_DAMAGE, 2f);
					}
				}
				
				if (isUnderWater) {
					//Make health appear lower than it is to future damage calculations
					healthPercent *= 0.80f;

					//Deal some extra custom damage to finish off any drowning entity right before they would otherwise die, thus showing a custom death message
					//Don't deal any damage to entities that are "tryingToBreatheSlop" to prevent the wrong death message from appearing
					if (!tryingToBreatheSlop && ((LivingEntity)entity).getAir() <= -19 && ((LivingEntity)entity).getHealth() <= 2f) {
						entity.damage(DamageManager.SLOP_DROWN_DAMAGE, 3);
					}

					//If they are a player, give them some effects
					boolean addExhaustion = (isMoving && (!((LivingEntity)entity).hasStatusEffect(StatusEffects.MINING_FATIGUE) || !((LivingEntity)entity).hasStatusEffect(StatusEffects.WEAKNESS)));
					if (entity.isPlayer() && (!((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS) || !((LivingEntity)entity).hasStatusEffect(StatusEffects.UNLUCK) || addExhaustion)) {
						((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 50, 0, false, false, false));
						((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.UNLUCK, 5, 0, false, false, false));
						if (addExhaustion) {
							((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 10, 1, false, false, false));
							((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 40, 1, false, false, false));
						}
					}
				}
				
				//Deal damage at a rate and level determined by how little health the entity has left, getting faster towards the end
				//Don't deal any damage to entities that are "tryingToBreatheSlop" to prevent the wrong death message from appearing
				if (healthPercent < 0.75f && !tryingToBreatheSlop) {
					if (entity.age % Math.round((827 * healthPercent) + 20) == 0) {
						entity.damage(DamageManager.SLOP_DAMAGE, (-1.75f * healthPercent + 1.438f));
					}
				}

				if (entity.isPlayer() && entity.isAlive()) {
					((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(EffectManager.SLOPPIFIED, 600, 0, false, true, true));
					if (isUnderWater) {
					((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(EffectManager.SLOPPIFIED, 1200, 0, false, true, true));
						((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(EffectManager.SLOPPIFIED, 600, 1, false, true, true));
						if (tryingToBreatheSlop) {
							((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(EffectManager.SLOPPIFIED, 300, 3, false, true, true));
							((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(EffectManager.SLOPPIFIED, 150, 7, false, true, true));
						}
					}
				}

			}
		//If the entity is a slime, buff them
		} else {
			//Get the slime's target
			LivingEntity target = ((SlimeEntity)entity).getTarget();
			boolean isUnderWater = entity.isSubmergedInWater();

			//Prevent them from drowning
			if (isUnderWater && entity.age % 20 == 0) {
				entity.setAir(entity.getMaxAir());
			}

			//If the slime has a target, allow them to swim under the slop
			if (target != null) {
				Vec3d slimePos = entity.getPos();
				Vec3d targetPos = target.getPos();
				Vec3d targetPosSurface = new Vec3d(targetPos.getX(), slimePos.getY(), targetPos.getZ());

				//Only dive down when they are over their target
				//This both increases surprise if the target is a player and it also prevents the slime from diving down too early and getting stuck
				if (!isUnderWater) {
					if (entity.age % 20 == 0 && targetPos.getY() < slimePos.getY() - 1 && slimePos.distanceTo(targetPosSurface) < 2) {
						entity.addVelocity(0, -0.05, 0);
					}
				//When it is underwater maintain y-level with the middle of the target
				} else {
					if (targetPos.getY() + (target.getHeight() / 2) < slimePos.getY()) {
						entity.addVelocity(0, -0.02, 0);
					}
				}
			}
		}
	}
}