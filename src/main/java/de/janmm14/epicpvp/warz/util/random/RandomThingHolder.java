package de.janmm14.epicpvp.warz.util.random;

import java.util.List;
import javax.annotation.Nullable;

import lombok.NonNull;

public interface RandomThingHolder<V> {

	double getProbability();

	@NonNull
	V getItem();

	@Nullable
	static <V, T extends RandomThingHolder<V>> V chooseRandomItem(@NonNull List<T> list) {
		T t = chooseRandomHolder( list );
		return t == null ? null : t.getItem();
	}

	@Nullable
	static <T extends RandomThingHolder> T chooseRandomHolder(@NonNull List<T> list) {
		double randomDouble = RandomUtil.RANDOM.nextDouble();
		double overallProbability = 0;

		for ( T itemHolder : list ) {
			double startingProbability = overallProbability;
			overallProbability += itemHolder.getProbability();
			double max = startingProbability + itemHolder.getProbability();
			if ( randomDouble > startingProbability && randomDouble <= max ) {
				return itemHolder;
			}
		}
		return null;
	}
}
