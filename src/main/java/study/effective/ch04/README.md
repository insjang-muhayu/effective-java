# 4장. 클래스와 인터페이스

## 목차

- [x] item 15. [클래스와 멤버의 접근을 최소화해라](#item-15-%ED%81%B4%EB%9E%98%EC%8A%A4%EC%99%80-%EB%A9%A4%EB%B2%84%EC%9D%98-%EC%A0%91%EA%B7%BC%EC%9D%84-%EC%B5%9C%EC%86%8C%ED%99%94%ED%95%B4%EB%9D%BC)
- [x] item 16. [public 클래스에서는 접근자 메서드를 사용하라](#item-16-public-%ED%81%B4%EB%9E%98%EC%8A%A4%EC%97%90%EC%84%9C%EB%8A%94-%EC%A0%91%EA%B7%BC%EC%9E%90-%EB%A9%94%EC%84%9C%EB%93%9C%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EB%9D%BC)
- [x] item 17. [변경 가능성을 최소화해라](#item-17-%EB%B3%80%EA%B2%BD-%EA%B0%80%EB%8A%A5%EC%84%B1%EC%9D%84-%EC%B5%9C%EC%86%8C%ED%99%94%ED%95%B4%EB%9D%BC)
- [x] item 18. [상속보단 컴포지션을 사용해라](#item-18-%EC%83%81%EC%86%8D%EB%B3%B4%EB%8B%A8-%EC%BB%B4%ED%8F%AC%EC%A7%80%EC%85%98%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%B4%EB%9D%BC)
- [x] item 19. [상속을 고려해 설계하고 문서화해라](#item-19-%EC%83%81%EC%86%8D%EC%9D%84-%EA%B3%A0%EB%A0%A4%ED%95%B4-%EC%84%A4%EA%B3%84%ED%95%98%EA%B3%A0-%EB%AC%B8%EC%84%9C%ED%99%94%ED%95%B4%EB%9D%BC)
- [x] item 20. [추상 클래스보다 인터페이스를 우선하라](#item-20-%EC%B6%94%EC%83%81-%ED%81%B4%EB%9E%98%EC%8A%A4%EB%B3%B4%EB%8B%A4-%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4%EB%A5%BC-%EC%9A%B0%EC%84%A0%ED%95%98%EB%9D%BC)
- [x] item 21. [인터페이스는 구현하는 쪽을 생각해 설계해라](#item-21-%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4%EB%8A%94-%EA%B5%AC%ED%98%84%ED%95%98%EB%8A%94-%EC%AA%BD%EC%9D%84-%EC%83%9D%EA%B0%81%ED%95%B4-%EC%84%A4%EA%B3%84%ED%95%B4%EB%9D%BC)
- [x] item 22. [인터페이스는 타입을 정의하는 용도로만 사용해라](#item-22-%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4%EB%8A%94-%ED%83%80%EC%9E%85%EC%9D%84-%EC%A0%95%EC%9D%98%ED%95%98%EB%8A%94-%EC%9A%A9%EB%8F%84%EB%A1%9C%EB%A7%8C-%EC%82%AC%EC%9A%A9%ED%95%B4%EB%9D%BC)
- [x] item 23. [태그 달린 클래스보다 클래스 계층구조를 활용해라](#item-23-%ED%83%9C%EA%B7%B8-%EB%8B%AC%EB%A6%B0-%ED%81%B4%EB%9E%98%EC%8A%A4%EB%B3%B4%EB%8B%A4-%ED%81%B4%EB%9E%98%EC%8A%A4-%EA%B3%84%EC%B8%B5%EA%B5%AC%EC%A1%B0%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%B4%EB%9D%BC)
- [x] item 24. [멤버 클래스는 되도록 static으로 구현해라](#item-24-%EB%A9%A4%EB%B2%84-%ED%81%B4%EB%9E%98%EC%8A%A4%EB%8A%94-%EB%90%98%EB%8F%84%EB%A1%9D-static%EC%9C%BC%EB%A1%9C-%EA%B5%AC%ED%98%84%ED%95%B4%EB%9D%BC)
- [x] item 25. [톱레벨 클래스는 한 파일에 하나만 생성해라](#item-25-%ED%86%B1%EB%A0%88%EB%B2%A8-%ED%81%B4%EB%9E%98%EC%8A%A4%EB%8A%94-%ED%95%9C-%ED%8C%8C%EC%9D%BC%EC%97%90-%ED%95%98%EB%82%98%EB%A7%8C-%EC%83%9D%EC%84%B1%ED%95%B4%EB%9D%BC)

---------------------------------------------------------------
[[TOC]](#목차)

## item 15. 클래스와 멤버의 접근을 최소화해라
: `[캡슐화]` 잘 설계된 컴포넌트는 내부구현을 숨기고, 구현과 API를 깔끔히 분리한다. 다른 컴포넌트와 API를 통해 소통한다.

### __캡슐화(정보은닉)의 장점__

* 여러 컴포넌트를 __병렬로 개발__ 하여 __개발 속도 향상__
* 컴포넌트 교체부담 감소를 통해 __시스템 관리 비용 낮춤__
* 타 컴포넌트에 영향없이 __컴포넌트 성능 최적화 가능__
* 독립적으로 동작하는 컴포넌트는 __소프트웨어 재사용성 높임__
* 개별 컴포넌트 동작 검증을 통해 큰 시스템의 __제작 난이도를 낮춤__

__정보은닉__ 은 접근 `제한자(private, protected, public)`을 제대로 활용하는 것이 핵심

### __접근 제한자 (private, protected, public)__

* 모든 클래스와 멤버의 접근성을 가능한 제한해야 함
* __Top Class & Interface__ 를
	- `public` 선언 : 공개 API로 사용 (변경시 하위 호환을 항상 고려)
	- `package-private` 선언 : 패키지 안에서만 사용 가능 (side-effect 없음)
* __멤버 접근 제한자__
	| Modifier		| TopClass	| Package	| SubClass	| World		|
	| :------------	| :---:		| :---:		| :---:		| :---:		|
	|**public**		|<b style='color:green'>Y</b>	|<b style='color:green'>Y</b>	|<b style='color:green'>Y</b>	|<b style='color:green'>Y</b>	|
	|**protected**	|<b style='color:green'>Y</b>	|<b style='color:green'>Y</b>	|<b style='color:green'>Y</b>	|<b style='color:red'>X</b>		|
	|**package-private**|<b style='color:green'>Y</b>	|<b style='color:green'>Y</b>	|<b style='color:red'>X</b>		|<b style='color:red'>X</b>		|
	|**private**	|<b style='color:green'>Y</b>	|<b style='color:red'>X</b>		|<b style='color:red'>X</b>		|<b style='color:red'>X</b>		|

### __모듈__

* JAVA 9 부터 모듈 개념이 도입됨
* __모듈은 패키지들의 묶음__
* 패키지 중 공개(export)할 것들은 `module-info.java`에 선언

---------------------------------------------------------------
[[TOC]](#목차)

## item 16. public 클래스에서는 접근자 메서드를 사용하라

```java
	class Point {
		public double x;
		public double y;
	}
```
__단점 :__
* 캡슐화 이점 제공하지 못함 (데이터 필드에 직접 접근 가능)
* 내부 표현 변경시 API 수정 필요
* 불변식 보장 불가능
* 외부에서 필드 접근 시 부수 작업 수행 불가능

### __접근자 방식__

```java
	class Point{
		private double x; private double y;

		public Point(double x, double y) {
			this.x = x; this.y = y;
		}

		public double getX() { return x; }
		public double getY() { return y; }
		public void setX(double x) { return this.x = x; }
		public void setY(double y) { return this.y = y; }
	}
```
__public 클래스에서 이 방식은 확실히 맞다.__

### __불변 필드__

```java
	public final class Time {
		private static final int HOURS_PER_DAY = 24;
		private static final int MINUTES_PER_DAY = 60;

		public final int hour;		// 불변 필드
		public final int minute;	// 불변 필드

		public Time(int hour, int minute) {
			if (hour < 0 || hour >= HOURS_PER_DAY) {
				throw new IllegalArgumentException("시간 : "+ hour);
			}
			if (minute < 0 || minute >= MINUTES_PER_DAY) {
				throw new IllegalArgumentException("분 : "+ hour);
			}
			this.hour = hour;
			this.minute = minute;
		}
	}
```
__단점 :__
* 내부 표현 변경시 API 수정 필요
* 필드를 읽을 때 부수 작업 수행 불가

---------------------------------------------------------------
[[TOC]](#목차)

## item 17. 변경 가능성을 최소화해라 (불변 클래스)

### __불변 클래스(Immutable Class) 란?__

: 인스턴스의 내부 값을 수정할 수 없는 클래스
* 객체 상태를 변경하는 메서드(변경자)를 제공하지 않는다.
* 클래스를 확장할 수 없도록 한다.
* 모든 필드를 `private final`로 선언한다.
* 자신 외에는 내부 가변 컴포넌트에 접근할 수 없도록 한다. (방어적 복사)

### __불변객체 장점__

* `thread safe` 하므로 동기화할 필요 없음
* 자유롭게 공유할 수 있으며, 불변객체끼리는 내부 데이터를 공유 가능
* 객체 생성시 다른 불변객체들을 구성요소로 사용 이점 (Map의 Key, Set의 원소)
* 자체적으로 실패원자성(_객체가 method 예외발생 후에도 동일 유효상태 유지됨_) 제공

클래스가 불변임을 보장하기 위해 :
```java
	public final class Complex { // 불변클래스
		// 모든 필드는 private final로 선언
		private final double re; 
		private final double im;

		// private, package-private 생성자
		private Complex(double re, double im) { 			
			this.re = re; this.im = im;
		}

		// public static 팩터리
		public static Complex valueOf(double re, double im) {
			return new Complex(re, im);
		}
	...
	}
```

신뢰할 수 없는 하위클래스의 인스턴스인 경우, 
이 인수들은 가변이라 가정하고 방어적 복사를 사용
```java
	public static BigInteger safeInsatance(BigInteger val) {
		return val.getClass() == BigInteger.class ? val : new BigInteger(val.toByteArray());
	}
```

### __요약__

* `Getter`가 있다고 해서 무조건 `Setter`를 구현하지 말자
* 클래스는 꼭 필요한 경우가 아니라면 불변이어야 한다
* 불변 클래스로 만들기 힘들면 변경할 수 있는 부분은 최대한 줄이자
* 생성자는 불변식 설정이 모두 완료된 객체를 생성해야한다

---------------------------------------------------------------
[[TOC]](#목차)

## item 18. Inheritance보단 Composition을 사용해라

재사용 수단인 `상속`을 잘못 사용하면, `캡슐화`를 깨뜨려 객체 유연성을 해침

### __Composition (Forwarding 메서드) 사용__

* __`composition`__ : 기존 클래스의 확장 대신에 <u>__`forwarding(전달)`__ 방식을 통해 기능을 확장시킴</u>
* __`forwarding`__ : 새로운 클래스의 인스턴스 메서드들은 <u>`private 필드`로 참조하는 기존 클래스의 대응하는 메서드(forwarding method)를 호출해 그 결과를 반환</u>
* 기존 클래스의 내부구현방식(메서드 추가와 같은) 영향에서 벗어날 수 있음

```java
	public class ForwardingSet<E> implements Set<E> {
		private final Set<E> s; // private 필드로 기존 클래스의 인스턴스 참조

		public ForwardingSet(Set<E> s) { this.s = s; }

		@Override public int size() { return s.size(); }
		@Override public boolean isEmpty() { return s.isEmpty(); }
		@Override public boolean contains(Object o) { return s.contains(o); }
		@Override public Iterator<E> iterator() { return s.iterator(); }
		@Override public Object[] toArray() { return s.toArray(); }
		@Override public <T> T[] toArray(T[] a) { return s.toArray(a); }
		@Override public boolean add(E e) { return s.add(e); }
		@Override public boolean remove(Object o) { return s.remove(o); }
		@Override public boolean containsAll(Collection<?> c) { return s.containsAll(c); }
		@Override public boolean addAll(Collection<? extends E> c) { return s.addAll(c); }
		@Override public boolean retainAll(Collection<?> c) { return s.retainAll(c); }
		@Override public boolean removeAll(Collection<?> c) { return s.removeAll(c); }
		@Override public void clear() { s.clear(); }
	}
```

```java
	public class InstrumentedSet<E> extends ForwardingSet<E> {
		private int addCount = 0; // 추가된 원소

		public InstrumentedSet(Set<E> s) { super(s); }
		@Override public boolean add(E e) {
			addCount++; return super.add(e);
		}
		@Override public boolean addAll(Collection<? extends E> c) {
			addCount += c.size(); return super.addAll(c);
		}
		public int getAddCount() { return addCount; }
	}
```

__컴포지션 방식__ 은 어떠한 `Set 구현체`라도 계측할 수 있으며, 기존 생성자들과도 함께 사용 가능
```java
	Set<Instant> times = new InstrumentedSet<>(new TreeSet<> cmp); // 래퍼 클래스
	Set<E> s = new InstrumentedSet<>(new HashSet<>(INIT_CAPACITY));
```
__래퍼 클래스(`Wrapper class` == `Decorator pattern`)__ 는 단점이 거의 없으나, __콜백 프레임워크__ 와는 어울리지 않는다는 점은 주의


### __상속(Inheritance)을 사용할 때__

* 상속은 `Sub Class`가 `Super Class`의 진짜 하위 타입인 상황에서만 사용 
* `(B is A 인 경우)`
* `Stack`과 `Properties`는 원칙을 위반한 클래스이다
* 확장하려는 클래스 API의 자체 결함 및 `Sub Class`의 결함 전파 여부 체크해야 함

---------------------------------------------------------------
[[TOC]](#목차)

## item 19. 상속을 고려해 설계하고 문서화해라



---------------------------------------------------------------
[[TOC]](#목차)

## item 20. 추상 클래스보다 인터페이스를 우선하라

---------------------------------------------------------------
[[TOC]](#목차)

## item 21. 인터페이스는 구현하는 쪽을 생각해 설계해라

---------------------------------------------------------------
[[TOC]](#목차)

## item 22. 인터페이스는 타입을 정의하는 용도로만 사용해라

---------------------------------------------------------------
[[TOC]](#목차)

## item 23. 태그 달린 클래스보다 클래스 계층구조를 활용해라

---------------------------------------------------------------
[[TOC]](#목차)

## item 24. 멤버 클래스는 되도록 static으로 구현해라

---------------------------------------------------------------
[[TOC]](#목차)

## item 25. 톱레벨 클래스는 한 파일에 하나만 생성해라

---------------------------------------------------------------
[[TOC]](#목차)


... 4장 끝 ...

[<<< 3장 [이전]](../ch03/README.md) ----- [[TOC]](#목차) -----  [[다음] 5장 >>>](../ch05/README.md)