package study.effective.ch02;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.ToString;

@ToString
public abstract class Stock {
	//=========================================================
	// ITEM-01. [생성자] Static Factory Method를 고려하라.
	//=========================================================
	public enum ExchangeType { KOSPI, KOSDAQ, NYSE, NASDAQ } // 거래소
	public enum NationType { KOR, USA, CHN, JPN, HKG, VNM } // 거래소 국가

	public abstract ExchangeType getExchange();
	public abstract NationType getNation();
	public abstract void whoami();

	// 2.인스턴스를 새로 생성하지 않아도 됨
	public static Stock EXC_KOSPI = new StockKospi();
	public static Stock EXC_KOSDAQ = new StockKosdaq();
	public static Stock EXC_NYSE = new StockNyse();
	public static Stock EXC_NASDAQ = new StockNasdaq();

	// 1. 객체의 특성에 적합한 작명 가능
	public static Stock getKospi() { return EXC_KOSPI; }
	public static Stock getKosdaq() { return EXC_KOSDAQ; }
	public static Stock getNyse() { return EXC_NYSE; }
	public static Stock getNasdaq() { return EXC_NASDAQ; }

	public static Stock create(final Stock.ExchangeType exchange) {
		switch (exchange) {
			case KOSPI : return new StockKospi();
			case KOSDAQ : return new StockKosdaq();
			case NYSE : new StockNyse();
			case NASDAQ : new StockNasdaq();
			default : throw new IllegalArgumentException("Not found Index Code :" + exchange.name());
		}
	}
	public static Stock instance(final Stock.ExchangeType exchange) {
		switch (exchange) {
			case KOSPI : return EXC_KOSPI;
			case KOSDAQ : return EXC_KOSDAQ;
			case NYSE : return EXC_NYSE;
			case NASDAQ : return EXC_NASDAQ;
			default : throw new IllegalArgumentException("Not found Index Code :" + exchange.name());
		}
	}

	//=========================================================
	// ITEM-02. [생성자: 많은 매개변수] Builder를 고려하라.
	//	: 주식 종목을 추가할 수 있는 builder를 만들어 보자
	//=========================================================
	abstract static class Builder<T extends Builder<T>> {
		List<StockItem> items = new ArrayList<>();

		abstract Stock build();
		protected abstract T self(); // 하위클래스에서 this로 재정의 해야함

		public T addItem(StockItem item) {
			items.add(Objects.requireNonNull(item));
			return self();
		}
	}

	final List<StockItem> stockItems;


	Stock() {
		stockItems = new ArrayList<>();
	}
	Stock(Builder<?> builder) {
		// List는 Deep Copy가 복잡하기 때문에 stream을 사용해서 clone() 대체
		stockItems = builder.items.stream().collect(Collectors.toList());
	}

	//================================================================================
	public static void main(String[] args) {
		Stock stock = Stock.instance(Stock.ExchangeType.KOSPI);
		stock.whoami(); // output : I am Kospi.


		StockKospi kospi_my = new StockKospi.Builder()
			.addItem(new StockItem.Builder("035420", "NAVER").build())
			.addItem(new StockItem.Builder("035720", "카카오").build())
			.addItem(new StockItem.Builder("005930", "삼성전자").build())
			.build();

		System.out.println(kospi_my.stockItems.toString());
	}
}
