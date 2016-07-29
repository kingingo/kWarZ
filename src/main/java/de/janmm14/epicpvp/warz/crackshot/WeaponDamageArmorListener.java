package de.janmm14.epicpvp.warz.crackshot;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

import de.janmm14.epicpvp.warz.WarZ;

import org.jetbrains.annotations.Nullable;

import static de.janmm14.epicpvp.warz.crackshot.CrackShotTweakModule.ARMOR_PREFIX;

public class WeaponDamageArmorListener implements Listener {

	private static final double FAKE_EVENT_DMG = 0.01D;
	private final CrackShotTweakModule module;

	public WeaponDamageArmorListener(CrackShotTweakModule module) {
		this.module = module;
	}

	@SuppressWarnings("SimplifiableConditionalExpression")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onWeaponDamage(WeaponDamageEntityEvent event) {
		Entity victimEntity = event.getVictim();

		if ( !( victimEntity instanceof LivingEntity ) ) {
			return;
		}

		double damagePercentage = 1;
		boolean headShot = module.isHeadOnlyHelmetReduction() ? event.isHeadshot() : false;

		LivingEntity victim = ( LivingEntity ) victimEntity;
		String weaponTitle = event.getWeaponTitle();
		if ( headShot ) {
			ItemStack helmet = victim.getEquipment().getHelmet();
			damagePercentage = damagePercentage - getReductionPercentage( weaponTitle, helmet );
			helmet = reduceDurability( helmet );
			victim.getEquipment().setHelmet( helmet );
		} else {
			ItemStack[] armorContents = victim.getEquipment().getArmorContents();
			for ( int i = 0; i < armorContents.length; i++ ) {
				ItemStack armorItem = armorContents[ i ];
				damagePercentage = damagePercentage - getReductionPercentage( weaponTitle, armorItem );
				armorContents[ i ] = reduceDurability( armorItem );
			}
			victim.getEquipment().setArmorContents( armorContents );
		}
		damagePercentage = damagePercentage <= 0 ? 0 : damagePercentage;
		double reducedDmg = event.getDamage() * damagePercentage;

		reducedDmg = reducedDmg <= 0 ? 0 : Math.round( reducedDmg );

		if ( WarZ.DEBUG ) {
			Bukkit.broadcastMessage( event.getPlayer().getName() + " schoss auf " + event.getVictim().getName() + " mit " + event.getWeaponTitle() + ". Base-DMG: " + ( event.getDamage() ) + " halbe Herzen, Multiplikator (1-Rüstung): " + damagePercentage + ", Final-DMG:" + ( reducedDmg ) + " halbe Herzen" );
		}

		if ( reducedDmg > FAKE_EVENT_DMG ) {
			double health = victim.getHealth() - reducedDmg;
			if ( health >= FAKE_EVENT_DMG ) {
				victim.setHealth( health + FAKE_EVENT_DMG );
				event.setDamage( FAKE_EVENT_DMG );
			} else { //player is going to die, but since crackshot applies default armor, set victim health to 1 half heart and the damage ridiculous high to force the death
				victim.setHealth( 1 );
				event.setDamage( 1000 );
			}
		} else {
			event.setDamage( 0 );
			event.setCancelled( true );
		}
	}

	@Nullable
	private ItemStack reduceDurability(ItemStack stack) {
		short durability = ( short ) ( stack.getDurability() - 1 );
		if ( durability > 0 ) {
			stack.setDurability( durability );
		} else {
			stack = null;
		}
		return stack;
	}

	private double getReductionPercentage(String weaponTitle, ItemStack armorItem) {
		if ( armorItem != null && armorItem.getType() != Material.AIR ) {
			String armorName = armorItem.getType().toString().toUpperCase();

			String path = ARMOR_PREFIX + weaponTitle.toLowerCase() + "." + armorName;
			if ( module.getConfig().get( path ) != null ) {
				return module.getConfig().getDouble( path );
			} else {
				return module.getConfig().getDouble( ARMOR_PREFIX + "default." + armorName );
			}
		}
		return 0;
	}
}
