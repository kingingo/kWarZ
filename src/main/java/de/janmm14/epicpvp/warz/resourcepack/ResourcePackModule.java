package de.janmm14.epicpvp.warz.resourcepack;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;

public class ResourcePackModule extends Module<ResourcePackModule> {

	private static final String PATH_PREFIX = "resourcepack.";

	private static final String PATH_PACK_URL = PATH_PREFIX + "url";
	private static final String PATH_INFOTEXT = PATH_PREFIX + "infotext";
	private static final String PATH_PACK_DELAY = PATH_PREFIX + "joindelay_ticks";

	public ResourcePackModule(WarZ plugin) {
		super( plugin, ResourcePackListener::new );
		plugin.getCommand( "resourcepack" ).setExecutor( new CommandResourcePack( this ) );
	}

	@Override
	public void reloadConfig() {
		getPlugin().getConfig().addDefault( PATH_PACK_URL, "https://resourcepacks.epicpvp.org/warz.zip" );
		getPlugin().getConfig().addDefault( PATH_INFOTEXT, "&aBitte akzeptiere das Resourcepack!" );
		getPlugin().getConfig().addDefault( PATH_PACK_DELAY, 2 * 20 );
	}

	public String getPackUrl() {
		return getPlugin().getConfig().getString( PATH_PACK_URL );
	}

	public String getInfoText() {
		return getPlugin().getConfig().getString( PATH_INFOTEXT );
	}

	public int getResourcePackDelay() {
		return getPlugin().getConfig().getInt( PATH_PACK_DELAY );
	}

	/**
	 * Sends the configured resource pack and the info message to the given player
	 *
	 * @param plr the player
	 */
	public void sendResourcePack(Player plr) {
		plr.setResourcePack( getPackUrl() );

		String infoText = getInfoText();
		if ( !infoText.trim().isEmpty() ) {
			plr.sendMessage( ChatColor.translateAlternateColorCodes( '&', infoText ) );
		}
	}
}
