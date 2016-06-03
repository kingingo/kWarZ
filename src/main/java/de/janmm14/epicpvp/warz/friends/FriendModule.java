package de.janmm14.epicpvp.warz.friends;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

import lombok.Getter;

public class FriendModule extends Module<FriendModule> {

	@SuppressWarnings("FieldCanBeLocal")
	private String prefix = "§7";
	@Getter
	private final FriendInfoManager friendInfoManager;

	public FriendModule(WarZ plugin) {
		super( plugin, FriendHurtListener::new, FriendNotifyListener::new );
		friendInfoManager = new FriendInfoManager( this );
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public void reloadConfig() {
	}
}
