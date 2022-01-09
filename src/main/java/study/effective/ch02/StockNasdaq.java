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

	//=========================================================
	// ITEM-02. [생성자: 많은 매개변수] Builder를 고려하라.
	StockNasdaq() { super(); }
	private StockNasdaq(Builder builder) {
		super(builder);
	}
	public static class Builder extends Stock.Builder<Builder> {
		@Override public StockNasdaq build() { return new StockNasdaq(this); }
		@Override protected Builder self() { return this; }
	}
}
