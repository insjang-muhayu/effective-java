# 2장. 객체 생성과 파괴

- [x] ITEM-01. [Static Factory Method](#item-01.-static-factory-method)
- [x] ITEM-02. [Builder](#item-02.-builder)
- [x] ITEM-03. [Singleton](#item-03.-singleton)
- [x] ITEM-04. [Private Constructor](#item\-04.-private-constructor)
- [x] ITEM-05. [Dependency Injection](#item-05)
- [ ] ITEM-06. [Avoid Unnecessary Object](#item-06)
- [ ] ITEM-07. [Eliminate Object Reference](#item-07)
- [ ] ITEM-08. [Avoid finalizer and cleaner](#item-08)
- [ ] ITEM-09. [try-with-resources](#item-09)

---------------------------------------------------------------
## ITEM-01. Static Factory Method
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
	> `Date dt = Date.from(instant);`
* **of** :
	> `Set<Rank> faceCards = EnumSet.of(JACK, QUEEN, KING);`
* **valueOf** :
	> `BigInteger prime = BigInteger.valueOf(Integer.MAX_VALUE);`
* **instance** :
	> `StackWalker luke = StackWalker.instance(options);`
* **create** :
	> `Object newArray = Array.create(classObject, arrayLen);`
* **get{Type}** :
	> `FileStore fs = Files.getFileStore(path)`
* **new{Type}** :
	> `BufferedReader br = Files.newBufferedReader(path);`
* **{Type}** :
	> `List<Complaint> litany = Collectins.list(legacyLitany);`

-----------------------------------------------------------------
# item 02

## ITEM-02. Builder
### 점층적 생성자 패턴 (Telescoping Constructor Pattern)
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

### 자바빈즈 패턴 (JavaBeans Pattern)
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

### 빌더 패턴 (Builder Pattern)
* 안전성과 가독성 겸비
* Builder는 일반적으로 _Static Member Class_ 로 생성
* `Method Chaining` 혹은 `Fluent API` 형으로 사용 가능
* 사용 방법 :
	1. 생성자(필수 매개변수) 호출로 객체 생성
	2. Builder객체의 `setter method`로 매개변수 설정
	3. `build()` 로 최종 객체 얻음
* 빌더 패턴 유형
	* 점층적(Telescoping) 생성자 방식
		( [StockItem.java](StockItem.java) )
		```java
		public class StockItem {
			private final String ticker;	// 종목코드
			private final String title;		// 종목명

			public static class Builder {
				private final String ticker;	// 필수
				private final String title;		// 선택
				// 1. builder constructor
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
		> (
			[Stock.java](Stock.java) /
			[StockKospi.java](StockKospi.java) /
			[StockKosdaq.java](StockKosdaq.java) /
			[StockNyse.java](StockNyse.java) /
			[StockNasdaq.java](StockNasdaq.java)
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

-----------------------------------------------------------------

## ITEM-03. Singleton
`singleton` : 인스턴스를 오직 하나만 생성할 수 있는 클래스
### [ Public Static Final 필드 방식 ]
```java
	public class Elvis {
		public static final Elvis INSTANCE = new Elvis();
		private Elvis() { ... }
	}
```
* 클래스가 싱글턴임이 API에서 명백히 드러남
* `final`이므로 다른 객체 참조 불가
* **간결함.. !!**

### [ Static Factory 방식 ]
```java
	public class Elvis {
		private static final Elvis INSTANCE = new Elvis();
		private Elvis() { ... }
		public static Elvis getInstance() { return INSTANCE; }
	}
```
* 정적팩터리를 제네릭 싱글턴 팩터리로 확장 가능
* 정적팩터리의 '메서드 참조'를 Supplier로 사용 가능
* ___위 두가지 장점이 불필요하면, `public static final` 방식이 더 좋음___

### [ Reflection 방어 ]
```java
	public class Elvis {
		public static final Elvis INSTANCE = new Elvis();
		private Elvis() {
			if (INSTANCE != null) throw new RuntimeException("생성자 생성불가!!");
			//...
		}
	}
```

### [ Singleton Class 직렬화 ] : implements Serializable
```java
	public class Elvis implements Serializable {
		private static final Elvis INSTANCE = new Elvis();
		private Elvis() { ... }
		public static Elvis getInstance() { return INSTANCE; }

		private Object readResolve() { // singleton임을 보장
			return INSTANCE; // 진짜 INSTANCE 반환하고, 가짜는 GC로 보냄
		}
	}
```
* 역직렬화 시, 새로운 인스턴스 생성을 방지하기 위해 readResolve() 를 제공해야함

### [ Enum 방식 ] : 싱글턴 끝판왕!!
```java
	public enum Elvis {
		INSTANCE;

		public void leaveTheBuilding() { ... }
	}
```
* 가장 간결함
* 추가 코드없이 직렬화 가능
* ___Reflection 공격과 복잡한 직렬화 상황에도 싱글턴 방어 유지___


-----------------------------------------------------------------
#item-04

## ITEM-04. Private Constructor
* `java.util.Arrays` or `java.lang.Math` 처럼 Static Method외 Static Field 를 모아둔 Utitlity Class를 만들 경우,<br>
   비어있는 `private형 생성자`를 만들어서 인스턴스 화를 방지해야 한다. _(컴파일러에서 자동으로 public 생성자를 생성하기 때문)_
* `final class`를 상속해서 하위 클래스에 메서드를 넣는 것은 불가능하므로, final 클래스와 관련 메서드들을 모아놓을때도 사용

```java
	public class StockUtility {
		// Suppresses default constructor, ensuring non-instantiability.
		private StockUtility() { throw new AssertionError(); }
		// ...
	}
```

### java.util.Arrays
```java
	public class Arrays {
		private static final int MIN_ARRAY_SORT_GRAN = 1 << 13;

		// Suppresses default constructor, ensuring non-instantiability.
		private Arrays() {}

		static final class NaturalOrder implements Comparator<Object> { ... }
		...
	}
```

-----------------------------------------------------------------
#item-05


## ITEM-05. [Constructor] Dependency Injection
* 여러 자원에 의존적인 클래스의 경우는 `Static Utility Class` or `Singleton` 방식은 부적합
* `의존 객체 주입(Dependency Injection)` 패턴은 인스턴스 생성 시, 필요한 자원을 넘겨주는 방식
* __유연성, 재사용성, 테스트 용이성__ 높여줌
* 단점 : 의존성이 너무 많은 프로젝트에서는 코드가 어지럽다. (Spring FW를 사용해 해결할 것)

```java
	public class SpellChecker {
		private final Lexicon dictionary; // 주입된 자원을 final로 불변 보장

		public SpellChecker(Lexicon dictionary) { // 의존 객체 주입 !!
			// null이면 NullPointerException, 아닌경우 objects 반환
			this.dictionary = Objects.requireNonNull(dictionary);
		}

		public boolean isValid(String word){}
	}
```

### Factory : Supplier\<T\> 인터페이스

```java
	@FunctionalInterface
	public interface Supplier<T> {
		T get();
	}
```
```java
	Mosaic create(Supplier<? extends Tile> tileFactory) { ... }
```

-----------------------------------------------------------------


## ITEM-06. [Constructor] Avoid Unnecessary Object

