package de.janmm14.epicpvp.warz.hooks;

import java.util.Arrays;

import org.bukkit.entity.Player;

public class LanguageConverter {

	public String getLocalized(Player target, String identifier, Object... arguments) {
		return "<missing translation '" + identifier + "' " + Arrays.toString( arguments ) + ">";
	}
}
