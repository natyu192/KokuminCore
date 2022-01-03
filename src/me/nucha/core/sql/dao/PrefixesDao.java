package me.nucha.core.sql.dao;

import java.sql.SQLException;
import java.util.List;

public interface PrefixesDao {

	public List<Prefix> selectAll(String playerUUID) throws SQLException;

	public void add(String playerUUID, Prefix prefix) throws SQLException;

	public void delete(String playerUUID, String id) throws SQLException;

	public void createTable(String playerUUID) throws SQLException;

	public void createDatabase() throws SQLException;

	public List<String> getTables() throws SQLException;

}
