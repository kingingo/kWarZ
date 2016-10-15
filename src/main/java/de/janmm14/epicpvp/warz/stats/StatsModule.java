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

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.friends.FriendInfo;
import de.janmm14.epicpvp.warz.friends.FriendModule;
import de.janmm14.epicpvp.warz.util.ScoreboardAdapter;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.GameType;
import eu.epicpvp.datenserver.definitions.dataserver.gamestats.StatsKey;
import eu.epicpvp.kcore.Scoreboard.Events.PlayerSetScoreboardEvent;
import eu.epicpvp.kcore.StatsManager.StatsManager;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.StatsManager.Event.PlayerStatsChangedEvent;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;

public class StatsModule extends Module<StatsModule> implements Listener { //TODO /stats <name> | scoreboard

	private final StatsManager manager = StatsManagerRepository.getStatsManager( GameType.WARZ );
	private final Map<UUID, ScoreboardAdapter> scoreboardAdapters = new HashMap<>();

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

	public void onDisable() {

	}

	@EventHandler
	public void onScoreboardSet(PlayerSetScoreboardEvent event) {
		Player plr = event.getPlayer();
		Scoreboard scoreboard = plr.getScoreboard();
		Objective sidebar = scoreboard.registerNewObjective( "EpicPvP_warz", "dummy" );
		ScoreboardAdapter adapter = new ScoreboardAdapter( sidebar );
		scoreboardAdapters.put( plr.getUniqueId(), adapter );

		sidebar.setDisplaySlot( DisplaySlot.SIDEBAR );
		sidebar.setDisplayName( "§6§lWarZ§7 - §6§lEpicPvP.eu" );

		sidebar.getScore( "§7Kills" ).setScore( 9 );
		adapter.setEntryKeyWithValue( 8, "§0§fLoading..." );
		sidebar.getScore( "§7Deaths" ).setScore( 7 );
		adapter.setEntryKeyWithValue( 6, "§1§fLoading..." );
		sidebar.getScore( "§7Ratio" ).setScore( 5 );
		adapter.setEntryKeyWithValue( 4, "§2§fLoading..." );
		plr.setScoreboard( scoreboard );

		manager.getAsync( plr, StatsKey.KILLS, (killsObj, exception) -> {
			adapter.setEntryKeyWithValue( 8, "§0§f" + killsObj );

			manager.getAsync( plr, StatsKey.DEATHS, (deathsObj, exception1) -> {
				adapter.setEntryKeyWithValue( 6, "§1§f" + deathsObj );
				int kills = ( int ) killsObj;
				int deaths = ( int ) deathsObj;
				double klr_d = ( ( double ) kills ) / ( ( ( double ) deaths + 1 ) );
				int klr_100 = ( int ) ( klr_d * 100 );
				double klr_anz = ( ( double ) klr_100 ) / 100.0d;
				adapter.setEntryKeyWithValue( 4, "§2§f" + klr_anz );
			} );
		} );


		plr.setScoreboard( scoreboard );
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onStatsChanged(PlayerStatsChangedEvent event) {
		if ( event.getManager().getType() != GameType.WARZ ) {
			return;
		}
		Player plr = Bukkit.getPlayer( UtilServer.getClient().getPlayerAndLoad( event.getPlayerId() ).getUUID() );
		ScoreboardAdapter adapter;
		if ( plr == null ) {
			adapter = new ScoreboardAdapter( new DummyObjective() );
		} else {
			adapter = scoreboardAdapters.get( plr.getUniqueId() );
		}
		if ( adapter == null ) {
			return;
		}
		int newVal = event.getManager().getInt( event.getPlayerId(), event.getStats() );
		switch ( event.getStats() ) {
			case KILLS:
				adapter.setEntryKeyWithValue( 8, "§0§f" + newVal );
				break;
			case DEATHS:
				adapter.setEntryKeyWithValue( 6, "§1§f" + newVal );
				break;
			default:
				return;
		}
		int kills = event.getManager().getInt( event.getPlayerId(), StatsKey.KILLS );
		int deaths = event.getManager().getInt( event.getPlayerId(), StatsKey.DEATHS );
		double klr_d = ( ( double ) kills ) / ( ( ( double ) deaths + 1 ) );
		int klr_100 = ( int ) ( klr_d * 100 );
		double klr_anz = ( ( double ) klr_100 ) / 100.0d;
		adapter.setEntryKeyWithValue( 4, "§2§f" + klr_anz );
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		getPlugin().getServer().getScheduler().runTaskAsynchronously( getPlugin(), () -> {
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
				msg( victim, "DEATH", victim.getName() );
				victim.sendMessage( TranslationHandler.getPrefixAndText( victim, "DEATH", victim.getName() ) );
				return;
			}

			msg( victim, killer, "KILL_BY", victim.getName(), killer.getName() );

			victim.sendMessage( TranslationHandler.getPrefixAndText( victim, "GUNGAME_KILLED_BY", killer.getName() ) );
			killer.sendMessage( TranslationHandler.getPrefixAndText( killer, "GUNGAME_KILL", victim.getName() ) );
			increaseStatistic( killer, StatsKey.KILLS );
		} );
	}

	public void msg(Player player, String tr, Object... input) {
		msg( player, null, tr, input );
	}

	public void msg(Player victim, Player killer, String tr, Object... input) {
		FriendModule module = getModuleManager().getModule( FriendModule.class );
		FriendInfo info = module.getFriendInfoManager().get( UtilPlayer.getPlayerId( victim ) );

		Player friend;
		for ( int friendId : info.getFriendWith().toArray() ) {
			friend = UtilPlayer.searchExact( friendId );

			if ( friend != null ) {
				friend.sendMessage( TranslationHandler.getPrefixAndText( friend, tr, input ) );
			}
			friend = null;
		}

		if ( killer != null ) {
			FriendInfo k_info = module.getFriendInfoManager().get( UtilPlayer.getPlayerId( victim ) );

			for ( int friendId : k_info.getFriendWith().toArray() ) {
				friend = null;
				if ( info.getFriendWith().contains( friendId ) ) continue;
				friend = UtilPlayer.searchExact( friendId );

				if ( friend != null ) {
					friend.sendMessage( TranslationHandler.getPrefixAndText( friend, tr, input ) );
				}
			}
		}
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		scoreboardAdapters.remove( event.getPlayer().getUniqueId() );
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
