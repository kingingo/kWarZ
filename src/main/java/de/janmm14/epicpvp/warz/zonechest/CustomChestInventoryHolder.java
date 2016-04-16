package de.janmm14.epicpvp.warz.zonechest;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.BlockVector;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * currently not really needed, but maybe for later
 */
@Getter
@Setter
@RequiredArgsConstructor
public class CustomChestInventoryHolder implements InventoryHolder {

	private final BlockVector blockLocation;
	private Inventory inventory;
}
