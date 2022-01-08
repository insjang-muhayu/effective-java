package study.effective.ch02;

import lombok.Getter;
import lombok.ToString;

@ToString
public class StockNyse extends Stock {
	@Getter private final ExchangeType exchange = ExchangeType.NYSE; 
	@Getter private final NationType nation = NationType.USA;
	
	@Override
	public void whoami() {
		System.out.println("I am NewYork Stock Exchange.");
	}
}
