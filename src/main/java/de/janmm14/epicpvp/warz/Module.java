package de.janmm14.epicpvp.warz;

import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;

import lombok.Getter;

@Getter
public abstract class Module<M extends Module> {

	private static final Map<Class<? extends Module>, Module> MODULE_MAP = new HashMap<>();
	private final WarZ plugin;
	private final List<Listener> listeners;

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public Module(WarZ plugin, Function<M, Listener>... listenerCreators) {
		MODULE_MAP.put( getClass(), this );
		this.plugin = plugin;
		listeners = Lists.newArrayListWithExpectedSize( listenerCreators.length );
		Arrays.stream( listenerCreators )
			.map( lr -> lr.apply( ( M ) Module.this ) )
			.forEach( listener -> {
				getPlugin().getServer().getPluginManager().registerEvents( listener, plugin );
				listeners.add( listener );
			} );
	}

	public abstract void reloadConfig();

	@SuppressWarnings({ "unchecked", "FinalStaticMethod" })
	public static final <M extends Module> M getModule(Class<M> clazz) {
		return ( M ) MODULE_MAP.get( clazz );
	}
}
