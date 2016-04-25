package de.janmm14.epicpvp.warz.util.random;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public class SimpleRandomThingHolder<K> implements RandomThingHolder<K> {

	@NonNull
	@Getter
	private final K item;
	private final double probability;
}
