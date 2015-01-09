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
    private User user;
    private JSONObject registrationJSON; 
    
    public RegisterUser() {
    	db = new DatabaseHelper();
    	user = new User();
    }
          
    private String validateUserRegistration (String fname, String lname, String email,	
    		String username, String invitationCode,	String password) {

    	if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() 
				|| username.isEmpty() || invitationCode.isEmpty() || password.isEmpty()) {
			return "Error: Empty fields";
		}
    	if (!user.checkFName (fname)) {
    		return "Error: Invalid first name";
    	}
    	if (!user.checkLName (lname)) {
    		return "Error: Invalid last name";
    	}
    	if (user.existInDB(db, username, user.USER_NAME_DB)) {
		    return "Error: Usename already exist";
	    }
    	if (!user.checkPassword (password)) {
    		return "Error: Invalid password";
    	}
    	if (!user.checkEmail(email)) {
    		return "Error: Invalid email";
    	}
    	if (!user.checkInvitation (invitationCode)) {
    		return "Error: Invalid invitation code";
    	}	   
	    if (user.existInDB(db, email, user.EMAIL_DB)) {
		    return "Error: Email already exist";
	    }
    	return "Success";
    }
    
    private void setRegistrationResponse (String msg, boolean success, HttpServletResponse response) throws IOException {
    	JSONObject registrationResultJSON = new JSONObject();
    	JSONObject resultJSON = new JSONObject();
    	resultJSON.put("Success", success);
    	resultJSON.put("Message", msg);
    	registrationResultJSON.put("RegistrationResult", resultJSON);
    	response.setContentType("application/json");
    	response.getWriter().write(registrationResultJSON.toString());
    	if (success) {
    		response.setStatus(response.SC_OK);
    	}
    	else { 
    		response.setStatus(response.SC_FORBIDDEN);
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
		LOGGER.info("get");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		registrationJSON = user.getJSON(request, "registration");
		
		// TODO:need to escape them in order to prevent sql injection
		String fname = user.getValueFromRegistration(registrationJSON, user.FIRST_NAME_SERVER);
		String lname = user.getValueFromRegistration(registrationJSON, user.LAST_NAME_SERVER);
		String email = user.getValueFromRegistration(registrationJSON, user.EMAIL_SERVER);
		String username = user.getValueFromRegistration(registrationJSON, user.USER_NAME_SERVER);
		String invitationCode = user.getValueFromRegistration(registrationJSON, user.INVITATION_CODE_SERVER);
		String password = user.getValueFromRegistration(registrationJSON, user.PASSWORD_SERVER);
		Boolean success = true;
		
		String result = validateUserRegistration(fname, lname, email, username, invitationCode, password);
		LOGGER.info (result);
		if (result.contains("Error")) {
			success = false;
			setRegistrationResponse(result, success, response);
			return;
		}
		
		success = user.insertUserIntoDB (db, fname, lname, email, username, invitationCode, password);
		try {
			setRegistrationResponse(result, success, response);
		}
		catch (IOException e) {
			e.printStackTrace();
			response.setStatus(response.SC_FORBIDDEN);
		}

		//readAllUsers();
		db.closeDatabase();

	}

}

