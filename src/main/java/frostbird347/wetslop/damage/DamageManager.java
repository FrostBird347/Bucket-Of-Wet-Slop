package frostbird347.wetslop.damage;

public class DamageManager {
	
	public static final AbstractDamage SLOP_DAMAGE = (AbstractDamage)new AbstractDamage("wet_slop").setBypassesArmor().setUnblockable();
	public static final AbstractDamage SLOP_DROWN_DAMAGE = (AbstractDamage)new AbstractDamage("wet_slop_drowning").setBypassesArmor().setUnblockable();
	public static final AbstractDamage SLOP_DRINK_DAMAGE = (AbstractDamage)new AbstractDamage("wet_slop_drinking").setBypassesArmor().setUnblockable();
	public static final AbstractDamage SLOPPIFIED_DAMAGE = (AbstractDamage)new AbstractDamage("wet_slop_exposure").setBypassesArmor().setUnblockable();
	
}