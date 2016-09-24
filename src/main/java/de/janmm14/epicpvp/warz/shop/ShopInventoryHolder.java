package de.janmm14.epicpvp.warz.shop;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import de.janmm14.epicpvp.warz.hooks.UserDataConverter;
import eu.epicpvp.kcore.kConfig.kConfig;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ShopInventoryHolder implements InventoryHolder {

	@Getter
	@Setter
	private Inventory inventory;
	@Getter
	@NonNull
	private final UserDataConverter.Profile profile;
	@Getter
	@NonNull
	private final kConfig config;
}
