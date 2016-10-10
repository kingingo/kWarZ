package de.janmm14.epicpvp.warz.loot;

import org.bukkit.entity.Player;

import eu.epicpvp.kcore.Util.UtilPlayer;
import eu.epicpvp.kcore.Util.UtilTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LootRunner implements Runnable {

	private final LootModule module;
	
	@Override
	public void run() {
		if(!module.getLoottimer().isEmpty()){
			Player plr;
			for(int i = 0; i<module.getLoottimer().size(); i++){
				plr = (Player)module.getLoottimer().keySet().toArray()[i];
				
				if(plr.isOnline()){
					if(module.getLoottimer().get(plr) < System.currentTimeMillis()){
						module.stopLootTime(plr);
					}else{
						UtilPlayer.sendHovbarText(plr, "§cLoot Zeit §e"+ UtilTime.formatMili( (module.getLoottimer().get(plr)-System.currentTimeMillis()) ) );
					}
				}else{
					module.stopLootTime(plr);
				}
			}
		}
	}

}
