package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DBConnect {
	
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private PreparedStatement preparedStatement = null;
	
	private String url = "jdbc:mysql://localhost/apps";
	private String username = "";
	private String password = "root";
	private String data;
	
	public void getDataBaseConnection() {
		
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			System.out.println("Unable to load driver");
			e.printStackTrace();
		}
		
		try {
			connection = DriverManager.getConnection(url, password, username);
		} catch (SQLException e) {
			System.out.println("Unable to connect to database");
			e.printStackTrace();
		}
		
		if(connection != null) {
			try {
				statement = connection.createStatement();
			} catch (SQLException e) {
				System.out.println("Unable to create Statement");
				e.printStackTrace();
			}
		}
		
	}
	
	public String getDataFromDB() {
		
		if(statement != null) {
			try {
				resultSet = statement.executeQuery("SELECT * FROM bestresult");
			} catch (SQLException e) {
				System.out.println("Unable to create resultSet");
				e.printStackTrace();
			}
		}
		
		if(resultSet != null) {
			try {
				resultSet.next();
				data = resultSet.getString(1) + " with score - " + resultSet.getString(2) + ", " + resultSet.getString(3);
			} catch (SQLException e) {
				System.out.println("Unable to iterate over resultset");
				e.printStackTrace();
			}
		}
		return data;
	}
	
	public void addResultToDB(int score, String nickname) {
		
		if(statement != null) {
			try {
				resultSet = statement.executeQuery("SELECT * FROM bestresult");
			} catch (SQLException e) {
				System.out.println("Unable to create resultSet");
				e.printStackTrace();
			}
		}
		
		if(resultSet != null) {
			int scoreDB = 0;
			String sql = null;
			try {
				resultSet.next();
				scoreDB = resultSet.getInt("Score");
				if(score >= scoreDB) {
					DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
					Date date = new Date();
					sql = "UPDATE bestresult SET Nickname = '" + nickname + "', Score = '" + score + "', Date = '" + dateFormat.format(date) + "'";
					preparedStatement = connection.prepareStatement(sql);
					preparedStatement.executeUpdate();
				}
			} catch (SQLException e) {
				System.out.println("Unable to iterate over resultset");
				e.printStackTrace();
			}
		}
		
	}
	
}
