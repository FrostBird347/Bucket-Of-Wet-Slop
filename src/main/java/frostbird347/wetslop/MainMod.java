package frostbird347.wetslop;

import net.fabricmc.api.ModInitializer;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import frostbird347.wetslop.block.BlockManager;
import frostbird347.wetslop.effect.EffectManager;
import frostbird347.wetslop.fluid.FluidManager;
import frostbird347.wetslop.item.ItemManager;

public class MainMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("bucket-of-wet-slop");
	public static int CLIENT_SLOP_AGE = -100;
	public static UUID CLIENT_UUID = UUID.randomUUID();

	@Override
	public void onInitialize() {
		// Register stuff
		EffectManager.registerEffects();
		ItemManager.registerItems();
		BlockManager.registerBlocks();
		FluidManager.registerFluids();
	}
}