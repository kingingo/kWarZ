package de.janmm14.epicpvp.warz.crackshot;

import static de.janmm14.epicpvp.warz.crackshot.CrackShotTweakModule.ARMOR_DAMAGE_PREFIX;
import static de.janmm14.epicpvp.warz.crackshot.CrackShotTweakModule.ARMOR_DURABILITY_PREFIX;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.shampaggon.crackshot.events.WeaponPreShootEvent;
import com.shampaggon.crackshot.events.WeaponPrepareShootEvent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import de.janmm14.epicpvp.warz.WarZ;
import eu.epicpvp.kcore.Util.UtilWorldGuard;

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
			helmet = reduceDurability( weaponTitle, helmet );
			victim.getEquipment().setHelmet( helmet );
			if ( WarZ.DEBUG ) {
				Bukkit.broadcastMessage( "Headshot!" );
			}
		} else {
			ItemStack[] armorContents = victim.getEquipment().getArmorContents();
			for ( int i = 0; i < armorContents.length; i++ ) {
				ItemStack armorItem = armorContents[ i ];
				damagePercentage = damagePercentage - getReductionPercentage( weaponTitle, armorItem );
				armorContents[ i ] = reduceDurability( weaponTitle, armorItem );
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
				victim.setHealth( 0.001 );
				event.setDamage( 1 );
			}
		} else {
			event.setDamage( 0 );
			event.setCancelled( true );
		}
	}

	@Nullable
	private ItemStack reduceDurability(String weaponTitle, ItemStack armorItem) {
		String path = ARMOR_DURABILITY_PREFIX + weaponTitle + "." + armorItem.getType().toString().toUpperCase();
		int lose_durability = 1;
		
		if( module.getConfig().get(path) != null ){
			lose_durability = module.getConfig().getInt(path);
		}else{
			lose_durability = module.getConfig().getInt(ARMOR_DURABILITY_PREFIX + "default." + armorItem.getType().toString().toUpperCase());
		}
		
		if ( WarZ.DEBUG ) {
			System.out.println( "itemstack = [" + armorItem.getType().name() + "], max-durability= [" + armorItem.getType().getMaxDurability() + "], durability= [" + armorItem.getDurability() + "], lose-durability= ["+lose_durability+"]" );
		}

		short durability = ( short ) ( armorItem.getDurability() + lose_durability );
		armorItem.setDurability( durability );
		return armorItem;
	}

	private double getReductionPercentage(String weaponTitle, ItemStack armorItem) {
		if ( armorItem != null && armorItem.getType() != Material.AIR ) {
			String armorName = armorItem.getType().toString().toUpperCase();

			String path = ARMOR_DAMAGE_PREFIX + weaponTitle + "." + armorName;
			if ( module.getConfig().get( path ) != null ) {
				double percentage = module.getConfig().getDouble( path );
				if ( WarZ.DEBUG ) {
					System.out.println( "weaponTitle = [" + weaponTitle + "], armorName = [" + armorName + "] -> reductionPercentage: " + percentage );
				}
				return percentage;
			} else {
				double percentage = module.getConfig().getDouble( ARMOR_DAMAGE_PREFIX + "default." + armorName );
				if ( WarZ.DEBUG ) {
					System.out.println( "weaponTitle = [" + weaponTitle + "], armorName = [" + armorName + "] -> reductionPercentage: " + percentage );
				}
				return percentage;
			}
		}
		return 0;
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		if ( isWeapon( event.getPlayer().getItemInHand().getType() ) ) {
			event.setCancelled( true );
		}
	}

	private boolean isWeapon(Material mat) {
		switch ( mat ) {
			case WOOD_HOE:
			case WOOD_AXE:
			case WOOD_SPADE:
			case WOOD_PICKAXE:
			case GOLD_HOE:
			case GOLD_AXE:
			case GOLD_SPADE:
			case GOLD_PICKAXE:
			case STONE_HOE:
			case STONE_AXE:
			case STONE_SPADE:
			case STONE_PICKAXE:
			case IRON_HOE:
			case IRON_AXE:
			case IRON_SPADE:
			case IRON_PICKAXE:
			case DIAMOND_HOE:
			case DIAMOND_AXE:
			case DIAMOND_SPADE:
			case DIAMOND_PICKAXE:
			case BLAZE_ROD:
				return true;
			default:
				return false;
		}
	}

	@EventHandler
	public void onShoot(WeaponPreShootEvent event) {
		if ( !UtilWorldGuard.RegionFlag( event.getPlayer(), DefaultFlag.PVP ) ) {
			event.setCancelled( true );
		}
	}

	@EventHandler
	public void onShoot(WeaponPrepareShootEvent event) {
		if ( !UtilWorldGuard.RegionFlag( event.getPlayer(), DefaultFlag.PVP ) ) {
			event.setCancelled( true );
		}
	}
}
