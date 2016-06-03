package de.janmm14.epicpvp.warz.util;

import java.util.function.IntPredicate;

import org.jetbrains.annotations.NotNull;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MiscUtil {

	@NotNull
	public static IntPredicate not(@NotNull IntPredicate intPredicate) {
		return intPredicate.negate();
	}
}
