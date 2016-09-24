package de.janmm14.epicpvp.warz.gilde;

import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.epicpvp.kcore.Command.CommandHandler.Sender;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandSetGildeChest implements CommandExecutor {
	
	private GildeModule module;
	
	public CommandSetGildeChest(GildeModule module){
		this.module=module;
	}

	@eu.epicpvp.kcore.Command.CommandHandler.Command(command = "setgildechest", sender = Sender.PLAYER)
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if ( !sender.isOp() ) {
			return true;
		}
		
		Player plr = ( Player ) sender;
		List<Block> blocks = plr.getLineOfSight( ( Set<Material> ) null, 6 );
		for ( Block block : blocks ) {
			if ( block == null || block.getType() == Material.AIR ) {
				continue;
			}
			if ( block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST ) {
				module.setChest( block.getLocation().toVector().toBlockVector() );
				sender.sendMessage( "§aDie Gildenkiste ist nun bei " + module.getChest() );
				return true;
			}
		}
		sender.sendMessage( "§cDu musst die Kiste ansehen!" );
		return true;
	}
}
