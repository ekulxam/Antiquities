package net.hollowed.antique.items;

import net.minecraft.item.Instrument;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface ModInstruments {
	RegistryKey<Instrument> WARHORN = of("warhorn");

	private static RegistryKey<Instrument> of(String id) {
		return RegistryKey.of(RegistryKeys.INSTRUMENT, Identifier.ofVanilla(id));
	}
}
