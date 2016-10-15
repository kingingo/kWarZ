package de.janmm14.epicpvp.warz.clan;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ClanChestHolder implements InventoryHolder {

	@Getter
	@Setter
	private Inventory inventory;
	@Getter
	@NonNull
	private ClanModule module;
}
