package study.effective.ch02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class StockUtils {

	// item 04. Private Constructor
	private StockUtils() {
		// 빈 private형 생성자를 만들어 인스턴스 화를 방지
		throw new AssertionError();
	}

	// item 08. Avoid finalizer and cleaner
	// item 09. try-with-resource
	public static String getHtml(String url) {
		String result = "";

		try {
			URL targetUrl = new URL(url);
			// item 09. try-with-resource
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(targetUrl.openStream()))) {
				StringBuffer html = new StringBuffer();
				String str;
				while ((str = reader.readLine()) != null) html.append(str);
				return html.toString();
			}
		}
		catch (IOException e) { e.printStackTrace(); }

		return result;
	}

}
