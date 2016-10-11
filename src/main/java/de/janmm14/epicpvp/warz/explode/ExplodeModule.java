package de.janmm14.epicpvp.warz.explode;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.shampaggon.crackshot.events.WeaponExplodeEvent;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class ExplodeModule extends Module<ExplodeModule> implements Listener {

	private final static String PATH = "explode.";
	private final static String PATH_DAMAGE = PATH + "damage.";
	private final HashMap<String, Double> weapons = Maps.newHashMap();
	
	public ExplodeModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	@Override
	public void reloadConfig() {
		weapons.clear();
		
		getConfig().addDefault(PATH_DAMAGE + ".weaponTitle", 0.0);
		
		ConfigurationSection weaponSection = getConfig().getConfigurationSection( PATH_DAMAGE );
		for ( String key : weaponSection.getKeys( false ) ) {
			if ( key.startsWith( "weaponTitle" ) ) {
				continue;
			}
			weapons.put( key , weaponSection.getDouble( key ) );
		}
	}

	public void onDisable() {
		
	}
	
	@EventHandler
	public void damage(WeaponDamageEntityEvent ev){
		if(this.weapons.containsKey(ev.getWeaponTitle())){
			if ( WarZ.DEBUG ) System.out.println( "WeaponDamageEntityEvent -> Use " + ev.getWeaponTitle() +", Damage=["+this.weapons.get(ev.getWeaponTitle())+"]");
			if(this.weapons.get(ev.getWeaponTitle())<0){
				ev.setCancelled(true);
			}else{
				ev.setDamage( this.weapons.get(ev.getWeaponTitle()) );
			}
		}
	}
	
	@EventHandler
	public void weaponExp(WeaponExplodeEvent ev) {
		if ( WarZ.DEBUG ) System.out.println( "WeaponExplodeEvent -> Use " + ev.getWeaponTitle() );
		if ( ev.getWeaponTitle().equalsIgnoreCase( "Rauchgranate" ) ) {
			new BukkitRunnable() {
				int t = 0;

				public void run() {
					this.t += 1;
					if ( this.t > 10 ) {
						cancel();
					} else {
						ev.getLocation().getWorld().spigot().playEffect( ev.getLocation(), Effect.EXPLOSION_HUGE, 0, 0, 1.0F, 1.0F, 1.0F, 0.0F, 3, 100 );
					}
				}
			}
				.runTaskTimer( getPlugin(), 0L, 5L );
		}
	}
}
