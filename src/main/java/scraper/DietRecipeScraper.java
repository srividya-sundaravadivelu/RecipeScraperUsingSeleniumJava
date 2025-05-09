package scraper;

import filters.DietFilter;
import model.Recipe;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.DatabaseHelper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The DietRecipeScraper class is responsible for scraping individual recipe
 * details from the Tarla Dalal website based on specific diet filters (e.g.,
 * LFV, LCHF). It extracts relevant recipe information such as ingredients,
 * preparation method, and nutrition values, and stores them in the appropriate
 * database tables.
 */
public class DietRecipeScraper {

	private final WebDriver driver;
	private final DatabaseHelper dbHelper;
	private final List<DietFilter> filters;
	private final List<String> failedUrls = Collections.synchronizedList(new ArrayList<>());
	private static final Logger logger = LogManager.getLogger(DietRecipeScraper.class);

	public DietRecipeScraper(WebDriver driver, DatabaseHelper dbHelper, List<DietFilter> filters) {
		this.driver = driver;
		this.dbHelper = dbHelper;
		this.filters = filters;
	}

	public void scrapeRecipe(String url) {
		try {
			driver.get(url);
			waitForRecipePageToLoad();

			// Extract only the fields needed for filter check - this improves performance
			String ingredientsText = getTextFromElement(By.id("ingredients"));
			List<String> tags = extractTags();

			// Identify matching filters
			List<DietFilter> matchingFilters = filters.stream()
					.filter(f -> f.includesAddedIngredients(ingredientsText)
							&& f.excludesEliminatedIngredients(ingredientsText) && f.avoidsRecipeTags(tags))
					.collect(Collectors.toList());

			// Proceed only if there is at least one match
			if (!matchingFilters.isEmpty()) {
				Recipe recipe = extractFullRecipeDetails(url, ingredientsText, tags);
				storeMatchingRecipe(recipe, matchingFilters);
			} else {
				logger.info("❌ Skipped (no matching filter): " + url);
			}

		} catch (Exception e) {
			handleError(url, e);
		}
	}

	private Recipe extractFullRecipeDetails(String url, String ingredientsText, List<String> tags) {
		String name = getTextFromElement("//h4[contains(@class,'rec-heading')]//span");
		List<String> breadcrumbs = extractBreadcrumbTrail();

		// Combine both tags and bread crumbs for category mapping
		List<String> combined = new ArrayList<>();
		combined.addAll(tags);
		combined.addAll(breadcrumbs);

		String[] categoryInfo = CategoryMapper.mapTagsToCategories(combined);

		String prepTime = getTextFromElement("//h6[contains(text(),'Preparation Time')]/following-sibling::p/strong");
		String cookTime = getTextFromElement("//h6[contains(text(),'Cooking Time')]/following-sibling::p/strong");
		String method = getTextFromElement("//div[@id='methods']");
		String description = getTextFromElement("(//div[@id='aboutrecipe']//p)[1]");
		String servings = getTextFromElement("//h6[contains(text(),'Makes')]/following-sibling::p//strong");
		String nutrientValues = extractNutrientInfo(driver);

		// Create Recipe object and populate fields
		Recipe recipe = new Recipe();
		recipe.setName(name);
		recipe.setIngredientsText(ingredientsText);
		recipe.setTags(tags);
		recipe.setPrepTime(prepTime);
		recipe.setCookTime(cookTime);
		recipe.setMethod(method);
		recipe.setDescription(description);
		recipe.setServings(servings);
		recipe.setNutrients(nutrientValues);
		recipe.setUrl(url);
		recipe.setFoodCategory(categoryInfo[0]);
		recipe.setCuisineCategory(categoryInfo[1]);
		recipe.setRecipeCategory(categoryInfo[2]);

		return recipe;
	}

	private void waitForRecipePageToLoad() {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5)); // Reduced wait time
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h4[contains(@class,'rec-heading')]")));
	}

	private String getTextFromElement(String xpath) {
		try {
			WebElement element = driver.findElement(By.xpath(xpath));
			return element != null ? element.getText().trim() : "";
		} catch (NoSuchElementException e) {
			return "";
		}
	}

	private String getTextFromElement(By by) {
		try {
			WebElement element = driver.findElement(by);
			return element != null ? element.getText().trim() : "";
		} catch (NoSuchElementException e) {
			return "";
		}
	}

	private List<String> extractTags() {
		List<WebElement> tagElements = driver.findElements(By.xpath("//ul[@class='tags-list']//li/a"));
		List<String> tags = new ArrayList<>();
		for (WebElement el : tagElements) {
			tags.add(el.getText().trim());
		}
		return tags;
	}

	private List<String> extractBreadcrumbTrail() {
		List<String> breadcrumbs = new ArrayList<>();
		try {
			WebElement breadcrumbContainer = driver.findElement(By.xpath("//p[contains(.,'You are here')]"));
			List<WebElement> links = breadcrumbContainer.findElements(By.tagName("a"));
			for (WebElement link : links) {
				breadcrumbs.add(link.getText().trim());
			}

			List<WebElement> spans = breadcrumbContainer.findElements(By.tagName("span"));
			for (WebElement span : spans) {
				List<WebElement> innerLinks = span.findElements(By.tagName("a"));
				if (!innerLinks.isEmpty()) {
					breadcrumbs.add(innerLinks.get(0).getText().trim());
				} else {
					breadcrumbs.add(span.getText().trim());
				}
			}
		} catch (NoSuchElementException e) {
			// Handle missing breadcrumb trail gracefully
		}

		return breadcrumbs;
	}

	private void storeMatchingRecipe(Recipe recipe, List<DietFilter> matchingFilters) throws SQLException {

		for (DietFilter filter : matchingFilters) {
			if (dbHelper.insertRecipe(filter.getDietTableName(), recipe)) {
				logger.info("✔️ Stored (" + filter.getDietName() + "): " + recipe.getName());
			} else {
				logger.warn("❌ Recipe already exists in table (" + filter.getDietTableName() + "): " + recipe.getUrl());
			}
		}

	}

	private void handleError(String url, Exception e) {
		logger.error("⚠ Error: " + url);
		failedUrls.add(url);

		try (BufferedWriter failedWriter = new BufferedWriter(new FileWriter("failed_urls.txt", true))) {
			failedWriter.write(url + " --> " + e.getClass().getSimpleName() + ": " + e.getMessage());
			failedWriter.newLine();
		} catch (IOException ioException) {
			logger.error("⚠️ Failed to write to failed_urls.txt: " + ioException.getMessage());
		}

		try {
			Thread.sleep(500); // small delay to avoid overloading the server
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

	private String extractNutrientInfo(WebDriver driver) {
		StringBuilder nutrientData = new StringBuilder();
		try {
			List<WebElement> rows = driver.findElements(By.xpath("//table[@id='rcpnutrients']//tr"));
			for (WebElement row : rows) {
				List<WebElement> cols = row.findElements(By.tagName("td"));
				if (cols.size() == 2) {
					nutrientData.append(cols.get(0).getText().trim()).append(": ").append(cols.get(1).getText().trim())
							.append(", ");
				}
			}
			if (nutrientData.length() > 0) {
				nutrientData.setLength(nutrientData.length() - 2); // remove trailing comma
			}
		} catch (Exception e) {
			logger.error("Nutrient extraction failed: " + e.getMessage());
		}
		return nutrientData.toString();
	}
}
