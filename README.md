# Tarla Dalal Recipe Web Scraper

A Java-based web scraping project using Selenium to extract healthy recipes (LFV, LCHF, LF) from [TarlaDalal.com](https://www.tarladalal.com). The scraped data is stored in a PostgreSQL database for further analysis or usage.

---

## 📌 Features

- Scrapes recipe details like ingredients, prep time, cooking time, nutrient values, and more.
- Filters recipes based on diet categories (Low Fat Vegan - LFV, Low Carb High Fat - LCHF, Low Fat - LF).
- Avoids duplicate entries using unique recipe URLs.
- Automatically creates database tables if they don't exist.
- Tracks scraping progress for resuming after interruption.
- Logs scraping results using Log4j.
- Supports retrying failed URLs.

---

## 🛠️ Tech Stack

- **Java**
- **Selenium WebDriver**
- **PostgreSQL**
- **Log4j** for logging
- **Maven** for project management

---

## 🚀 How to Run

1. **Clone the repository**

```bash
git clone https://github.com/srividya-sundaravadivelu/RecipeScraperUsingSeleniumJava.git
cd RecipeScraperUsingSeleniumJava

```
2. **Configure database**

Update your database credentials in DatabaseHelper.java:

```bash
private static final String DB_URL = "jdbc:postgresql://localhost:5432/your_db";
private static final String DB_USER = "your_user";
private static final String DB_PASSWORD = "your_password";
```
3. **Build the project**
```bash
mvn clean install
```
4. **Run the scraper**   
Run main() in TarlaDalalScraper.java
From IDE:
Open the project in Eclipse or IntelliJ
Right-click on TarlaDalalScraper.java and choose Run -> Java Application
---
## ⚠️ Disclaimer
This project is for educational purposes only. Frequent scraping can place an unexpected load on websites and may violate their terms of use. Please respect the site's robots.txt and usage policy.





