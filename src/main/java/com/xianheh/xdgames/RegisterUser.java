package com.xianheh.xdgames;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Servlet implementation class xdgamesServetMain
 */
@WebServlet("/MysteryGame/register")
public class RegisterUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final static Logger LOGGER = Logger.getLogger(DatabaseHelper.class.getName()); 
    private DatabaseHelper db;
    private JSONObject registrationJSON;
    
    private final String FIRST_NAME_DB = "FirstName";
    private final String LAST_NAME_DB = "LastName";
    private final String EMAIL_DB = "EmailAddress";
    private final String USER_NAME_DB = "UserName";
    private final String INVITATION_CODE_DB = "InvitationCode";
    private final String PASSWORD_DB = "Password";
    private final String FIRST_NAME_SERVER = "firstname";
    private final String LAST_NAME_SERVER = "lastname";
    private final String EMAIL_SERVER = "email";
    private final String USER_NAME_SERVER = "username";
    private final String INVITATION_CODE_SERVER = "invitation_code";
    private final String PASSWORD_SERVER = "password";

    public RegisterUser() {
    	db = new DatabaseHelper();
    }
    
    private void readAllUsers () {
    	db.openDatabase();
    	try {
	    	ResultSet rs = db.readQuery("SELECT * FROM Users");
	    	if (rs != null) {
		    	while (rs.next()) {
					String fname = rs.getString(FIRST_NAME_DB);
					String lname = rs.getString(LAST_NAME_DB);
					String email = rs.getString(EMAIL_DB);
					String username = rs.getString(USER_NAME_DB);
					String invitationCode = rs.getString(INVITATION_CODE_DB);
					String password = rs.getString(PASSWORD_DB);
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
    	finally {
    		db.closeDatabase();
    	}
    }
    
    private void getRegistrationJSON (HttpServletRequest request) {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Object obj = JSONValue.parse(jb.toString());
		JSONObject jsonObject = (JSONObject) obj;
		String registrationJSONString = jsonObject.get("registration").toString();
		obj = JSONValue.parse(registrationJSONString);
		registrationJSON = (JSONObject) obj;

    }
    
    private String getValueFromRegistration (String key) {
		return registrationJSON.get(key).toString();
    }
    
    private boolean insertUserIntoDB (String fname, String lname, String email,	
    		String username, String invitationCode,	String password) {
    	
    	String stmt; 
    	stmt = "INSERT INTO Users (";
    	stmt += FIRST_NAME_DB + ", " + LAST_NAME_DB + ", " + EMAIL_DB + ", "; 
    	stmt += USER_NAME_DB + ", " + INVITATION_CODE_DB + ", " + PASSWORD_DB +") "; 
    	stmt += "VALUES ('" + fname + "','" + lname + "','" + email + "','"; 
    	stmt += username + "','" + invitationCode + "','" + password +"');";  	
    	
    	db.openDatabase();
    	return db.insertQuery(stmt);
    }
    
    public void init() throws ServletException
    {
        // Do required initialization
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOGGER.info("get");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getRegistrationJSON(request);
		
		// TODO:need to escape them in order to prevent sql injection
		String fname = getValueFromRegistration(FIRST_NAME_SERVER);
		String lname =getValueFromRegistration(LAST_NAME_SERVER);
		String email = getValueFromRegistration(EMAIL_SERVER);
		String username = getValueFromRegistration(USER_NAME_SERVER);
		String invitationCode = getValueFromRegistration(INVITATION_CODE_SERVER);
		String password = getValueFromRegistration(PASSWORD_SERVER);
		
		// TODO: properly handle response and put into seperate function
		// TODO: Check before inserting into SQL database
		if (insertUserIntoDB (fname, lname, email, username, invitationCode, password)) {
			response.setStatus(response.SC_OK);
			response.addHeader("Access-Control-Allow-Origin", "*");
		    response.addHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
		    response.addHeader("Access-Control-Allow-Headers", "Content-Type");
		    response.addHeader("Access-Control-Max-Age", "86400");
		}
		
		else{
			response.setStatus(response.SC_FORBIDDEN);
			response.addHeader("Access-Control-Allow-Origin", "*");
		    response.addHeader("Access-Control-Allow-Methods", "GET, PUT, POST, OPTIONS, DELETE");
		    response.addHeader("Access-Control-Allow-Headers", "Content-Type");
		    response.addHeader("Access-Control-Max-Age", "86400");
		}

	}

}

