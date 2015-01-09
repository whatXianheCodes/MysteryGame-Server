package com.xianheh.xdgames;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Servlet implementation class LoginAuthentication
 */
@WebServlet("/MysteryGame/login")
public class LoginAuthentication extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DatabaseHelper db;
    private JSONObject loginJSON; 
    private User user;
    
    private final static Logger LOGGER = Logger.getLogger(DatabaseHelper.class.getName()); 
   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginAuthentication() {
        super();
        db = new DatabaseHelper();
        user = new User();
    }
    
    private String validateUserLogin (String email,	String username, String password) {
    	final String ERROR_MESSAGE = "Error: Invalid credential please try again";
    	if ((email.isEmpty() && username.isEmpty()) || password.isEmpty()) {
			return "Error: Empty fields";
		}
    	if (!user.existInDB(db, password, user.PASSWORD_DB)) {
		    return ERROR_MESSAGE;
	    }
    	if (!email.isEmpty()) {
    		if (!user.existInDB(db, email, user.EMAIL_DB)) {
    		    return ERROR_MESSAGE;
    	    }
    	}
    	else {
    		if (!user.existInDB(db, username, user.USER_NAME_DB)) {
    		    return ERROR_MESSAGE;
    	    }
    	}

    	return "Success";
    }
    
    private void setRegistrationResponse (String msg, boolean success, HttpServletResponse response) throws IOException {
    	JSONObject loginResultJSON = new JSONObject();
    	JSONObject resultJSON = new JSONObject();
    	resultJSON.put("Success", success);
    	resultJSON.put("Message", msg);
    	loginResultJSON.put("LoginResult", resultJSON);
    	response.setContentType("application/json");
    	response.getWriter().write(loginResultJSON.toString());
    	if (success) {
    		response.setStatus(response.SC_OK);
    	}
    	else { 
    		response.setStatus(response.SC_FORBIDDEN);
    	}
    }
    
    // This should be refactored into another class will do later to prevent duplicate code       

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		loginJSON = user.getJSON(request, "login");
		String email = user.getValueFromRegistration(loginJSON, user.EMAIL_SERVER);
		String username = user.getValueFromRegistration(loginJSON, user.USER_NAME_SERVER);
		String password = user.getValueFromRegistration(loginJSON, user.PASSWORD_SERVER);
		Boolean success = true;
		
		String result = validateUserLogin(email, username, password);
		LOGGER.info (result);
		if (result.contains("Error")) {
			success = false;
			setRegistrationResponse(result, success, response);
			return;
		}
		
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
