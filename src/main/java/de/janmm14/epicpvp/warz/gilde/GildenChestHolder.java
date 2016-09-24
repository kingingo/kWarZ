package de.janmm14.epicpvp.warz.gilde;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class GildenChestHolder implements InventoryHolder {

	@Getter
	@Setter
	private Inventory inventory;
	@Getter
	@NonNull
	private GildeModule module;
}
