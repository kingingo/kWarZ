package de.janmm14.epicpvp.warz.friends;

import java.util.UUID;

import lombok.NonNull;

/**
 * Logic for friend checking
 */
public class PlayerFriendRelation {

	/**
	 * Whether to check data for consistency
	 */
	private static final boolean CONSISTENCY_CHECKS = true;

	private PlayerFriendRelation() {
		throw new UnsupportedOperationException();
	}

	public static boolean areFriends(@NonNull FriendInfoManager manager, @NonNull FriendInfo initiator, @NonNull UUID targetUuid) {
		boolean initiatorFriend = initiator.getFriendWith().contains( targetUuid );
		if ( CONSISTENCY_CHECKS ) {
			FriendInfo target = manager.get( targetUuid );
			boolean targetFriend = target.getFriendWith().contains( initiator.getUuid() );
			if ( initiatorFriend && targetFriend ) {
				return true;
			}
			if ( !initiatorFriend && !targetFriend ) {
				return false;
			}
			if ( initiatorFriend ) { // targetFriend = false
				manager.getModule().getPlugin().getLogger().warning( "[Friends] Data inconsistency found! " + initiator + " has friend with " + target + ", but not the other way round! Setting second to have friend with first." );
				target.getFriendWith().add( initiator.getUuid() );
				target.setDirty();
			} else { // initiatorFriend = false, targetFriend = true
				manager.getModule().getPlugin().getLogger().warning( "[Friends] Data inconsistency found! " + target + " has friend with " + initiator + ", but not the other way round! Setting second to have friend with first." );

				initiator.getFriendWith().add( targetUuid );
				initiator.setDirty();
			}
			return true;
		}
		return initiatorFriend;
	}

	public static boolean isRequestSent(@NonNull FriendInfoManager manager, @NonNull FriendInfo initiator, @NonNull UUID targetUuid) {
		boolean initiatorSent = initiator.getRequestsSent().contains( targetUuid );
		if ( CONSISTENCY_CHECKS ) {
			FriendInfo target = manager.get( targetUuid );
			boolean targetGot = target.getRequestsGot().contains( initiator.getUuid() );
			if ( initiatorSent && targetGot ) {
				return true;
			}
			if ( !initiatorSent && !targetGot ) {
				return false;
			}
			if ( initiatorSent ) { // targetGot = false
				manager.getModule().getPlugin().getLogger().warning( "[Friends] Data inconsistency found! " + initiator + " has sent a request to " + target + ", but request target got no request! Setting second to have recieved a request from first." );
				target.getRequestsGot().add( initiator.getUuid() );
				target.setDirty();
			} else { // initiatorSent = false, targetGot = true
				manager.getModule().getPlugin().getLogger().warning( "[Friends] Data inconsistency found! " + target + " has sent a request to " + initiator + ", but request target got no request! Setting second to have recieved a request from first." );
				initiator.getRequestsSent().add( targetUuid );
				initiator.setDirty();
			}
			return true;
		}
		return initiatorSent;
	}

	public static boolean isRequestRecieved(@NonNull FriendInfoManager manager, @NonNull FriendInfo initiator, @NonNull UUID targetUuid) {
		boolean initiatorGot = initiator.getRequestsGot().contains( targetUuid );
		if ( CONSISTENCY_CHECKS ) {
			FriendInfo target = manager.get( targetUuid );
			boolean targetSent = target.getRequestsSent().contains( initiator.getUuid() );
			if ( initiatorGot && targetSent ) {
				return true;
			}
			if ( !initiatorGot && !targetSent ) {
				return false;
			}
			if ( initiatorGot ) { // targetSent = false
				manager.getModule().getPlugin().getLogger().warning( "[Friends] Data inconsistency found! " + initiator + " has got a request from " + target + ", but request origin has not sent! Setting second to have sent a request to first." );
				target.getRequestsSent().add( initiator.getUuid() );
				target.setDirty();
			} else { //initiatorGot = false, targetSent = true
				manager.getModule().getPlugin().getLogger().warning( "[Friends] Data inconsistency found! " + target + " has got a request from " + initiator + ", but request origin has not sent! Setting second to have sent a request to first." );
				initiator.getRequestsSent().add( targetUuid );
				initiator.setDirty();
			}
			return true;
		}
		return initiatorGot;
	}
}
