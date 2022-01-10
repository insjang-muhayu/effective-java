package study.effective.ch02;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EJTestItem02 {
	@Test void testBuild1() {
		final StockItem naver = new StockItem.Builder("035420", "NAVER")
			.sector("서비스업")
			.industy("양방향미디어와서비스")
			.ipoyear(2008)
			.marketcap(554486)
			.build();

		Assertions.assertNotNull(naver);
		Assertions.assertInstanceOf(StockItem.class, naver);

		System.out.println(naver);
		// output : StockItem(ticker=035420, name=NAVER, sector=서비스업, industy=양방향미디어와서비스, ipoyear=2008, lastsale=0.0, marketcap=554486.0)

	}

	// '035420':'naver', '035720':'kakao', '181710':'nhn', '000270':'kia', '010950':'soil',
	// '005930':'ss_elec', '000660':'sk_hynx', '066570':'lg_elec', '051910':'lg_chem'

	@Test void testBuild2() {
		StockKospi kospi_my = new StockKospi.Builder()
			.addItem(new StockItem.Builder("035420", "NAVER").sector("서비스업").ipoyear(2008).build())
			.addItem(new StockItem.Builder("035720", "카카오").build())
			.addItem(new StockItem.Builder("005930", "삼성전자").build())
			.build();

		Assertions.assertNotNull(kospi_my);
		Assertions.assertInstanceOf(StockKospi.class, kospi_my);

		System.out.println(kospi_my.stockItems);
		// output : [
		// StockItem(ticker=035420, name=NAVER, sector=서비스업, industy=N/A, ipoyear=2008, lastsale=0.0, marketcap=0.0),
		// StockItem(ticker=035720, name=카카오, sector=N/A, industy=N/A, ipoyear=0, lastsale=0.0, marketcap=0.0),
		// StockItem(ticker=005930, name=삼성전자, sector=N/A, industy=N/A, ipoyear=0, lastsale=0.0, marketcap=0.0)]

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
