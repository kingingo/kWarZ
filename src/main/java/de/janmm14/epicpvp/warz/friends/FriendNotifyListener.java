package de.janmm14.epicpvp.warz.friends;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.epicpvp.kcore.Translation.TranslationHandler;

import de.janmm14.epicpvp.warz.util.GnuTroveJavaAdapter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FriendNotifyListener implements Listener {

	private final FriendModule module;

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		FriendInfo friendInfo = module.getFriendInfoManager().get( player.getUniqueId() );

		GnuTroveJavaAdapter.stream( friendInfo.getNotifyFriendshipEnded() )
			.mapToObj( module.getFriendInfoManager()::get )
			.map( rqFriendInfo -> module.getPlugin().getUserDataConverter().getProfile( rqFriendInfo.getPlayerId() ) )
			.forEach( rqProfile -> player.sendMessage( module.getPrefix() + TranslationHandler.getText( player, "WARZ_CMD_FRIEND_DISSOLVE_FROM_FRIENDSHIP", rqProfile.getName() ) ) );
		friendInfo.getNotifyFriendshipEnded().clear();

		GnuTroveJavaAdapter.stream( friendInfo.getRequestsGot() )
			.mapToObj( module.getFriendInfoManager()::get )
			.map( rqFriendInfo -> module.getPlugin().getUserDataConverter().getProfile( rqFriendInfo.getPlayerId() ) )
			.forEach( rqProfile -> player.sendMessage( module.getPrefix() + TranslationHandler.getText( player, "WARZ_CMD_FRIEND_RECEIVE_FRIENDSHIP", rqProfile.getName() ) ) );

		GnuTroveJavaAdapter.stream( friendInfo.getNotifyRequestAccepted() )
			.mapToObj( module.getFriendInfoManager()::get )
			.map( rqFriendInfo -> module.getPlugin().getUserDataConverter().getProfile( rqFriendInfo.getPlayerId() ) )
			.forEach( rqProfile -> player.sendMessage( module.getPrefix() + TranslationHandler.getText( player, "WARZ_CMD_FRIEND_ACCEPT_FROM_FRIENDSHIP", rqProfile.getName() ) ) );
		friendInfo.getNotifyRequestAccepted().clear();

		GnuTroveJavaAdapter.stream( friendInfo.getNotifyRequestDenied() )
			.mapToObj( module.getFriendInfoManager()::get )
			.map( rqFriendInfo -> module.getPlugin().getUserDataConverter().getProfile( rqFriendInfo.getPlayerId() ) )
			.forEach( rqProfile -> player.sendMessage( module.getPrefix() + TranslationHandler.getText( player, "WARZ_CMD_FRIEND_REJECT_FROM_FRIENDSHIP_REQUEST", rqProfile.getName() ) ) );
		friendInfo.getNotifyRequestDenied().clear();

		friendInfo.setDirty();
	}
}
