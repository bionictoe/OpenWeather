package Weather;

import getImage.images;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import serverRetrieveTest.retrieveFromDB;
import database.DbConnect;

import java.util.Scanner;
import java.util.Date;

/**
 *
 * Started with sample code found here:
 * http://java-buddy.blogspot.de/2013/07/parse
 * -json-with-java-se-and-java-json.html
 * 
 * From which I added functionality
 */
public class WeatherTest
{
	// API URL
	static final String URL = "http://api.openweathermap.org/data/2.5/weather?q=";
	// API Key
	static final String api = "&APPID=ea5329773b3f8dfd39ae89496bb7a7d3";
	// city name to be taken from user and concatenated together
	static String city;
	// specify temp in celsius
	static String metric = "&units=metric";
	// specify temp in fahrenheit
	static String imperial = "&units=imperial";
	// get current time
	static long timeNow = System.currentTimeMillis();
	// string to hold JSON data from API or saved data from server
	static String result = "";

	public static void main(String[] args)
	{
		// ask user if they want full report or anything specific
		Scanner s1 = new Scanner(System.in);
		System.out.println("Enter your city name: ");
		city = s1.nextLine();

		//ask user what type of weather report they would like
		System.out.println
		(
				"\nPress 1 for Full Report"
				+ "\nPress 2 for Temperature"
				+ "\nPress 3 for Wind Info (Speed & Direction)"
				+ "\nPress 4 for Cloud Percentage"
		);
		Scanner s2 = new Scanner(System.in);

		//take weather report type choice from user
		int choice = s2.nextInt();


		// call method to see if city data was asked for recently
		// DbConnect.testForRecentCall(city) will return true if city was called less than ten minutes ago
		// pass choice to get relevant report type from server
		if (DbConnect.testForRecentCall(city, choice))
		{
			/* get data from server, if city called less than 10 minutes ago */
			System.out.println(retrieveFromDB.getData(city, choice));

		} else
		{
			// otherwise call from the API do this
			// pass user report type choice
			goToAPI(choice);
		}

	}

	// connect to API
	private static void goToAPI(int choice)
	{
		try
		{
			//mainly boiler plate server connection code
			//personal additions are commented
			
			// construct URL to connect to API
			// base URL + city name to be typed by user + API key + units
			// standard(metric or imperial)
			URL url_weather = new URL(URL + city + api + metric);

			HttpURLConnection httpURLConnection = (HttpURLConnection) url_weather.openConnection();

			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
			{

				InputStreamReader inputStreamReader = new InputStreamReader(
						httpURLConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader, 8192);
				String line = null;
				while ((line = bufferedReader.readLine()) != null)
				{
					result += line;
				}

				bufferedReader.close();

				String weatherResult = ParseResult(result, city, 1, choice);

				System.out.println(weatherResult);

			} else
			{
				System.out
						.println("Error in httpURLConnection.getResponseCode()!!!");
			}

		} catch (MalformedURLException ex)
		{
			Logger.getLogger(WeatherTest.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (IOException ex)
		{
			Logger.getLogger(WeatherTest.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (JSONException ex)
		{
			Logger.getLogger(WeatherTest.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	// method to parse JSON data from API
	// passed parameters: JSON file for parsing, Location user entered, and user
	// choice (whether full report wanted, or selected info)
	static private String ParseResult(String json, String city, int type,
			int choice) throws JSONException
	{
		// initialise result string before fetching JSON data
		String parsedResult = "";
		// create new timer object, and set time of data retrieved

		// new JSON object
		JSONObject jsonObject = new JSONObject(json);

		// get coord parent object
		JSONObject JSONObject_coord = jsonObject.getJSONObject("coord");
		Double result_lon = JSONObject_coord.getDouble("lon");
		Double result_lat = JSONObject_coord.getDouble("lat");

		// get sys parent object
		JSONObject JSONObject_sys = jsonObject.getJSONObject("sys");
		String result_country = JSONObject_sys.getString("country");
		int result_sunrise = JSONObject_sys.getInt("sunrise");
		int result_sunset = JSONObject_sys.getInt("sunset");

		String result_weather = "";
		String icon = "";
		// get weather Array
		JSONArray JSONArray_weather = jsonObject.getJSONArray("weather");

		if (JSONArray_weather.length() > 0)
		{
			// get weather parent object
			JSONObject JSONObject_weather = JSONArray_weather.getJSONObject(0);
			// main weather result for location
			String result_main = JSONObject_weather.getString("main");
			// weather description for location
			String result_description = JSONObject_weather
					.getString("description");

			icon = JSONObject_weather.getString("icon");

			// construct weather result output
			result_weather = "Mainly " + result_main + "\nDescription: "
					+ result_description;
		} else
		{
			// if no JSON retrieved
			result_weather = "weather empty!";
		}

		// get main details
		JSONObject JSONObject_main = jsonObject.getJSONObject("main");
		// get temp data
		Double result_temp = JSONObject_main.getDouble("temp");
		// get pressure data
		Double result_pressure = JSONObject_main.getDouble("pressure");
		// get humidity data
		Double result_humidity = JSONObject_main.getDouble("humidity");
		// get temperature min
		Double result_temp_min = JSONObject_main.getDouble("temp_min");
		// get temperature max
		Double result_temp_max = JSONObject_main.getDouble("temp_max");

		// Get wind parent object
		JSONObject JSONObject_wind = jsonObject.getJSONObject("wind");
		// get wind speed
		Double result_speed = JSONObject_wind.getDouble("speed");
		// get wind direction
		Double result_deg = JSONObject_wind.getDouble("deg");
		// prepare wind data
		String result_wind = "\nWind:\nspeed: " + result_speed + "m/sec"
				+ "\tDirection: " + headingToString(result_deg);

		// get clouds parent object
		JSONObject JSONObject_clouds = jsonObject.getJSONObject("clouds");
		// get clouds percentage
		int result_all = JSONObject_clouds.getInt("all");

		// get time information was retrieved at
		int result_dt = jsonObject.getInt("dt");

		// get city ID
		int result_id = jsonObject.getInt("id");

		// Get city name
		String result_name = jsonObject.getString("name");

		// save parsed and formatted data into a string
		String jsonData = "Co-Ordinates\t\nLatitude: " + result_lat
				+ "\tLongtitude: " + result_lon + "\n\n" + "Country: "
				+ result_country + "\n\n"

				+ "Sunrise & Sunset:\n" + "sunrise: "
				+ convertToTime(result_sunrise) + "   \tsunset: "
				+ convertToTime(result_sunset) + "\n\n"

				+ "Weather Conditions:\n\n" + result_weather + "\n"
				+ "Cloudiness: " + result_all + "%"

				+ "\n" + result_wind + "\n" + "\n\n" + "Overview:\nTemp: "
				+ result_temp + "\tHumidity: " + result_humidity
				+ "\tPressure: " + result_pressure + "\n\nTemperature\n"
				+ "temp_min: " + Math.floor(result_temp_min) + "celsius "
				+ "\ttemp_max: " + Math.floor(result_temp_max) + "celsius"

				+ "\n\n\nLocation: " + result_name + "\nCity id: " + result_id
				+ "\nRetrieved: " + convertToDate(result_dt);

		String temp = "Overview:\nTemp: " + result_temp + "\tHumidity: "
				+ result_humidity + "\tPressure: " + result_pressure;
		String wind = "\n" + result_wind + "\n";
		String cloud = "\nCloudiness: " + result_all + "%";

		//String to hold data to return to method caller
		String dataToReturn = "";

		// System.out.println
		// (
		// "\nPress 1 for Full Report"
		// +"\nPress 2 for Temperature"
		// +"\nPress 3 for Wind Info (Speed & Direction)"
		// +"\nPress 4 for Cloud Percentage"
		// );
		// Scanner s2 = new Scanner(System.in);
		//
		// int choice = s2.nextInt();

		// full report
		if (choice == 1)
		{
			System.out.println("getting full report!");
			// send data to server to be saved 'cached' for quicker retrieval in
			// future
			DbConnect dbc = new DbConnect(city, jsonData, choice);

			// output information in JOptionPane
			// JOptionPane.showMessageDialog(null, jsonData,
			// result_name+" weather report", JOptionPane.INFORMATION_MESSAGE);

			// get image icon, from specified location, from google images, and
			// d
			images img = new images(jsonData, city);

			dataToReturn = jsonData;
		}
		if (choice == 2)
		{
			System.out.println("getting temp report!");
			// send data to server to be saved 'cached' for quicker retrieval in
			// future
			DbConnect dbc = new DbConnect(city, temp, choice);

			// output information in JOptionPane
			// JOptionPane.showMessageDialog(null, jsonData,
			// result_name+" weather report", JOptionPane.INFORMATION_MESSAGE);

			// get image icon, from specified location, from google images, and
			// d
			images img = new images(temp, city);

			dataToReturn = temp;
		}
		if (choice == 3)
		{
			System.out.println("getting wind report!");
			// send data to server to be saved 'cached' for quicker retrieval in
			// future
			DbConnect dbc = new DbConnect(city, wind, choice);

			// output information in JOptionPane
			// JOptionPane.showMessageDialog(null, jsonData,
			// result_name+" weather report", JOptionPane.INFORMATION_MESSAGE);

			// get image icon, from specified location, from google images, and
			// d
			images img = new images(wind, city);

			dataToReturn = wind;
		}
		if (choice == 4)
		{
			System.out.println("getting cloud report!");
			// send data to server to be saved 'cached' for quicker retrieval in
			// future
			DbConnect dbc = new DbConnect(city, cloud, choice);

			// output information in JOptionPane
			// JOptionPane.showMessageDialog(null, jsonData,
			// result_name+" weather report", JOptionPane.INFORMATION_MESSAGE);

			// get image icon, from specified location, from google images, and
			// d
			images img = new images(cloud, city);

			dataToReturn = cloud;
		}
		// s2.nextLine();

		// GetIcon img = new GetIcon(jsonData, result_name, icon);

		// ImageIcon icon = new
		// ImageIcon("/Users/Darragh/Documents/workspace/OpenWeather/src/galway.jpg");

		// JOptionPane.showMessageDialog(null, jsonData,
		// result_name+" weather report", JOptionPane.OK_OPTION, icon);
		// return formatted string for outputting in console (debugging
		// purposes)

		//return selected data
		return dataToReturn;

	}

	// change wind direction in degrees into cardinal wind direction
	public static String headingToString(double x)
	{
		String directions[] = { "North", "North East", "East", "SouthEast",
				"South", "SouthWest", "West", "NorthWest", "North" };
		return directions[(int) Math.round((((double) x % 360) / 45))];
	}

	// get date/time from UNIX timestamp
	private static Date convertToDate(int unixTimeStamp)
	{
		return new Time(Long.valueOf(unixTimeStamp) * 1000);
	}

	// get real time from timestamp
	public static Time convertToTime(long unixTimeStamp)
	{
		return new Time(Long.valueOf(unixTimeStamp) * 1000);
	}

}