package com.gk.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.gk.object.BoardGameObject;

public class DAO {

	private static Connection connect = null;
	public static Statement statement = null;
	private static ResultSet resultSet = null;
	public static PreparedStatement preparedStatement = null;

	public static void main(String[] args) {
		try {
			ArrayList<BoardGameObject> games = createBoardGameListByName("Cathedral");
			System.out.println(games.get(0).getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Connection connectToDatabase() throws Exception {

		try {
			// Setup the connection with the DB
			Class.forName("com.mysql.jdbc.Driver");

			connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/games?", "root", "admin"); // change
																											// username
																											// passowrd
			return connect;
		} catch (Exception e) {
			throw e;
		}
	}

	public static Statement readDatabase() throws Exception {
		try {
			connect = connectToDatabase();
			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();

			return statement;
		} catch (Exception e) {
			throw e;
		}
	}

	public static boolean addGameToDatabase(String name, String mechanics) throws Exception {
		connect = connectToDatabase();
		String preparedQuery = "INSERT INTO listofgames (Name, Mechanics)" + "Values(?, ?)";

		boolean isComplete = false;
		try {

			preparedStatement = connect.prepareStatement(preparedQuery);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, mechanics);
			preparedStatement.executeUpdate();
			isComplete = true;
			return isComplete;

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return isComplete;
	}

	public static void addExpansionToDatabase(int ID, String nameOfOriginalGame, String nameOfExpansion,
			String mechanics) throws Exception {
		connect = connectToDatabase();
		String preparedQuery = "INSERT INTO expansions (ID, NameOfOriginalGame, NameOfExpansion, Mechanics)"
				+ "Values(?, ?, ?, ?)";

		try {

			preparedStatement = connect.prepareStatement(preparedQuery);
			preparedStatement.setInt(1, ID);
			preparedStatement.setString(2, nameOfOriginalGame);
			preparedStatement.setString(3, nameOfExpansion);
			preparedStatement.setString(4, mechanics);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public static int getIDFromDatabase(String nameOfOriginalGame) throws Exception {
		statement = readDatabase();
		resultSet = statement.executeQuery("Select * from listofgames where name = \"" + nameOfOriginalGame + "\";");

		while (resultSet.next()) {
			int idNumber = resultSet.getInt("ID");
			return idNumber;
		}
		return 0;
	}

	public static ArrayList<BoardGameObject> createBoardGameListByName(String name) throws Exception {
		int idNumber;
		String nameOfGame;
		String mechanics;
		ArrayList<String> mechanicsList = null;
		ArrayList<BoardGameObject> games = new ArrayList<BoardGameObject>();
		statement = readDatabase();
		resultSet = statement.executeQuery("Select * from listofgames where name = \"" + name + "\";");

		while (resultSet.next()) {
			idNumber = resultSet.getInt("ID");
			nameOfGame = resultSet.getString("name");
			mechanics = resultSet.getString("mechanics");
			mechanicsList = separateMechanics(mechanics);
			BoardGameObject boardGame = new BoardGameObject(idNumber, nameOfGame, mechanicsList);
			games.add(boardGame);
		}

		return games;
	}

	public static ArrayList<String> separateMechanics(String mechanics) {
		String[] temp = mechanics.split(",");
		ArrayList<String> mechanicsList = new ArrayList<String>();
		for (int i = 0; i < temp.length; i++) {
			mechanicsList.add(temp[i]);
		}
		return mechanicsList;
	}
}
