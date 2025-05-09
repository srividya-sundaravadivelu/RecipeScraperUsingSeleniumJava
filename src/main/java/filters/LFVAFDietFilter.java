package filters;

/**
 * The LFVAFDietFilter class represents a filter for Low Fat Vegan - Allergy
 * Free(LFVAF) diets. It is used to check if a recipe's ingredients and tags
 * meet the criteria for an LFV-Allergy Free diet. This filter ensures that only
 * recipes adhering to low-fat vegan - allergy free requirements are selected.
 */
public class LFVAFDietFilter extends DietFilter {
	public LFVAFDietFilter() {
		super("src/main/resources/config/lfv_af_config.json");
	}

	@Override
	public String getDietName() {
		return "LFV_AF";
	}

	@Override
	public String getDietTableName() {
		return "lfv_af_recipes";
	}
}
