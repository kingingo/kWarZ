package de.janmm14.epicpvp.warz;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import de.janmm14.epicpvp.warz.hooks.UuidNameConverter;

import lombok.Getter;

@Getter
public class WarZ extends JavaPlugin {

	public static boolean DEBUG;
	private ModuleManager moduleManager;
	private UuidNameConverter uuidNameConverter;

	@Override
	public void onEnable() {
		setConfigOptions();
		registerTabExecutor( "warz", new CommandWarZ( this ) );
		Bukkit.getWorld( "world" ).setAutoSave( false );

		moduleManager = new ModuleManager( this );
		moduleManager.discoverAndLoadModules();

		saveConfig();
	}

	public void reloadCfg() {
		reloadConfig();
		setConfigOptions();
		moduleManager.triggerReloadConfig();
		DEBUG = getConfig().getBoolean( "debug" );
		saveConfig();
	}

	private void setConfigOptions() {
		getConfig().options()
			.copyDefaults( true )
			.copyHeader( true )
			.header( "WarZ v" + getDescription().getVersion() );
		getConfig().addDefault( "debug", false );
	}

	public <T extends CommandExecutor & TabCompleter> void registerTabExecutor(String command, T tabExecutor) {
		PluginCommand cmd = getCommand( command );
		cmd.setExecutor( tabExecutor );
		cmd.setTabCompleter( tabExecutor );
	}
}
