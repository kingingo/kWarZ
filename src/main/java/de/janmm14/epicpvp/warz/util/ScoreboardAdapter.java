package de.janmm14.epicpvp.warz.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.scoreboard.Objective;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScoreboardAdapter {

	@Getter
	private final Objective objective;
	private final Map<Integer, String> entryKeys = new HashMap<>();

	public void setEntryKeyWithValue(int value, String newKey) {
		String oldKey = entryKeys.put( value, newKey );
		if ( oldKey != null ) {
			objective.getScoreboard().resetScores( oldKey );
		}
		objective.getScore( newKey ).setScore( value );
	}
}
