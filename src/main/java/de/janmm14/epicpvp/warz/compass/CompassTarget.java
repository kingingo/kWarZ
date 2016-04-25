package de.janmm14.epicpvp.warz.compass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

import de.janmm14.epicpvp.warz.friends.FriendInfo;
import de.janmm14.epicpvp.warz.friends.FriendInfoManager;
import de.janmm14.epicpvp.warz.friends.FriendModule;
import de.janmm14.epicpvp.warz.friends.PlayerFriendRelation;

import org.jetbrains.annotations.NotNull;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CompassTarget {

	ENEMY {
		@Override
		Location getTarget(@NonNull CompassTargetModule module, @NonNull Player plr) {
			return null;
		}

		@Override
		Location getTargetByOtherMove(@NonNull CompassTargetModule module, @NonNull Player moved, @NonNull Player plr) {
			return null;
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
				if (!plr.getWorld().equals( possTarget.getWorld() )) {
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
			return null;
		}

		@Override
		Location getTargetByOtherMove(@NotNull CompassTargetModule module, @NonNull Player moved, @NonNull Player plr) {
			return null;
		}
	};

	@Nullable
	abstract Location getTarget(@NotNull CompassTargetModule module, @NotNull Player plr);

	@Nullable
	abstract Location getTargetByOtherMove(@NotNull CompassTargetModule module, @NotNull Player moved, @NotNull Player plr);
}
