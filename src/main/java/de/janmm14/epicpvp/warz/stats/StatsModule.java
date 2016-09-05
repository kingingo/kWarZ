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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.dataserver.gamestats.StatsKey;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsChangedEvent;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsLoadedEvent;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.Util.UtilServer;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.ScoreboardAdapter;

public class StatsModule extends Module<StatsModule> implements Listener { //TODO /stats <name> | scoreboard

	private final StatsManager manager = StatsManagerRepository.getStatsManager( GameType.WARZ );
	private final Map<UUID, ScoreboardAdapter> scoreboardAdapters = new HashMap<>();

	public StatsModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	@Override
	public void reloadConfig() {

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerStatsLoadedEvent event) {
		if ( event.getManager().getType() != GameType.WARZ ) {
			return;
		}
		getPlugin().getServer().getScheduler().runTask( getPlugin(), () -> {
			Scoreboard scoreboard = getPlugin().getServer().getScoreboardManager().getNewScoreboard();
			Objective sidebar = scoreboard.registerNewObjective( "clashmc_warz", "dummy" );
			Player plr = Bukkit.getPlayer( UtilServer.getClient().getPlayer( event.getPlayerId() ).getUUID() );
			ScoreboardAdapter adapter = new ScoreboardAdapter( sidebar );
			scoreboardAdapters.put( plr.getUniqueId(), adapter );

			sidebar.setDisplaySlot( DisplaySlot.SIDEBAR );
			sidebar.setDisplayName( "§b§lWarZ§7 - §6§lClashMC.eu" );
			sidebar.getScore( "§7Kills" ).setScore( 9 );
			int kills = event.getManager().getInt( event.getPlayerId(), StatsKey.ANIMAL_KILLS );
			sidebar.getScore( "§0§f" + kills ).setScore( 8 );
			sidebar.getScore( "§7Deaths" ).setScore( 7 );
			int deaths = event.getManager().getInt( event.getPlayerId(), StatsKey.DEATHS );
			sidebar.getScore( "§1§f" + deaths ).setScore( 6 );
			sidebar.getScore( "§7Ratio" + ( kills / ( deaths + 1 ) ) ).setScore( 5 );
			sidebar.getScore( "§2§f" ).setScore( 4 );
			plr.setScoreboard( scoreboard );
		} );
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onStatsChanged(PlayerStatsChangedEvent event) {
		if ( event.getManager().getType() != GameType.WARZ ) {
			return;
		}
		Player plr = Bukkit.getPlayer( UtilServer.getClient().getPlayer( event.getPlayerId() ).getUUID() );
		plr.getScoreboard().getObjective( DisplaySlot.SIDEBAR );
		ScoreboardAdapter adapter = scoreboardAdapters.get( plr.getUniqueId() );
		int newVal = event.getManager().getInt( event.getPlayerId(), event.getStats() );
		switch ( event.getStats() ) {
			case ANIMAL_KILLS:
				adapter.setEntryKeyWithValue( 8, "§0§f" + newVal );
				break;
			case DEATHS:
				adapter.setEntryKeyWithValue( 6, "§1§f" + newVal );
				break;
			default:
				return;
		}
		int kills = event.getManager().getInt( event.getPlayerId(), StatsKey.ANIMAL_KILLS );
		int deaths = event.getManager().getInt( event.getPlayerId(), StatsKey.DEATHS );
		adapter.setEntryKeyWithValue( 5, "§7Ratio" + ( kills / ( deaths + 1 ) ) );
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		scoreboardAdapters.remove( event.getPlayer().getUniqueId() );
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
