package de.janmm14.epicpvp.warz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import lombok.Getter;

@Getter
public abstract class Module<M extends Module> {

	private final WarZ plugin;
	private final List<Listener> listeners;

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public Module(WarZ plugin, Function<M, Listener>... listenerCreators) {
		this.plugin = plugin;
		listeners = new ArrayList<>( listenerCreators.length );
		Arrays.stream( listenerCreators )
			.map( lr -> lr.apply( ( M ) Module.this ) )
			.forEach( listener -> {
				getPlugin().getServer().getPluginManager().registerEvents( listener, plugin );
				listeners.add( listener );
			} );
	}

	@SuppressWarnings("unchecked")
	public <T extends Listener> T getListener(Class<T> clazz) {
		return ( T ) listeners.stream()
			.filter( listener -> listener.getClass().equals( clazz ) )
			.findFirst().orElse( null );
	}

	public ModuleManager getModuleManager() {
		return plugin.getModuleManager();
	}

	public abstract void reloadConfig();
	
	public abstract void onDisable();

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

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Priority {

		/**
		 * A higher priority means it is loaded later - a bad logic atm
		 */
		int value() default 0;
	}
}
