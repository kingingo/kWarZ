package de.janmm14.epicpvp.warz.hooks;

import org.bukkit.entity.Player;

import java.util.Arrays;

public class LanguageConverter {

	public String getLocalized(Player target, String identifier, Object... arguments) {
		return "<missing translation '" + identifier + "' " + Arrays.toString( arguments ) + ">";
	}
}
