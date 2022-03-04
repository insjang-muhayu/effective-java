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

## item 46. 스트림에서는 부작용 없는 함수를 사용하라

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

## item 47. 반환 타입으로는 스트림보다 컬렉션이 낫다

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
