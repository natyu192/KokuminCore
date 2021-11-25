package me.nucha.core.sql;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.core.KokuminCore;

public class PrefixManager {

	private static HashMap<UUID, String> prefixes;
	private static HashMap<UUID, String> shortprefixes;

	public static void init(KokuminCore plugin) {
		prefixes = new HashMap<>();
		shortprefixes = new HashMap<>();
		plugin.getServer().getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onJoin(PlayerJoinEvent event) {
				Player p = event.getPlayer();
				UUID uuid = p.getUniqueId();
				Bukkit.getScheduler().runTaskAsynchronously(plugin, new BukkitRunnable() {
					@Override
					public void run() {
						String prefix = SQLManager.getPrefix(uuid, false);
						prefix = ChatColor.translateAlternateColorCodes('&', prefix);
						prefixes.put(uuid, prefix);
						String shortprefix = SQLManager.getPrefix(uuid, true);
						shortprefix = ChatColor.translateAlternateColorCodes('&', shortprefix);
						shortprefixes.put(uuid, shortprefix);
					}
				});
			}
		}, plugin);
	}

	public static String getPrefix(UUID uuid, boolean shorter) {
		if (!prefixes.containsKey(uuid)) {
			return "";
		}
		return shorter ? shortprefixes.get(uuid) : prefixes.get(uuid);
	}

}
