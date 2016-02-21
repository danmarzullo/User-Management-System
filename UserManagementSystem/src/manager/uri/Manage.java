package manager.uri;

import java.io.FileNotFoundException;
import java.io.FileReader;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.h2.tools.RunScript;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import manager.database.User;

@Path("/")
public class Manage
{
	
	private final boolean MVCC = true;
	private final long lockTimeout = 20000;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response JSONResponse
	(
		@DefaultValue("getUsers")
		@QueryParam("request")
		String request
	)throws FileNotFoundException, SQLException, ClassNotFoundException 
	{
		String response = null;
		
		switch (request)
		{
			case "getUsers":
				response = getUsers();
				break;
			case "initDB":
				System.out.println("Initialize Database");
				if (initDB())
				{
					System.out.println("Database Initialized");
				}
				break;
			default:		
				System.out.println("Default");
		}
		
		
		// return HTTP response 200 in case of success
		Response httpResponse = Response.status(200).entity(response).build();
		return httpResponse;
		
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post
	(
		String data,
		@QueryParam("request")
		String request,
		@QueryParam("name")
		String name,
		@QueryParam("phone")
		String phone,
		@QueryParam("address")
		String address,
		@QueryParam("role")
		String role
	)throws FileNotFoundException, SQLException, ClassNotFoundException 
	{
		String response = null;
		
		switch (request)
		{
			
			case "addUser":
				response = addUser(data);
				break;
			case "removeUser":
				response = removeUser(data);
				break;
			case "updateUser":
				response = updateUser(data);
				break;
			default:		
				System.out.println("Default");
		}
		
		
		// return HTTP response 200 in case of success
		Response httpResponse = Response.status(200).entity(response).build();

		return httpResponse;
		
	}
	
	private String getUsers() throws ClassNotFoundException, SQLException
	{
		//Initialize list to hold users
		Gson gson = new GsonBuilder().serializeNulls().create();
		List<User> userList = new ArrayList<User>();
		Type aListType = new TypeToken<ArrayList<User>>(){}.getType();
				
		//Connect to DB
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:h2:~/UserManagement;IFEXISTS=TRUE;LOCK_TIMEOUT=" + lockTimeout + ";", "sa", "sa");
		Statement stat = conn.createStatement();
		ResultSet rs;
		//Request all users
		rs = stat.executeQuery("SELECT * FROM SYSTEM.USERS");
		while (rs.next())
		{
			User tempUser = new User(rs.getString("name"),
									 rs.getString("phone"),
									 rs.getString("address"),
									 rs.getString("role"));
			userList.add(tempUser);
		}
		//Close statement and connection
		stat.close();
		conn.close();
	
		//Convert userList to JSON
		String json = gson.toJson(userList,aListType);
		
		return json;
	}
	
	
	private String addUser(String data) throws SQLException, ClassNotFoundException
	{
		//Parse incoming JSON
		JsonParser parser = new JsonParser();
		Gson gson = new Gson();
		JsonObject obj = parser.parse(data).getAsJsonObject();
		String name = gson.fromJson(obj.get("name"),String.class);
		String phone = gson.fromJson(obj.get("phone"),String.class);
		String address = gson.fromJson(obj.get("address"),String.class);
		String role = gson.fromJson(obj.get("role"),String.class);
		
		//Connect to DB
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:h2:~/UserManagement;IFEXISTS=TRUE;LOCK_TIMEOUT=" + lockTimeout + ";", "sa", "sa");
		Statement stat = conn.createStatement();
		//Insert new user into DB
		stat.execute("INSERT INTO SYSTEM.USERS values('" + name + "','" + phone + "','" + address + "','" + role + "')");

		//Close statement and connection
		stat.close();
		conn.close();
		return "User added";
	}
	
	private String removeUser(String data) throws SQLException, ClassNotFoundException
	{
		//Parse incoming JSON
		JsonParser parser = new JsonParser();
		Gson gson = new Gson();
		JsonObject obj = parser.parse(data).getAsJsonObject();
		String name = gson.fromJson(obj.get("name"),String.class);
		String phone = gson.fromJson(obj.get("phone"),String.class);
		String address = gson.fromJson(obj.get("address"),String.class);
		String role = gson.fromJson(obj.get("role"),String.class);
		
		//Connect to DB
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:h2:~/UserManagement;IFEXISTS=TRUE;LOCK_TIMEOUT=" + lockTimeout + ";", "sa", "sa");
		Statement stat = conn.createStatement();
		//Delete user from DB
		stat.execute("DELETE FROM SYSTEM.USERS WHERE NAME='" + name + "' AND PHONE='" + phone + "' AND ADDRESS='" + address + "' AND ROLE='" + role + "'");
		
		//Close statement and connection
		stat.close();
		conn.close();
		return "User Removed";
	}
	
	
	private String updateUser(String data) throws SQLException, ClassNotFoundException
	{
		//Parse incoming JSON
		JsonParser parser = new JsonParser();
		Gson gson = new Gson();
		JsonObject obj = parser.parse(data).getAsJsonObject();
		String name = gson.fromJson(obj.get("name"),String.class);
		String phone = gson.fromJson(obj.get("phone"),String.class);
		String address = gson.fromJson(obj.get("address"),String.class);
		String role = gson.fromJson(obj.get("role"),String.class);
		
		//Connect to DB		
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:h2:~/UserManagement;IFEXISTS=TRUE;LOCK_TIMEOUT=" + lockTimeout + ";", "sa", "sa");
		Statement stat = conn.createStatement();
		//Update user in DB
		stat.execute("UPDATE SYSTEM.USERS SET PHONE='" + phone + "', ADDRESS='" + address + "', ROLE='" + role + "' WHERE NAME='" + name + "'");
		
		//Close statement and connection
		stat.close();
		conn.close();
		return "User Updated";
	}
	
	private Boolean initDB() throws SQLException, FileNotFoundException
	{
		Boolean response = false;
		
		String catHome = System.getProperty("catalina.home");
		String catBase = System.getProperty("catalina.base");
		System.out.println("MyHome: "+catHome);
		System.out.println("MyBase: "+catBase);
		
		try
		{
			//Instantiate Driver class for H2 libs
			Class.forName("org.h2.Driver");
			
			//Connect to previously existing database
			Connection conn = DriverManager.getConnection(
					"jdbc:h2:~/UserManagement;IFEXISTS=TRUE;LOCK_TIMEOUT=" + lockTimeout + ";", "sa", "sa");
			conn.close();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (SQLException e)
		{
			response = true;
			System.out.println("Database doesn't exist, creating...");
			//Database not found, so build it
			Connection conn = DriverManager.getConnection(
					"jdbc:h2:~/UserManagement;MVCC=" + MVCC + ";LOCK_TIMEOUT=" + lockTimeout + ";", "sa", "sa");
			

			//RunScript.execute(conn, new FileReader("./src/manager/database/sql/Table_Schema.sql"));
			RunScript.execute(conn, new FileReader(catBase+"/wtpwebapps/UserManagementSystem/WEB-INF/classes/manager/database/sql/Table_Schema.sql"));

			conn.close();
		}
		return response;
	}
}