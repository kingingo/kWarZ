package de.janmm14.epicpvp.warz.hooks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import dev.wolveringer.client.LoadedPlayer;
import eu.epicpvp.kcore.Util.UtilServer;

import lombok.Data;

public class UserDataConverter {

	public Profile getProfileFromInput(String nameOrUuid) {
		if ( nameOrUuid.length() > 16 ) {
			if ( nameOrUuid.contains( "-" ) ) {
				return getProfile( UUID.fromString( nameOrUuid ) );
			} else {
				return getProfile( getMojangUuid( nameOrUuid ) );
			}
		} else {
			return getProfile( nameOrUuid );
		}
	}

	private static UUID getMojangUuid(String uuidWithoutDashes) {
		return new UUID( Long.parseUnsignedLong( uuidWithoutDashes.substring( 0, 16 ), 16 ), Long.parseUnsignedLong( uuidWithoutDashes.substring( 16 ), 16 ) );
	}

	public Profile getProfile(OfflinePlayer player) {
		return Profile.byLoadedPlayer( UtilServer.getClient().getPlayerAndLoad( player.getUniqueId() ) );
	}

	public Profile getProfile(int playerId) {
		return Profile.byLoadedPlayer( UtilServer.getClient().getPlayerAndLoad( playerId ) );
	}

	public Profile getProfile(UUID uuid) {
		return Profile.byLoadedPlayer( UtilServer.getClient().getPlayerAndLoad( uuid ) );
	}

	public Profile getProfile(String name) {
		return Profile.byLoadedPlayer( UtilServer.getClient().getPlayerAndLoad( name ) );
	}

	@Data
	public static class Profile {

		private final UUID uuid;

		private final String name;

		private final int playerId;

		public boolean isOnline() {
			return Bukkit.getPlayerExact( name ) == null;
		}

		/**
		 * @deprecated returns implementation-specific class
		 */
		@Deprecated
		public LoadedPlayer toLoadedPlayer() {
			return UtilServer.getClient().getPlayerAndLoad( name );
		}

		public static Profile byLoadedPlayer(LoadedPlayer lplr) {
			return new Profile( lplr.getUUID(), lplr.getName(), lplr.getPlayerId() );
		}
	}
}
