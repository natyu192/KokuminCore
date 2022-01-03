package me.nucha.core.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.nucha.core.KokuminCore;

public class SQLManager {

	private static Connection connection;
	private static String host, database, username, password;
	private static int port;
	private static int timeFailedToConnect;

	public static void init(KokuminCore plugin) {
		host = plugin.getConfig().getString("database.host");
		database = plugin.getConfig().getString("database.database");
		username = plugin.getConfig().getString("database.username");
		password = plugin.getConfig().getString("database.password");
		port = plugin.getConfig().getInt("database.port");
		timeFailedToConnect = 0;
		if (host.equalsIgnoreCase("host")) {
			Bukkit.getConsoleSender().sendMessage("§8[§aKokuminCore§8] §eYou can also setup MySQL in the config.");
		} else {
			connect();
		}
	}

	public static void reconnect() {
		closeConnection();
		connect();
	}

	private static void closeConnection() {
		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void connect() {
		try {
			if (getConnection() != null && getConnection().isClosed()) {
				return;
			}
			setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password));
			Bukkit.getConsoleSender().sendMessage("§8[§aKokuminCore§8] §aMySQL connected: §e" + host);
			PrefixManager.init(connection);
		} catch (SQLException e) {
			e.printStackTrace();
			timeFailedToConnect++;
			Bukkit.getConsoleSender().sendMessage("§8[§aKokuminCore§8] §cMySQL failed to connect (" + timeFailedToConnect + "): §e" + host);
			if (timeFailedToConnect <= 3) {
				Bukkit.getConsoleSender().sendMessage("§8[§aKokuminCore§8] §cReconnecting in 30 seconds...");
				Bukkit.getScheduler().runTaskLaterAsynchronously(KokuminCore.getInstance(), new BukkitRunnable() {
					@Override
					public void run() {
						reconnect();
					}
				}, 30 * 20L);
			}
		}
	}

	public static Connection getConnection() {
		return connection;
	}

	public static void setConnection(Connection connection) {
		SQLManager.connection = connection;
	}
}
