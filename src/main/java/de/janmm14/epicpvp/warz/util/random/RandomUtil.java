package de.janmm14.epicpvp.warz.util.random;

import java.util.Random;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class RandomUtil {

	public static final Random RANDOM = new Random();

	/**
	 * @param min inclusive
	 * @param max inclusive
	 * @return random int between (inlcusive) the given borders
	 */
	public static int getRandomInt(int min, int max) {
		return min + RANDOM.nextInt( max - min + 1 );
	}
}
