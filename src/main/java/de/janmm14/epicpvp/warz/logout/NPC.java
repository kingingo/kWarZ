package de.janmm14.epicpvp.warz.logout;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.janmm14.epicpvp.warz.WarZ;
import eu.epicpvp.kcore.Disguise.disguises.livings.DisguisePlayer;
import eu.epicpvp.kcore.Util.UtilEnt;
import eu.epicpvp.kcore.Util.UtilServer;

import lombok.Getter;

public class NPC {

	@Getter
	private Location location;
	@Getter
	private LivingEntity npc;
	@Getter
	private int entityId;
	private DisguisePlayer dbase;
	@Getter
	private ItemStack[] amor;
	@Getter
	private ItemStack[] items;
	@Getter
	private String playername;
	@Getter
	private int playerId;
	@Getter
	private long time;

	public NPC(Player player, LogoutModule module) {
		this.location = player.getLocation();
		this.amor = player.getInventory().getArmorContents();
		this.items = player.getInventory().getContents();
		this.playername = player.getName();
		this.playerId = UtilServer.getClient().getPlayerAndLoad( playername ).getPlayerId();

		setup( module );
	}

	public void drop() {
		for ( ItemStack item : items )
			if ( item != null && item.getType() != Material.AIR )
				location.getWorld().dropItemNaturally( location, item );
		for ( ItemStack item : amor )
			if ( item != null && item.getType() != Material.AIR )
				location.getWorld().dropItemNaturally( location, item );

		remove();
	}

	public void remove() {
		if(WarZ.DEBUG)System.out.println("NPC removed "+(npc!=null)+" && "+npc.isDead());
		if ( npc != null && !npc.isDead() ){
			npc.remove();
		}
		UtilServer.getDisguiseManager().undisguise( entityId );
	}

	public void setup(LogoutModule module) {
		time = System.currentTimeMillis();
		LivingEntity npc = ( LivingEntity ) location.getWorld().spawnEntity( location, EntityType.SKELETON );
		npc.getEquipment().clear();
		npc.getEquipment().setItemInHand( null );
		entityId = npc.getEntityId();
		UtilEnt.setNoAI( npc, true );
		UtilEnt.setSilent( npc, true );
		dbase = new DisguisePlayer( npc, playername );
		dbase.loadSkin( playername ); //TODO Skin load fix
		UtilServer.getDisguiseManager().disguise( dbase );
		module.getNpcs().put( npc.getEntityId(), this );
	}
}
