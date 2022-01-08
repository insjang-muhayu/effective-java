package study.effective.ch02;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import study.effective.ch02.Stock.ExchangeType;

class EJTestItem01 {
	@Test void testCreate() {
		final Stock kospi = Stock.create(ExchangeType.KOSPI);
	}
}
