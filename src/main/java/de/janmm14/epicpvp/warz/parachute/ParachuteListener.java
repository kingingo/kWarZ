package de.janmm14.epicpvp.warz.parachute;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParachuteListener implements Listener {

	private final ParachuteModule module;

	@EventHandler(priority = EventPriority.MONITOR)
	public void inter(PlayerInteractEvent event) {
		Player plr = event.getPlayer();
		ItemStack itemInHand = plr.getItemInHand();
		if ( itemInHand != null && itemInHand.getType() == Material.FEATHER ) {
			if ( itemInHand.getAmount() > 1 ) {
				itemInHand.setAmount( itemInHand.getAmount() - 1 );
				plr.setItemInHand( itemInHand );
			} else {
				plr.setItemInHand( null );
			}

			Chicken chickenBelow = plr.getWorld().spawn( plr.getLocation(), Chicken.class );
			chickenBelow.addPotionEffect( new PotionEffect( PotionEffectType.INVISIBILITY, 999999, 1 ), true );
			plr.setPassenger( chickenBelow );

			Location aboveChickenLocation = plr.getEyeLocation().add( 0, .5, 0 );
			Chicken aboveChicken = event.getPlayer().getWorld().spawn( aboveChickenLocation, Chicken.class );
			chickenBelow.setPassenger( aboveChicken );

			module.getPlugin().getServer().getScheduler().runTaskLater( module.getPlugin(), () -> {
				if ( plr.isOnline() && plr.getPassenger().getUniqueId().equals( aboveChicken.getUniqueId() ) ) { //check online and if its the same parachute
					stopParachuting( plr );
				}
			}, module.getMaxTimeTicks() );
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player plr = event.getPlayer();
		Entity plrPassenger = plr.getPassenger();
		if ( plrPassenger != null && plrPassenger.getType() == EntityType.CHICKEN ) {
			Location plrLocation = plr.getLocation();
			if ( plr.isSneaking() || plr.isOnGround() || plrLocation.getY() < -10 ) {
				stopParachuting( plr );
				return;
			}

			Vector dir = plrLocation.getDirection().normalize();
			plr.setVelocity( new Vector(
				dir.getX() * 0.4,
				plr.getVelocity().getY() * 0.4,
				dir.getZ() * 0.4 ) );

			plr.setFallDistance( 1.0F );
		}
	}

	private void stopParachuting(Player plr) {
		Entity plrPassenger = plr.getPassenger();
		Entity plrVehicle = plr.getVehicle();

		plr.leaveVehicle();
		plr.eject();

		if ( plrPassenger != null ) {
			plrPassenger.remove();
		}
		if ( plrVehicle != null ) {
			plrVehicle.remove();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void damage(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity plrPassenger = entity.getPassenger();
		if ( entity.getType() == EntityType.CHICKEN ) {
			if ( plrPassenger != null && plrPassenger.getType() == EntityType.PLAYER ) {
                entity.getWorld().spigot().playEffect( entity.getLocation(), Effect.EXPLOSION_LARGE );
                stopParachuting( ( Player ) plrPassenger );
			}
			Entity plrVehicle = entity.getPassenger();
			if ( plrVehicle != null && plrVehicle.getType() == EntityType.PLAYER ) {
				entity.getWorld().spigot().playEffect( entity.getLocation(), Effect.EXPLOSION_LARGE );
				stopParachuting( ( Player ) plrVehicle );
			}
		}

	}
}
