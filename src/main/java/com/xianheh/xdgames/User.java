package com.xianheh.xdgames;

import java.io.BufferedReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class User {

    private final static Logger LOGGER = Logger.getLogger(DatabaseHelper.class.getName()); 

    public final String FIRST_NAME_DB = "FirstName";
	public final String LAST_NAME_DB = "LastName";
    public final String EMAIL_DB = "EmailAddress";
    public final String USER_NAME_DB = "UserName";
    public final String INVITATION_CODE_DB = "InvitationCode";
    public final String PASSWORD_DB = "Password";
    public final String FIRST_NAME_SERVER = "firstname";
    public final String LAST_NAME_SERVER = "lastname";
    public final String EMAIL_SERVER = "email";
    public final String USER_NAME_SERVER = "username";
    public final String INVITATION_CODE_SERVER = "invitation_code";
    public final String PASSWORD_SERVER = "password";

       
    public void readAllUsers (DatabaseHelper db) {
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
    
    public JSONObject getJSON (HttpServletRequest request, String jsonName) {
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
		String JSONString = jsonObject.get(jsonName).toString();
		obj = JSONValue.parse(JSONString);
		return (JSONObject) obj;
    }
    
    public String getValueFromRegistration (JSONObject jsonObject, String key) {
		return jsonObject.get(key).toString();
    }
    
    public boolean insertUserIntoDB (DatabaseHelper db, String fname, String lname, String email,	
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
    
    public boolean checkInvitation (String invitationCode) {
        //TODO: add security check to determine whether the code is valid
        final String INVITE_CODE_PATTERN = "^[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}-[A-Za-z0-9]{4}$";
        return invitationCode.matches(INVITE_CODE_PATTERN);
    }

    public boolean checkFName (String firstName) {
        final String NAME_PATTERN = "[-A-Za-z]{1,}";
        return (firstName.matches(NAME_PATTERN));
    }
    
    public boolean checkLName (String lastname) {
        final String NAME_PATTERN = "[-A-Za-z]{1,}";
        return (lastname.matches(NAME_PATTERN));
    }

    public boolean checkEmail (String email) {
    	final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return email.matches(EMAIL_PATTERN);
    }

    public boolean checkPassword (String password) {
        return (password.length()!= 0);
    }
   
    
    public boolean existInDB (DatabaseHelper db, String serverName, String dbName) {
    	serverName = serverName.toLowerCase();
    	String stmt = "SELECT * FROM Users WHERE ";
     	stmt  += dbName + "= '" + serverName + "';";
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
}
