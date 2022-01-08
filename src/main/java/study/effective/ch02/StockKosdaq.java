package study.effective.ch02;

import lombok.Getter;
import lombok.ToString;

@ToString
public class StockKosdaq extends Stock {
	@Getter private final ExchangeType exchange = ExchangeType.KOSDAQ; 
	@Getter private final NationType nation = NationType.KOR;
	
	@Override
	public void whoami() {
		System.out.println("I am Kosdaq.");
	}
}
