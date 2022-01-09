package study.effective.ch02;

import lombok.ToString;

@ToString
public class StockItem {
	private final String ticker;	// 종목코드
	private final String name;		// 종목명
	private final String sector;	// 섹터
	private final String industy;	// 업종
	private final int ipoyear;		// 상장년
	private final double lastsale;	// 매출액
	private final double marketcap;	// 시가총액

	public static class Builder {
		// 필수
		private final String ticker;
		private final String name;
		// 선택
		private String sector		= "N/A";
		private String industy		= "N/A";
		private int ipoyear			= 0;
		private double lastsale		= 0L;
		private double marketcap	= 0L;

		public Builder(String ticker, String name) {
			this.ticker	= ticker;
			this.name	= name;
		}
		public Builder sector(String val) { sector = val; return this; }
		public Builder industy(String val) { industy = val; return this; }
		public Builder ipoyear(int val) { ipoyear = val; return this; }
		public Builder lastsale(double val) { lastsale = val; return this; }
		public Builder marketcap(double val) { marketcap = val; return this; }

		public StockItem build() {
			return new StockItem(this);
		}
	}

	private StockItem(Builder builder) {
		ticker		= builder.ticker;
		name		= builder.name;
		sector		= builder.sector;
		industy		= builder.industy;
		ipoyear		= builder.ipoyear;
		lastsale	= builder.lastsale;
		marketcap	= builder.marketcap;
	}
}
