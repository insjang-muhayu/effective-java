package study.effective.ch02;

import lombok.Getter;
import lombok.ToString;

@ToString
public class StockNasdaq extends Stock {
	@Getter private final ExchangeType exchange = ExchangeType.NASDAQ; 
	@Getter private final NationType nation = NationType.USA;

	@Override
	public void whoami() {
		System.out.println("I am Nasdaq.");
	}
	
}
