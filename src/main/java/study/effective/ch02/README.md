# 2장. 객체 생성과 파괴

## 목차

- [x] item 01. [Static Factory Method](#item-01-static-factory-method)
- [x] item 02. [Builder](#item-02-builder)
- [x] item 03. [Singleton](#item-03-singleton)
- [x] item 04. [Private Constructor](#item-04-private-constructor)
- [x] item 05. [Dependency Injection](#item-05-dependency-injection)
- [x] item 06. [Avoid Unnecessary Object](#item-06-avoid-unnecessary-object)
- [x] item 07. [Eliminate Object Reference](#item-07-eliminate-object-reference)
- [x] item 08. [Avoid finalizer and cleaner](#item-08-avoid-finalizer-and-cleaner)
- [x] item 09. [try-with-resources](#item-09-try-with-resources)

---------------------------------------------------------------

## item 01. Static Factory Method

[[TOC]](#목차)

`[객체생성]`

### __Static Factory Method 특징__

1. __객체의 특성에 적합한 명명 가능__
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

### __Naming Convention__

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

## item 02. Builder

[[TOC]](#목차)

`[객체생성]`

### __점층적 생성자 패턴 (Telescoping Constructor Pattern)__

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

### __자바빈즈 패턴 (JavaBeans Pattern)__

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

### __빌더 패턴 (Builder Pattern)__

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

## item 03. Singleton

[[TOC]](#목차)

`[객체생성]`
`singleton` : 인스턴스를 오직 하나만 생성할 수 있는 클래스

### __[ Public Static Final 필드 방식 ]__

```java
	public class Elvis {
		public static final Elvis INSTANCE = new Elvis();
		private Elvis() { ... }
	}
```

* 클래스가 싱글턴임이 API에서 명백히 드러남
* `final`이므로 다른 객체 참조 불가
* **간결함.. !!**

### __[ Static Factory 방식 ]__

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

### __[ Reflection 방어 ]__

```java
	public class Elvis {
		public static final Elvis INSTANCE = new Elvis();
		private Elvis() {
			if (INSTANCE != null) throw new RuntimeException("생성자 생성불가!!");
			//...
		}
	}
```

### __[ Singleton Class 직렬화 ] : implements Serializable__

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

### __[ Enum 방식 ] : 싱글턴 끝판왕!!__

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

## item 04. Private Constructor

[[TOC]](#목차)

`[객체생성]` : ( [StockUtils.java : StockUtils()](./StockUtils.java) )
* `java.util.Arrays` or `java.lang.Math` 처럼 Static Method외 Static Field 를 모아둔 Utitlity Class를 만들 경우,
   비어있는 `private형 생성자`를 만들어서 인스턴스 화를 방지해야 한다. _(컴파일러에서 자동으로 public 생성자를 생성하기 때문)_
* `final class`를 상속해서 하위 클래스에 메서드를 넣는 것은 불가능하므로, final 클래스와 관련 메서드들을 모아놓을때도 사용

```java
	public class StockUtils {
		// Suppresses default constructor, ensuring non-instantiability.
		private StockUtils() { throw new AssertionError(); }
		// ...
	}
```

### __java.util.Arrays__

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

## item 05. Dependency Injection

[[TOC]](#목차)

`[객체생성]`

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

### __Factory : Supplier\<T\> 인터페이스__

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

## item 06. Avoid Unnecessary Object

[[TOC]](#목차)

`[객체생성]`

### __불필요한 객체 생성을 피해 성능 개선__

```java
	// 나쁜 예 - 호출마다 인스턴스 새로 생성
	String bad = new String("반복문 안이면 폭망");

	// 좋은 예 - 하나의 인스턴스 재사용
	String good = "인스턴스 재사용";
```
* 동일 기능의 객체를 매번 생성보다 __하나의 객체를 재사용하는 편이 좋다.__
* [불변객체(item 17)](../ch04/README.md)는 항상 재사용 가능
* 반복문 안에서는 중요한 이슈가 될 수 있음

### __캐싱하여 재사용__

```java
	public class RomanNumerals {
		// 1.불변인 `Pattern` 인스턴스를 클래스 초기화 과정에서 생성 후 캐싱
		private static final Pattern ROMAN = Pattern.compile("^(?=.)M*(C[MD]|D?C{0,3})" + "(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$");

		static boolean isRomanNumeral(String s) {
			return ROMAN.matcher(s).matches(); // 2. ROMAN 재사용
		}
	}
```

### __주의! Auto Boxing(자동 형변환)__

```java
	private static long sum() {
		Long sum = 0L; // Long 보다 long을 사용하라

		for (long i = 0; i < Integer.MAX_VALUE; i++) {
			// long i 보다 int i 사용하라
			sum += i;
		}

		return sum;
	}
```


-----------------------------------------------------------------

## item 07. Eliminate Object Reference

[[TOC]](#목차)

`[객체소멸]` 다 쓴 참조(obsolete reference) 객체는 NULL 처리하라.
* __Stack 메모리 누수 다루기__
	```java
		public Object pop() {
			if (size == 0) throw new EmptyStackException();
			Object result = elements[--size];
			elements[size] = null; // 다 쓴 참조 해제
			return result;
		}
	```
	* 객체참조를 null 처리하는 일은 예외적인 경우에만 사용할 것
	* 비활성 영역이 되는 순간, null 처리를 통해 다쓴객체란 것을 GC에 알림
	* Collection 클래스의 원소가 참조한 객체들에 대해 모두 null 처리 해줘야 함

* __Cache 메모리 누수 다루기__
	* 캐시 외부에서 키를 참조하는 동안만 엔트리가 필요한 상황이라면 `WeakHashMap` 을 사용해서 캐시를 생성
	* 백그라운드 스레드(`ScheduledThreadPoolExecutor`)를 활용
	* 캐시에 새 엔트리 추가할 때 부수 작업으로 수행하는 방법
		```java
		// LinkedHashMap 사용한 캐시에서 사용하지 않는 엔트리를 처리 방법
		void afterNodeInsertion(boolean evict) { // 가장 오래된 것을 제거
			LinkedHashMap.Entry<K,V> first;
			if (evict && (first = head) != null && removeEldestEntry(first)) {
				K key = first.key;
				removeNode(hash(key), key, null, false, true);
			}
		}
		```

* __Listener or Callback 메모리 누수 다루기__
	* Client가 콜백 등록 후에 미해지한 경우, 콜백을 약한 참조(`weak reference`) 로 저장하면 GC가 즉시 수거함
		> (ex. `WeakHashMap` 의 키로 저장하는 방법)

( [EJTestItem07.java](/insjang-muhayu/effective-java/blob/main/src/test/java/study/effective/ch02/EJTestItem07.java) )
```java
	// 출처 : https://dahye-jeong.gitbook.io/java/java/effective_java/2021-01-22-eliminate-object-reference
	public class ReferenceTest {
		public static void main(String[] args){
			Integer key1 = 1000; Integer key2 = 2000; Integer key3 = 3000;

			WeakHashMap<Integer, String> weakMap = new WeakHashMap<>();
			weakMap.put(key1, "test a");
			weakMap.put(key2, "test b");

			key1 = null; // HashMap에서는 GC 수행 시 바로 처리 안됨

			System.out.println("WeakHashMap GC 수행 이전");
			weakMap.entrySet().stream().forEach(el -> System.out.println(el));

			System.gc(); // GC 수행

			System.out.println("WeakHashMap GC 수행 이후");
			weakMap.entrySet().stream().forEach(el -> System.out.println(el));
		}
	}

/*
output :
	WeakHashMap GC 수행 이전
	1000=test a // <-- key a = null
	2000=test b
	WeakHashMap GC 수행 이후
	2000=test b
*/
```


-----------------------------------------------------------------

## item 08. Avoid finalizer and cleaner

[[TOC]](#목차)

`[객체소멸]` `finalizer` & `cleaner`는 피하라

### __객체 소멸자 : (`finalizer`, `cleaner`)__

* `finalizer`
	* 예측 불가능, 위험 가능성으로 일반적 불필요
	* 오동작, 낮은 성능, 이식성 문제의 원인
	* JAVA 9 부터 `deprecated` 됨
* `cleaner`
	* finalizer의 대안으로 등장 (덜 위험함)
	* 예측 불가능, 느림, 일반적으로 불필요

### __사용 자제 이유__

1. 실제 소멸 수행 시점을 알 수 없음 (보장이 안됨)
	> 상태를 영구적으로 수정하는 작업에서는 절대 finalizer or cleaner에 의존하면 안됨
2. 심각한 성능 문제가 동반될 수 있음
	> GC의 효율을 상당히 떨어뜨림
3. `finalizer 공격` 노출로 심각한 보안문제 유발 가능
	> 방어 방법 : 빈 finalize 메소드 생성 후 final로 선언

### __사용하는 곳__

1. 자원 소유자가 `close()`를 호출하지 않는 것에 대비한 안전망 역할
	> `FileInputStream`, `FileOutputStream`, `ThreadPoolExecutor` 에서 안전망 역할의 finalizer 제공함
2. 네이티브 피어(`Native Peer`)와 연결된 객체 자원 회수용으로 사용


-----------------------------------------------------------------

## item 09. try-with-resources

[[TOC]](#목차)

`[객체소멸]` : ( [StockUtils.java : getHtml(url)](./StockUtils.java) )

### __try - with -resources__

```java
	try (SomeResource resource = getResource()) {
		use(resource);
	} catch (...) {
		...
	}
```

try에 복수 자원 객체 전달
```java
try (Something1 s1 = new Something1(); Something2 s2 = new Something2()) {

} catch (...) {
    ...
}
```

`try-with-resources` 구조를 사용할려면 __`AutoCloseable` 인터페이스를 구현해야 함__
```java
public interface AutoCloseable {
    void close() throws Exception;
}
```


-----------------------------------------------------------------
[[TOC]](#목차)

... 2장 끝 ...

[다음 3장 >>>](../ch03/README.md)