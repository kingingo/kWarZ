package de.janmm14.epicpvp.warz.friends;

import java.sql.SQLException;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

import lombok.Getter;

public class FriendModule extends Module<FriendModule> {

	@Getter
	private final FriendInfoManager friendInfoManager;

	public FriendModule(WarZ plugin) throws SQLException {
		super( plugin, FriendHurtListener::new );
		friendInfoManager = new FriendInfoManager( this );
	}
}
