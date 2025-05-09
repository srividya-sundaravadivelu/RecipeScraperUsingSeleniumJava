package filters;

/**
 * The LCHFDietFilter class represents a filter for Low-Carb, High-Fat (LCHF)
 * diets. It is used to check if a recipe's ingredients and tags meet the
 * criteria for an LCHF diet. This filter ensures that only recipes adhering to
 * low-carb, high-fat requirements are selected.
 */
public class LCHFDietFilter extends DietFilter {
	public LCHFDietFilter() {
		super("src/main/resources/config/lchf_config.json");
	}

	@Override
	public String getDietName() {
		return "LCHF";
	}

	@Override
	public String getDietTableName() {
		return "lchf_recipes";
	}
}
