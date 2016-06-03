package de.janmm14.epicpvp.warz.friends;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
			.map( rqFriendInfo -> module.getPlugin().getUuidNameConverter().getProfile( rqFriendInfo.getPlayerId() ) )
			.forEach( rqProfile -> player.sendMessage( module.getPrefix() + "§6" + rqProfile.getName() + "§c hat eure Freundschaft aufgelöst." ) );
		friendInfo.getNotifyFriendshipEnded().clear();

		GnuTroveJavaAdapter.stream( friendInfo.getRequestsGot() )
			.mapToObj( module.getFriendInfoManager()::get )
			.map( rqFriendInfo -> module.getPlugin().getUuidNameConverter().getProfile( rqFriendInfo.getPlayerId() ) )
			.forEach( rqProfile -> player.sendMessage( module.getPrefix() + "§6" + rqProfile.getName() + "§7 hat dir eine Freundschaftsanfrage geschickt." ) );

		GnuTroveJavaAdapter.stream( friendInfo.getNotifyRequestAccepted() )
			.mapToObj( module.getFriendInfoManager()::get )
			.map( rqFriendInfo -> module.getPlugin().getUuidNameConverter().getProfile( rqFriendInfo.getPlayerId() ) )
			.forEach( rqProfile -> player.sendMessage( module.getPrefix() + "§6" + rqProfile.getName() + "§7 hat deine Freundschaftsanfrage angenommen." ) );
		friendInfo.getNotifyRequestAccepted().clear();

		GnuTroveJavaAdapter.stream( friendInfo.getNotifyRequestDenied() )
			.mapToObj( module.getFriendInfoManager()::get )
			.map( rqFriendInfo -> module.getPlugin().getUuidNameConverter().getProfile( rqFriendInfo.getPlayerId() ) )
			.forEach( rqProfile -> player.sendMessage( module.getPrefix() + "§6" + rqProfile.getName() + "§7 hat deine Freundschaftsanfrage abgelehnt." ) );
		friendInfo.getNotifyRequestDenied().clear();

		friendInfo.setDirty();
	}
}
