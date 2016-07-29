package de.janmm14.epicpvp.warz;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import com.google.common.collect.Lists;

import lombok.Getter;

@Getter
public abstract class Module<M extends Module> {

	private final WarZ plugin;
	private final List<Listener> listeners;

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public Module(WarZ plugin, Function<M, Listener>... listenerCreators) {
		this.plugin = plugin;
		listeners = Lists.newArrayListWithExpectedSize( listenerCreators.length );
		Arrays.stream( listenerCreators )
			.map( lr -> lr.apply( ( M ) Module.this ) )
			.forEach( listener -> {
				getPlugin().getServer().getPluginManager().registerEvents( listener, plugin );
				listeners.add( listener );
			} );
	}

	public ModuleManager getModuleManager() {
		return plugin.getModuleManager();
	}

	public abstract void reloadConfig();

	final boolean tryReloadConfig() {
		try {
			reloadConfig();
			return true;
		}
		catch ( Throwable t ) {
			plugin.getLogger().log( Level.SEVERE, "An error occurred while reloading the configuration of " + getClass().getSimpleName(), t );
			return false;
		}
	}

	final boolean tryLoadConfig() {
		try {
			reloadConfig();
			return true;
		}
		catch ( Throwable t ) {
			plugin.getLogger().log( Level.SEVERE, "An error occurred while loading the configuration of " + getClass().getSimpleName(), t );
			return false;
		}
	}

	public final FileConfiguration getConfig() {
		//for possible configuration splitting later on
		return getPlugin().getConfig();
	}
}
