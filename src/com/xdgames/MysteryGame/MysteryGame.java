package com.xdgames.MysteryGame;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet implementation class xdgamesServetMain
 */
@WebServlet("/MysteryGame")
public class MysteryGame extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final static Logger LOGGER = Logger.getLogger(DatabaseHelper.class.getName()); 
    private DatabaseHelper db;

    public MysteryGame() {
    	db = new DatabaseHelper();
    }
    
    private void readAllUsers () {
    	db.openDatabase();
    	try {
	    	ResultSet rs = db.executeQuery("SELECT * FROM Users");
	    	if (rs != null) {
		    	while (rs.next()) {
					String fname = rs.getString("FirstName");
					String lname = rs.getString("LastName");
					String email = rs.getString("EmailAddress");
					String username = rs.getString("UserName");
					String invitationCode = rs.getString("InvitationCode");
					String password = rs.getString("Password");
					LOGGER.setLevel(Level.INFO);
					LOGGER.info("First name " + fname);
					LOGGER.info("Last name " + lname);
					LOGGER.info("User name " + username);
					LOGGER.info("Password " + password);
					LOGGER.info("Email " + email);
					LOGGER.info("Invitation code " + invitationCode);	
				}
	    	}
    	}
    	catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public void init() throws ServletException
    {
        // Do required initialization
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.readAllUsers();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
