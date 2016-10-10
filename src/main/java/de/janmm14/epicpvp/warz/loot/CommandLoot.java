package de.janmm14.epicpvp.warz.loot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandLoot implements CommandExecutor {

	private final LootModule module;

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "loot", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		Player plr = (Player)sender;
		
		if(args.length==0){
			
		}else{
			switch(args[0]){
			case "use":
				if(module.getCachedlist().contains(plr) && module.canUseLoot(plr)){
					module.startLootTime(plr);
				}
				return true;
			case "stop":
				if(module.getLoottimer().containsKey(plr)){
					module.stopLootTime(plr);
				}
				return true;
			}
		}
		return false;
	}

}
