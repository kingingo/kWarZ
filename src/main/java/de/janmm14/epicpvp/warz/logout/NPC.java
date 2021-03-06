package de.janmm14.epicpvp.warz.logout;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.janmm14.epicpvp.warz.WarZ;
import eu.epicpvp.datenserver.definitions.skin.Skin;
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
	private ItemStack[] armor;
	@Getter
	private ItemStack[] items;
	@Getter
	private String playername;
	@Getter
	private int playerId;
	@Getter
	private long time;
	private LogoutModule module;

	public NPC(LogoutModule module, Player player) {
		this.location = player.getLocation();
		this.armor = player.getInventory().getArmorContents();
		this.items = player.getInventory().getContents();
		this.playername = player.getName();
		this.playerId = UtilServer.getClient().getPlayerAndLoad( playername ).getPlayerId();

		setup( module, player );
	}

	public void drop() {
		for ( ItemStack item : items )
			if ( item != null && item.getType() != Material.AIR )
				location.getWorld().dropItemNaturally( location, item );
		for ( ItemStack item : armor )
			if ( item != null && item.getType() != Material.AIR )
				location.getWorld().dropItemNaturally( location, item );

		remove();
	}

	public void remove() {
		if ( WarZ.DEBUG )
			System.out.println( "NPC removed NULL = " + ( npc == null ) );

		module.getNpcs().remove( getEntityId() );
		module.getNpcs_playerId().remove( getPlayerId() );
		UtilServer.getDisguiseManager().undisguise( entityId );
		if ( npc != null ) npc.remove();
	}

	public void setup(LogoutModule module, Player player) {
		this.module = module;
		time = System.currentTimeMillis();
		npc = ( LivingEntity ) location.getWorld().spawnEntity( location, EntityType.SKELETON );
		npc.getEquipment().clear();
		npc.getEquipment().setItemInHand( null );
		entityId = npc.getEntityId();
		UtilEnt.setNoAI( npc, true );
		UtilEnt.setSilent( npc, true );
		dbase = new DisguisePlayer( npc, playername );
		Skin skin = module.getSkinCache().get( player.getUniqueId() );
		if ( skin != null ) {
			dbase.loadSkin( skin );
		}
		UtilServer.getDisguiseManager().disguise( dbase );
		module.getNpcs().put( npc.getEntityId(), this );
		module.getNpcs_playerId().put( getPlayerId(), this );
	}
}
