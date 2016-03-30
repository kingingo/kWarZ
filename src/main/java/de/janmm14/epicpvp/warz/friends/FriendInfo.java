package de.janmm14.epicpvp.warz.friends;

import java.util.List;
import java.util.UUID;

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
@EqualsAndHashCode(of = "uuid")
public class FriendInfo {

	@NonNull
	private final UUID uuid;
	@NonNull
	private final List<UUID> friendWith;
	@NonNull
	private final List<UUID> requestsGot;
	@NonNull
	private final List<UUID> requestsSent;
	private boolean dirty;

	public FriendInfo(@NonNull UUID uuid, @NonNull List<UUID> friendWith, @NonNull List<UUID> requestsGot, @NonNull List<UUID> requestsSent) {
		this.uuid = uuid;
		this.friendWith = friendWith;
		this.requestsGot = requestsGot;
		this.requestsSent = requestsSent;
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
}
