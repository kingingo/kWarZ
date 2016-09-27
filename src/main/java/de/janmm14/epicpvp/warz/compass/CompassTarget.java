package de.janmm14.epicpvp.warz.compass;

import java.util.Collection;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import eu.epicpvp.kcore.Command.Admin.CommandVanish;
import eu.epicpvp.kcore.Util.UtilPlayer;

import de.janmm14.epicpvp.warz.friends.FriendInfo;
import de.janmm14.epicpvp.warz.friends.FriendInfoManager;
import de.janmm14.epicpvp.warz.friends.FriendModule;
import de.janmm14.epicpvp.warz.friends.PlayerFriendRelation;
import de.janmm14.epicpvp.warz.hooks.UserDataConverter;
import de.janmm14.epicpvp.warz.zonechest.Zone;
import de.janmm14.epicpvp.warz.zonechest.ZoneAndChestsModule;

import org.jetbrains.annotations.NotNull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CompassTarget {

	ENEMY {
		@Override
		Location getTarget(@NonNull CompassTargetModule module, @NonNull Player plr) {
			FriendInfoManager manager = module.getModuleManager().getModule( FriendModule.class ).getFriendInfoManager();
			FriendInfo friendInfo = manager.get( plr.getUniqueId() );
			double distanceSquared = module.getEnemyRadius() * module.getEnemyRadius();
			Player nearestEnemy = null;
			for ( Player possTarget : Bukkit.getOnlinePlayers() ) {
				if ( possTarget.getUniqueId().equals( plr.getUniqueId() ) ) {
					continue;
				}
				if ( !plr.getWorld().equals( possTarget.getWorld() ) ) {
					continue;
				}
				if ( possTarget.getGameMode() != GameMode.SURVIVAL ) {
					continue;
				}
				if ( CommandVanish.getInvisible() != null && CommandVanish.getInvisible().contains( possTarget ) ) {
					continue;
				}
				if ( PlayerFriendRelation.areFriends( manager, friendInfo, UtilPlayer.getPlayerId( possTarget ) ) ) {
					continue;
				}
				double currDistanceSquared = plr.getLocation().distanceSquared( possTarget.getLocation() );
				if ( currDistanceSquared < distanceSquared ) {
					distanceSquared = currDistanceSquared;
					nearestEnemy = possTarget;
				}
			}
			return nearestEnemy != null ? nearestEnemy.getLocation() : null;
		}

		@Override
		Location getTargetByOtherMove(@NonNull CompassTargetModule module, @NonNull Player moved, @NonNull Player plr) {
			if ( moved.getUniqueId().equals( plr.getUniqueId() ) ) {
				return null;
			}
			if ( !moved.getWorld().equals( plr.getWorld() ) ) {
				return null;
			}
			if ( plr.getGameMode() != GameMode.SURVIVAL ) {
				return null;
			}
			if ( CommandVanish.getInvisible() != null && CommandVanish.getInvisible().contains( plr ) ) {
				return null;
			}
			FriendInfoManager manager = module.getModuleManager().getModule( FriendModule.class ).getFriendInfoManager();
			FriendInfo friendInfo = manager.get( plr.getUniqueId() );
			UserDataConverter.Profile movedProfile = manager.getModule().getPlugin().getUserDataConverter().getProfile( moved.getUniqueId() );
			if ( PlayerFriendRelation.areFriends( manager, friendInfo, movedProfile.getPlayerId() ) ) {
				return null;
			}
			return getTarget( module, plr );
		}
	},
	FRIEND {
		@Override
		Location getTarget(@NonNull CompassTargetModule module, @NonNull Player plr) {
			FriendInfoManager manager = module.getModuleManager().getModule( FriendModule.class ).getFriendInfoManager();
			FriendInfo friendInfo = manager.get( plr.getUniqueId() );
			double distanceSquared = Double.MAX_VALUE;
			Player nearestFriend = null;
			for ( Player possTarget : Bukkit.getOnlinePlayers() ) {
				if ( possTarget.getUniqueId().equals( plr.getUniqueId() ) ) {
					continue;
				}
				if ( !plr.getWorld().equals( possTarget.getWorld() ) ) {
					continue;
				}
				if ( possTarget.getGameMode() != GameMode.SURVIVAL ) {
					continue;
				}
				if ( CommandVanish.getInvisible() != null && CommandVanish.getInvisible().contains( possTarget ) ) {
					continue;
				}
				UserDataConverter.Profile possTargetProfile = manager.getModule().getPlugin().getUserDataConverter().getProfile( possTarget.getUniqueId() );
				if ( !PlayerFriendRelation.areFriends( manager, friendInfo, possTargetProfile.getPlayerId() ) ) {
					continue;
				}
				double currDistanceSquared = plr.getLocation().distanceSquared( possTarget.getLocation() );
				if ( currDistanceSquared < distanceSquared ) {
					distanceSquared = currDistanceSquared;
					nearestFriend = possTarget;
				}
			}
			return nearestFriend != null ? nearestFriend.getLocation() : null;
		}

		@Override
		Location getTargetByOtherMove(@NonNull CompassTargetModule module, @NonNull Player moved, @NonNull Player plr) {
			if ( moved.getUniqueId().equals( plr.getUniqueId() ) ) {
				return null;
			}
			if ( !moved.getWorld().equals( plr.getWorld() ) ) {
				return null;
			}
			if ( plr.getGameMode() != GameMode.SURVIVAL ) {
				return null;
			}
			if ( CommandVanish.getInvisible() != null && CommandVanish.getInvisible().contains( plr ) ) {
				return null;
			}
			FriendInfoManager manager = module.getModuleManager().getModule( FriendModule.class ).getFriendInfoManager();
			FriendInfo friendInfo = manager.get( plr.getUniqueId() );
			UserDataConverter.Profile movedProfile = manager.getModule().getPlugin().getUserDataConverter().getProfile( moved.getUniqueId() );

			if ( !PlayerFriendRelation.areFriends( manager, friendInfo, movedProfile.getPlayerId() ) ) {
				return null;
			}
			return getTarget( module, plr );
		}
	},
	ZONE {
		@Override
		Location getTarget(@NonNull CompassTargetModule module, @NonNull Player plr) {
			//nächste Zone
			Collection<Zone> zones = module.getModuleManager().getModule( ZoneAndChestsModule.class ).getZones();
			if ( zones == null ) {
				return null;
			}
			Vector plrVector = plr.getLocation().toVector();
			double minDistSquared = Double.MAX_VALUE;
			Location nearest = null;
			for ( Zone zone : zones ) {
				if ( zone == null ) {
					continue;
				}
				Vector zoneMiddle = zone.calculateMiddle();
				double distSquared = zoneMiddle.distanceSquared( plrVector );
				if ( distSquared < minDistSquared ) {
					minDistSquared = distSquared;
					nearest = zoneMiddle.toLocation( plr.getWorld() );
				}
			}
			return nearest;
		}

		@Override
		Location getTargetByOtherMove(@NotNull CompassTargetModule module, @NonNull Player moved, @NonNull Player plr) {
			return null;
		}
	};

	@Nullable
	abstract Location getTarget(@NotNull CompassTargetModule module, @NotNull Player plr);

	/**
	 * ignore result null - do not set to no aim
	 *
	 * currently not used
	 */
	@Nullable
	abstract Location getTargetByOtherMove(@NotNull CompassTargetModule module, @NotNull Player moved, @NotNull Player plr);
}
