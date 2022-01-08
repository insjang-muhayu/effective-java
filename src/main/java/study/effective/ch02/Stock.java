package study.effective.ch02;


public abstract class Stock {	
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
	public static Stock from(Stock instant) {
		switch (instant.getExchange()) {
			case KOSPI : return new StockKospi();
			case KOSDAQ : return new StockKosdaq();
			case NYSE : new StockNyse();
			case NASDAQ : new StockNasdaq();
			default : throw new IllegalArgumentException("Not found Index Code :" + exchange.name());
		}
	}
	
}
