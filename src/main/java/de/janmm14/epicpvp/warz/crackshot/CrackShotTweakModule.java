package de.janmm14.epicpvp.warz.crackshot;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import eu.epicpvp.kcore.Util.UtilItem;
import lombok.Getter;

public class CrackShotTweakModule extends Module<CrackShotTweakModule> implements Listener{

	public static final String PATH_PREFIX = "crackshottweak.";
	public static final String ARMOR_DAMAGE_PREFIX = PATH_PREFIX + "armor.damage.";
	public static final String ARMOR_DURABILITY_PREFIX = PATH_PREFIX + "armor.durability.";
	@Getter
	private long glassMillis = TimeUnit.SECONDS.toMillis( 30 );
	private boolean headShotOnlyHelmet;

	public CrackShotTweakModule(WarZ plugin) {
		super( plugin, WeaponDamageArmorListener::new, BlockBreakListener::new, module -> module );
		UtilItem.modifyMaxStack(Material.COAL, 16);
	}
	
	@EventHandler
	public void click(InventoryClickEvent ev){
		if(ev.getCurrentItem()!=null&&ev.getCurrentItem().getType()==Material.COAL
				||ev.getCursor()!=null&&ev.getCursor().getType()==Material.COAL){
			Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
				
				@Override
				public void run() {
					((Player)ev.getWhoClicked()).updateInventory();
				}
			}, 1L);
		}
	}

	public boolean isHeadOnlyHelmetReduction() {

		return headShotOnlyHelmet;
	}
	
	public void onDisable() {
		
	}

	@Override
	public void reloadConfig() {
		getConfig().addDefault( PATH_PREFIX + "headShotOnlyHelmet", true );
		headShotOnlyHelmet = getConfig().getBoolean( PATH_PREFIX + "headShotOnlyHelmet" );

		getConfig().addDefault( PATH_PREFIX + "glassResetSeconds", 30 );
		glassMillis = TimeUnit.SECONDS.toMillis( getConfig().getInt( PATH_PREFIX + "glassResetSeconds" ) );

		getConfig().addDefault( ARMOR_DAMAGE_PREFIX + "default.LEATHER_BOOTS", 0.005 );
		getConfig().addDefault( ARMOR_DAMAGE_PREFIX + "default.LEATHER_LEGGINGS", 0.005 );
		getConfig().addDefault( ARMOR_DAMAGE_PREFIX + "default.LEATHER_CHESTPLATE", 0.005 );
		getConfig().addDefault( ARMOR_DAMAGE_PREFIX + "default.LEATHER_HELMET", 0.005 );

		getConfig().addDefault( ARMOR_DAMAGE_PREFIX + "waffenname.LEATHER_BOOTS", 0.006 );
		getConfig().addDefault( ARMOR_DAMAGE_PREFIX + "waffenname.LEATHER_LEGGINGS", 0.006 );
		getConfig().addDefault( ARMOR_DAMAGE_PREFIX + "waffenname.LEATHER_CHESTPLATE", 0.006 );
		getConfig().addDefault( ARMOR_DAMAGE_PREFIX + "waffenname.LEATHER_HELMET", 0.006 );
		
		getConfig().addDefault( ARMOR_DURABILITY_PREFIX + "default.LEATHER_BOOTS", 1 );
		getConfig().addDefault( ARMOR_DURABILITY_PREFIX + "default.LEATHER_LEGGINGS", 1 );
		getConfig().addDefault( ARMOR_DURABILITY_PREFIX + "default.LEATHER_CHESTPLATE", 1 );
		getConfig().addDefault( ARMOR_DURABILITY_PREFIX + "default.LEATHER_HELMET", 1 );

		getConfig().addDefault( ARMOR_DURABILITY_PREFIX + "waffenname.LEATHER_BOOTS", 1 );
		getConfig().addDefault( ARMOR_DURABILITY_PREFIX + "waffenname.LEATHER_LEGGINGS", 1 );
		getConfig().addDefault( ARMOR_DURABILITY_PREFIX + "waffenname.LEATHER_CHESTPLATE", 1 );
		getConfig().addDefault( ARMOR_DURABILITY_PREFIX + "waffenname.LEATHER_HELMET", 1 );
	}
}
