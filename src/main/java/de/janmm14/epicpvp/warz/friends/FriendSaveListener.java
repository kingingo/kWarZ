package de.janmm14.epicpvp.warz.friends;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FriendSaveListener implements Listener {

	private final FriendModule module;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent event) {
		int playerId = module.getPlugin().getUserDataConverter().getProfile( event.getPlayer() ).getPlayerId();
		FriendInfo friendInfo = module.getFriendInfoManager().getIfCached( playerId );
		if ( friendInfo != null ) {
			module.getFriendInfoManager().save( friendInfo, false );
		}
	}
}
