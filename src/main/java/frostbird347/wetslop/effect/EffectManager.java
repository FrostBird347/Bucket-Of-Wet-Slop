package frostbird347.wetslop.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EffectManager {
	
	public static final StatusEffect SLOPPIFIED = (StatusEffect)new Sloppified(StatusEffectCategory.HARMFUL, 1612143);
	
	public static Potion SLOPPIFIED_POTION;
	
	public static void registerEffects() {
		Registry.register(Registry.STATUS_EFFECT, new Identifier("bucket-of-wet-slop", "slop_exposure"), SLOPPIFIED);

		//Also set up and register custom potion type
		StatusEffectInstance slopPotionDefs[] = { new StatusEffectInstance(SLOPPIFIED, 600, 0) };
		SLOPPIFIED_POTION = new Potion(slopPotionDefs);
		Registry.register(Registry.POTION, new Identifier("bucket-of-wet-slop", "slop_exposure"), SLOPPIFIED_POTION);
	}
	
}