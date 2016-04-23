package de.janmm14.epicpvp.warz.hooks;

import java.util.UUID;

import lombok.Data;

public class UuidNameConverter {

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

	public Profile getProfile(UUID uuid) {
		return null;//TODO implement
	}

	public Profile getProfile(String name) {
		return null;//TODO implement
	}

	@Data
	public static class Profile {

		private final UUID uuid;

		private final String name;
	}
}
