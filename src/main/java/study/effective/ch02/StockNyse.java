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

	//=========================================================
	// ITEM-02. [생성자: 많은 매개변수] Builder를 고려하라.
	StockNyse() { super(); }
	private StockNyse(Builder builder) {
		super(builder);
	}
	public static class Builder extends Stock.Builder<Builder> {
		@Override public StockNyse build() { return new StockNyse(this); }
		@Override protected Builder self() { return this; }
	}
}
