package study.effective.ch02;

import java.util.Date;

import lombok.Getter;
import lombok.ToString;

@ToString
public class StockKospi extends Stock {
	@Getter private final ExchangeType exchange = ExchangeType.KOSPI; 
	@Getter private final NationType nation = NationType.KOR;
	
	@Override
	public void whoami() {
		System.out.println("I am Kospi.");
		Date dt = Date.from(instant)
	}
}
