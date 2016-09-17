package de.janmm14.epicpvp.warz.stats;

import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class DummyObjective implements Objective {

	@Override
	public String getName() throws IllegalStateException {
		return null;
	}

	@Override
	public String getDisplayName() throws IllegalStateException {
		return null;
	}

	@Override
	public void setDisplayName(String displayName) throws IllegalStateException, IllegalArgumentException {
	}

	@Override
	public String getCriteria() throws IllegalStateException {
		return null;
	}

	@Override
	public boolean isModifiable() throws IllegalStateException {
		return true;
	}

	@Override
	public Scoreboard getScoreboard() {
		return null;
	}

	@Override
	public void unregister() throws IllegalStateException {
	}

	@Override
	public void setDisplaySlot(DisplaySlot slot) throws IllegalStateException {

	}

	@Override
	public DisplaySlot getDisplaySlot() throws IllegalStateException {
		return null;
	}

	@Override
	public Score getScore(OfflinePlayer player) throws IllegalArgumentException, IllegalStateException {
		return new DummyScore();
	}

	@Override
	public Score getScore(String entry) throws IllegalArgumentException, IllegalStateException {
		return new DummyScore();
	}
}
