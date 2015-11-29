package database;

import java.sql.*;

import serverRetrieveTest.retrieveFromDB;

public class DbConnect
{
	static Connection conn = null;
	//get current time
	static long timeNow = System.currentTimeMillis();
	static //to hold results from database
	int rs;
	
	static PreparedStatement pst = null;
	static ResultSet rset = null;

	
	//constructor starts server connection with parameters, which are, 'location' from parsed JSON, and total parsed JSON 'result'
	public DbConnect(String location, String result, int type)
	{
		openConnection(location, result, type);
	}
	//parameters are 'location' from parsed JSON, and total parsed JSON 'result'
	private static void openConnection(String location, String result, int type)
	{
		try
		{
			String userName = "root";
			String password = "root";
			String url = "jdbc:mysql://localhost:8889/test";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, userName, password);
			System.out.println("Database connection successful.Opened Conection 1");
			
			Statement stmt=conn.createStatement();
			//create entry with location from JSON data class and JSON data 'result'
			//stmt.executeUpdate("INSERT INTO `JSON`(`city`, `data`, `time`) VALUES ('"+location+"','"+result+"','"+timeNow+"')");
			
			//create entry in database, if entry exists, entry will be overwritten
			//example SQL command
			//REPLACE INTO JSON SET city='', data='', time='', type=''
			//stmt.executeUpdate("REPLACE INTO JSON SET city='"+location+"', data='"+result+"', time='"+timeNow+"', type='"+type+"'");
			//close connection to server
			
			//if location and type combo hasnt been asked for before
			if(retrieveFromDB.testEntry(location, type) == null)
			{
				//insert data into DB
				stmt.executeUpdate("INSERT INTO `JSON`(`city`, `data`, `time`) VALUES ('"+location+type+"', '"+timeNow+"');");
			}
			//if combo has been asked before
			else
			{
				//update the entry
				stmt.executeUpdate("REPLACE INTO JSON SET city='"+location+type+"', data='"+result+"', time='"+timeNow+"';");
			}
			
			conn.close();
		} catch (Exception e)
		{
			System.out.println("Error! Cannot connect to database. - "+e);
			e.printStackTrace();
		} 
	}
	//overloaded method
	//this method if only given location will pull that locations data from the server
	public static int openConnection(String location, int type)
	{
		try
		{
			String userName = "root";
			String password = "root";
			String url = "jdbc:mysql://localhost:8889/test";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, userName, password);
			System.out.println("Database connection successful.");
			
			Statement stmt=conn.createStatement();
			
			//retrieve data that matches fields location and type
			rs = stmt.executeUpdate("SELECT `data` FROM `JSON` WHERE city='"+location+type+"'");
						
			//close connection to server
			conn.close();
		} catch (Exception e)
		{
			System.out.println("Error! Cannot connect to database. - "+e);
			e.printStackTrace();
		} 
		return rs;
	}
	//test if city and request type was called recently from API, 
	public static Boolean testForRecentCall(String location, int type)
	{
		//get time this city and report type was called
		long timeOfLastCall = retrieveFromDB.getTime(location, type);
		//tell user when this combo was last called
		System.out.println("TIME OF LAST CALL: (UNIX Timestamp)"+timeOfLastCall);
		
		//if call was less than 10 minutes ago
		if((timeNow - timeOfLastCall) < 60000)
		{
			//get city data again
			System.out.println("\nLocation was called less than 10 minutes ago! \nPulling data from server and not API\n");
			return true;
		}
		//loner than 10 minutes ago, or no entry that matches
		System.out.println("\nLocation was \nA. Never called, \nor, \nB. Called more than 10 minutes ago. Pulling data from API!\n");
		return false;
	}
}
