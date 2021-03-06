# 6장. EnumType과 Annotation

## 목차

- [x] item 34. [int 상수 대신 EnumType을 사용해라](#item-34-int-상수-대신-enumtype을-사용해라)
- [x] item 35. [ordinal 메서드 대신 인스턴스 필드를 사용해라](#item-35-ordinal-메서드-대신-인스턴스-필드를-사용해라)
- [x] item 36. [Bit 필드 대신 EnumSet을 사용해라](#item-36-bit-필드-대신-enumset을-사용해라)
- [x] item 37. [ordinal 인덱싱 대신 EnumMap을 사용해라](#item-37-ordinal-인덱싱-대신-enummap을-사용해라)
- [x] item 38. [확장할 수 있는 EnumType이 필요하면 Interface를 사용해라](#item-38-확장할-수-있는-enumtype이-필요하면-interface를-사용해라)
- [x] item 39. [명명 패턴보다 Annotation을 사용해라](#item-39-명명-패턴보다-annotation을-사용해라)
- [x] item 40. [@Override Annotation을 일관되게 사용해라](#item-40-override-annotation을-일관되게-사용해라)
- [x] item 41. [정의하려는 것이 타입이라면 Marker Interface를 사용해라](#item-41-정의하려는-것이-타입이라면-marker-interface를-사용해라)


---------------------------------------------------------------

## item 34. int 상수 대신 EnumType을 사용해라

[[TOC]](#목차)

* 필요한 원소를 컴파일타임에 알 수 있는 상수집합이라면 항상 `EnumType`을 사용하자
* `EnumType`에 정의된 상수개수가 영원히 고정불변일 필요는 없다.

### __상수__

```java
static final 타입 상수 [=초기값];
static final 타입 상수;
static { 상수 = 초기값; }
```

### __정수 열거 패턴(int enum pattern)__

```java
public static final int APPLE_FUJI = 0;
```

### __문자열 열거 패턴(string enum pattern)__

```java
public static final String APPLE_FUJI = "apple fuji";
```

### __열거 타입(enumeration type)__

```java
public enum Week {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, ...};
```
* `EnumType` 자체는 클래스이며, 개별 상수 당 인스턴스를 만들어 `public static final` 필드로 공개
* `EnumType`은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로, 사실상 `final`
* 클라이언트가 인스턴스를 직접 생성할 수 없으므로, `EnumType` 인스턴스들은 단일 존재함
	> `Singleton`은 원소가 하나뿐인 `EnumType`이고, `EnumType`은 싱글턴을 일반화한 형태
* `EnumType`은 컴파일타임에서의 __타입 안전성을 제공__
* 각자의 이름공간이 있으며, `EnumType`에 상수를 추가하거나 순서를 바꿔도, 컴파일 불필요
* `EnumType`의 `toString`은 출력하기에 적합한 문자열을 제공함
* `EnumType`에는 메서드나 필드를 추가할 수 있고, 인터페이스를 구현할 수 있음

#### __[ 데이터와 메서드를 갖는 열거타입 ]__

```java
public enum Planet {
	MERCURY(3.302e+23, 2.439e6),
	VENUS(4.869e+24, 6.052e6),
	EARTH(5.975e+24, 6.378e6);

	private final double mass;           // 질량
	private final double raduis;         // 반지름
	private final double surfaceGravity; // 표면중력

	private static final double G = 6.67300E-11; // 중력상수

	Planet(double mass, double raduis) { // 생성자(질량, 반지름)
		this.mass = mass;
		this.raduis = raduis;
		this.surfaceGravity = G * mass / (raduis * raduis);
	}

	public double mass() { return mass; }
	public double radius() { return raduis; }
	public double surfaceGravity() { return surfaceGravity; }
	public double surfaceWeight(double mass) {
		return mass * surfaceGravity; // F = ma
	}
}
```

__`EnumType` 상수 각각을 특정 데이터와 연결지으려면,__
* 생성자에 데이터를 받아 인스턴스 필드에 저장하면된다.
* `EnumType`은 근본적으로 불변이라 모든 필드는 `final` 이어야한다.
* 필드를 `private`으로 두고 별도의 `public` 접근자 메서드를 두는 것이 낫다.

```java
public class WeightTable {
	public static void main(String[] args) {
		double earthWeight = Double.parseDouble("185");
		double mass = earthWeight / Planet.EARTH.surfaceGravity();
		for (Planet p : Planet.values()) {
			System.out.printf("%s에서의 무게는 %f이다.%n", p, p.surfaceWeight(mass));
		}
		// MERCURY에서의 무게는 69.912739이다.
		// VENUS에서의 무게는 167.434436이다.
		// EARTH에서의 무게는 185.000000이다.
	}
}
```

#### __[ 상수별 메서드 구현 ]__
```java
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Operation {
	PLUS("+")    { public double apply(double x, double y) { return x + y; } },
	MINUS("-")   { public double apply(double x, double y) { return x - y; } },
	TIMES("*")   { public double apply(double x, double y) { return x * y; } },
	DIVIDE("/")  { public double apply(double x, double y) { return x / y; } };

	private final String symbol;

	Operation(String symbol) { this.symbol = symbol; }

	@Override public String toString() { return symbol; }
	public abstract double apply(double x, double y);

	// 열거타입 상수 생성 후 정적필드가 초기화될 때 추가됨
	private static final Map<String, Operation> stringToEnum =
		Stream.of(values()).collect(Collectors.toMap(Object::toString, e->e));

	public static Optional<Operation> fromString(String symbol) {
		// 주어진 연산이 가리키는 상수가 존재하지 않을 수 있음
		return Optional.ofNullable(stringToEnum.get(symbol));
	}
}
```

#### __[ 전략 열거 타입 패턴 ]__
```java
import static ch06.item34.PayrollDay.PayType.*;

enum PayrollDay {
	MON(WKDAY), TUE(WKDAY), WED(WKDAY), THU(WKDAY), FRI(WKDAY), SAT(WKEND), SUN(WKEND);
	private final PayType payType;

	PayrollDay(PayType payType) { this.payType = payType; }
	int pay(int minutesWorked, int payRate) {
		return payType.pay(minutesWorked, payRate);
	}

	enum PayType {
		WKDAY {	int overtimePay(int minsWorked, int payRate) {
			return minsWorked <= MINS_SHIFT ? 0 : (minsWorked - MINS_SHIFT) * payRate / 2; }},
		WKEND {	int overtimePay(int minsWorked, int payRate) {
			return minsWorked * payRate / 2; }};

		abstract int overtimePay(int minsWorked, int payRate);
		private static final int MINS_SHIFT = 8 * 60;

		public int pay(int minsWorked, int payRate){
			int basePay = minsWorked * payRate;
			return basePay + overtimePay(minsWorked, payRate);
		}
	}
}
```

#### __[ Switch문이 적합한 경우 ]__
기존 `EnumType`에 상수별 동작을 혼합해서 넣는 경우에는 `switch`문이 더 좋은 선택이 될 수 있다.
```java
public static Operation inverse(Operation op) {
	switch (op) {
		case PLUS:      return Operation.MINUS;
		case MINUS:     return Operation.PLUS;
		case TIMES:     return Operation.DIVIDE;
		case DIVIDE:    return Operation.TIMES;
		default: throw new AssertionError("알 수 없는 연산: " + this);
	}
}
```


---------------------------------------------------------------

## item 35. ordinal 메서드 대신 인스턴스 필드를 사용해라

[[TOC]](#목차)

* 대부분의 열거타입 상수는 하나의 정숫값에 대응된다.
* 모든 열거타입 상수는 열거타입에서 몇 번째 위치인지를 반환하는 `ordinal` 메서드를 제공
	> __`ordinal()` 을 잘못 사용하는 경우 : 실용성이 떨어짐__

	```java
	public enum Esemble {
		SOLO, DUET, TRIO, QUARTET, QUINTET, SEXTET, SEPTET, OCTET, NONET, DECTET;
		// ordinal을 잘못 사용한 경우
		public int numberOfMusicians() { return ordinal() + 1; }
	}
	```
* 해결방법
	> __열거 타입 상수에 연결된 값은 ordinal 메서드로 얻지말고 인스턴스 필드에 저장__
	```java
	public enum Esemble {
		SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5), SEXTET(6),
		SEPTET(7), OCTET(8), DOUBLE_QUARTET(8), NONET(9), DECTET(10), TRIPLE_QUARTET(12);

		private final int numberOfMusicians;
		Esemble(int size) { this.numberOfMusicians = size; }
		public int numberOfMusicians() { return numberOfMusicians; }
	}
	```
`Enum` API문서를 보면
> 대부분 프로그래머는 이 메서드를 사용할 일이 없다.
> 이 메서드는 `EnumSet`과 `EnumMap` 같이 열거타입기반의 범용자료구조에 쓸 목적으로 설계되었다.

__이러한 용도가 아니라면, `ordinal` 메서드는 절대로 사용하지 말아야한다.__


---------------------------------------------------------------

## item 36. Bit 필드 대신 EnumSet을 사용해라

[[TOC]](#목차)

열거한 값들이 집합으로 사용될 경우, 이전에는 비트 필드 열거 상수를 사용했다.

* __Bit 필드 열거 상수 : 예전 방식__
	```java
	public class Text {
		public static final int STYLE_BOLD          = 1 << 0; // 1
		public static final int STYLE_ITALIC        = 1 << 1; // 2
		public static final int STYLE_UNDERLINE     = 1 << 2; // 4
		public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8

		// 매개변수 syltes는 0개 이상의 STYLE_ 상수를 비트별 OR한 값
		public void applyStyles(int styles) { ... }
	}
	```
	> `text.applyStyles(STYLE_BOLD | STYLE_UNDERLINE);`

* __`EnumSet` - Bit 필드를 대체하는 최신 방식__
	```java
	import java.util.Set;
	public class NewText {
		public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }
		// 어떤 Set을 넘겨도 되나, EnumSet이 가장 좋음
		public void applyStyles(Set<Style> styles) { ... }
	}
	```
	> `text.applyStyles(EnumSet.of(Style.BOLD, Style.UNDERLINE));
`
	- `EnumSet` 클래스는 열거타입 상수값으로 구성된 집합을 효과적으로 표현
	- `Set` 인터페이스를 완벽히 구현
	- 타입 안전
	- 다른 어떠한 `Set` 구현체와도 함꼐 사용 가능

* __`EnumSet`의 유일한 단점 : 불변 `EnumSet`을 만들 수 없다 (자바 11까지도 미지원)__


---------------------------------------------------------------

## item 37. ordinal 인덱싱 대신 EnumMap을 사용해라

[[TOC]](#목차)

```java
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Plant {
	enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

	final String name;
	final LifeCycle lifeCycle;

	@Override public String toString() { return name; }

	public static void main(String[] args) {
		List<Plant> garden = new ArrayList<>();
		garden.add(new Plant("호도", LifeCycle.ANNUAL));
		garden.add(new Plant("땅콩", LifeCycle.ANNUAL));
		garden.add(new Plant("쌀", LifeCycle.PERENNIAL));
		// ...
	}
}
```

### __ordinal() 기반 배열 인덱싱 문제점__

```java
Set<Plant>[] pSets = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];

for (int i = 0; i < pSets.length; i++) pSets[i] = new HashSet<>();
for (Plant p : garden) pSets[p.lifeCycle.ordinal()].add(p);

for (int i = 0; i < pSets.length; i++)
	System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], pSets[i]);
```

* 배열은 제네릭과 호환되지 않아 비검사 형변환 오류로 컴파일이 안됨
	> `@SuppressWarnings("unchecked") Set<Plant>[] pSets = (Set<Plant>[]) new Set[...]`;
* 배열은 인덱스 의미를 모르니 출력결과에 레이블을 달아야 함
	> `Plant.LifeCycle.values()[i]`
* ordinal()은 상수 선언 순서에 따라 변한다.
* 잘못된 값을 사용하면 이상한 동작을 유발한다.
	> 운이 좋다면 `ArrayIndexOutOfBoundsException`을 던질 것이다.

### __EnumMap을 사용해 매핑__

열거타입을 키로 사용하도록 설계된 EnumMap을 사용해 문제점을 해결

```java
Map<Plant.LifeCycle, Set<Plant>> pMaps = new EnumMap<>(Plant.LifeCycle.class);

for (Plant.LifeCycle lc : Plant.LifeCycle.values()) pMaps.put(lc, new HashSet<>());
for (Plant p : garden) pMaps.get(p.lifeCycle).add(p);

System.out.println(pMaps);
```
* 타입 안전성 확보, 간단 명료한 코드, 배열과 성능도 비슷
* `Map` 키를 열거타입의 출력문자열로 제공하므로, 출력결과에 별도의 formatting 불필요
* 배열 인덱스를 계산하는 과정에서 오류가 날 가능성도 원천봉쇄
* `EnumMap` 내부에서 `ordinal`을 사용한 배열을 사용하기 때문에 배열과 성능이 비슷
* 개발자가 직접 제어하지 않고 Map을 사용하여, 타입안정성을 얻을 뿐더러 성능상의 이점까지 그대로 가져간다.

### __Stream을 이용한 코드__

* __EnumMap 미사용__
	```java
	System.out.println(garden.stream().collect(
		Collectors.groupingBy(p -> p.lifeCycle) // 고유 맵 구현체
	));
	```
	> `EnumMap`이 아닌 `Map` 구현체를 사용했기 때문에 `EnumMap`을 써서 얻은 공간과 성능 이점이 사라지는 문제가 있다.

* __EnumMap 사용__
	```java
	System.out.println(garden.stream().collect(
		Collectors.groupingBy(
			p -> p.lifeCycle,
			() -> new EnumMap<>(LifeCycle.class), // 원하는 맵 구현체 명시
			Collectors.toSet()
		)
	));
	```
* `EnumMap` vs `Stream`
	* `EnumMap`을 이용한 방식 : garden의 모든 키가 생성
	* `Stream`을 이용한 방식 : garden의 존재하는 키만 생성

	```java
	======================== ordinal 기반 배열 인덱싱
	ANNUAL: [땅콩, 호도]
	PERENNIAL: [쌀]
	BIENNIAL: []
	======================= EnumMap을 사용해 매핑
	{ANNUAL=[땅콩, 호도], PERENNIAL=[쌀], BIENNIAL=[]}
	======================= [Stream] EnumMap 미사용
	{PERENNIAL=[쌀], ANNUAL=[호도, 땅콩]}
	======================= [Stream] EnumMap 사용
	{ANNUAL=[땅콩, 호도], PERENNIAL=[쌀]}
	```

### __중첩 EnumMap__

* EnumMap을 이용
	```java
	import lombok.RequiredArgsConstructor;

	public enum Phase {
		SOLID, LIQUID, GAS;

		@RequiredArgsConstructor
		public enum Transition {
			MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
			BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
			SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

			private final Phase from;
			private final Phase to;

			// 이전상태에서 '이후상태에서 전이로의 맵'에 대응하는 맵
			private static final Map<Phase, Map<Phase, Transition>> m
				= Stream.of(values()).collect(Collectors.groupingBy(
					t -> t.from,
					() -> new EnumMap<>(Phase.class),
					Collectors.toMap(
						t -> t.to, // key-mapper
						t -> t, // value-mapper : 자기자신 참조
						(x, y) -> y, // merge-function
						() -> new EnumMap<>(Phase.class) // 내부 Map 선언
					)
				));

			public static Transition from(Phase from, Phase to) {
				return m.get(from).get(to);
			}
		}
	}
	```

* 새로운 Phase가 추가되는 경우

	```java
	public enum Phase {
		SOLID, LIQUID, GAS, PLASMA; // PLASMA 추가!!!

		@RequiredArgsConstructor
		public enum Transition {
			MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
			BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
			SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
			IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS); // 추가!!!

			// ... 동일 ...
		}
	}
	```


---------------------------------------------------------------

## item 38. 확장할 수 있는 EnumType이 필요하면 Interface를 사용해라

[[TOC]](#목차)

### __인터페이스 활용 확장 열거타입을 흉내 낸다.__

```java
// 인터페이스 정의
public interface Oper {
	double apply(double x, double y);

	@RequiredArgsConstructor
	public enum BasicOper implements Oper {
		PLUS("+") { public double apply(double x, double y) { return x + y; } },
		MINUS("-") { public double apply(double x, double y) { return x - y; } },
		TIMES("*") { public double apply(double x, double y) { return x * y; } },
		DIVIDE("/") { public double apply(double x, double y) { return x / y; } };

		private final String symbol;

		@Override public String toString() { return symbol; }
	}
}
```

```java
@RequiredArgsConstructor
public enum ExtendedOper implements Oper {
	EXP("^") {
		public double apply(double x, double y) { return Math.pow(x, y); }
	},
	REMAINDER("%") {
		public double apply(double x, double y) { return x % y; }
	};

	private final String symbol;

	@Override public String toString() { return symbol; }
}
```

### __타입수준에서의 확장된 열거타입__
```java
public class ImplementsEnumTest {
	@Test
	void extendedOperTest() {
		double x = 4.0; double y = 2.0;

		test1(ExtendedOper.class, x, y);
		test2(Arrays.asList(ExtendedOper.values()), x, y);
	}

	// `Class 객체`를 넘기는 방법
	private static <T extends Enum<T> & Oper> void test1(Class<T> opEnumType, double x, double y) {
		for (Oper op : opEnumType.getEnumConstants()) {
			System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
		}
	}

	// `Collection<? extends Oper>`을 넘기는 방법
	private static void test2(Collection<? extends Oper> opSet, double x, double y) {
		for (Oper op : opSet) {
			System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
		}
	}
}
```
* `Class 객체`를 넘기는 방법
* `Collection<? extends Oper>`을 넘기는 방법

### 단점
* 인터페이스를 이용해 확장 가능한 열거타입을 구현하는 방법에는 __"열거 타입끼리 구현을 상속할수 없다"__ 
* 코드중복 제거방법 (확장 Enum 타입끼리 로직을 공유하려 할 때) :
	- 별도의 **helper class** 작성
	- **static helper method**로 분리
	- 디폴트 메서드(item-20) : 아무 상태에도 의존하지 않는 경우

---------------------------------------------------------------

## item 39. 명명 패턴보다 Annotation을 사용해라

[[TOC]](#목차)

### 명명패턴 (Naming Patterns)

* 기존에는 도구나 프레임워크가 다뤄야할 프로그램 요소에 대해 **명명패턴**을 통해 구분하고자 함
* 예) JUnit 3에서 테스트 메서드 이름을 `testXxxx` 형식으로 사용
* 명명패턴 단점 :
	- 오타가 나면 안됨 (패턴규칙에 벋어나면 안됨)
	- 올바른 프로그램 요소에서만 사용된다고 보장 못함
	- 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없음
	- 컴파일러는 문자열이 무엇을 가리키는지 모름

### 어노테이션 (Annotations)

명명패턴의 단점을 해결하고자 **Annotation** 등장 (JUnit 4부터 어노테이션 전면 도입)

#### [메타 어노테이션]

```java
	import java.lang.annotation.*;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface I39_Test { }
```
**Meta-Annotation** : `@Retention`과 `@Target`과 같이 어노테이션 선언에 다는 어노테이션
* `@Retention(RetentionPolicy.RUNTIME)` : 런타임에도 유지됨
* `@Target(ElementType.METHOD)` : 메서드 선언에서만 사용

#### [마커 어노테이션]
**Marker-Annotation** : `@Test` 처럼 아무 매개변수 없이 단순히 대상에 마킹만 하는 어노테이션
* 대상 코드의 의미는 그대로 두고, 그 어노테이션에 관심 있는 도구에서 특별한 처리를 가능하게 해줌
* 실제 클래스에 영향은 주지 않으며, 어노테이션에 관심있는 프로그램에 추가 정보를 제공

```java
public class I39_Sample {
	@I39_Test public static void m1() { } // 성공
	public static void m2() { }
	@I39_Test public static void m3() { throw new RuntimeException("실패"); } // 실패
	public static void m4() { }
	@I39_Test public void m5() { } // 잘못 사용한 예: 정적 메서드가 아니다.
	public static void m6() { }
	@I39_Test public static void m7() { throw new RuntimeException("실패"); } // 실패
	public static void m8() { }
}
```

```java
import java.lang.reflect.*;

public class I39_RunTests {
	public static void main(String[] args) 
		throws ClassNotFoundException, InvocationTargetException, IllegalAccessException 
	{
		int tests = 0; int passed = 0;
		Class<?> testClass = Class.forName("study.effective.ch06.I39_Sample"); //(args[0]);
		for (Method m : testClass.getDeclaredMethods()) {
			if (m.isAnnotationPresent(I39_Test.class)) {
				tests++;
				try {
					m.invoke(null);
					passed++;
				} catch (InvocationTargetException wrappedExc) {
					Throwable exc = wrappedExc.getCause();
					System.out.println(m + "실패: " + exc);
				} catch (Exception exception) {
					System.out.println("잘못 사용한 @I39_Test: " + m);
				}
			}
		}
		System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
	}
}

// public static void study.effective.ch06.I39_Sample.m3()실패: java.lang.RuntimeException: 실패
// public static void study.effective.ch06.I39_Sample.m7()실패: java.lang.RuntimeException: 실패
// 잘못 사용한 @I39_Test: public void study.effective.ch06.I39_Sample.m5()
// 성공: 1, 실패: 3

```

#### __[단일 매개변수를 받는 어노테이션]__

```java
// 명시한 예외를 던저야만 성공하는 테스트 메서드 어노테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface I39_ExceptOne {
	Class<? extends Throwable> value();
}
```

#### __[배열 매개변수를 받는 어노테이션]__

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface I39_ExceptArr {
	Class<? extends Throwable> [] value();
}
```

```java
public class I39_ExceptRunTests {
	@I39_ExceptArr({IndexOutOfBoundsException.class, NullPointerException.class})
	public static void doublyBad() { // 성공해야 한다.
		List<String> list = new ArrayList<>();

		// 자바 API 명세에 따르면 다음 메서드는 
		// IndexOutOfBoundsException이나 NullPointerException을 던질 수 있다.
		list.addAll(5, null);
	}
}
```


#### __[반복 가능한 어노테이션 타입]__

* 자바 8에서는 여러 개의 값을 받는 애너테이션을 다른 방식으로도 만들 수 있다.
* 배열 매개변수를 사용하는 대신 애너테이션에 @Repeatable 메타애너테이션을 다는 방식이다.
* @Repeatable을 단 애너테이션은 하나의 프로그램 요소에 여러 번 달 수 있다.

**Repeatable 어노테이션**
```java
import java.lang.annotation.*;

/**
 * 명시한 예외를 던져야만 성공하는 테스트 메서드용 어노테이션
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class)
public @interface ExceptionTest {
	Class<? extends Throwable> value();
}
```

**Container Annotation**
```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer {
	ExceptionTest[] value();
}
```
```java
@ExceptionTest(IndexOutOfBoundsException.class)
@ExceptionTest(NullPointerException.class)
public static void doubleBad() {
	List<String> list = new ArrayList<>();
	// IndexOutOfBoundsException, NullPointerException
	list.addAll(5, null);
}
```

---------------------------------------------------------------

## item 40. @Override Annotation을 일관되게 사용해라

[[TOC]](#목차)

```java
package java.lang;
import java.lang.annotation.*;

@Target(ElementType.METHOD) // 메서드 선언에만 달 수 있음
@Retention(RetentionPolicy.SOURCE) // 소스코드(.java)까지 유지
public @interface Override { }
```
* `@Override`는 상위 타입 메서드를 재정의할 때 사용
* `@Override`를 일관되게 사용하면, 여러 버그 예방 가능

```java
public class Biagram {
	private final char first;
	private final char second;

	public Biagram(char first, char second) {
		this.first = first;
		this.second = second;
	}
	@Override public boolean equals(Biagram b) { // @Override로 버그예방
		return b.first == first && b.second == second;
	}
	@Override public int hashCode() { // @Override로 버그예방
		return 31 * first + second;
	}
	public static void main(String[] args) {
		Set<Biagram> s = new HashSet<>(); // Set이므로 중복 불허
		for (int i = 0; i < 10; i++) {
			for (char ch = 'a'; ch <= 'z'; ch++) {
				s.add(new Biagram(ch, ch));
			}
		}
		// @Override 안쓸경우 260 출력 (원하는 결과는 26)
		System.out.println(s.size());
	}
}
```

```
java: method does not override or implement a method from a supertype
```

`@Override`를 붙이게 되면 컴파일러는 해당 메서드가 상위메서드와 다르다는 것을 찾아 오류가 발생하며, 올바르게 수정할 수 있다.

__상위클래스의 메서드를 재정의하는 모든 메서드에 `@Override` 를 다는 것을 권장한다.__
단, 상위 클래스의 추성메서드는 굳이 `@Override`를 달지 않아도 된다.

__@Override 어노테이션 정리 :__
* 클래스, 인터페이스의 메서드를 재정의할 때 사용
* 시그니처가 올바른지 재차 확인 가능
* 컴파일타임에 오류 발견 가능
* 추상클래스에서는 상위클래스의 메서드를 재정의한 모든 메서드에 추가하는 것을 권장
* 추상 메서드는 굳이 어노테이션을 달지 않아도 됨


---------------------------------------------------------------

## item 41. 정의하려는 것이 타입이라면 Marker Interface를 사용해라

[[TOC]](#목차)


### __마커 인터페이스(marker interface)란?__
> 일반적인 인터페이스와 동일하지만, 아무 메서드도 없는 인터페이스 (단순 타입 체크용) 
> (Serializable, Cloneable, EventListener)

```java
  package java.io;
  public interface Serializable {  
	  // 아무 메서드가 없다.
  }
```

`Serializable` 인터페이스를 구현한 클래스는 `ObjectOutputStream`을 통해 직렬화할 수 있다.
```java
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @AllArgsConstructor
public class Item { // Serializable을 구현하지 않음
	private long id;
	private String name;
	private BigDecimal price;
}
```

```java
public class SerializableTest {
	@Test void serializableTest() throws IOException {
		File f = new File("test.txt");
		ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream(f));
		// java.io.NotSerializableException 예외 발생 !!! 
		// 단순 Serializable이 구현되었는지 타입확인 정도만 수행 (마커인터페이스)
		ostream.writeObject(new Item(1L, "item A", new BigDecimal(30000)));
	}
}
```
`ObjectOutputStream.writeObject0` 소스 중 `NotSerializableException` 예외 발생 부분
```java
if (obj instanceof String) { writeString((String) obj, unshared); } 
else if (cl.isArray()) { writeArray(obj, desc, unshared); } 
else if (obj instanceof Enum) { writeEnum((Enum<?>) obj, desc, unshared); } 
else if (obj instanceof Serializable) { writeOrdinaryObject(obj, desc, unshared); } 
else {
	if (extendedDebugInfo) {
		throw new NotSerializableException(cl.getName() + "\n" + debugInfoStack.toString());
	} else {
		throw new NotSerializableException(cl.getName());
	}
}
```

### __마커 인터페이스 장점__

* 구현 클래스의 인스턴스를 구분하는 타입으로 쓸 수 있다.
* 런타임시 발견될 오류를 컴파일타임에 발견할 수 있다.
	- 위에 `ObjectOutputStream.writeObject`는 이 잇점을 못살림 (실제 런타임시 발생)
* 적용 대상을 더 정밀하게 지정할 수 있다.
	- 마커 어노테이션은 `@Target(ElementType.TYPE)` 으로 타겟을 지정하므로 모든 타입(클래스, 인터페이스, 열거타입, 어노테이션)에 적용
	- 마킹하고 싶은 클래스에만 마커인터페이스를 구현하여 적용대상을 더 정밀 지정

### __마커 어노테이션 장점__

* 거대한 어노테이션 시스템의 지원을 받는다
* 어노테이션을 적극 활용하는 프레임워크에서는 어노테이션을 사용하는게 일관성에 좋다.

### __마커 어노테이션, 마커 인터페이스 사용할 때__

* 마커 어노테이션 사용
	- 클래스, 인터페이스 외 프로그램 요소(모듈, 패키지, 필드, 지역변수 등)에 마킹해야하는 경우
		> 클래스와 인터페이스만이 인터페이스를 구현하거나 확장이 가능
	- 어노테이션을 적극적으로 사용하는 프레임워크

* 마커 인터페이스 사용
	- 마킹된 객체를 매개변수로 받는 메서드를 작성해야 할 때
		> 마커인터페이스를 해당 메서드의 매개변수 타입으로 사용하면 컴파일타임에 오류발생
	- 새로 추가하는 메서드 없이 단지 타입 정의가 목적인 경우


---------------------------------------------------------------

[[TOC]](#목차)


... 6장 끝 ...

[<<< 5장 [이전]](../ch05/README.md) ----- [[TOC]](#목차) -----  [[다음] 7장 >>>](../ch07/README.md)