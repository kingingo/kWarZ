package de.janmm14.epicpvp.warz;

import org.bukkit.Bukkit;
import org.bukkit.World;
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
		DEBUG = getConfig().getBoolean( "debug" );
		if ( DEBUG ) {
			getLogger().info( "Debug mode activated!" );
		}

		registerTabExecutor( "warz", new CommandWarZ( this ) );
		World world = Bukkit.getWorld( "world" );
		if ( world != null ) {
			getLogger().info( "Disabled automatic saving of world 'world', please do not use /save-all /save-on or any plugin to save worlds." );
			world.setAutoSave( false );
		}

		moduleManager = new ModuleManager( this );
		moduleManager.discoverAndLoadModules();

		saveConfig();
	}

	public void reloadCfg() {
		reloadConfig();
		setConfigOptions();
		DEBUG = getConfig().getBoolean( "debug" );
		if ( DEBUG ) {
			getLogger().info( "Debug mode is activated!" );
		} else {
			getLogger().info( "Debug mode is deactivated!" );
		}
		moduleManager.triggerReloadConfig();
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
