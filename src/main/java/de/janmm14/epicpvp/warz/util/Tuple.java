package de.janmm14.epicpvp.warz.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Tuple<A, B> {

	private A a;
	private B b;
}
