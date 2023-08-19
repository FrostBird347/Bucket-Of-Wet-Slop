package frostbird347.wetslop.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public class PotionItemMixin extends Item {

	public PotionItemMixin(Settings settings) {
		super(settings);
	}

	//Makes the slop bottle not have an enchant glint
	//My reasoning for this change is below:
	//	- The method for obtaining the bottle is completely disconnected from brewing
	//	- The vanilla "potion" you obtain outside of brewing (water bottles) does not have an enchantment glint either, nor does it's splash/lingering variants
	//	- Spider eyes and pufferfish also give the player effects and they don't have an enchantment glint either
	//	- It just doesn't clearly look like a bottle of slop, it looks like a potion with the glint on. This is an issue water and honey bottles don't have, because they don't have a glint.
	@Inject(at = @At("HEAD"), method = "hasGlint", cancellable = true)
	private void hideSlopGlint(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
		String potionType = stack.getOrCreateNbt().getString("Potion");
		if (!super.hasGlint(stack) && potionType.equals("bucket-of-wet-slop:slop_exposure") || potionType.equals("bucket-of-wet-slop:boosted_slop_exposure")) {
			callback.setReturnValue(false);
		}
	}
}