package filters;

/**
 * The LFVDietFilter class represents a filter for Low Fat Vegan(LFV) diets. It
 * is used to check if a recipe's ingredients and tags meet the criteria for an
 * LFV diet. This filter ensures that only recipes adhering to low-fat vegan
 * requirements are selected.
 */
public class LFVDietFilter extends DietFilter {
	public LFVDietFilter() {
		super("src/main/resources/config/lfv_config.json");
	}

	@Override
	public String getDietName() {
		return "LFV";
	}

	@Override
	public String getDietTableName() {
		return "lfv_recipes";
	}
}
