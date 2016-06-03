package de.janmm14.epicpvp.warz.crackshot;

import java.util.concurrent.TimeUnit;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

import lombok.Getter;

public class CrackShotTweakModule extends Module<CrackShotTweakModule> {

	public static final String PATH_PREFIX = "crackshottweak.";
	public static final String ARMOR_PREFIX = PATH_PREFIX + "armor.";
	@Getter
	private long glassMillis = TimeUnit.SECONDS.toMillis( 30 );

	public CrackShotTweakModule(WarZ plugin) {
		super( plugin, WeaponDamageArmorListener::new, WeaponBlockHitListener::new );
	}

	public boolean isHeadOnlyHelmetReduction() {
		return getPlugin().getConfig().getBoolean( PATH_PREFIX + "headShotOnlyHelmet" );
	}

	@Override
	public void reloadConfig() {
		getPlugin().getConfig().addDefault( PATH_PREFIX + "headShotOnlyHelmet", true );

		getPlugin().getConfig().addDefault( PATH_PREFIX + "glassResetSeconds", 30 );
		glassMillis = TimeUnit.SECONDS.toMillis( getPlugin().getConfig().getInt( PATH_PREFIX + "glassResetSeconds" ) );

		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "default.LEATHER_BOOTS", 0.005 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "default.LEATHER_LEGGINGS", 0.005 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "default.LEATHER_CHESTPLATE", 0.005 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "default.LEATHER_HELMET", 0.005 );

		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "waffenname.LEATHER_BOOTS", 0.006 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "waffenname.LEATHER_LEGGINGS", 0.006 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "waffenname.LEATHER_CHESTPLATE", 0.006 );
		getPlugin().getConfig().addDefault( ARMOR_PREFIX + "waffenname.LEATHER_HELMET", 0.006 );
	}
}
