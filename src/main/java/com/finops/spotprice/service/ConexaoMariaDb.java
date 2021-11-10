package com.finops.spotprice.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * 
 * ESSA CLASSE É UTILIZADA PARA POSSIBILITAR O ENVIO DOS DADOS DA AZURE COM JDBC
 * 
 */

public class ConexaoMariaDb {

	static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
	static final String URL = "jdbc:mariadb://localhost:3306/spotpricehistory";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "root";

	private static Connection conn = null;
	private Statement stmt = null;

	public static Connection conectar() {

		try {
			// STEP 2: Register JDBC driver
			Class.forName("org.mariadb.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(URL, USER, PASS);
			System.out.println("Connected database successfully...");

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}

		return conn;
	}

	public void desconectar() {

		// finally block used to close resources
		try {
			if (stmt != null) {
				conn.close();
				System.out.println("Desconected");
			}
		} catch (SQLException se) {

		} // do nothing
		try {
			if (conn != null) {
				conn.close();
				System.out.println("Desconected");
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} // end finally try

	} // end try

}
