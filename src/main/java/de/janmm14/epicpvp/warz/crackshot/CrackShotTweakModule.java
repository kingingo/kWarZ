package de.janmm14.epicpvp.warz.crackshot;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class CrackShotTweakModule extends Module<CrackShotTweakModule> {

	public static final String PATH_PREFIX = "crackshottweak.";
	public static final String ARMOR_PREFIX = PATH_PREFIX + "armor.";

	public CrackShotTweakModule(WarZ plugin) {
		super( plugin, WeaponDamageArmorListener::new );
	}

	public boolean isHeadOnlyHelmetReduction() {
		return getPlugin().getConfig().getBoolean( PATH_PREFIX + "headShotOnlyHelmet" );
	}

	@Override
	public void reloadConfig() {
		getPlugin().getConfig().addDefault( PATH_PREFIX + "headShotOnlyHelmet", true );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "default.LEATHER_BOOTS", 0.05 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "default.LEATHER_LEGGINGS", 0.05 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "default.LEATHER_CHESTPLATE", 0.05 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "default.LEATHER_HELMET", 0.05 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "waffenname.LEATHER_BOOTS", 0.15 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "waffenname.LEATHER_LEGGINGS", 0.15 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "waffenname.LEATHER_CHESTPLATE", 0.15 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "waffenname.LEATHER_HELMET", 0.15 );
	}
}
