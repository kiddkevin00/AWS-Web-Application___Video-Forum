package com.marcus.function;

import java.sql.DriverManager;
import java.sql.ResultSet;

import com.amazonaws.auth.policy.Statement;
import com.amazonaws.services.directconnect.model.Connection;

public class RDSManager {

	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private String DB_END_POINT = "YOUR_DB_END_POINT";
	private final String DB_USER_NAME = "YOUR_DB_USER_NAME";
	private final String DB_PWD = "YOUR_DB_PASSWORD";
	private final String DB_NAME = "YOUR_DB_NAME";
	private final int DB_PORT = 3306; // default port number

	public void createConnectionAndStatement() {
		try {
			// This will load the MySQL driver, each DB has its own driver
			Class.forName("com.mysql.jdbc.OracleDriver").newInstance();
			// Setup the connection with the DB
			connect = (Connection) DriverManager.getConnection("jdbc:mysql://"
					+ DB_END_POINT + ":" + DB_PORT + "/" + DB_NAME,
					DB_USER_NAME, DB_PWD);

			// Statements allow to issue SQL queries to the database
			statement = (Statement) ((java.sql.Connection) connect)
					.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			// close();
		}
	}

	public void createTable() {
		try {
			createConnectionAndStatement();
			String createTableSql = "CREATE TABLE VIDEO_INFO (name VARCHAR(255) not NULL, timestamp TIMESTAMP, "
					+ " s3link VARCHAR(255), cflink VARCHAR(255), rating INTEGER, totalvotes INTEGER)";
			((java.sql.Statement) statement).executeUpdate(createTableSql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// close();
		}

	}

}
