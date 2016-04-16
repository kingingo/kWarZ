package de.janmm14.epicpvp.warz.util.random;

import java.util.Random;

public final class RandomUtil {

	private RandomUtil() {
		throw new UnsupportedOperationException();
	}

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
