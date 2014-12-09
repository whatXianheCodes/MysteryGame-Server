package com.xdgames.MysteryGame;

import java.sql.*;

class DatabaseHelper {
	private final String JDBC_DRIVER="com.mysql.jdbc.Driver";  
    private String DB_URL;
    private String USERNAME;
    private String PASSWORD;
    private Connection connection;

    
    public DatabaseHelper () {
    	// TODO: rename database from xdgames to xdgamesDB
    	this.DB_URL ="jdbc:mysql://localhost/xdgames";
		this.USERNAME = "root";
		// TODO: have a better password once this repo is private
		this.PASSWORD = "pass1234";
    }
	public DatabaseHelper (String dbUrl, String userName, String password) {
		this.DB_URL = dbUrl;
		this.USERNAME = userName;
		this.PASSWORD = password;
	}
	
	public void openDatabase() {
		try{
	         // Register JDBC driver
	         Class.forName(this.JDBC_DRIVER);

	         if (connection == null) {
	        	 connection = DriverManager.getConnection(DB_URL, this.USERNAME, this.PASSWORD);
	         }
		}catch(Exception e){
	         e.printStackTrace();
	    }
	}
	
	public ResultSet executeQuery (String statement) {
		try {
			if (connection == null) {
				this.openDatabase();
			}
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt .executeQuery(statement);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public void closeDatabase () {
		if (connection != null){
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
