package net.hollowed.antique.index;

import net.minecraft.item.Instrument;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface AntiqueInstruments {
	RegistryKey<Instrument> WARHORN = of("warhorn");

	@SuppressWarnings("all")
	private static RegistryKey<Instrument> of(String id) {
		return RegistryKey.of(RegistryKeys.INSTRUMENT, Identifier.ofVanilla(id));
	}
}
