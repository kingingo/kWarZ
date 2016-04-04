package de.janmm14.epicpvp.warz.crackshot;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import de.janmm14.epicpvp.warz.WarZ;

import static de.janmm14.epicpvp.warz.crackshot.CrackShotTweakModule.ARMOR_PREFIX;

public class WeaponDamageArmorAmplificationListener implements Listener {

	private static final double FAKE_WEAPON_DMG = 0.01D;
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

		if ( !( victimEntity instanceof LivingEntity ) ) {
			return;
		}

		double damagePercentage = 1;
		boolean headShot = module.isHeadOnlyHelmetReduction() ? isHeadShot( event ) : false;

		LivingEntity victim = ( LivingEntity ) victimEntity;
		String weaponTitle = event.getWeaponTitle();
		if ( headShot ) {
			ItemStack helmet = victim.getEquipment().getHelmet();
			damagePercentage = damagePercentage - getReductionPercentage( weaponTitle, helmet );
		} else {
			for ( ItemStack armorItem : victim.getEquipment().getArmorContents() ) {
				damagePercentage = damagePercentage - getReductionPercentage( weaponTitle, armorItem );
			}
		}
		damagePercentage = damagePercentage <= 0 ? 0 : damagePercentage;
		double reducedDmg = event.getDamage() * damagePercentage;

		reducedDmg = reducedDmg <= 0 ? 0 : Math.round( reducedDmg );

		if ( WarZ.DEBUG ) {
			Bukkit.broadcastMessage( event.getPlayer().getName() + " schoss auf " + event.getVictim().getName() + " mit " + event.getWeaponTitle() + ". Base-DMG: " + ( event.getDamage() ) + " halbe Herzen, Multiplikator (1-Rüstung): " + damagePercentage + ", Final-DMG:" + ( reducedDmg ) + " halbe Herzen" );
		}

		if ( reducedDmg > FAKE_WEAPON_DMG ) {
			reducedDmg = reducedDmg - FAKE_WEAPON_DMG;
			double health = victim.getHealth() - reducedDmg;
			victim.setHealth( health );
			event.setDamage( FAKE_WEAPON_DMG );
		} else {
			event.setDamage( 0 );
			event.setCancelled( true );
		}
	}

	private double getReductionPercentage(String weaponTitle, ItemStack armorItem) {
		if ( armorItem != null && armorItem.getType() != Material.AIR ) {
			String armorName = armorItem.getType().toString().toUpperCase();

			String path = ARMOR_PREFIX + weaponTitle.toLowerCase() + "." + armorName;
			if ( module.getPlugin().getConfig().get( path ) != null ) {
				return module.getPlugin().getConfig().getDouble( path );
			} else {
				return module.getPlugin().getConfig().getDouble( ARMOR_PREFIX + "default." + armorName );
			}
		}
		return 0;
	}
}
