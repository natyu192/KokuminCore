package me.nucha.core.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

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

	public static boolean isPrefixSet(UUID uuid, boolean shorter) {
		String prefix = getPrefix(uuid, shorter);
		return prefix != null && !prefix.isEmpty();
	}

	public static void setPrefix(UUID uuid, String prefix, boolean shorter) {
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM prefix WHERE UUID=?");
			statement.setString(1, uuid.toString());
			ResultSet results = statement.executeQuery();
			if (results.next()) { // If line is already inserted:
				PreparedStatement insert = connection.prepareStatement("UPDATE prefix SET " + (shorter ? "SHORT" : "") + "PREFIX=? WHERE UUID=?");
				insert.setString(1, prefix);
				insert.setString(2, uuid.toString());
				insert.executeUpdate();
			} else {
				PreparedStatement insert = connection.prepareStatement("INSERT INTO prefix (UUID," + (shorter ? "SHORT" : "") + "PREFIX) VALUES (?,?)");
				insert.setString(1, uuid.toString());
				insert.setString(2, prefix);
				insert.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getPrefix(UUID uuid, boolean shorter) {
		try {
			if (connection != null) {
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM prefix WHERE UUID=?");
				statement.setString(1, uuid.toString());
				ResultSet results = statement.executeQuery();
				if (results.next()) {
					String prefix = results.getString((shorter ? "SHORT" : "") + "PREFIX");
					return prefix != null ? prefix : "";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

}
