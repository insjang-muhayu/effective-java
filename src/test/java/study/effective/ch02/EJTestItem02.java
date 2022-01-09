package study.effective.ch02;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EJTestItem02 {
	@Test void testCreate() {
		final StockItem naver = new StockItem.Builder("035420", "NAVER")
			.sector("서비스업")
			.industy("양방향미디어와서비스")
			.ipoyear(2008)
			.marketcap(554486)
			.build();

		Assertions.assertNotNull(naver);
		Assertions.assertInstanceOf(StockItem.class, naver);
	}

	public static void main(String[] args) {
		StockItem naver = new StockItem.Builder("035420", "NAVER")
			.sector("서비스업")
			.industy("양방향미디어와서비스")
			.ipoyear(2008)
			.marketcap(554486)
			.build();
		System.out.println(naver);
	}
}
