package me.nucha.core.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import me.nucha.core.KokuminCore;
import me.nucha.core.sql.dao.Prefix;
import me.nucha.core.sql.dao.PrefixesDao;
import me.nucha.core.sql.dao.PrefixesDaoImpl;

public class PrefixManager {

	private static PrefixesDao prefixesDao;
	private static HashMap<UUID, List<Prefix>> prefixes = new HashMap<>();
	private static HashMap<String, String> unicodeChars = new HashMap<>();

	public static void init(Connection connection) {
		prefixesDao = new PrefixesDaoImpl(connection);
		Bukkit.getServer().getPluginManager().registerEvents(new JoinListener(), KokuminCore.getInstance());

		try {
			prefixesDao.createDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		unicodeChars.put("flower", "✿");
		unicodeChars.put("pound", "£");
		unicodeChars.put("star", "✫");
		unicodeChars.put("s", "☯");
		unicodeChars.put("sigma", "Σ");
		unicodeChars.put("circled_star", "✪");
		unicodeChars.put("star2", "✯");
		unicodeChars.put("arrow", "➳");
		unicodeChars.put("manzi", "卍");
		unicodeChars.put("dagger", "✞");
		unicodeChars.put("x", "✘");
		unicodeChars.put("heart", "❤");
		unicodeChars.put("heart2", "❥");
		unicodeChars.put("peace", "✌");
		unicodeChars.put("check", "✔");
		unicodeChars.put("airplane", "✈");
	}

	public static List<Prefix> getPrefixes(UUID uuid) {
		if (prefixesDao == null) {
			return new ArrayList<>();
		}
		if (prefixes.containsKey(uuid)) {
			return prefixes.get(uuid);
		}
		try {
			List<Prefix> list = prefixesDao.selectAll(uuid.toString());
			prefixes.put(uuid, list);
			return list;
		} catch (SQLException e) {
			return new ArrayList<>();
		}
	}

	public static String getPrefix(UUID uuid) {
		if (prefixesDao == null) {
			return "";
		}
		StringBuilder sbuilder = new StringBuilder();
		for (Prefix prefix : getPrefixes(uuid)) {
			String s = prefix.getPrefix();
			s = ChatColor.translateAlternateColorCodes('&', prefix.getPrefix());
			s = replaceUnicodes(s);
			sbuilder.append(s);
		}
		return sbuilder.toString();
	}

	public static void addPrefix(UUID uuid, Prefix prefix) {
		if (prefixesDao == null) {
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(KokuminCore.getInstance(), () -> {
			try {
				if (!prefixes.containsKey(uuid)) {
					prefixesDao.createTable(uuid.toString());
					prefixes.put(uuid, new ArrayList<>());
				}
				prefixesDao.add(uuid.toString(), prefix);
				Prefix toRemove = null;
				for (Prefix _prefix : prefixes.get(uuid)) {
					if (_prefix.getId().equalsIgnoreCase(prefix.getId())) {
						toRemove = _prefix;
						break;
					}
				}
				if (toRemove != null) {
					prefixes.get(uuid).remove(toRemove);
				}
				prefixes.get(uuid).add(prefix);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	public static void removePrefix(UUID uuid, String id) {
		if (prefixesDao == null) {
			return;
		}
		if (!prefixes.containsKey(uuid)) {
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(KokuminCore.getInstance(), () -> {
			try {
				prefixesDao.delete(uuid.toString(), id);
				List<Prefix> list = prefixes.get(uuid);
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getId().equalsIgnoreCase(id)) {
						list.remove(i);
						return;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	protected static void cachePrefix(UUID uuid, List<Prefix> prefix) {
		prefixes.put(uuid, prefix);
	}

	protected static PrefixesDao getPrefixesDao() {
		return prefixesDao;
	}

	public static String replaceUnicodes(String s) {
		for (String key : unicodeChars.keySet()) {
			s = s.replaceAll("\\{" + key + "\\}", unicodeChars.get(key));
		}
		return s;
	}

}

class JoinListener implements Listener {

	@EventHandler
	public void onJoin(AsyncPlayerPreLoginEvent event) {
		UUID uuid = event.getUniqueId();
		try {
			PrefixesDao dao = PrefixManager.getPrefixesDao();
			dao.createTable(uuid.toString());
			List<Prefix> prefix = dao.selectAll(uuid.toString());
			PrefixManager.cachePrefix(uuid, prefix);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}