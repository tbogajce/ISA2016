package netgloo.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import netgloo.dao.UserDao;
import netgloo.models.User;
import netgloo.models.UserProba;

/**
 * A class to test interactions with the MySQL database using the UserDao class.
 *
 * @author netgloo
 */

@RestController
@RequestMapping("/users")
public class UserController {
	
	// ------------------------
	// PUBLIC METHODS
	// ------------------------
	HttpServletRequest request;
	
	HttpServletResponse response;
	
	ServletContext ctx;
	
	/**
	 * /create --> Create a new user and save it in the database.
	 * 
	 * @return A string describing if the user is succesfully created or not.
	 */
	@RequestMapping(value="/createUser",  method = RequestMethod.POST,
		    headers = {"content-type=application/json"})
	public String createUser(@RequestBody UserProba user1) {
		
		try {
		User user = null;
		String user_reg_date = new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
		user = new User(user1.getEmail(), user1.getPassword(), user1.getName(), user1.getSurname(), user1.getBirthDate(), user_reg_date, "Guest");
		userDao.save(user);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "EXCEPTION";
		}
	    return "OK";
	}
	
	
	
	@RequestMapping(value="/loginUser",  method = RequestMethod.POST,
		    headers = {"content-type=application/json"})
	public String loginUser(@RequestBody UserProba user1) {
		
		try {
			User user = userDao.findByEmail(user1.getEmail());
			String email = String.valueOf(user.getEmail());
			String pass = String.valueOf(user.getUser_password());
			if(email.equals(user1.getEmail()) && pass.equals(user1.getPassword()) ) {
				request.getSession().setAttribute("user", user );
				return "SUCCESS";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return "EXCEPTION";
		}
	    return "OK";
	}
	
	/**
	 * /delete --> Delete the user having the passed id.
	 * 
	 * @param id
	 *            The id of the user to delete
	 * @return A string describing if the user is succesfully deleted or not.
	 */
	@RequestMapping("/deleteUser")
	@ResponseBody
	public String deleteUser(Integer user_id) {
		try {
			User user = new User(user_id);
			userDao.delete(user);
		} catch (Exception ex) {
			return "Error deleting the user: " + ex.toString();
		}
		return "User succesfully deleted!";
	}

	/**
	 * /get-by-email --> Return the id for the user having the passed email.
	 * 
	 * @param email
	 *            The email to search in the database.
	 * @return The user id or a message error if the user is not found.
	 */
	@RequestMapping("/get-by-email")
	@ResponseBody
	public String getByEmail(String user_email) {
		String user_id;
		try {
			User user = userDao.findByEmail(user_email);
			user_id = String.valueOf(user.getUser_id());
		} catch (Exception ex) {
			return "User not found";
		}
		return "The user id is: " + user_id;
	}

	/**
	 * /update --> Update the email and the name for the user in the database
	 * having the passed id.
	 * 
	 * @param id
	 *            The id for the user to update.
	 * @param email
	 *            The new email.
	 * @param name
	 *            The new name.
	 * @return A string describing if the user is succesfully updated or not.
	 */
	@RequestMapping("/updateUser")
	@ResponseBody
	public String updateUser(Integer user_id, String user_email, String user_password, String user_name, 
			String user_surname, String user_birth_date, String user_registration_date, String user_role) {
		try {
			User user = userDao.findOne(user_id);
			user.setEmail(user_email);
			user.setUser_password(user_password);
			user.setUser_name(user_name);
			user.setUser_surname(user_surname);
			user.setUser_birth_date(user_birth_date);
			user.setUser_registration_date(user_registration_date);
			user.setUser_role(user_role);
			userDao.save(user);
		} catch (Exception ex) {
			return "Error updating the user: " + ex.toString();
		}
		return "User succesfully updated!";
	}

	// ------------------------
	// PRIVATE FIELDS
	// ------------------------

	@Autowired
	private UserDao userDao;

} // class UserController
