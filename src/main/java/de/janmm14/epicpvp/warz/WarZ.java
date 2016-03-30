package de.janmm14.epicpvp.warz;

import org.bukkit.plugin.java.JavaPlugin;

import de.janmm14.epicpvp.warz.hooks.UuidNameConverter;

import lombok.Getter;

@Getter
public class WarZ extends JavaPlugin {

	private ModuleManager moduleManager;
	private UuidNameConverter uuidNameConverter;

	@Override
	public void onEnable() {
		moduleManager = new ModuleManager( this );
		moduleManager.discoverAndLoadModules();

		getConfig().options()
			.copyDefaults( true )
			.copyHeader( true )
			.header( "WarZ v" + getDescription().getVersion() );
		saveConfig();
	}
}
