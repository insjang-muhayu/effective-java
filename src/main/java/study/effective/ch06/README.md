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



```java

```

```java

```

```java

```

```java

```

```java

```


---------------------------------------------------------------

## item 40. @Override Annotation을 일관되게 사용해라

[[TOC]](#목차)



```java

```

```java

```

```java

```

```java

```

```java

```


---------------------------------------------------------------

## item 41. 정의하려는 것이 타입이라면 Marker Interface를 사용해라

[[TOC]](#목차)



```java

```

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


... 6장 끝 ...

[<<< 5장 [이전]](../ch05/README.md) ----- [[TOC]](#목차) -----  [[다음] 7장 >>>](../ch07/README.md)