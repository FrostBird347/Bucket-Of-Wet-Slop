package frostbird347.wetslop.item;

import frostbird347.wetslop.fluid.FluidManager;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.util.Identifier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class ItemManager {
 
	public static final WetSlopBucket WET_SLOP_BUCKET = new WetSlopBucket(FluidManager.WET_SLOP, new FabricItemSettings().recipeRemainder(Items.BUCKET).maxCount(1).group(ItemGroup.MISC));

	public static void registerItems() {
		Registry.register(Registry.ITEM, new Identifier("bucket-of-wet-slop", "wet_slop_bucket"), WET_SLOP_BUCKET);
	}
	
}