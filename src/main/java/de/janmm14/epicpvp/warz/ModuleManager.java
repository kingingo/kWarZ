package de.janmm14.epicpvp.warz;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.reflections.Reflections;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ModuleManager {

	private final WarZ plugin;
	private final Map<Class<? extends Module>, Module> modules = new HashMap<>();

	public void discoverAndLoadModules() {
		Reflections reflections = new Reflections( getClass().getPackage().getName() );
		List<Class<? extends Module>> moduleClasses = new ArrayList<>( reflections.getSubTypesOf( Module.class ) );
		moduleClasses.sort( (o1, o2) -> Integer.compare( getPriority( o1 ), getPriority( o2 ) ) );

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
			catch ( InstantiationException ex ) {
				plugin.getLogger().log( Level.SEVERE, "Could not load WarZ " + moduleName + ", invalid class", ex );
			}
			catch ( IllegalAccessException ex ) {
				plugin.getLogger().log( Level.SEVERE, "Could not load WarZ " + moduleName + ", constructor not accessible", ex );
			}
			catch ( Throwable t ) {
				plugin.getLogger().log( Level.SEVERE, "Unknown error while enabling " + moduleName, t );
			}
		}
	}

	private int getPriority(Class<? extends Module> clazz) {
		Module.Priority priority = clazz.getAnnotation( Module.Priority.class );
		return priority == null ? 0 : priority.value();
	}

	public void onDisable() {
		modules.values().forEach( Module::onDisable );
	}
	
	public void reloadAllModuleConfigs() {
		modules.values().forEach( Module::tryReloadConfig );
	}

	@SuppressWarnings({ "unchecked", "FinalStaticMethod" })
	public <M extends Module> M getModule(Class<M> clazz) {
		return ( M ) modules.get( clazz );
	}
}
