package de.janmm14.epicpvp.warz.logout;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.friends.FriendInfoManager;
import de.janmm14.epicpvp.warz.friends.FriendModule;
import de.janmm14.epicpvp.warz.friends.PlayerFriendRelation;
import eu.epicpvp.datenserver.definitions.dataserver.player.LanguageType;
import eu.epicpvp.datenserver.definitions.skin.Skin;
import eu.epicpvp.kcore.Translation.TranslationHandler;
import eu.epicpvp.kcore.Update.UpdateType;
import eu.epicpvp.kcore.Update.Event.UpdateEvent;
import eu.epicpvp.kcore.UserDataConfig.Events.UserDataConfigLoadEvent;
import eu.epicpvp.kcore.Util.TimeSpan;
import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilServer;
import eu.epicpvp.kcore.Util.UtilSkin;
import eu.epicpvp.kcore.Util.UtilWorldGuard;
import eu.epicpvp.kcore.kConfig.kConfig;
import lombok.Getter;

public class LogoutModule extends Module<LogoutModule> implements Listener {

	static {
		TranslationHandler.registerFallback( LanguageType.GERMAN, "warz.module.logout.death", "§cDer Spieler §e%s0§c hat dich getötet!" );
		TranslationHandler.registerFallback( LanguageType.ENGLISH, "warz.module.logout.death", "§cThe Player §e%s0§c has killed you!" );
	}

	@Getter
	private Map<Integer, NPC> npcs = new HashMap<>();
	@Getter
	private Map<Integer, NPC> npcs_playerId = new HashMap<>();
	@Getter
	private Map<UUID, Skin> skinCache = new HashMap<>();
	private int time = 25;

	public LogoutModule(WarZ plugin) {
		super( plugin, module -> module );
	}

	public void onDisable() {
		NPC npc;
		for ( int i = 0; i < this.npcs.size(); i++ ) {
			npc = ( NPC ) this.npcs.values().toArray()[ i ];
			npc.remove();
		}

		this.npcs.clear();
		this.npcs_playerId.clear();
		this.skinCache.clear();
	}

	public boolean containsNpc(Player player) {
		if ( npcs_playerId.containsKey( UtilPlayer.getPlayerId( player ) ) ) {
			NPC npc = npcs_playerId.get( UtilPlayer.getPlayerId( player ) );

			npc.remove();
			if ( WarZ.DEBUG ) System.out.println( "Player joined and replaced the NPC" );
			return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void damage(EntityDamageByEntityEvent ev) {
		if ( ev.getEntity().getType() == EntityType.SKELETON && npcs.containsKey( ev.getEntity().getEntityId() ) ) {
			if ( ev.getDamager() instanceof Player ) {
				Player damager = ( Player ) ev.getDamager();
				int victimId = npcs.get( ev.getEntity().getEntityId() ).getPlayerId();

				FriendInfoManager info = getModuleManager().getModule( FriendModule.class ).getFriendInfoManager();

				if ( PlayerFriendRelation.areFriends( info, info.get( UtilPlayer.getPlayerId( damager ) ), victimId ) ) {
					ev.setCancelled( true );
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true) //highest is for CrackShotTweakModule
	public void onWeaponDamage(WeaponDamageEntityEvent event) {
		if ( event.getVictim() instanceof Skeleton && npcs.containsKey( event.getVictim().getEntityId() ) ) {
			int victimId = npcs.get( event.getVictim().getEntityId() ).getPlayerId();
			UUID damagerUuid = event.getPlayer().getUniqueId();

			FriendInfoManager info = getModuleManager().getModule( FriendModule.class ).getFriendInfoManager();
			if ( PlayerFriendRelation.areFriends( info, info.get( damagerUuid ), victimId ) ) {
				event.setCancelled( true );
			}
		}
	}

	@EventHandler
	public void kill(EntityDeathEvent ev) {
		if ( this.npcs.containsKey( ev.getEntity().getEntityId() ) && ev.getEntity().getKiller() != null ) {
			NPC npc = this.npcs.get( ev.getEntity().getEntityId() );
			npc.drop();

			kConfig config = UtilServer.getUserData().loadConfig( npc.getPlayerId() );
			config.set( "lastMapPos", null );
			config.set( "Death", ev.getEntity().getKiller().getName() );
			config.save();
		}
	}

	@EventHandler
	public void load(UserDataConfigLoadEvent ev) {
		if ( ev.getConfig().contains( "Death" ) ) {
			String killer = ev.getConfig().getString( "Death" );
			if ( !killer.isEmpty() ) {
				ev.getPlayer().sendMessage( TranslationHandler.getPrefixAndText( ev.getPlayer(), "warz.module.logout.death", killer ) );
				ev.getPlayer().getInventory().clear();
				ev.getPlayer().getInventory().setArmorContents( null );
				ev.getConfig().set( "Death", null );
				ev.getConfig().save();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		UtilSkin.loadSkin( (loadedSkin, throwable) -> {
			if ( throwable != null ) {
				throwable.printStackTrace();
			}
			skinCache.put( event.getPlayer().getUniqueId(), loadedSkin );
		}, event.getPlayer().getUniqueId() );
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if ( UtilWorldGuard.RegionFlag( event.getPlayer(), DefaultFlag.PVP ) ) {
			new NPC( this, event.getPlayer() );

			if ( WarZ.DEBUG )
				System.err.println( "Player logout out " + event.getPlayer().getName() );
		}
		skinCache.remove( event.getPlayer().getUniqueId() );
	}

	@EventHandler
	public void time(UpdateEvent ev) {
		if ( ev.getType() == UpdateType.FAST ) {
			NPC npc;
			for ( int i = 0; i < npcs.size(); i++ ) {
				npc = ( NPC ) npcs.values().toArray()[ i ];

				if ( ( System.currentTimeMillis() - npc.getTime() ) > TimeSpan.SECOND * time ) {
					if ( WarZ.DEBUG )
						System.err.println( "NPC Time is over " + npc.getPlayername() + " " + ( ( System.currentTimeMillis() - npc.getTime() ) > TimeSpan.SECOND * 25 ) );
					npc.remove();
				}
			}
		}
	}

	@EventHandler
	public void loadWorld(WorldLoadEvent ev) {
		for ( Entity e : ev.getWorld().getEntities() ) {
			if ( e instanceof Skeleton ) {
				e.remove();
			}
		}
	}

	@Override
	public void reloadConfig() {
		getConfig().addDefault( "logout.time", 25 );
		this.time = getConfig().getInt( "logout.time" );
	}
}
