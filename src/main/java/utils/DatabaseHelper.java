package utils;

import java.sql.*;
import java.util.List;

import filters.DietFilter;
import model.Recipe;

/**
 * The DatabaseHelper class provides utility methods for interacting with the
 * database. It handles establishing a database connection, executing SQL
 * queries, and managing tables. It also includes methods for checking and
 * creating necessary tables for diet recipes.
 */
public class DatabaseHelper {
	private static final String DB_URL = "jdbc:postgresql://localhost:5432/tarla_recipes";
	private static final String DB_USER = "postgres";
	private static final String DB_PASSWORD = "password123";
	private Connection connection;

	public DatabaseHelper() throws SQLException {
		this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	}

	public Connection getConnection() {
		return this.connection;
	}

	public void createDietTablesIfNotExist(List<DietFilter> filters) throws SQLException {
		for (DietFilter filter : filters) {
			String tableName = filter.getDietTableName();

			String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + "recipe_id SERIAL PRIMARY KEY,"
					+ "recipe_name TEXT," + "recipe_category TEXT," + "food_category TEXT," + "ingredients TEXT,"
					+ "preparation_time TEXT," + "cooking_time TEXT," + "tag TEXT," + "no_of_servings TEXT,"
					+ "cuisine_category TEXT," + "recipe_description TEXT," + "preparation_method TEXT,"
					+ "nutrient_values TEXT," + "recipe_url TEXT UNIQUE" + ");";

			try (Statement stmt = connection.createStatement()) {
				stmt.executeUpdate(sql);
				System.out.println("✅ Ensured table exists: " + tableName);
			}
		}
	}

	public boolean insertRecipe(String tableName, Recipe recipe) throws SQLException {

		if (recipeExists(tableName, recipe.getUrl()))
			return false;

		String query = String.format("INSERT INTO %s(recipe_name, recipe_category, food_category, ingredients, "
				+ "preparation_time, cooking_time, tag, no_of_servings, cuisine_category, recipe_description, "
				+ "preparation_method, nutrient_values, recipe_url) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", tableName);

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, recipe.getName());
			stmt.setString(2, recipe.getRecipeCategory());
			stmt.setString(3, recipe.getFoodCategory());
			stmt.setString(4, recipe.getIngredientsText());
			stmt.setString(5, recipe.getPrepTime());
			stmt.setString(6, recipe.getCookTime());
			stmt.setString(7, String.join(", ", recipe.getTags()));
			stmt.setString(8, recipe.getServings());
			stmt.setString(9, recipe.getCuisineCategory());
			stmt.setString(10, recipe.getDescription());
			stmt.setString(11, recipe.getMethod());
			stmt.setString(12, recipe.getNutrients());
			stmt.setString(13, recipe.getUrl());
			stmt.executeUpdate();
		}
		return true;
	}

	public void close() {
		try {
			if (connection != null && !connection.isClosed())
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean recipeExists(String tableName, String url) throws SQLException {
		String query = String.format("SELECT 1 FROM %s WHERE recipe_url = ?", tableName);
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, url);
			ResultSet rs = stmt.executeQuery();
			return rs.next();
		}
	}
}
