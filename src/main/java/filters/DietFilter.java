package filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * The DietFilter class is an abstract representation of a diet-based filter
 * used to categorize and process recipes based on dietary restrictions. It
 * includes methods to check if a recipe's ingredients and tags match the
 * criteria for specific diet types such as Low Fat Vegan, Low Carb High Fat,
 * etc.
 */
public abstract class DietFilter {

	protected List<String> addList;
	protected List<String> eliminateList;
	protected List<String> recipesToAvoid;

	public DietFilter(String configFilePath) {
		loadFromJson(configFilePath);
	}

	private void loadFromJson(String filePath) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(new File(filePath));

			addList = readList(root, "add");
			eliminateList = readList(root, "eliminate");
			recipesToAvoid = readList(root, "recipesToAvoid");

		} catch (Exception e) {
			throw new RuntimeException("Failed to load diet config: " + e.getMessage(), e);
		}
	}

	private List<String> readList(JsonNode root, String key) {
		JsonNode arrayNode = root.get(key);
		if (arrayNode == null || !arrayNode.isArray()) {
			return Collections.emptyList();
		}
		return StreamSupport.stream(arrayNode.spliterator(), false).map(JsonNode::asText).collect(Collectors.toList());
	}

	public List<String> getAddedIngredients() {
		return addList;
	}

	public List<String> getEliminatedIngredients() {
		return eliminateList;
	}

	public List<String> getRecipesToAvoid() {
		return recipesToAvoid;
	}

	public boolean includesAddedIngredients(String ingredientsText) {
		String ingredientsTextLower = ingredientsText.toLowerCase();
		return addList.stream().anyMatch(ingredient -> ingredientsTextLower.contains(ingredient.toLowerCase()));
	}

	public boolean excludesEliminatedIngredients(String ingredientsText) {
		String ingredientsTextLower = ingredientsText.toLowerCase();
		return eliminateList.stream()
				.noneMatch(eliminateIngredient -> ingredientsTextLower.contains(eliminateIngredient.toLowerCase()));
	}

	public boolean avoidsRecipeTags(List<String> tags) {
		List<String> tagsLower = tags.stream().map(String::toLowerCase).collect(Collectors.toList());
		return recipesToAvoid.stream().noneMatch(avoidTag -> tagsLower.contains(avoidTag.toLowerCase()));
	}

	public abstract String getDietName();

	public abstract String getDietTableName();
}
