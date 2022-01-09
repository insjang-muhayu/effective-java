package study.effective.ch02;

import lombok.Getter;
import lombok.ToString;

@ToString
public class StockKospi extends Stock {
	@Getter private final ExchangeType exchange = ExchangeType.KOSPI; 
	@Getter private final NationType nation = NationType.KOR;
	
	public StockKospi() {
		
	}
	
	@Override
	public void whoami() {
		System.out.println("I am Kospi.");
	}

}
