package scraper;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import java.time.Duration;

/**
 * The WebDriverSetup class is responsible for initializing and configuring a
 * WebDriver instance for web scraping. It provides a method to create and
 * return a properly configured WebDriver instance.
 */
public class WebDriverSetup {
	public static WebDriver createDriver() {
		ChromeOptions options = new ChromeOptions();

		options.addArguments("--headless", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage",
				"--blink-settings=imagesEnabled=false", "--disable-javascript", "--disable-popup-blocking",
				"--disable-notifications", "--disable-extensions", "--disable-infobars", "--remote-allow-origins=*");

		options.setPageLoadStrategy(PageLoadStrategy.NORMAL);

		WebDriver driver = new ChromeDriver(options);
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		return driver;
	}
}
