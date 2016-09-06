package de.janmm14.epicpvp.warz.friends;

import org.bukkit.command.PluginCommand;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

import lombok.Getter;

public class FriendModule extends Module<FriendModule> {

	@SuppressWarnings("FieldCanBeLocal")
	@Getter
	private String prefix = "§7";
	@Getter
	private final FriendInfoManager friendInfoManager;

	public FriendModule(WarZ plugin) {
		super( plugin, FriendHurtListener::new, FriendNotifyListener::new );
		friendInfoManager = new FriendInfoManager( this );
		CommandFriends handler = new CommandFriends( this );
		PluginCommand cmd = getPlugin().getCommand( "friends" );
		cmd.setExecutor( handler );
		cmd.setTabCompleter( handler );
	}

	@Override
	public void reloadConfig() {
		friendInfoManager.flushAll();
	}
}
