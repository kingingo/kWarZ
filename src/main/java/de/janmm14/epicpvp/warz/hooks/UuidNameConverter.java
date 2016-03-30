package de.janmm14.epicpvp.warz.hooks;

import java.util.UUID;

import lombok.Data;

public class UuidNameConverter {

	public Profile getProfileFromInput(String nameOrUuid) {
		return null;//TODO implement
	}

	public Profile getProfile(UUID uuid) {
		return null;//TODO implement
	}

	@Data
	public static class Profile {

		private final UUID uuid;

		private final String name;
	}
}
