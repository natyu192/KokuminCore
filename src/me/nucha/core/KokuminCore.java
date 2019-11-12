package me.nucha.core;

import org.bukkit.plugin.java.JavaPlugin;

import me.nucha.core.hologram.HologramManager;
import me.nucha.core.listener.NPCDisplayListener;
import me.nucha.core.listener.NPCUpdater;
import me.nucha.core.npc.NPCManager;
import me.nucha.core.packet.PacketHandler;

public class KokuminCore extends JavaPlugin {

	private static KokuminCore plugin;
	private static NPCDisplayListener npcDisplayListener;

	@Override
	public void onEnable() {
		plugin = this;
		PacketHandler.init();
		NPCManager.init();
		HologramManager.init();
		NPCUpdater.init();
		PacketHandler.registerPacketListener(npcDisplayListener = new NPCDisplayListener());
	}

	@Override
	public void onDisable() {
		PacketHandler.unregisterPacketListener(npcDisplayListener);
	}

	public static KokuminCore getInstance() {
		return plugin;
	}

}
