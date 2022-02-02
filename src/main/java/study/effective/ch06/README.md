# 6장. EnumType과 Annotation

## 목차

- [x] item 34. [int 상수 대신 EnumType을 사용해라](#item-34-int-상수-대신-enumtype을-사용해라)
- [x] item 35. [ordinal 메서드 대신 인스턴스 필드를 사용해라](#item-35-ordinal-메서드-대신-인스턴스-필드를-사용해라)
- [x] item 36. [비트 필드 대신 EnumSet을 사용해라](#item-36-비트-필드-대신-enumset을-사용해라)
- [x] item 37. [ordinal 인덱싱 대신 EnumMap을 사용해라](#item-37-ordinal-인덱싱-대신-enummap을-사용해라)
- [x] item 38. [확장할 수 있는 EnumType이 필요하면 Interface를 사용해라](#item-38-확장할-수-있는-enumtype이-필요하면-interface를-사용해라)
- [x] item 39. [명명 패턴보다 Annotation을 사용해라](#item-39-명명-패턴보다-annotation을-사용해라)
- [x] item 40. [@Override Annotation을 일관되게 사용해라](#item-40-override-annotation을-일관되게-사용해라)
- [x] item 41. [정의하려는 것이 타입이라면 Marker Interface를 사용해라](#item-41-정의하려는-것이-타입이라면-marker-interface를-사용해라)

---------------------------------------------------------------
[[TOC]](#목차)

## item 34. int 상수 대신 EnumType을 사용해라
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
[[TOC]](#목차)

## item 35. ordinal 메서드 대신 인스턴스 필드를 사용해라


---------------------------------------------------------------
[[TOC]](#목차)

## item 36. 비트 필드 대신 EnumSet을 사용해라


---------------------------------------------------------------
[[TOC]](#목차)

## item 37. ordinal 인덱싱 대신 EnumMap을 사용해라


---------------------------------------------------------------
[[TOC]](#목차)

## item 38. 확장할 수 있는 EnumType이 필요하면 Interface를 사용해라


---------------------------------------------------------------
[[TOC]](#목차)

## item 39. 명명 패턴보다 Annotation을 사용해라


---------------------------------------------------------------
[[TOC]](#목차)

## item 40. @Override Annotation을 일관되게 사용해라


---------------------------------------------------------------
[[TOC]](#목차)

## item 41. 정의하려는 것이 타입이라면 Marker Interface를 사용해라


---------------------------------------------------------------
[[TOC]](#목차)


... 6장 끝 ...

[<<< 5장 [이전]](../ch05/README.md) ----- [[TOC]](#목차) -----  [[다음] 7장 >>>](../ch07/README.md)