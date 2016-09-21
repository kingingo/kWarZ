package de.janmm14.epicpvp.warz.explode;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.shampaggon.crackshot.events.WeaponExplodeEvent;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class ExplodeModule extends Module<ExplodeModule> implements Listener {

	public ExplodeModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	@Override
	public void reloadConfig() {
	}

	@EventHandler
	public void damage(WeaponDamageEntityEvent ev){
		if(ev.getWeaponTitle().equalsIgnoreCase("Rauchgranate")||ev.getWeaponTitle().equalsIgnoreCase("Blentgranate")){
			ev.setDamage(0);
			ev.setCancelled(true);
		}else if(ev.getWeaponTitle().equalsIgnoreCase("Granate")){
			ev.setDamage(15);
		}
	}
	
	@EventHandler
	public void weaponExp(WeaponExplodeEvent ev) {

		if ( WarZ.DEBUG ) System.out.println( "Use " + ev.getWeaponTitle() );

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
