package frostbird347.wetslop.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Collection;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import frostbird347.wetslop.effect.EffectManager;

@Mixin(GlassBottleItem.class)
public class GlassBottleItemMixin extends Item {

	public GlassBottleItemMixin(Settings settings) {
		super(settings);
	}

	//Reimplement simplified vanilla bottle interaction code at the start to bypass the water check and hopefully keep it a little more compatible with other mods
	//Does make slop take preference over dragon's breath but who cares about that
	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	private void checkFluid(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> callback) {
		BlockHitResult hitResult = GlassBottleItem.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
		if (((HitResult)hitResult).getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = hitResult.getBlockPos();
			if (world.canPlayerModifyAt(user, blockPos) && world.getBlockState(blockPos).getBlock().getTranslationKey().equals("block.bucket-of-wet-slop.wet_slop")) {
				world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0f, 1.0f);
				world.emitGameEvent((Entity)user, GameEvent.FLUID_PICKUP, blockPos);

				//Reimplement vanilla fill code below because it's protected
				user.incrementStat(Stats.USED.getOrCreateStat(((GlassBottleItem)(Object)this)));
				
				//However add custom effects to the potion that will be removed when it's converted to a splash
				//Custom effects last for half as long as the create recipe, so there is still a reason to use up some of the liquid
				Collection<StatusEffectInstance> drinkEffects = List.of(new StatusEffectInstance(StatusEffects.NAUSEA, 100), new StatusEffectInstance(StatusEffects.HUNGER, 10), new StatusEffectInstance(StatusEffects.WEAKNESS, 50), new StatusEffectInstance(StatusEffects.POISON, 20));
				ItemStack filledBottle = PotionUtil.setCustomPotionEffects(PotionUtil.setPotion(new ItemStack(Items.POTION), EffectManager.SLOPPIFIED_POTION), drinkEffects);
				filledBottle.getOrCreateNbt().putInt("CustomPotionColor", PotionUtil.getColor(EffectManager.SLOPPIFIED_POTION));

				callback.setReturnValue(TypedActionResult.success(ItemUsage.exchangeStack(user.getStackInHand(hand), user, filledBottle), world.isClient()));
			}
		}
	}
}