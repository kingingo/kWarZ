package de.janmm14.epicpvp.warz.resourcepack;

import org.bukkit.entity.Player;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import de.janmm14.epicpvp.warz.util.MiscUtil;
import lombok.Getter;

@Getter
public class ResourcePackModule extends Module<ResourcePackModule> {

	private static final String PATH_PREFIX = "resourcepack.";

	private static final String PATH_PACK_URL = PATH_PREFIX + "url";
	private static final String PATH_EMPTY_PACK_URL = PATH_PREFIX + "emptyurl";
	private static final String PATH_INFOTEXT = PATH_PREFIX + "infotext";
	private static final String PATH_INFOTEXT_RESET = PATH_PREFIX + "resetinfotext";
	private static final String PATH_PACK_DELAY = PATH_PREFIX + "joindelay_ticks";
	private String packUrl;
	private String emptyPackUrl;
	private String infoText;
	private String resetInfoText;
	private int resourcePackDelay;

	public ResourcePackModule(WarZ plugin) {
		super( plugin/*, ResourcePackListener::new*/ );
		plugin.getCommand( "resourcepack" ).setExecutor( new CommandResourcePack( this ) );
	}

	@Override
	public void reloadConfig() {
		getConfig().addDefault( PATH_PACK_URL, "https://resourcepacks.epicpvp.org/warz.zip" );
		packUrl = getConfig().getString( PATH_PACK_URL );
		getConfig().addDefault( PATH_EMPTY_PACK_URL, "https://resourcepacks.epicpvp.org/empty.zip" );
		emptyPackUrl = getConfig().getString( PATH_EMPTY_PACK_URL );
		getConfig().addDefault( PATH_INFOTEXT, "&aBitte akzeptiere das Resourcepack!" );
		infoText = getConfig().getString( PATH_INFOTEXT );
		getConfig().addDefault( PATH_INFOTEXT_RESET, "&aExperimentell! Dies setzt ein neues, leeres Resourcepack, welches das alte überschreiben sollte." );
		resetInfoText = getConfig().getString( PATH_INFOTEXT_RESET );
		getConfig().addDefault( PATH_PACK_DELAY, 2 * 20 );
		resourcePackDelay = getConfig().getInt( PATH_PACK_DELAY );
	}

	/**
	 * Sends the configured resource pack and the info message to the given player
	 *
	 * @param plr the player
	 */
	public void sendResourcePack(Player plr, boolean isResetTest) {
		plr.setResourcePack( isResetTest ? getEmptyPackUrl() : getPackUrl() );

		String infoText = isResetTest ? getResetInfoText() : getInfoText();
		if ( !infoText.trim().isEmpty() ) {
			plr.sendMessage( MiscUtil.translateColorCode( infoText ) );
		}
	}
}
