package de.janmm14.epicpvp.warz.util.random;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import lombok.NonNull;

public interface RandomThingGroupHolder<ORIG> extends RandomThingHolder<List<RandomThingHolder<ORIG>>> {

	int getMinAmount();

	int getMaxAmount();

	@Nullable
	static <ORIG> List<ORIG> groupChooseRandom(@NonNull List<RandomThingGroupHolder<ORIG>> groupList) {
		return groupChooseRandom( RandomThingHolder.chooseRandomHolder( groupList ) );
	}

	@Nullable
	static <ORIG> List<ORIG> groupChooseRandom(@Nullable RandomThingGroupHolder<ORIG> origHolders) {
		if ( origHolders == null ) {
			return null;
		}
		int amount = RandomUtil.getRandomInt( origHolders.getMaxAmount(), origHolders.getMinAmount() );
		List<ORIG> result = new ArrayList<>( amount );
		for ( int i = 0; i < amount; i++ ) {
			result.add( RandomThingHolder.chooseRandomItem( origHolders.getItem() ) );
		}
		return result.isEmpty() ? null : result;
	}
}
