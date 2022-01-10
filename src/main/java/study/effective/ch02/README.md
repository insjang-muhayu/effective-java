# 2장 객체 생성과 파괴

## ITEM-01. [생성자] Static Factory Method를 고려하라.
-----------------------------------------------------------------
### Static Factory Method 특징
1. __객체의 특성에 적합한 작명 가능__
	* `BigInteger(int, int, Random)` -> `BigInteger.probablePrime`
2. __인스턴스를 새로 생성하지 않아도 됨__
	* 인스턴스 캐슁하여 재활용 : 불필요한 객체생성 회피, 반복 호출 시 성능향상 기여
	* 통제된 인스턴스 클래스 (instance-controlled) : Singleton(ITEM-03), Noninstantiable(ITEM-04) 가능
	* `Boolean.valueOf(boolean)`
3. __하위 타입 객체 반환(return) 가능__
	* 구체적인 예가 없어 이해안됨 -> skip (ITEM-20 에서 파악 필요)
	* 자바8 부터 인터페이스에 public static method 가능
4. __입력 매개변수에 따라 다른 클래스 객체 반환 가능__
5. __작성 시점에는 반환할 객체의 클래스 부재 가능__
	* Service Provider Framework 근간 : 대표적 사례 JDBC Provider
		* Service Interface : `Connection`
		* Privider Registration API : `DriverManager.registerDriver`
		* Service Access API : `DriverManager.getConnection`
		* *Service Provider Interface*
	* 자바6 부터 범용 Service Provider Framework 제공 : `java.util.ServiceLoader`
	* 단점 :
		* 하위 클래스 생성 불가
		* API 설명 힌트 지원 안됨 (주석 잘 달아야 함)
### Naming Convention
* **from** :
	`Date dt = Date.from(instant);`
* **of** :
	`Set<Rank> faceCards = EnumSet.of(JACK, QUEEN, KING);`
* **valueOf** :
	`BigInteger prime = BigInteger.valueOf(Integer.MAX_VALUE);`
* **instance** :
	`StackWalker luke = StackWalker.instance(options);`
* **create** :
	`Object newArray = Array.create(classObject, arrayLen);`
* **get{Type}** :
	`FileStore fs = Files.getFileStore(path)`
* **new{Type}** :
	`BufferedReader br = Files.newBufferedReader(path);`
* **{Type}** :
	`List<Complaint> litany = Collectins.list(legacyLitany);`


### ITEM-02. [생성자: 많은 매개변수] Builder를 고려하라.
-----------------------------------------------------------------
* __점층적 생성자 패턴 (Telescoping Constructor Pattern)__
	* 매개변수의 개수/타입로 생성자를 구분하는 방식
	* 생성자에 필요한 매개변수가 단순할 때 사용 (가장 기초적 생성자 패턴)
		```java
		public abstract class Stock02 {
			private final int A, B, C;
			public Stock02(int a) {
				this(a, 0);
			}
			public Stock02(int a, int b) {
				this(a, b, 0);
			}
			public Stock02(int a, int b, int c) {
				this.A = a; this.B = b; this.C = c;
			}
		}
		```

* __자바빈즈 패턴 (JavaBeans Pattern)__
	* MVC의 Model 클래스와 비슷 (Setter/Getter 함수로 이루어짐)
	* 장점 : `점층적 생성자 패턴`에 비해 매개변수 의미 부여 가능
	* 단점 :
		* 객체 하나 만들기 위해 `setter method` 를 여러번 호출해야 함
		* 객체 생성 완료 전까지 일관성 깨질수 있음 (심각한 버그 유발)
		* 일관성 유지를 위해 **Freezing Method** 필요
			```java
			public class FreezingMethod {
				private String name;
				private booleam freeze = false;

				public FreezingMethod() {}

				public void setName(String name) {
					if (isFreezing()) throw new AssertionError("[동결] 불변성(immutable) 유지!!");
					this.name = name;
				}
				public String getName() { return name; }

				public void freeze() { this.freeze = true; }
				public boolean isFreezing() { return freeze; }
			}
			```
			```java
			FreezingMethod A = new FreezingMethod();
			A.setName("insjang"); A.freeze();
			A.setName("muhayu"); // 예외발생!!
			```


* __빌더 패턴 (Builder Pattern)__
	* 안전성과 가독성 겸비
	* Builder는 일반적으로 _Static Member Class_ 로 생성
	* `Method Chaining` 혹은 `Fluent API` 형으로 사용 가능
	* 사용 방법 :
		1. 생성자(필수 매개변수) 호출로 객체 생성
		2. Builder객체의 `setter method`로 매개변수 설정
		3. `build()` 로 최종 객체 얻음
	* 빌더 패턴 유형
		* 점층적(Telescoping) 생성자 방식
			( [StockItem.java](https://github.com/insjang-muhayu/effective-java/blob/main/src/main/java/study/effective/ch02/StockItem.java) )
			```java
			public class StockItem {
				private final String ticker;	// 종목코드
				private final String title;		// 종목명

				public static class Builder {
					private final String ticker;	// 필수
					private final String title;		// 선택
					// 1. builder creator
					public Builder(String ticker) { this.ticker	= ticker; }
					// 2. setter method
					public Builder title(String val) { title = val; return this; }
					// 3. build()
					public StockItem build() { return new StockItem(this); }
				}

				private StockItem(Builder builder) {
					ticker		= builder.ticker;
					title		= builder.title;
				}
			}
			```
			```java
				StockItem naver = new StockItem.Builder("035420") .title("NAVER") .build();
			```
		* 계층적(Abstract 사용) 클래스 방식
			(
				[Stock.java](https://github.com/insjang-muhayu/effective-java/blob/main/src/main/java/study/effective/ch02/Stock.java) /
				[StockKospi.java](https://github.com/insjang-muhayu/effective-java/blob/main/src/main/java/study/effective/ch02/StockKospi.java) /
				[StockKosdaq.java](https://github.com/insjang-muhayu/effective-java/blob/main/src/main/java/study/effective/ch02/StockKosdaq.java) /
				[StockNyse.java](https://github.com/insjang-muhayu/effective-java/blob/main/src/main/java/study/effective/ch02/StockNyse.java) /
				[StockNasdaq.java](https://github.com/insjang-muhayu/effective-java/blob/main/src/main/java/study/effective/ch02/StockNasdaq.java)
			)

			<추상 클래스>
			```java
			public abstract class Stock {
				... 생략 ...
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

				Stock(Builder<?> builder) {
					// List는 Deep Copy가 복잡하기 때문에 stream을 사용해서 clone() 대체
					stockItems = builder.items.stream().collect(Collectors.toList());
				}
			}
			```
			<하위 클래스>
			```java
			public class StockKospi extends Stock {
				public static class Builder extends Stock.Builder<Builder> {
					@Override public StockKospi build() { return new StockKospi(this); }
					@Override protected Builder self() { return this; }
				}

				private StockKospi(Builder builder) {
					super(builder);
				}
			}
			```


			```java
			StockKospi kospi_my = new StockKospi.Builder()
				.addItem(new StockItem.Builder("035420", "NAVER").sector("서비스업").ipoyear(2008).build())
				.addItem(new StockItem.Builder("035720", "카카오").build())
				.addItem(new StockItem.Builder("005930", "삼성전자").build())
				.build();
			```

