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
    
    private boolean checkInvitation (String invitationCode) {
        //TODO: add security check to determine whether the code is valid
        final String INVITE_CODE_PATTERN = "^[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}$";
        return invitationCode.matches(INVITE_CODE_PATTERN);
    }

    private boolean checkFName (String firstName) {
        final String NAME_PATTERN = "[-A-Za-z]{1,}";
        return (firstName.matches(NAME_PATTERN));
    }
    
    private boolean checkLName (String lastname) {
        final String NAME_PATTERN = "[-A-Za-z]{1,}";
        return (lastname.matches(NAME_PATTERN));
    }

    private boolean checkEmail (String email) {
    	final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(EMAIL_PATTERN);
    }

    private boolean checkPassword (String password) {
        return (password.length()!= 0);
    }
   
    private boolean containUsername (String username) {
    	username = username.toLowerCase();
    	   
    	String stmt = "SELECT * FROM Users WHERE ";
    	stmt  += USER_NAME_DB + "= '" + username + "';";
    	LOGGER.info(stmt);
    	db.openDatabase();
    	ResultSet rs = db.readQuery(stmt );
	    try {
	    	if (rs.next()) {
	    		return true;
	    	}
	    }
	    catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	    return false;
    }
    
    private boolean containEmail (String email) {
    	email = email.toLowerCase();
    	String stmt = "SELECT * FROM Users WHERE ";
     	stmt  += EMAIL_DB + "= '" + email + "';";
     	db.openDatabase();
     	ResultSet rs = db.readQuery(stmt );
	    try {
	    	if (rs.next()) {
	    		return true;
	    	}
	    }
	    catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	    return false;
    }
    
    private String validateUserRegistration (String fname, String lname, String email,	
    		String username, String invitationCode,	String password) {

    	if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() 
				|| username.isEmpty() || invitationCode.isEmpty() || password.isEmpty()) {
			return "Error: Empty fields";
		}
    	if (!checkFName (fname)) {
    		return "Error: Invalid first name";
    	}
    	if (!checkLName (lname)) {
    		return "Error: Invalid last name";
    	}
    	if (containUsername(username)) {
		    return "Error: Usename already exist";
	    }
    	if (!checkPassword (password)) {
    		return "Error: Invalid password";
    	}
    	if (!checkEmail(email)) {
    		return "Error: Invalid email";
    	}
    	if (!checkInvitation (invitationCode)) {
    		return "Error: Invalid invitation code";
    	}	   
	    if (containEmail(email)) {
		    return "Error: Email already exist";
	    }
    	return "Success";
    }
    
    private void setRegistrationResponse (String msg, boolean success, HttpServletResponse response) throws IOException {
    	JSONObject registrationJSON = new JSONObject();
    	JSONObject resultJSON = new JSONObject();
    	resultJSON.put("Success", success);
    	resultJSON.put("Message", msg);
    	registrationJSON.put("RegistrationResult", resultJSON);
    	response.setContentType("application/json");
    	response.getWriter().write(registrationJSON.toString());
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
		getRegistrationJSON(request);
		
		// TODO:need to escape them in order to prevent sql injection
		String fname = getValueFromRegistration(FIRST_NAME_SERVER);
		String lname =getValueFromRegistration(LAST_NAME_SERVER);
		String email = getValueFromRegistration(EMAIL_SERVER);
		String username = getValueFromRegistration(USER_NAME_SERVER);
		String invitationCode = getValueFromRegistration(INVITATION_CODE_SERVER);
		String password = getValueFromRegistration(PASSWORD_SERVER);
		Boolean success = true;
		
		String result = validateUserRegistration(fname, lname, email, username, invitationCode, password);
		LOGGER.info (result);
		if (result.contains("Error")) {
			success = false;
			setRegistrationResponse(result, success, response);
			return;
		}
		
		success = insertUserIntoDB (fname, lname, email, username, invitationCode, password);
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

