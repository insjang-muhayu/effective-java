package study.effective.ch02;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EJTestItem01 {
	@Test void testCreate() {
		final Stock kospi = Stock.create(Stock.ExchangeType.KOSPI);
		
		Assertions.assertNotNull(kospi);
		Assertions.assertInstanceOf(StockKospi.class, kospi);
		Assertions.assertEquals(Stock.ExchangeType.KOSPI, ((StockKospi) kospi).getExchange());
	}
	
	public static void main(String[] args) {
		Stock stock = Stock.instance(Stock.ExchangeType.NASDAQ);
		stock.whoami(); // output : I am Nasdaq.
	}
}
