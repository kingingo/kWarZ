package de.janmm14.epicpvp.warz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;
import gnu.trove.TIntCollection;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GnuTroveJavaAdapter {

	public static IntStream stream(TIntCollection c) {
		IntStream.Builder builder = IntStream.builder();
		c.forEach( toT( builder ) );
		return builder.build();
	}

	public static TIntProcedure toT(IntConsumer ic) {
		return i -> {
			ic.accept( i );
			return true;
		};
	}

	public static List<Integer> toJava(TIntCollection c) {
		if ( c == null || c.isEmpty() ) {
			return new ArrayList<>();
		}
		ArrayList<Integer> result = Lists.newArrayListWithExpectedSize( c.size() );
		c.forEach( result::add );
		return result;
	}

	public static TIntSet toTSet(List<Integer> l) {
		if ( l == null || l.isEmpty() ) {
			return new TIntHashSet();
		}
		TIntSet result = new TIntHashSet( l.size() );
		l.forEach( result::add );
		return result;
	}
}
