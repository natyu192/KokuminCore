package me.nucha.core.sql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrefixesDaoImpl implements PrefixesDao {

	private Connection connection;

	public PrefixesDaoImpl(Connection connection) {
		this.connection = connection;
	}

	@Override
	public List<Prefix> selectAll(String playerUUID) throws SQLException {
		playerUUID = removeHyphens(playerUUID);
		String sql = "SELECT * FROM prefix.%s";
		sql = String.format(sql, playerUUID);
		PreparedStatement statement = connection.prepareStatement(sql);
		ResultSet result = statement.executeQuery();

		List<Prefix> prefixes = new ArrayList<>();
		while (result.next()) {
			String id = result.getString(1);
			String prefix = result.getString(2);
			String description = result.getString(3);
			prefixes.add(new Prefix(id, prefix, description));
		}
		return prefixes;
	}

	@Override
	public void add(String playerUUID, Prefix prefix) throws SQLException {
		playerUUID = removeHyphens(playerUUID);
		String sql = "INSERT INTO prefix.%s (id, prefix, description) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE prefix = ?, description = ?";
		sql = String.format(sql, playerUUID);
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, prefix.getId());
		statement.setString(2, prefix.getPrefix());
		statement.setString(3, prefix.getDescription());
		statement.setString(4, prefix.getPrefix());
		statement.setString(5, prefix.getDescription());
		statement.executeUpdate();
	}

	@Override
	public void delete(String playerUUID, String id) throws SQLException {
		playerUUID = removeHyphens(playerUUID);
		String sql = "DELETE FROM prefix.%s WHERE id = ?";
		sql = String.format(sql, playerUUID);
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, id);
		statement.executeUpdate();
	}

	@Override
	public void createTable(String playerUUID) throws SQLException {
		playerUUID = removeHyphens(playerUUID);
		String sql = "CREATE TABLE IF NOT EXISTS prefix.%s (id char(64) PRIMARY KEY, prefix nchar(255), description nchar(255))";
		sql = String.format(sql, playerUUID);
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.executeUpdate();
	}

	@Override
	public void createDatabase() throws SQLException {
		String sql = "CREATE DATABASE IF NOT EXISTS prefix";
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.executeUpdate();
	}

	@Override
	public List<String> getTables() throws SQLException {
		String sql = "SHOW TABLES FROM prefix";
		PreparedStatement statement = connection.prepareStatement(sql);
		ResultSet result = statement.executeQuery();

		List<String> tables = new ArrayList<>();
		while (result.next()) {
			tables.add(result.getString(1));
		}
		return tables;
	}

	private String removeHyphens(String s) {
		return s.replaceAll("-", "");
	}

}