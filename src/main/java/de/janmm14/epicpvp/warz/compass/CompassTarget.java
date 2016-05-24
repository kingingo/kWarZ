package de.janmm14.epicpvp.warz.compass;

import java.util.Collection;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import de.janmm14.epicpvp.warz.friends.FriendInfo;
import de.janmm14.epicpvp.warz.friends.FriendInfoManager;
import de.janmm14.epicpvp.warz.friends.FriendModule;
import de.janmm14.epicpvp.warz.friends.PlayerFriendRelation;
import de.janmm14.epicpvp.warz.zonechest.Zone;
import de.janmm14.epicpvp.warz.zonechest.ZoneAndChestsModule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CompassTarget {

	ENEMY {
		@Override
		Location getTarget(@NonNull CompassTargetModule module, @NonNull Player plr) {
			FriendInfoManager manager = FriendModule.getManager();
			FriendInfo friendInfo = manager.get( plr.getUniqueId() );
			double distanceSquared = Double.MAX_VALUE;
			Player nearestEnemy = null;
			for ( Player possTarget : Bukkit.getOnlinePlayers() ) {
				if ( !plr.getWorld().equals( possTarget.getWorld() ) ) {
					continue;
				}
				if ( PlayerFriendRelation.areFriends( manager, friendInfo, possTarget.getUniqueId() ) ) {
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
			if ( !moved.getWorld().equals( plr.getWorld() ) ) {
				return null;
			}
			FriendInfoManager manager = FriendModule.getManager();
			FriendInfo friendInfo = manager.get( plr.getUniqueId() );
			if ( PlayerFriendRelation.areFriends( manager, friendInfo, moved.getUniqueId() ) ) {
				return null;
			}
			return getTarget( module, plr );
		}
	},
	FRIEND {
		@Override
		Location getTarget(@NonNull CompassTargetModule module, @NonNull Player plr) {
			FriendInfoManager manager = FriendModule.getManager();
			FriendInfo friendInfo = manager.get( plr.getUniqueId() );
			double distanceSquared = Double.MAX_VALUE;
			Player nearestFriend = null;
			for ( Player possTarget : Bukkit.getOnlinePlayers() ) {
				if ( !plr.getWorld().equals( possTarget.getWorld() ) ) {
					continue;
				}
				if ( !PlayerFriendRelation.areFriends( manager, friendInfo, possTarget.getUniqueId() ) ) {
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
			if ( !moved.getWorld().equals( plr.getWorld() ) ) {
				return null;
			}
			FriendInfoManager manager = FriendModule.getManager();
			FriendInfo friendInfo = manager.get( plr.getUniqueId() );
			if ( !PlayerFriendRelation.areFriends( manager, friendInfo, moved.getUniqueId() ) ) {
				return null;
			}
			return getTarget( module, plr );
		}
	},
	ZONE {
		@Override
		Location getTarget(@NonNull CompassTargetModule module, @NonNull Player plr) {
			//nächste Zone
			Collection<Zone> zones = module.getPlugin().getModuleManager().getModule( ZoneAndChestsModule.class ).getZones();
			Vector plrVector = plr.getLocation().toVector();
			double minDistSquared = Double.MAX_VALUE;
			Location nearest = null;
			for ( Zone zone : zones ) {
				Vector zoneMiddle = zone.getMiddle();
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
