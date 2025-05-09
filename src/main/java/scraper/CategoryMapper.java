package scraper;

import java.util.List;

/**
 * The CategoryMapper class is responsible for mapping tags associated with
 * recipes to their respective categories, such as food categories, cuisine
 * categories, and recipe categories. It helps classify recipes based on the
 * tags found in their metadata.
 */
public class CategoryMapper {

	public static final List<String> FOOD_CATEGORIES = List.of("Vegan", "Vegetarian", "Veg", "Jain", "Eggitarian",
			"Non-veg");
	public static final List<String> CUISINE_CATEGORIES = List.of("Indian", "South Indian", "Punjabi", "Gujarati",
			"Tamil nadu", "Bengali", "Maharashtrian", "Kashmiri", "Andhra", "Kerala", "Goan", "Karnataka", "Rajasthani",
			"Sindhi", "North Indian", "Awadhi", "Bihari", "Uttar pradesh", "Delhi", "Assamese", "Manipuri", "Tripuri",
			"Sikkimese", "Mizo", "Arunachali", "Chhattisgarhi", "Madhya pradesh", "Haryanvi", "uttarakhand");
	public static final List<String> RECIPE_CATEGORIES = List.of("Breakfast", "Lunch", "Dinner", "Snack");

	public static String[] mapTagsToCategories(List<String> tags) {
		String foodCategory = ""; // Default value
		String cuisineCategory = "";
		String recipeCategory = "";

		for (String tag : tags) {
			String normalized = tag.trim().toLowerCase();

			for (String food : FOOD_CATEGORIES) {
				if (normalized.contains(food.toLowerCase())) {
					foodCategory = food;
					break;
				}
			}
			for (String cuisine : CUISINE_CATEGORIES) {
				if (normalized.contains(cuisine.toLowerCase())) {
					cuisineCategory = cuisine;
					break;
				}
			}
			for (String recipe : RECIPE_CATEGORIES) {
				if (normalized.contains(recipe.toLowerCase())) {
					recipeCategory = recipe;
					break;
				}
			}
		}

		return new String[] { foodCategory, cuisineCategory, recipeCategory };
	}
}
