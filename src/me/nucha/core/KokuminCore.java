package me.nucha.core;

import org.bukkit.plugin.java.JavaPlugin;

import me.nucha.core.command.CommandNPCUpdater;
import me.nucha.core.command.CommandPrefix;
import me.nucha.core.hologram.HologramManager;
import me.nucha.core.listener.NPCDisplayListener;
import me.nucha.core.listener.NPCUpdater;
import me.nucha.core.npc.NPCManager;
import me.nucha.core.packet.PacketHandler;
import me.nucha.core.sql.PrefixManager;
import me.nucha.core.sql.SQLManager;

public class KokuminCore extends JavaPlugin {

	private static KokuminCore plugin;
	private static NPCDisplayListener npcDisplayListener;

	@Override
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		SQLManager.init(plugin);
		PrefixManager.init(plugin);
		PacketHandler.init();
		NPCManager.init();
		HologramManager.init();
		NPCUpdater.init();
		PacketHandler.registerPacketListener(npcDisplayListener = new NPCDisplayListener());
		getCommand("npcupdater").setExecutor(new CommandNPCUpdater());
		getCommand("prefix").setExecutor(new CommandPrefix());
		getCommand("sprefix").setExecutor(new CommandPrefix());
	}

	@Override
	public void onDisable() {
		PacketHandler.unregisterPacketListener(npcDisplayListener);
	}

	public static KokuminCore getInstance() {
		return plugin;
	}

}
