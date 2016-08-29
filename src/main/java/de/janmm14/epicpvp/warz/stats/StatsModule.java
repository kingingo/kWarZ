package de.janmm14.epicpvp.warz.stats;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class StatsModule extends Module<StatsModule> implements Listener { //TODO /stats <name> | scoreboard

	private final StatsManager manager = StatsManagerRepository.getStatsManager( GameType.WARZ );

	public StatsModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	@Override
	public void reloadConfig() {

	}

//	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
//	public void onJoin(PlayerJoinEvent event) {
//
//	}

//	@EventHandler(priority = EventPriority.MONITOR)
//	public void onLeave(PlayerQuitEvent event) {
//
//	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		Player victim = event.getEntity();
		increaseStatistic( victim, StatsKey.DEATHS );
		Player killer = victim.getKiller();
		if ( killer == null ) {
			EntityDamageEvent lastDmg = victim.getLastDamageCause();
			if (lastDmg == null) {
				return;
			}
			if ( lastDmg instanceof EntityDamageByEntityEvent ) {
				EntityDamageByEntityEvent lastEntityDmg = ( EntityDamageByEntityEvent ) lastDmg;
				if ( lastEntityDmg.getDamager() instanceof Projectile ) {
					ProjectileSource shooter = ( ( Projectile ) lastEntityDmg.getDamager() ).getShooter();
					if (shooter instanceof LivingEntity) {
						LivingEntity shooterLiving = ( LivingEntity ) shooter;
						if ( shooterLiving instanceof Player ) {
							killer = ( Player ) shooterLiving;
						} else {
							increaseStatistic( victim, StatsKey.MONSTER_DEATHS );
						}
					}
				}
			}
		}
		if ( killer == null ) {
			return;
		}
		increaseStatistic( killer, StatsKey.KILLS );
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onMobDeath(EntityDeathEvent event) {
		LivingEntity victim = event.getEntity();
		if ( victim instanceof Player ) {
			return;
		}
		Player killer = victim.getKiller();
		if ( killer == null ) {
			return;
		}
		increaseStatistic( killer, StatsKey.MONSTER_KILLS );
	}

	private void increaseStatistic(Player killer, StatsKey statsKey) {
		manager.getAsync( killer, statsKey, (o, throwable) -> {
			if ( throwable != null ) {
				throwable.printStackTrace();
				return;
			}
			int amount = ( Integer ) o;
			manager.set( killer, statsKey, amount + 1 );
		} );
	}
}
