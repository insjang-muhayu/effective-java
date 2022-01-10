package study.effective.ch02;

import lombok.Getter;
import lombok.ToString;

@ToString
public class StockKosdaq extends Stock {
	@Getter private final ExchangeType exchange = ExchangeType.KOSDAQ;
	@Getter private final NationType nation = NationType.KOR;

	StockKosdaq() { super(); }

	@Override
	public void whoami() {
		System.out.println("I am Kosdaq.");
	}

	//=========================================================
	// ITEM-02. [생성자: 많은 매개변수] Builder를 고려하라.
	private StockKosdaq(Builder builder) {
		super(builder);
	}
	public static class Builder extends Stock.Builder<Builder> {
		@Override public StockKosdaq build() { return new StockKosdaq(this); }
		@Override protected Builder self() { return this; }
	}
}
