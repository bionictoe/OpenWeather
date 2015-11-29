package serverRetrieveTest;

import getImage.images;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class retrieveFromDB
{
	//get data from database for specified location
	public static String getData(String location, int type)
	{
		//boilerplate server connection code
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		String url = "jdbc:mysql://localhost:8889/test";
		String user = "root";
		String password = "root";
		String data ="";

		try
		{
			//open connection
			con = DriverManager.getConnection(url, user, password);
			//prepare SQL statement to retrieve data from server
			pst = con.prepareStatement("SELECT data FROM JSON WHERE city='"+location+type+"'");
			//execute statement
			rs = pst.executeQuery();

			while (rs.next())
			{
				//get data from server response
				data = rs.getString(1);
				//pass data from server and location from method call to images class to get relevant location image
				images img = new images(data, location);
			}
			

		} catch (SQLException ex)
		{
			Logger lgr = Logger.getLogger(retrieveFromDB.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally
		{

			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (pst != null)
				{
					pst.close();
				}
				if (con != null)
				{
					con.close();
				}

			} catch (SQLException ex)
			{
				Logger lgr = Logger.getLogger(retrieveFromDB.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		
		return data;
	}
	//get time of last call for specified location
	public static long getTime(String location, int type)
	{
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		String url = "jdbc:mysql://localhost:8889/test";
		String user = "root";
		String password = "root";
		long time = 0;

		try
		{

			con = DriverManager.getConnection(url, user, password);
			//get the time on the server for matching location and type entries
			//SQL = SELECT time FROM JSON WHERE city='(location)' AND type='(type)'
			pst = con.prepareStatement("SELECT time FROM JSON WHERE city='"+location+type+"'");
			rs = pst.executeQuery();

			while (rs.next())
			{
				time = rs.getLong(1);
			}

		} catch (SQLException ex)
		{
			Logger lgr = Logger.getLogger(retrieveFromDB.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally
		{

			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (pst != null)
				{
					pst.close();
				}
				if (con != null)
				{
					con.close();
				}

			} catch (SQLException ex)
			{
				Logger lgr = Logger.getLogger(retrieveFromDB.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return time;
	}
	//retrieve data from server if location and type fields match
	public static String testEntry(String location, int type)
	{
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		String url = "jdbc:mysql://localhost:8889/test";
		String user = "root";
		String password = "root";
		String result = "";

		try
		{

			con = DriverManager.getConnection(url, user, password);
			//get data for location and type entry, if it exists
			//get data from server. location and report type are concatenated together to make new key entry for database
			pst = con.prepareStatement("SELECT data FROM JSON WHERE city='"+location+type+"'");
			rs = pst.executeQuery();

			while (rs.next())
			{
				result = rs.getString(1);
			}

		} catch (SQLException ex)
		{
			Logger lgr = Logger.getLogger(retrieveFromDB.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally
		{

			try
			{
				if (rs != null)
				{
					rs.close();
				}
				if (pst != null)
				{
					pst.close();
				}
				if (con != null)
				{
					con.close();
				}

			} catch (SQLException ex)
			{
				Logger lgr = Logger.getLogger(retrieveFromDB.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		return result;
	}
}
