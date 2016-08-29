package de.janmm14.epicpvp.warz.hooks;

import org.bukkit.entity.Player;

import eu.epicpvp.kcore.Translation.TranslationHandler;

public class LanguageConverter {

	public String getLocalized(Player target, String identifier, Object... arguments) {
		return TranslationHandler.getText( target, identifier, arguments );
	}
}
