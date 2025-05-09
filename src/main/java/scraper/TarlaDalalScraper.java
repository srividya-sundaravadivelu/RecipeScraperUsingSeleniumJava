package scraper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import filters.DietFilter;
import filters.LCHFDietFilter;
import filters.LFDietFilter;
import filters.LFVAFDietFilter;
import filters.LFVDietFilter;
import utils.DatabaseHelper;

import java.sql.*;
import java.util.*;

/**
 * The TarlaDalalScraper class is responsible for managing the entire web
 * scraping process of recipes from the Tarla Dalal website. It navigates
 * through multiple pages, collects recipe URLs, and processes each URL using
 * diet filters. The class also handles the creation of necessary database
 * tables, tracks progress, and retries failed URL scraping.
 */
public class TarlaDalalScraper {
	private ProgressTracker progressTracker;
	private WebDriver driver;
	private DatabaseHelper dbHelper;
	private final List<DietFilter> filters;
	private final List<String> failedUrls = Collections.synchronizedList(new ArrayList<>());
	private static final Logger logger = LogManager.getLogger(DietRecipeScraper.class);

	public TarlaDalalScraper(List<DietFilter> filters) throws SQLException {
		this.driver = WebDriverSetup.createDriver();
		this.dbHelper = new DatabaseHelper();
		this.progressTracker = new ProgressTracker();
		this.filters = filters;
	}

	public void scrapeAllPages() {
		try {

			dbHelper.createDietTablesIfNotExist(filters);

			driver.get("https://www.tarladalal.com/recipes");

			WebElement lastPageElement = driver.findElement(
					By.xpath("//li[@class='page-item']//a[text()='Next']/parent::li/preceding-sibling::li[1]/a"));
			int totalPages = Integer.parseInt(lastPageElement.getText().trim());
			int startPage = progressTracker.readProgress();

			DietRecipeScraper scraper = new DietRecipeScraper(driver, dbHelper, filters);

			// Scrape page by page
			for (int page = startPage + 1; page <= totalPages; page++) {
				logger.info("Processing Page " + page);
				driver.get("https://www.tarladalal.com/recipes/?page=" + page);

				List<WebElement> recipeLinks = driver
						.findElements(By.xpath("//div[contains(@class, 'recipe-list')]//h5//a"));
				List<String> recipeUrls = new ArrayList<>();
				for (WebElement el : recipeLinks) {
					recipeUrls.add(el.getAttribute("href"));
				}

				// Scrape each recipe in a page one by one
				for (String url : recipeUrls) {
					try {
						scraper.scrapeRecipe(url);
					} catch (Exception e) {
						logger.error("❌ Failed to scrape: " + url, e);
					}
				}

				// Once a page is done, save progress.
				progressTracker.saveProgress(page);
			}

			retryFailedUrls();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (driver != null) {
				driver.quit();
			}
			if (dbHelper != null) {
				dbHelper.close();
			}
		}
	}

	private void retryFailedUrls() {
		DietRecipeScraper scraper = new DietRecipeScraper(driver, dbHelper, filters);
		for (String url : failedUrls) {
			try {
				logger.info("🔄 Retrying: " + url);
				scraper.scrapeRecipe(url);
			} catch (Exception e) {
				logger.error("❌ Retry failed for: " + url);
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws SQLException {
		List<DietFilter> filters = Arrays.asList(new LFVDietFilter(), new LCHFDietFilter(), new LFDietFilter(),
				new LFVAFDietFilter());
		TarlaDalalScraper scraper = new TarlaDalalScraper(filters);
		scraper.scrapeAllPages();
	}
}
