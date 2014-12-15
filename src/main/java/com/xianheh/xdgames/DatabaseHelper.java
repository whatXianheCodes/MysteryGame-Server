package com.xianheh.xdgames;

import java.sql.*;

class DatabaseHelper {
	private final String JDBC_DRIVER="com.mysql.jdbc.Driver";  
    private String DB_URL;
    private String USERNAME = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
    private String PASSWORD = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
    private String hostname = System.getenv("OPENSHIFT_MYSQL_DB_HOST"); 
    private String portNumber = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
    private String appName = System.getenv("OPENSHIFT_APP_NAME");
    private Connection connection;

    
    public DatabaseHelper () {
    	this.DB_URL ="jdbc:mysql://" + this.hostname + ":" + this.portNumber + "/" + this.appName;
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
