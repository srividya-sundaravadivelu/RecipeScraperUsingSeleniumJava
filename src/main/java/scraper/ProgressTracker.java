package scraper;

import java.io.*;

/**
 * The ProgressTracker class is responsible for tracking the progress of web
 * scraping across multiple pages. It stores and retrieves the last scraped page
 * number to ensure the scraping process can be resumed from the point it was
 * previously stopped. This is particularly useful for long-running scraping
 * tasks that span multiple pages.
 */
public class ProgressTracker {
	private static final String PROGRESS_FILE = "progress.txt";

	public int readProgress() {
		try (BufferedReader br = new BufferedReader(new FileReader(PROGRESS_FILE))) {
			return Integer.parseInt(br.readLine().trim());
		} catch (Exception e) {
			return 0;
		}
	}

	public void saveProgress(int page) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(PROGRESS_FILE))) {
			bw.write(String.valueOf(page));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
