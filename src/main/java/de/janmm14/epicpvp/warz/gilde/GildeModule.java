package de.janmm14.epicpvp.warz.gilde;

import javax.annotation.Nullable;

import org.bukkit.event.Listener;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import de.janmm14.epicpvp.warz.Module;
import de.janmm14.epicpvp.warz.WarZ;
import dev.wolveringer.dataserver.gamestats.GameType;
import dev.wolveringer.gilde.GildeType;
import eu.epicpvp.kcore.StatsManager.StatsManagerRepository;
import eu.epicpvp.kcore.newGilde.GildeHandler;
import lombok.Getter;

public class GildeModule extends Module<GildeModule> implements Listener {

	@Getter
	private BlockVector chest;
	@Getter
	private GildeHandler handler;
	
	public GildeModule(WarZ plugin) {
		super( plugin, module -> module );
		this.handler=new GildeHandler(GildeType.WARZ);
		StatsManagerRepository.getStatsManager(GameType.WARZ).setGilde(handler);
	}
	
	public void setChest(@Nullable BlockVector chest){
		this.chest=chest;
		getConfig().set("gilde.chest",this.chest);
		getPlugin().saveConfig();
	}

	@Override
	public void reloadConfig() {
		Vector vector = getConfig().getVector( "gilde.chest" );
		
		if(vector != null){
			this.chest=vector.toBlockVector();
		}
	}

}
