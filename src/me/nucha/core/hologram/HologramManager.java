package me.nucha.core.hologram;

import java.util.List;

import com.google.common.collect.Lists;

public class HologramManager {

	private static List<Hologram> holograms;

	public static void init() {
		holograms = Lists.newArrayList();
	}

	public static void registerHologram(Hologram hologram) {
		if (!holograms.contains(hologram)) {
			holograms.add(hologram);
		}
	}

	public static void unregisterHologram(Hologram hologram) {
		if (holograms.contains(hologram)) {
			holograms.remove(hologram);
		}
	}

	public static List<Hologram> getHolograms() {
		return holograms;
	}

}
