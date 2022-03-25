# 7장. 람다와 스트림

## 목차

- [x] item 42. [익명 클래스보다는 람다를 사용하라](#item-42-익명-클래스보다는-람다를-사용하라)
- [x] item 43. [람다보다는 메서드 참조를 사용하라](#item-43-람다보다는-메서드-참조를-사용하라)
- [x] item 44. [표준 함수형 인터페이스를 사용하라](#item-44-표준-함수형-인터페이스를-사용하라)
- [x] item 45. [스트림은 주의해서 사용하라](#item-45-스트림은-주의해서-사용하라)
- [x] item 46. [스트림에서는 부작용 없는 함수를 사용하라](#item-46-스트림에서는-부작용-없는-함수를-사용하라)
- [x] item 47. [반환 타입으로는 스트림보다 컬렉션이 낫다](#item-47-반환-타입으로는-스트림보다-컬렉션이-낫다)
- [x] item 48. [스트림 병렬화는 주의해서 적용하라](#item-48-스트림-병렬화는-주의해서-적용하라)


---------------------------------------------------------------

## item 42. 익명 클래스보다는 람다를 사용하라

[[TOC]](#목차)

### **익명클래스를 이용한 함수객체**

```java
public class I42_Anonymous {
	public static void main(String[] args) {
		List<String> words = Arrays.asList("kim", "taeng", "mad", "play");

		Collections.sort(words, new Comparator<String>() {
			@Override public int compare(String o1, String o2) {
				return Integer.compare(o1.length(), o2.length());
			}
		});
	}
}
```
* 전략패턴과 같이 함수객체를 사용하는 객체지향 디자인패턴에는 익명클래스면 충분
* 익명클래스 방식은 코드가 너무 길기 때문에 함수형 프로그래밍에 부적합

### **Lambda expression**

람다는 함수나 익명클래스와 개념은 비슷하지만 __코드는 훨씬 간결__
```java
public class I42_Lambdas {
	public static void main(String[] args) {
		List<String> words = Arrays.asList("kim", "taeng", "mad", "play");

		// 람다타입 : (Comparator<String>)
		// 매개변수(s1, s2) 타입 : String
		Collections.sort(words, (s1, s2) -> Integer.compare(s1.length(), s2.length()));
	}
}
```

__타입을 명시해야 코드가 더 명확한 경우를 제외하고는 람다의 모든 매개변수 타입은 생략하는 것이 좋다.__
* 생성자 메서드
	```java
	Collections.sort(words, comparingInt(String::length));
	```

* `List` 인터페이스에 추가된 `sort` 메서드
	```java
	words.sort(comparingInt(String::length));
	```

### __람다 활용 예제__

`java.util.function.DoubleBinaryOperator`
```java
// double 인수 2개를 받아 double 결과를 돌려줌
@FunctionalInterface
public interface DoubleBinaryOperator {
	double applyAsDouble(double left, double right);
}
```


```java
import java.util.function.DoubleBinaryOperator;

enum Operation {
	PLUS	("+", (x, y) -> x + y),
	MINUS	("-", (x, y) -> x - y),
	TIMES	("*", (x, y) -> x * y),
	DIVIDE	("/", (x, y) -> x / y);

	private final String symbol;
	private final DoubleBinaryOperator op;

	Operation(String symbol, DoubleBinaryOperator op) {
		this.symbol = symbol; this.op = op;
	}

	@Override public String toString() { return symbol; }

	public double apply(double x, double y) {
		return op.applyAsDouble(x, y);
	};
}

public class I42_OperLambda {
	public static void main(String[] args) {
		Operation.PLUS.apply(2, 3);
	}
}
```

* __람다는 메서드,클래스와는 달리 이름이 없고 문서화도 못한다.__
	- 람다는 1 ~ 3 줄 이내로 작성
	- 람다가 길거나 읽기 어렵다면 람다를 사용하지 않는 쪽으로 리팩터링  

* __람다는 함수형 인터페이스에서만 사용된다.__
	- 추상클래스의 인스턴스를 만드는 경우 __익명클래스__ 사용
	- 추상메서드가 여러개인 인터페이스의 인스턴스를 만드는 경우 익명클래스 사용   

	```java
	abstract class I42_AbstractHello {
		public void sayHello() { System.out.println("Hello!"); }
	}

	public class I42_AbstractAnonym {
		public static void main(String[] args) {
			// Hello hello = new Hello(); // 이건 원래 안됨
			Hello instance1 = new I42_AbstractHello() {
				private String msg = "Hi";
				@Override public void sayHello() { System.out.println(msg); }
			};

			Hello instance2 = new I42_AbstractHello() {
				private String msg = "Hola";
				@Override public void sayHello() { System.out.println(msg); }
			};
			
			instance1.sayHello(); // Hi!
			instance2.sayHello(); // Hola!
			System.out.println(instance1 == instance2); // false
		}
	}
	```

* __람다는 자기 자신을 참조할 수 없다.__
	- 람다에서 `this`는 바깥 인스턴스
	- 익명클래스에서 `this`는 익명클래스 인스턴스 자신

	```java
	class I42_thisAnonym {
		public void say() {}
	}

	public class I42_thisLambda {
		public void someMethod() {
			List<I42_thisAnonym> list = Arrays.asList(new I42_thisAnonym());

			I42_thisAnonym anonymous = new I42_thisAnonym() {
				@Override public void say() {
					System.out.println(
						"this -> Anonym : " + (this instanceof I42_thisAnonym));
				}
			};			
			anonymous.say(); // this -> Anonym : true

			list.forEach(o -> System.out.println( // this -> Lambda : true
				"this -> Lambda : " + (this instanceof I42_thisLambda)));
		}

		public static void main(String[] args) {
			new I42_thisLambda().someMethod();
		}
	}
	```


---------------------------------------------------------------

## item 43. 람다보다는 메서드 참조를 사용하라

[[TOC]](#목차)

### **메서드 참조**

함수객체를 **"람다"** 보다 더 간결하게 구현할 수 있는 **"메서드참조(method reference)"**

* __람다__
	```java
	default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> func) {
		Objects.requireNonNull(func);
		Objects.requireNonNull(value);
		V oldValue = get(key);
		V newValue = (oldValue == null) ? value : func.apply(oldValue, value);
		if (newValue == null) remove(key);
		else put(key, newValue);

		return newValue;
	}
	```

	```java
	// 키가 맵 안에 없으면 키와 숫자 1을 매핑하고, 이미 있다면 기존 매핑 값을 증가
	map.merge(key, 1, (count, incr) -> count + incr);
	```

* __메서드 참조__
	> 람다대신 메서드 참조를 전달하면 똑같은 결과도 더 간결하게 구현할 수 있다.
	```java
	public final class Integer extends Number implements Comparable<Integer> {
		...
		public static int sum(int a, int b) { return a + b; }
		...
	}
	```

	```java
	map.merge(key, 1, Integer::sum);
	```

람다와 메서드가 모두 `GoshThisClassNameIsHumonous` 클래스에 있다면 람다로 구현한 것이 더 간결하고, 명확하다.

```java
// 메서드 참조
service.execute(GoshThisClassNameIsHumonous::action);

// 람다
service.execute(() -> action());
```
### **메서드 참조 유형**

| 유형              | 예                        | 람다                          |
| ----------------- | ------------------------- | ----------------------------- |
| 정적              | Integer::parseInt         | str -> Integer.parseInt(str)  |
| 한정적(인스턴스)  | Instant.now()::isAfter    | Instant then = instant.now();<br> t -> then.isAfter(t); |
| 비한정적(인스턴스)| String::toLowerCase       | str -> str.toLowerCase()      |
| 클래스 생성자     | TreeMap::new              | () -> new TreeMap()           |
| 배열 생성자       | int[]::new                | len -> new int[len]           |

**메서드참조가 더 짧고 명확하다면 메서드참조를, 그렇지 않다면 람다를 사용**

---------------------------------------------------------------

## item 44. 표준 함수형 인터페이스를 사용하라

[[TOC]](#목차)

### **표준 함수형 인터페이스**

```java
@FunctionInterface interface EldestEntryRemovalFunction<K, V> {
    boolean remove(Map<K,V> map, Map.Entry<K, V> eldest);
}

// 위 EldestEntryRemovalFunction 를 표준 함수형 인터페이스로 대체 가능
BiPredicate<Map<K,V>, Map.Entry<K,V> eldest>
```

필요한 용도에 맞는게 있다면, 직접 구현하는 것보다 표준 함수형 인터페이스를 활용하는 것이 좋다

| 인터페이스		| 함수 시그니처			| 설명										| 예					|
| ----------------- | --------------------- | ----------------------------------------- | --------------------- |
|`UnaryOperator<T>`	|`T apply(T t)`			| 반환값과 인수타입이 같은 함수, 인수 1개	| `String::toLowerCase`	|
|`BinaryOperator<T>`|`T apply(T t1, T t2)`	| 반환값과 인수타입이 같은 함수, 인수 2개	| `BigInteger::add`		|
|`Predicate<T>`		|`boolean test(T t)`	| 1개 인수를 받아 boolean을 반환하는 함수	| `Collection::isEmpty`	|
|`Function<T,R>`	|`R apply(T t)`			| 인수와 반환 타입이 다른 함수				| `Arrays::asList`		|
|`Supplier<T>`		|`T get()`				| 인수를 받지않고 값을 반환, 제공하는 함수	| `Instant::now`		|
|`Consumer<T>`		|`void accept(T t)`		| 한 개의 인수를 받고 반환값이 없는 함수	| `System.out::println`	|

* 기본 인터페이스는 기본 타입인 int, long, double용으로 각 3개씩 변형이 있다.
* 표준 함수형 인터페이스는 대부분 기본 타입만 지원한다. 

### **직접 구현**
표준 함수형 인터페이스 중 필요한 용도에 맞는게 없다면 직접 구현해야한다.

* 자주 쓰이며, 이름 자체가 용도를 명확히 설명
* 반드시 지켜야할 규약이 있음
* 유용한 디폴트 메서드를 제공할 수 있음

3가지 이유 중 하나 이상을 만족한다면 전용 함수형 인터페이스를 구현할지 고민해보는 것이 좋다.

* **`Comparator` 인터페이스**
	```java
	// 직접 구현 함수형 인터페이스
	@FunctionalInterface
	public interface Comparator<T> { int compare(T o1, T o2); }
	```

* **`BiFunction<T, U>`**
	```java
	// 표준 함수형 인터페이스
	@FunctionalInterface 
	public interface ToIntBiFunction<T, U> { int applyAsInt(T t, U u); }
	```

### **@FunctionInterface**

@FunctionInterface 어노테이션은 프로그래머의 의도를 명시하는 것으로 3가지 목적이 있다.

* 해당 인터페이스가 람다용으로 설계된 것임을 명시
* 해당 인터페이스가 추상 메서드를 오직 한개만 가지고 있어야 컴파일 가능
* 유지보수 과정에서 누군가 실수로 메서드를 추가하지 못하게 막아줌

즉, 직접 만든 함수형 인터페이스에는 항상 @FunctionInterface 어노테이션을 붙여줘야한다.


---------------------------------------------------------------

## item 45. 스트림은 주의해서 사용하라

[[TOC]](#목차)

* __스트림 (Stream)__ : 데이터 원소의 유한/무한 시퀀스를 뜻함
* __스트림 파이프라인 (Stream Pipeline)__ : 원소들로 수행하는 연산 단계를 표현

### **대표적인 스트림 소스**
* 컬렉션 (Collection)
* 배열 (Array)
* 파일 (File)
* 정규표현식 (Regex Pattern Matcher)
* 난수생성기 (Random Generator)
* 기본 스트림
	- IntStream
	- LongStream
	- DoubleStream

### **스트림 연산**
> 소스 스트림에서 시작해 __종단연산__ 으로 끝나며, 그 사이에 하나 이상의 __중간연산__ 이 있을 수 있다.

* __중간 연산 ([intermediate operation](https://www.javacodegeeks.com/2020/04/java-8-stream-intermediate-operations-methods-examples.html))__
	| 중간 연산 함수 									| 설명											|
	| ------------------------------------------------- | --------------------------------------------	|
	| `filter(Predicate<? super T> predicate)`			| predicate 함수에 맞는 요소만 사용하도록 필터	|
	| `map(Function<? Super T, ? extends R> func)`		| 요소 각각의 function 적용						|
	| `flatMap(Function<? Super T, ? extends R> func)`	| 스트림의 스트림을 하나의 스트림으로 변환		|
	| `distinct()`										| 스트림 내의 중복 요소 제거					|
	| `sorted()`										| 기본 정렬										|
	| `sorted(Comparator<? super T> comparator)`		| comparator 함수를 이용하여 정렬				|
	| `peek(Consumer<? super T> action)`				| 각각의 요소를 대상으로 특정 연산을 수행		|
	| `limit(long maxSize)`								| maxSize 갯수만큼만 출력						|
	| `skip(long n)`									| n개 만큼의 스트림 요소 건너뜀					|

	```java
	// filter()
	IntStream sub1 = IntStream.of(1, 2, 3, 4, 5).filter(val -> val > 3);
	System.out.println(sub1.count());	// 2

	// map()
	Stream inp2 = Stream.of("Welcome", "To", "java", "blog");
	Stream sub2 = inp2.map(str -> { return (str.equals("java"))? "Insjang" : str; });
	System.out.println(sub2.collect(Collectors.toList())); // [Welcome, To, Insjang, blog]

	// flatMap()
	String[] arr = {"I study hard", "You study JAVA", "I am hungry"};
	Stream<String> inp3 = Arrays.stream(arr);
	inp3.flatMap(s -> Stream.of(s.split(" "))).distinct().forEach(System.out::println);

	// Stream sorted()
	Stream.of("[tomoto]", "[Green Chilli]", "[Pototo]", "[Beet root]")
		.sorted().forEach(System.out::print); // [Beet root][Green Chilli][Pototo][tomoto]
		
	// Stream limit​(long maxSize)
	Stream.of("[one]", "[two]", "[three]", "[four]")
		.limit(2).forEach(System.out::print); // [one][two]
	
	// Stream skip​(long n)
	Stream.of("[one]", "[two]", "[three]", "[four]", "[five]")
		.skip(2).forEach(System.out::print); // [three][four][five]

	```

* __종단 연산 (terminal operation)__

	| 종단 연산 함수                            | 설명                                              |
	| ----------------------------------------- | ------------------------------------------------- |
	| `forEach(Consumer<? super T> consumer)`   | Stream의 요소를 순회                              |
	| `reduce(BinaryOperator<T> accu)`			| 요소를 처리하여 하나의 결과로 만듦				|
	| `collect(Collector<? super T,A,R> coll)`	| 연산 결과를 컬렉션 형태로 모아줌					|
	| `allMatch(Predicate<? super T> predi)`    | 모든 요소가 predi함수에 만족하면 true             |
	| `anyMatch(Predicate<? super T> predi)`    | 하나의 요소라도 predi함수에 만족하면 true         |
	| `noneMatch(Predicate<? super T> predi)`   | 모든 요소가 predi함수에 만족 안하면 true          |
	| `count()`                                 | 요소 수 반환                                      |
	| `max(Comparator<? super T> comp)`         | 최대 값 반환 (Optional 반환)                      |
	| `min(Comparator<? super T> comp)`         | 최소 값 반환 (Optional 반환)                      |
	| `sum()`                                   | 요소의 합 (IntStream, LongStream, DoubleStream)   |
	| `average()`                               | 요소의 평균 (Optional 반환)                       |



	```java
		Stream.of("[넷]", "[둘]", "[셋]", "[하나]").forEach(System.out::println);
		Stream.of("[넷]", "[둘]", "[셋]", "[하나]")
			.reduce((s1, s2) -> s1 + "++" + s2).ifPresent(System.out::println);
		System.out.println(Stream.of("[넷]", "[둘]", "[셋]", "[하나]").reduce("시작", (s1, s2) -> s1 + "++" + s2));
	
		System.out.println(IntStream.of(30, 90, 70, 10).anyMatch(n -> n > 80)); // true
		System.out.println(IntStream.of(30, 90, 70, 10).allMatch(n -> n > 80)); // false
	
		System.out.println(IntStream.of(30, 90, 70, 10).count()); // 4
		System.out.println(IntStream.of(30, 90, 70, 10).sum()); // 200
	
		System.out.println(IntStream.of(30, 90, 70, 10).max().getAsInt()); // 90
		System.out.println(DoubleStream.of(30.3, 90.9, 70.7, 10.1).average().getAsDouble()); // 50.5
	
		Map<Boolean, List<String>> part = Stream.of("HTML", "CSS", "JAVA", "PHP")
						.collect(Collectors.partitioningBy(s -> (s.length() % 2) == 0));
		List<String> oddlst = part.get(false); System.out.println(oddlst); // [CSS, PHP]
		List<String> evenlst = part.get(true); System.out.println(evenlst); // [HTML, JAVA]
	```

### **지연 평가(Lazy evaluation)**
* 평가는 종단연산이 호출될 때 진행되며, 종단연산에 사용되지 않은 데이터원소는 계산에 안쓰임
* 지연평가는 무한스트림을 다룰 수 있게 해주는 핵심
* 종단연산이 없는 스트림 파이프라인은 아무 일도 하지 않는 명령어인 `no-op`과 같음

### **가독성**
Stream을 적절히 활용한 코드
```java
	public class Anagrams {
		public static void main(String[] args) {
			Path dictionary = Paths.get(args[0]);
			int minGroupSize = Integer.parseInt(args[1]);

			try (Stream<String> words = Files.lines(dictionary)) {
				words.collect(groupingBy(word -> alphabetize(word)))
					.values().stream()
					.filter(group -> group.size() >= minGroupSize)
					.forEach(g -> System.out.println(g.size() + ": " + g));
			}
		}

		private static String alphabetize(String s) {
			char[] a = s.toCharArray();
			Arrays.sort(a);
			return new String(a);
		}
	}
```

### **코드 블록 vs 람다 블록**

* 코드블록
	- 범위 안의 지역변수를 읽고 수정 가능
	- `return`, `break`, `continue` 사용 가능
	- 메서드 선언에 명시된 검사 예외(Exception)를 던질 수 있음
* 람다블록
	- `final` 이거나 사실상 `final`인 변수만 읽을 수 있으며, 지역변수를 수정하는건 불가능
	- `return`, `break`, `continue` 문 사용 불가능
	- 명시된 검사 예외 던지는것 불가능

### **스트림을 사용하기 좋은 경우**
* 원소들의 시퀀스를 일관되게 변환
* 원소들의 시퀀스를 필터링
* 원소들의 시퀀스를 하나의 연산을 사용하여 결합(더하기, 최솟값 구하기 등)
* 원소들의 시퀀스를 컬렉션에 모으는 경우
* 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는 경우1


---------------------------------------------------------------

## item 46. 스트림에서는 부작용 없는 함수를 사용하라

[[TOC]](#목차)

### **스트림 패러다임**
* 스트림 패러다임의 핵심은 __계산을 일련의 변환으로 재구성하는 부분__ 이다. 
* 각 변환 단계는 가능한 이전 단계의 결과를 받아 처리하는 순수 함수여야한다.
	> 순수함수 : 오직 입력만이 결과에 영향을 주는 함수 



### **스트림을 제대로 활용한 코드**

`forEach` : 스트림이 수행한 연산 결과를 보여줄 때 사용하고, 계산할 때는 사용하지 말자

```java
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class I46_Stream {
	public static void main(String[] args) throws FileNotFoundException {
		File file = new File("D:/temp/README.txt");

		// [스트림 패러다임은 이해하지 못한채 API만 사용한 예]
		// - forEach 내부에서 외부상태(freq)를 수정하는 람다를 실행하고 있으므로 순수함수가 아님
		// - 같은 기능의 반복문보다 코드가 길고, 읽기 어려우며, 유지보수에도 나쁨
//		Map<String, Long> freq = new HashMap<>();
//		try (Stream<String> words = new Scanner(file).tokens()) {
//			words.forEach(word -> { freq.merge(word.toLowerCase(), 1L, Long::sum); });
//		}

		Map<String, Long> freq;
		try (Stream<String> words = new Scanner(file).tokens()) {
			freq = words.collect(groupingBy(String::toLowerCase, counting()));
		}
		System.out.println(freq);

		List<String> topTen = freq.keySet().stream() 
			.sorted(comparing(freq::get).reversed()) 
			.limit(10).collect(toList());

		topTen.stream().forEach(System.out::println);
	}
}
```

### **java.util.stream.Collectors**

* `java.util.stream.Collectors` : 자주 사용하는 API 제공
	- `Collectors` 멤버를 `static import` 사용해서 스트림 가독성이 좋아짐
* 스트림의 원소를 손쉽게 컬렉션으로 생성 가능
* 최종 처리 (스트림 종료 작업)

#### **toList()**
```java
List<String> topTen = freq.keySet().stream()
	.sorted(comparing(freq::get).reversed()) // Comparator.comparing
	.limit(10)
	.collect(toList()); // List 형태로 반환
```

#### **toMap()**
```java
Collector<T,?,Map<K,U>> toMap(
	Function<? super T, ? extends K> keyMapper, 
	Function<? super T, ? extends U> valueMapper)
```
* toMap수집기를 사용하여 문자열을 열거 타입 상수에 매핑
	```java
	private static final Map<String, Operation> stringToEnum = 
		Stream.of(values()).collect(toMap(Object::toString, e -> e));
	```
* 각 키와 해당 키의 특정 원소를 연관 짓는 맵을 생성하는 수집기
	```java
	Map<Artist, Album> topHits = albums.collect(
		toMap(Album::artist, a->a, maxBy(comparing(Album::sales))));
	```
* 마지막에 쓴 값을 취하는 수집기
	```java
	toMap(keyMapper, valueMapper, (oldVal, newVal) -> newVal);
	```

* 네번째 인수로 맵 팩터리(EnumMap, TreeMap, HashMap)를 받는 toMap
	```java
	Stream<String> s = Stream.of("apple", "banana", "apricot", "orange", "apple");
	LinkedHashMap<Character, String> m = s.collect(
		Collectors.toMap(
			s1 -> s1.charAt(0), 
			s1 -> s1, 
			(s1, s2) -> s1 + "|" + s2, 
			LinkedHashMap::new
		)
	);
	```

#### **groupingBy()**

* 입력으로 분류 함수(classifier)를 받고 출력으로 원소들을 카테고리별로 모아 놓은 맵을 담은 수집기 반환
	```java
	Collector<T,?,Map<K,List<T>>> groupingBy(
		Function<? super T, ? extends K> classifier)
	```

	```java
	List<Product> productList = Arrays.asList(
		new Product(23, "potatoes"),
		new Product(14, "orange"),
		new Product(13, "lemon"),
		new Product(23, "bread"),
		new Product(13, "sugar"));
	
	Map<Integer, List<Product>> collectorMapOfLists = productList.stream()
		.collect(Collectors.groupingBy(Product::getAmount));
	```

* groupingBy가 반환하는 수집기(collector)가 리스트 외의 값을 갖는 맵을 생성하게 할 때 사용
	```java
	Collector<T, ?, Map<K,D>> groupingBy(
		Function<? super T, ? extends K> classifier, 
		Collector<? super T, A, D> downstream)
	```
	- 다운스트림 수집기(해당 카테고리의 모든 원소를 담은 스트림으로부터 값을 생성)도 명시 필요
	- toSet() : 원소들의 리스트가 아닌 집합(Set)을 값으로 갖는 맵 생성
	- toCollection(collectionFactory) : 컬렉션을 값으로 갖는 맵 생성
	- counting() : 각 키를 해당 키에 속하는 원소의 개수와 매핑한 맵 생성
		```java
		Map<String, Long> freq = words.collect(groupingBy(String::toLowerCase, counting()));
		```

* 다운스트림 수집기에 더해 맵 팩터리도 지정 : 맵과 그 안에 담긴 컬렉션 타입 모두 지정 가능
	```java
	Collector<T, ?, M> groupingBy(
		Function<? super T, ? extends K> classifier, 
		Supplier<M> mapFactory, 
		Collector<? super T,A,D> downstream)

	```

* groupingByConcurrent : 위 3개 groupingBy에 각각 대응하는 메서드의 동시 수행 버전, ConcurrentHashMap 인스턴스 생성


#### **partitioningBy()**
분류 함수 자리에 `Predicate` 를 받고, 키가 `Boolean` 인 맵을 반환
```java
Stream<String> stream = Stream.of("HTML", "CSS", "JAVA", "PHP");
 
Map<Boolean, List<String>> patition = stream.collect(
	Collectors.partitioningBy(s -> (s.length() % 2) == 0)
);
 
List<String> oddLengthList = patition.get(false);
System.out.println(oddLengthList);
 
List<String> evenLengthList = patition.get(true);
System.out.println(evenLengthList);
```

#### **minBy(), maxBy()**
인수로 받은 비교자를 이용해 스트림에서 값이 가장 작은/큰 원소를 찾아 반환

#### **joining()**
`CharSequence` 인스턴스의 스트림에만 적용 가능
```java
String listToString = productList.stream()
	.map(Product::getName)
	.collect(Collectors.joining(", ", "<", ">"));
```
* 매개변수가 없는 `joining`은 단순히 원소들을 연결하는 수집기를 반환
* 1개 인자를 넣으면 구분자를 추가한 문자열 생성
* 인수 3개 : 접두문자( `[` ) + 구분문자( `,` )  + 접미문자( `]` )
	> ex) [came, saw, conquered]로 출력


---------------------------------------------------------------

## item 47. 반환 타입으로는 스트림보다 컬렉션이 낫다

[[TOC]](#목차)

### **자바8 이전 목록 자료구조 반환 타입**
* __`Collection` 인터페이스__ : 
	> 기본적으로 사용 (`Collection`, `Set`, `List`)
* __`E[]` 와 같은 배열__ :
	> 반환 원소가 기본 타입이거나, 성능이 중요시될 때 사용
* __`Iterable` 인터페이스__ :
	> `for-each`문에서만 사용되거나, 일부 `Collection`을 구현할 수 없을 때 사용

### **Stream 반복문**
`Stream` 은 반복(Iterable)을 지원하지 않기 때문에 `for-each`를 사용하기 위해서 다음 방식을 사용
* `Stream::iterator()` 사용
* `Stream -> Iterable` 어댑터
* `Iterable -> Stream` 어댑터


```java
public class I47_StreamIter {
	// Stream -> Iterable 어댑터
	public static <E> Iterable<E> iterableOf(Stream<E> stream) {
		return stream::iterator;
	}

	// Iterable -> Stream 어댑터
	public static <E> Stream<E> streamOf(Iterable<E> iterable){
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	public static void main(String[] args) {
		Stream<String> dev2u = Stream.of("장인순", "이민승", "최혜환", "이규명");

		System.out.println("\n1. Stream iterator() 사용");
		for (String member : (Iterable<String>) dev2u::iterator) { // 형변환
			System.out.println(member);
		}

		// [빈번한 실수] 스트림은 재사용할 수 없다. 오직 한번만 사용
		// Stream dev2u 재사용하면 다음과 같은 오류 발생
		//	--> java.lang.IllegalStateException: stream has already been operated upon or closed
		Stream<String> dev4u = Stream.of("이강원", "최유빈");
		System.out.println("\n2. Stream -> Iterable 어댑터");
		for (String member : iterableOf(dev4u)) { // 어댑터 메소드 사용
			System.out.println(member);
		}

		Iterable<String> dev8u = Arrays.asList("장인순", "이민승", "최혜환", "이규명", "이강원", "최유빈");
		System.out.println("\n3. Iterable -> Stream 어댑터");
		streamOf(dev8u).forEach(System.out::println);
	}
}
```
### **전용 컬렉션 구현**

```java

```

```java

```

```java

```

```java

```


---------------------------------------------------------------

## item 48. 스트림 병렬화는 주의해서 적용하라

[[TOC]](#목차)

### **gggg**

```java

```

```java

```

```java

```

```java

```


---------------------------------------------------------------

[[TOC]](#목차)


... 7장 끝 ...

[<<< 6장 [이전]](../ch06/README.md) ----- [[TOC]](#목차) -----  [[다음] 8장 >>>](../ch08/README.md)
