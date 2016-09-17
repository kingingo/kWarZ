package de.janmm14.epicpvp.warz.stats;

import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class DummyScore implements Score {

	@Override
	public OfflinePlayer getPlayer() {
		return null;
	}

	@Override
	public String getEntry() {
		return null;
	}

	@Override
	public Objective getObjective() {
		return null;
	}

	@Override
	public int getScore() throws IllegalStateException {
		return 0;
	}

	@Override
	public void setScore(int score) throws IllegalStateException {
	}

	@Override
	public boolean isScoreSet() throws IllegalStateException {
		return true;
	}

	@Override
	public Scoreboard getScoreboard() {
		return null;
	}
}
