package filters;

/**
 * The LFDietFilter class represents a filter for Low Fat (LF) diets. It is used
 * to check if a recipe's ingredients and tags meet the criteria for an LF diet.
 * This filter ensures that only recipes adhering to low-fat requirements are
 * selected.
 */
public class LFDietFilter extends DietFilter {
	public LFDietFilter() {
		super("src/main/resources/config/lf_config.json");
	}

	@Override
	public String getDietName() {
		return "LF";
	}

	@Override
	public String getDietTableName() {
		return "lf_recipes";
	}
}
