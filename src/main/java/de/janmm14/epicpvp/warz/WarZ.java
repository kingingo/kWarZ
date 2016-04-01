package de.janmm14.epicpvp.warz;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import de.janmm14.epicpvp.warz.hooks.UuidNameConverter;

import lombok.Getter;

@Getter
public class WarZ extends JavaPlugin {

	private ModuleManager moduleManager;
	private UuidNameConverter uuidNameConverter;

	@Override
	public void onEnable() {
		reloadCfg0();
		registerTabExecutor( "warz", new CommandWarZ( this ) );

		moduleManager = new ModuleManager( this );
		moduleManager.discoverAndLoadModules();

		saveConfig();
	}

	public void reloadCfg() {
		reloadConfig();
		reloadCfg0();
	}

	private void reloadCfg0() {
		moduleManager.triggerReloadConfig();
		getConfig().options()
			.copyDefaults( true )
			.copyHeader( true )
			.header( "WarZ v" + getDescription().getVersion() );
		saveConfig();
	}

	public <T extends CommandExecutor & TabCompleter> void registerTabExecutor(String command, T tabExecutor) {
		PluginCommand cmd = getCommand( command );
		cmd.setExecutor( tabExecutor );
		cmd.setTabCompleter( tabExecutor );
	}
}
