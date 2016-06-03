package de.janmm14.epicpvp.warz.friends;

import java.lang.ref.SoftReference;

import org.bukkit.entity.Player;

import eu.epicpvp.kcore.kConfig.kConfig;
import gnu.trove.set.TIntSet;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

/**
 * This class contains a uuid of the player and its friends' uuid
 *
 * @author Janmm14
 */
@Getter
@ToString
@EqualsAndHashCode(of = "playerId")
public class FriendInfo {

	private final FriendInfoManager manager;
	private final int playerId;
	@NonNull
	private final TIntSet friendWith;
	@NonNull
	private final TIntSet requestsGot;
	@NonNull
	private final TIntSet requestsSent;
	@NonNull
	private transient SoftReference<kConfig> config;
	private transient boolean dirty;

	public FriendInfo(FriendInfoManager manager, int playerId, @NonNull TIntSet friendWith, @NonNull TIntSet requestsGot, @NonNull TIntSet requestsSent, kConfig cfg) {
		this.manager = manager;
		this.playerId = playerId;
		this.friendWith = friendWith;
		this.requestsGot = requestsGot;
		this.requestsSent = requestsSent;
		this.config = new SoftReference<>( cfg );
	}

	/**
	 * Dirty means that something has changed in this object, but is not yet reflected in the sql table
	 *
	 * @return Whether this FriendInfo is dirty right now
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Changes current dirty status
	 *
	 * @param dirty the new dirty state
	 * @see #isDirty()
	 */
	void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * {@code setDirty(true)}
	 *
	 * @see #setDirty(boolean)
	 * @see #isDirty()
	 */
	void setDirty() {
		this.dirty = true;
	}

	kConfig getConfig() {
		kConfig cfg = config.get();
		if ( cfg == null ) {
			config = new SoftReference<>( manager.getUserDataConfig().getConfig( playerId ) );
		}
		return cfg;
	}

	public Player getPlayer() {
		return manager.getModule().getPlugin().getServer().getPlayer( manager.getModule().getPlugin().getUuidNameConverter().getProfile( playerId ).getUuid() );
	}
}
