package de.janmm14.epicpvp.warz.util.random;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class SimpleRandomThingGroupHolder<K> implements RandomThingGroupHolder<K> {

	@NonNull
	private final List<RandomThingHolder<K>> item;
	private final int minAmount, maxAmount;
	private final double probability;

	@NonNull
	@Override
	public List<RandomThingHolder<K>> getItem() {
		return new ArrayList<>( item );
	}
}
