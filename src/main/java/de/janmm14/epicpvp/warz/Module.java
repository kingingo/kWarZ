package de.janmm14.epicpvp.warz;

import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

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
}
