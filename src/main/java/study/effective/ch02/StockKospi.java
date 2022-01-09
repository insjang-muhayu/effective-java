package study.effective.ch02;

import lombok.Getter;
import lombok.ToString;

@ToString
public class StockKospi extends Stock {
	@Getter private final ExchangeType exchange = ExchangeType.KOSPI;
	@Getter private final NationType nation = NationType.KOR;

	@Override
	public void whoami() {
		System.out.println("I am Kospi.");
	}

	//=========================================================
	// ITEM-02. [생성자: 많은 매개변수] Builder를 고려하라.
	StockKospi() { super(); }
	private StockKospi(Builder builder) {
		super(builder);
	}
	public static class Builder extends Stock.Builder<Builder> {
		@Override public StockKospi build() { return new StockKospi(this); }
		@Override protected Builder self() { return this; }
	}
}
