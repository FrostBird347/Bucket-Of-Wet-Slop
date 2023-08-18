package frostbird347.wetslop.effect;

import frostbird347.wetslop.MainMod;
import frostbird347.wetslop.damage.DamageManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.util.math.BlockPos;

public class Sloppified extends StatusEffect {

	Sloppified(StatusEffectCategory statusEffectCategory, int colour) {
		super(statusEffectCategory, colour);
	}

	@Override
	public boolean canApplyUpdateEffect(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyUpdateEffect(LivingEntity entity, int amplifier) {
		if (!entity.world.isClient && entity.isAlive()) {
			//Don't run when they are in the slop or water (allow water that if they tried to breathe the slop), or if they are a slime
			if ((!entity.isWet() || (amplifier == 7 && !(entity.isSubmergedInWater() && entity.world.getBlockState(new BlockPos((entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0)))).getBlock().getTranslationKey().equals("block.bucket-of-wet-slop.wet_slop")))) && !(entity instanceof SlimeEntity)) {
				//Calculate strength of effect
				int duration = entity.getStatusEffect(EffectManager.SLOPPIFIED).getDuration();
				float healthPercent = ((LivingEntity)entity).getHealth() / ((LivingEntity)entity).getMaxHealth();
				if (amplifier < 8) {
					healthPercent *= (0.01 * Math.pow(amplifier, 2) - 0.27 * amplifier + 1.5);
				//Clamp strength before the function breaks down (shouldn't ever see effects this strong in survival)
				} else {
					healthPercent = 0;
				}

				//Deal damage at a rate and level determined by how little health the entity has left, getting faster towards the end
				if (healthPercent < 0.75f) {
					                                                                //Don't deal damage if the entity is about to die from breathing slop
					if (entity.age % Math.round((827 * healthPercent) + 20) == 0 && !(amplifier == 7 && ((LivingEntity)entity).getAir() > -19 && ((LivingEntity)entity).getHealth() <= 2f)) {
						entity.damage(DamageManager.SLOPPIFIED_DAMAGE, (-1.75f * healthPercent + 1.438f));
					}
				}

				MainMod.LOGGER.info(Float.toString(healthPercent) + "	:	" + Double.toString(amplifier) + "	:	" + Integer.toString(duration));
			}
		}
	}

}