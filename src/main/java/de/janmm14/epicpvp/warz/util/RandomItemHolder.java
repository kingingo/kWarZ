package de.janmm14.epicpvp.warz.util;

import java.util.List;
import java.util.Random;

public interface RandomItemHolder<V> {

	double getProbability();

	V getItem();

	Random RANDOM = new Random();

	static <K, T extends RandomItemHolder<K>> K chooseRandom(List<T> list) {
		double randomDouble = RANDOM.nextDouble();
		double overallProbability = 0;

		for ( T itemHolder : list ) {
			double startingProbability = overallProbability + itemHolder.getProbability();
			overallProbability += itemHolder.getProbability();

			if ( randomDouble > startingProbability && randomDouble <= ( startingProbability + itemHolder.getProbability() ) ) {
				return itemHolder.getItem();
			}
		}
		return null;
	}
}
