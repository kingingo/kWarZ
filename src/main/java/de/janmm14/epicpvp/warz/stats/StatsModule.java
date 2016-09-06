package de.janmm14.epicpvp.warz.stats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import dev.wolveringer.client.Callback;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsChangedEvent;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import eu.epicpvp.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.Util.UtilScoreboard;
import eu.epicpvp.kcore.Util.UtilServer;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.ScoreboardAdapter;

public class StatsModule extends Module<StatsModule> implements Listener { //TODO /stats <name> | scoreboard

	private final StatsManager manager = StatsManagerRepository.getStatsManager( GameType.WARZ );

	public StatsModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	@Override
	public void reloadConfig() {

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		manager.loadPlayer( event.getPlayer() );
	}

	@EventHandler
	public void setsco(PlayerSetScoreboardEvent ev){
			Scoreboard scoreboard = ev.getPlayer().getScoreboard();
			Player plr = ev.getPlayer();
			UtilScoreboard.addBoard(scoreboard, DisplaySlot.SIDEBAR, "§b§lWarZ§7 - §6§lClashMC.eu");
			
			UtilScoreboard.setScore(scoreboard, "§7Kills", DisplaySlot.SIDEBAR, 9);
			UtilScoreboard.setScore(scoreboard, "§0§fLoading...", DisplaySlot.SIDEBAR, 8);
			UtilScoreboard.setScore(scoreboard, "§7Deaths", DisplaySlot.SIDEBAR, 7);
			UtilScoreboard.setScore(scoreboard, "§1§fLoading...", DisplaySlot.SIDEBAR, 6);
			UtilScoreboard.setScore(scoreboard, "§7Ratio", DisplaySlot.SIDEBAR, 5);
			UtilScoreboard.setScore(scoreboard, "§2§fLoading...", DisplaySlot.SIDEBAR, 4);
			
			manager.getAsync(plr, StatsKey.ANIMAL_KILLS, new Callback<Object>() {
				@Override
				public void call(Object obj, Throwable exception) {
					Bukkit.getScheduler().runTask(manager.getInstance(), new Runnable() {
						public void run() {
							UtilScoreboard.resetScore(plr.getScoreboard(), 8, DisplaySlot.SIDEBAR);
							UtilScoreboard.setScore(plr.getScoreboard(), "§0§f" + ((int) obj), DisplaySlot.SIDEBAR, 8);
						}
					});
					
					manager.getAsync(plr, StatsKey.DEATHS, new Callback<Object>() {
						@Override
						public void call(Object obj1, Throwable exception) {
							Bukkit.getScheduler().runTask(manager.getInstance(), new Runnable() {
								public void run() {
									UtilScoreboard.resetScore(plr.getScoreboard(), 6, DisplaySlot.SIDEBAR);
									UtilScoreboard.setScore(plr.getScoreboard(), "§1§f" + ((int) obj1), DisplaySlot.SIDEBAR, 6);

									UtilScoreboard.resetScore(plr.getScoreboard(), 4, DisplaySlot.SIDEBAR);
									UtilScoreboard.setScore(plr.getScoreboard(), "§2§f" + (((int) obj)/ (((int) obj1)+1) ), DisplaySlot.SIDEBAR, 4);
								}
							});
						}
					});
				}
			});
			plr.setScoreboard( scoreboard );
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onStatsChanged(PlayerStatsChangedEvent event) {
		if ( event.getManager().getType() != GameType.WARZ ) {
			return;
		}
		Player plr = Bukkit.getPlayer( UtilServer.getClient().getPlayer( event.getPlayerId() ).getUUID() );
		plr.getScoreboard().getObjective( DisplaySlot.SIDEBAR );
		int newVal = event.getManager().getInt( event.getPlayerId(), event.getStats() );
		switch ( event.getStats() ) {
			case ANIMAL_KILLS:
				UtilScoreboard.resetScore(plr.getScoreboard(), 8, DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(plr.getScoreboard(), "§0§f" + ((int) newVal), DisplaySlot.SIDEBAR, 8);
				break;
			case DEATHS:
				UtilScoreboard.resetScore(plr.getScoreboard(), 6, DisplaySlot.SIDEBAR);
				UtilScoreboard.setScore(plr.getScoreboard(), "§1§f" + ((int) newVal), DisplaySlot.SIDEBAR, 6);
				break;
			default:
				return;
		}
		int kills = event.getManager().getInt( event.getPlayerId(), StatsKey.ANIMAL_KILLS );
		int deaths = event.getManager().getInt( event.getPlayerId(), StatsKey.DEATHS );
		UtilScoreboard.resetScore(plr.getScoreboard(), 4, DisplaySlot.SIDEBAR);
		UtilScoreboard.setScore(plr.getScoreboard(), "§2§f" + ( kills / ( deaths + 1 ) ), DisplaySlot.SIDEBAR, 4);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		Player victim = event.getEntity();
		increaseStatistic( victim, StatsKey.DEATHS );
		Player killer = victim.getKiller();
		if ( killer == null ) {
			EntityDamageEvent lastDmg = victim.getLastDamageCause();
			if ( lastDmg == null ) {
				return;
			}
			if ( lastDmg instanceof EntityDamageByEntityEvent ) {
				EntityDamageByEntityEvent lastEntityDmg = ( EntityDamageByEntityEvent ) lastDmg;
				if ( lastEntityDmg.getDamager() instanceof Projectile ) {
					ProjectileSource shooter = ( ( Projectile ) lastEntityDmg.getDamager() ).getShooter();
					if ( shooter instanceof LivingEntity ) {
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
		increaseStatistic( killer, StatsKey.ANIMAL_KILLS );
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
