package de.janmm14.epicpvp.warz;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.reflections.Reflections;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModuleManager {

	private final WarZ plugin;
	@Getter
	private final List<Module<?>> modules = new ArrayList<>();
	private static final Map<Class<? extends Module>, Module> MODULE_MAP = new HashMap<>();

	public void discoverAndLoadModules() {
		Reflections reflections = new Reflections( getClass().getPackage().getName() );
		Set<Class<? extends Module>> moduleClasses = reflections.getSubTypesOf( Module.class );
		for ( Class<? extends Module> clazz : moduleClasses ) {
			String moduleName = clazz.getSimpleName();
			try {
				Module module = clazz.getConstructor( WarZ.class ).newInstance( plugin );
				MODULE_MAP.put( clazz, module );
				module.reloadConfig();
				modules.add( module );
				plugin.getLogger().info( "Loaded WarZ " + moduleName );
			}
			catch ( NoSuchMethodException ex ) {
				plugin.getLogger().log( Level.SEVERE, "No constructor accepting the WarZ class as only parameter available in " + moduleName, ex );
			}
			catch ( InvocationTargetException ex ) {
				plugin.getLogger().log( Level.SEVERE, "Could not load WarZ " + moduleName + ", constructor generated exception", ex.getTargetException() );
			}
			catch ( ReflectiveOperationException ex ) {
				plugin.getLogger().log( Level.SEVERE, "Could not load WarZ " + moduleName + ", unknown error", ex );
			}
		}
	}

	public void triggerReloadConfig() {
		modules.forEach( Module::reloadConfig );
	}

	@SuppressWarnings({ "unchecked", "FinalStaticMethod" })
	public static final <M extends Module> M getModule(Class<M> clazz) {
		return ( M ) MODULE_MAP.get( clazz );
	}
}
