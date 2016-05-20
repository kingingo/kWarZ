package de.janmm14.epicpvp.warz;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.reflections.Reflections;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModuleManager {

	private final WarZ plugin;
	private final Map<Class<? extends Module>, Module> modules = new HashMap<>();

	public void discoverAndLoadModules() {
		Reflections reflections = new Reflections( getClass().getPackage().getName() );
		Set<Class<? extends Module>> moduleClasses = reflections.getSubTypesOf( Module.class );
		for ( Class<? extends Module> clazz : moduleClasses ) {
			String moduleName = clazz.getSimpleName();
			try {
				Module module = clazz.getConstructor( WarZ.class ).newInstance( plugin );

				if ( module.tryLoadConfig() ) {
					modules.put( clazz, module );
					plugin.getLogger().info( "Loaded WarZ " + moduleName );
				}
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
			catch ( Throwable t ) {
				plugin.getLogger().log( Level.SEVERE, "Unknown error while enabling " + moduleName, t );
			}
		}
	}

	public void triggerReloadConfig() {
		modules.values().forEach( Module::tryReloadConfig );
	}

	@SuppressWarnings({ "unchecked", "FinalStaticMethod" })
	public <M extends Module> M getModule(Class<M> clazz) {
		return ( M ) modules.get( clazz );
	}
}
