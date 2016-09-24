package de.janmm14.epicpvp.warz.util;

import java.util.function.IntPredicate;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MiscUtil {

	@NotNull
	public static IntPredicate not(@NotNull IntPredicate intPredicate) {
		return intPredicate.negate();
	}

	public static String translateColorCode(String input) {
		return ChatColor.translateAlternateColorCodes( '&', input );
	}
}
