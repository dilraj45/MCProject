package androidServer;

import java.sql.Connection;
import java.util.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class Database {
	public static double threshold_distance = 1;
	public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == "K") {
			dist = dist * 1.609344;
		} else if (unit == "N") {
			dist = dist * 0.8684;
		}

		return (dist);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}
	
	static List<String> getMessages(String lat, String lon, String dist, String times, String sos) {
		double latitude = Double.parseDouble(lat);
		double longitude = Double.parseDouble(lon);
		long time = Long.parseLong(times, 10);
		time *= 60000L;
		Date date = new Date();
		long t0 = date.getTime(); 
		Connection connection = null;
		List<String> ret = new ArrayList<String>();
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement(
					"select * from info where time > ? and sos=?");
			pstmt.setLong(1, t0 - time);
			pstmt.setString(2, sos);
			ResultSet rset = pstmt.executeQuery();
			threshold_distance = Double.parseDouble(dist);
			while(rset.next()) {
				if( distance(latitude, longitude, Double.parseDouble(rset.getString(2)), Double.parseDouble(rset.getString(3)), "K") <= threshold_distance) {
					ret.add(rset.getString(1));
					ret.add(rset.getString(2));
					ret.add(rset.getString(3));
					ret.add(rset.getString(5));
				}
			}
		} catch(SQLException sqle){
			System.out.println("SQL exception when posting message");
		} finally{
			closeConnection(connection);
		}
		return ret;
		
	}
	
	static void postMessage(String m, String lat, String lon, String sos) {
		Connection connection=null;
		
		try{
			connection=getConnection();
			PreparedStatement pstmt= connection.prepareStatement(
					"insert into info values(?,?,?,?,?,?)");
			pstmt.setString(1, m);
			pstmt.setString(2, lat);
			pstmt.setString(3, lon);
			Date date = new Date();
		    pstmt.setLong(4, date.getTime());
		    SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
		    sd.setTimeZone(TimeZone.getTimeZone("IST"));
		    pstmt.setString(5, sd.format(date));
		    pstmt.setString(6, sos);
			pstmt.executeUpdate();
			
		} catch(SQLException sqle){
			System.out.println("SQL exception when posting message");
		} finally{
			closeConnection(connection);
		}
		
		
	}
	
	static Connection getConnection() {
		String dbURL = "jdbc:postgresql://127.0.0.1/testdb";
	    String dbUser = "testuser";
	    String dbPass = "testpass";
	    Connection connection=null;
	    try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(dbURL, dbUser, dbPass);
	    } catch(ClassNotFoundException cnfe){
	    	cnfe.printStackTrace();
	    	System.out.println("JDBC Driver not found");
	    } catch(SQLException sqle){
	    	System.out.println("Error in getting connetcion from the database");
	    }
	    
	    return connection;
	}
	
	static void closeConnection(Connection connection) {
		try{
			connection.close();
		} catch(SQLException sqle) {
			System.out.println("Error in close database connetcion");
		}
	}	
}