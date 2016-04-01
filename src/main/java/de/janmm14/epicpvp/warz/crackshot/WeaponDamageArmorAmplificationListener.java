package de.janmm14.epicpvp.warz.crackshot;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

import static de.janmm14.epicpvp.warz.crackshot.CrackShotTweakModule.ARMOR_PREFIX;

public class WeaponDamageArmorAmplificationListener implements Listener {

	private final CrackShotTweakModule module;

	private static Field headShotField;

	static {
		try {
			headShotField = WeaponDamageEntityEvent.class.getDeclaredField( "headShot" );
			headShotField.setAccessible( true );
		}
		catch ( NoSuchFieldException e ) {
			e.printStackTrace();
			headShotField = null;
		}
	}

	public WeaponDamageArmorAmplificationListener(CrackShotTweakModule module) {
		this.module = module;
	}

	private static boolean isHeadShot(WeaponDamageEntityEvent event) {
		try {
			return ( boolean ) headShotField.get( event );
		}
		catch ( IllegalAccessException e ) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("SimplifiableConditionalExpression")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWeaponDamage(WeaponDamageEntityEvent event) {
		Entity victimEntity = event.getVictim();
		boolean headShot = module.isHeadShotSpecial() ? isHeadShot( event ) : false;
		double reduction = 0;

		if ( !( victimEntity instanceof LivingEntity ) ) {
			return;
		}

		LivingEntity victim = ( LivingEntity ) victimEntity;
		String weaponTitle = event.getWeaponTitle();
		if (headShot) {
			reduction += getReduction( weaponTitle, victim.getEquipment().getHelmet() );
		} else {
			for ( ItemStack armorItem : victim.getEquipment().getArmorContents() ) {
				reduction += getReduction( weaponTitle, armorItem );
			}
		}

		event.setDamage( event.getDamage() * reduction );
	}

	public double getReduction(String weaponTitle, ItemStack armor) {
		String path = ARMOR_PREFIX + weaponTitle + "." + armor.getType();
		if ( module.getPlugin().getConfig().get( path ) != null ) {
			return module.getPlugin().getConfig().getDouble( path );
        } else {
			return module.getPlugin().getConfig().getDouble( ARMOR_PREFIX + "default." + armor.getType() );
        }
	}
}
