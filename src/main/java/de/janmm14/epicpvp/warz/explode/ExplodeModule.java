package de.janmm14.epicpvp.warz.explode;

import org.bukkit.Effect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.shampaggon.crackshot.events.WeaponExplodeEvent;
import com.shampaggon.crackshot.events.WeaponPreShootEvent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import eu.epicpvp.kcore.Util.UtilWorldGuard;

public class ExplodeModule extends Module<ExplodeModule> implements Listener {

	public ExplodeModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	@Override
	public void reloadConfig() {
	}
	
	@EventHandler
	public void weaponExp(WeaponExplodeEvent ev) {
		
		if ( WarZ.DEBUG ) System.out.println( "Use " + ev.getWeaponTitle() );

		if ( ev.getWeaponTitle().equalsIgnoreCase( "Rauchgranate" ) ) {
			new BukkitRunnable() {
				int t = 0;

				public void run() {
					this.t += 5;
					if ( this.t > 50 ) {
						cancel();
						return;
					}
					ev.getLocation().getWorld().spigot().playEffect( ev.getLocation(), Effect.EXPLOSION_HUGE, 0, 0, 1.0F, 1.0F, 1.0F, 0.0F, 3, 100 );
				}
			}
				.runTaskTimer( getPlugin(), 0L, 5L );
		}
	}
}
