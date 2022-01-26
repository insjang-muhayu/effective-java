# 4장. 클래스와 인터페이스

## 목차

- [x] item 15. [클래스와 멤버의 접근을 최소화해라](#item-15-클래스와-멤버의-접근을-최소화해라)
- [x] item 16. [public 클래스에서는 접근자 메서드를 사용하라](#item-16-public-클래스에서는-접근자-메서드를-사용하라)
- [x] item 17. [변경 가능성을 최소화해라 (불변 클래스)](#item-17-변경-가능성을-최소화해라-불변-클래스)
- [x] item 18. [Inheritance보단 Composition을 사용해라](#item-18-inheritance보단-composition을-사용해라)
- [x] item 19. [상속을 고려해 설계하고 문서화해라](#item-19-상속을-고려해-설계하고-문서화해라)
- [x] item 20. [추상 클래스보다 인터페이스를 우선하라](#item-20-추상-클래스보다-인터페이스를-우선하라)
- [x] item 21. [인터페이스는 구현하는 쪽을 생각해 설계해라](#item-21-인터페이스는-구현하는-쪽을-생각해-설계해라)
- [x] item 22. [인터페이스는 타입을 정의하는 용도로만 사용해라](#item-22-인터페이스는-타입을-정의하는-용도로만-사용해라)
- [x] item 23. [태그 달린 클래스보다 클래스 계층구조를 활용해라](#item-23-태그-달린-클래스보다-클래스-계층구조를-활용해라)
- [x] item 24. [멤버 클래스는 되도록 static으로 구현해라](#item-24-멤버-클래스는-되도록-static으로-구현해라)
- [x] item 25. [톱레벨 클래스는 한 파일에 하나만 생성해라](#item-25-톱레벨-클래스는-한-파일에-하나만-생성해라)

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
* __[상속용 클래스]는 재정의(`public`과 `protected` 중 `final`이 아닌) 할 수 있는 메서드를 문서로 남겨야 함__
	> __`@implSpec` 태그__ : "Implementation Requirements" 메서드의 내부 동작 방식을 설명하는 곳 (java 8부터 사용)

	```java
		/**
		 * {@inheritDoc}
		 *
		 * @implSpec
		 * This implementation iterates over the collection looking for the specified element.
		 *
		 * <p>Note that this implementation throws an {@code UnsupportedOperationException}
		 * if the iterator returned by this collection's iterator method does not implement
		 * the {@code remove} method and this collection contains the specified object.
		 *
		 * @throws UnsupportedOperationException {@inheritDoc}
		 * @throws ClassCastException            {@inheritDoc}
		 * @throws NullPointerException          {@inheritDoc}
		 */
		public boolean remove(Object o) {
			Iterator<E> it = iterator();
			if (o == null) {
				while (it.hasNext()) {
					if (it.next() == null) { it.remove(); return true; }
				}
			} else {
				while (it.hasNext()) {
					if (o.equals(it.next())) { it.remove(); return true; }
				}
			}
			return false;
		}
	```

* __[상속용 클래스]를 시험하는 방법은 직접 `Sub Class`를 만들어보는 것이 유일__
	> `Sub Class`를 직접 구현해보면, `protected` 멤버의 필요 여부를 확인 가능

* __[상속용 클래스]의 생성자는 직간접적으로 `Override Method`를 호출하면 안됨__
	> `private`, `final`, `static` 메서드는 재정의 불가능하므로 생성자에서 호출 가능
	```java
		public class Super {
			// 잘못된 예 : 생성자가 재정의 가능한 메서드 호출
			public Super() { overrideMe(); }
			public void overrideMe() { }
		}
	```
	```java
		public final class Sub extends Super {
			private final Instant instant;

			Sub() { instant = Instant.now(); }			
			@Override public void overrideMe() { System.out.println(instant); }
		}
	```

* __[상속용 클래스]에서 `Cloneable`, `Serializable`을 구현할지 정해야 한다면, `clone`과 `readObject` 모두 `Override Method`를 호출하면 안됨__
	> `Override Method` 부터 호출되기 때문

	> `Serializable` 을 구현한 [상속용 클래스]가 `readResolve`나 `writeReplace`를 갖는다면 이 메서드들은 `private`가 아닌 `protected`로 선언해야 한다.

* __상속용으로 설계되지 않은 클래스는 상속을 금지해야 함__
	- __`Final Class`__ 를 선언하는 방법
	- 생성자를 `private` or `package-private`로 선언하고, __`Public Static Factory`__ 를 만들어주는 방법 ([ITEM-17](#item-17-변경-가능성을-최소화해라-불변-클래스))

---------------------------------------------------------------
[[TOC]](#목차)

## item 20. 추상 클래스보다 인터페이스를 우선하라

### 다중 구현 메커니즘 (인터페이스 & 추상클래스)
* 추상 클래스의 경우, 추상 클래스에서 정의한 메서드를 구현하는 클래스는 반드시 추상 클래스의 하위 클래스가 되어야 같은 타입으로 취급
* 인터페이스의 경우, 인터페이스에서 정의한 메서드를 모두 정의한 클래스라면 다른 어떤 클래스를 상속했든 상관없이 같은 타입으로 취급


### 인터페이스의 장점
* __기존 클래스에 손쉽게 새로운 인터페이스를 구현할 수 있다__

* __인터페이스는 mixin(믹스인) 정의에 안성맞춤이다__
	> 믹스인이란? 어떤 클래스의 주 기능에 추가 기능을 혼합한 것이다. 쉽게 말해 다른 클래스에서 이용할 메소드를 포함한 클래스
	```java
	public class Employee implements Comparable<Employee>{
		private int id;
		public Employee(int id) { this.id = id; }
		public int getId() { return id; }
		public int printId() { return this.id; }
		// 다른 Employee 클래스에서 이용할 compareTo 메소드 포함
		@Override public int compareTo(Employee o) {
			if (o.getId() < this.id) return -1;
			else if (o.getId() == this.id) return 0;
			else return 1;
		}
	}
	```

* __인터페이스로는 계층구조가 없는 타입 프레임워크를 만들 수 있다__
	```java
	public interface Singer { public void sing(); }
	public interface SongWriter { public void compose(); }

	public class People implements Singer, SongWriter{
		@Override public void sing() {}
		@Override public void compose() {}
	}

	public interface SingerSongWriter extends Singer, SongWriter {
		AudioClip strum();
		void actSensitive();
	}

	```
* __Wrapper Class와 함께 사용하면, 인터페이스는 기능을 향상시키는 안전하고 강력한 수단이 된다__

### 추상 골격 구현 클래스 (Skeletal Implementation)

인터페이스와 추상 골격 구현(Skeletal Implementation) 클래스를 함께 제공하는 방법
* 인터페이스로는 타입을 정의
* 메소드 구현이 필요한 부분은 추상 골격 구현 클래스에서 구현

```java
public class IntArrays {
	// public interface List<E> extends Collection<E> { ... }
	
	static List<Integer> intArrayAsList(int[] a) {
		Objects.requireNonNull(a);
		
		// public abstract class AbstractList<E> 
		//     extends AbstractCollection<E> implements List<E> { ... }
		
		// 추상 골격 구현 클래스를 구현해 반환
		return new AbstractList<Integer>() { // 익명 클래스 형태
			// AbstractList의 Abstract Method (반드시 구현)
			@Override public Integer get(int i) { return a[i]; }
			// AbstractCollection의 Abstract Method (반드시 구현)
			@Override public int size() { return a.length; }
			// Hook Method (선택적으로 구현)
			@Override public Integer set(int i, Integer val) {
				int oldVal = a[i];
				a[i] = val;		// 오토언박싱
				return oldVal;	// 오토박싱
			}
		};
	}
}
```

###  골격 구현 작성 방법

__골격구현 클래스는 추상 클래스처럼 구현을 도와주는 동시에 추상 클래스로 타입을 정의할 때 따라오는 제약에서 자유롭다__

1. 다른 메서드들의 구현에 기반 메서드 선정
2. 기반 메서드들을 사용해 직접 구현할 수 있는 메서드를 모두 디폴트 메서드로 제공
	> 단 `equals()`, `hashCode()`는 제공하면 안된다.
3. 기반 메서드나 디폴트 메서드로 만들지 못한 메서드가 남아 있다면, 인터페이스를 구현하는 골격 구현 클래스를 만들어 남은 메서드를 작성
5. 골격 구현은 기본적으로 상속이므로, 설계 및 문서화 지침을 따라야 한다.

```java
// Map.Entry 인터페이스나 그 하위 인터페이스로는 이 골격구현 제공 불가능
// equals, hashCode, toString 재정의 할 수 없기 때문
public abstract class AbstractMapEntry<K,V> implements Map.Entry<K,V> {
	// 변경 가능한 엔트리는 이 메서드를 반드시 재정의
	@Override public V setValue(V value){
		throw new UnsupportedOperationException();
	}
	// Map.Entry.equals의 일반 규약 구현
	@Override public boolean equals(Object o){
		if (o == this) return true;
		if (!(o instanceof Map.Entry)) return false;
		Map.Entry<?,?> e = (Map.Entry) o;
		return Objects.equals(e.getKey(), getKey())
				&& Objects.equals(e.getValue(), getValue());
	}
	// Map.Entry.hashCode 일반 규약 구현
	@Override public int hashCode() {
		return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
	}

	@Override public String toString() {
		return getKey() + "=" +getValue();
	}
}
```

### 단순 구현(Simple Implementation)

* 골격구현의 작은 변종 (골격구현과 유사점)
	> 상속을 위해 인터페이스를 구현
* 추상클래스가 아님 (골격구현과 차이점)
	> 그대로 사용하거나 필요에 맞게 확장 가능
* 대표 예) __`AbstractMap.SimpleEntry`__
	```java
	public static class SimpleEntry<K,V> implements Entry<K,V>, java.io.Serializable
	{
		private static final long serialVersionUID = -8499721149061103585L;

		private final K key;
		private V value;

		public SimpleEntry(K key, V value) {
			this.key = key; this.value = value;
		}
		public SimpleEntry(Entry<? extends K, ? extends V> entry) {
			this.key = entry.getKey(); this.value = entry.getValue();
		}

		public K getKey() { return key; }

		public V getValue() { return value; }

		public V setValue(V value) {
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		public boolean equals(Object o) {
			if (!(o instanceof Map.Entry)) return false;
			Map.Entry<?,?> e = (Map.Entry<?,?>)o;
			return eq(key, e.getKey()) && eq(value, e.getValue());
		}

		public int hashCode() {
			return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
		}

		public String toString() { return key + "=" + value; }
	}
	```

### 템플릿 메서드 패턴
```java
	public abstract class Super {
		public void templateMethod() { // 기본 알고리즘 코드
			hookMethod();	// 선택적
			abstractMethod();	// 필수적
		}

		protected void hookMethod() {} // 선택적으로 오버라이드 가능
		public abstract void abstractMethod(); // 반드시 SubClass에서 구현
	}
	public Sub extends Super {
		@Override protected void hookMethod() { ...선택적... }

		@Override public void abstractMethod() { ...필수적...  }
	}

```

---------------------------------------------------------------
[[TOC]](#목차)

## item 21. 인터페이스는 구현하는 쪽을 생각해 설계해라
생각할 수 있는 모든 상황에서 불변식을 해치지 않는 디폴트 메서드를 작성하기는 어렵다.


```java
	/**
	 * ...
	 * @implSpec
	 * The default implementation traverses all elements of the collection using
	 * its {@link #iterator}.  Each matching element is removed using
	 * {@link Iterator#remove()}.  If the collection's iterator does not
	 * support removal then an {@code UnsupportedOperationException} will be
	 * thrown on the first matching element.
	 * ...
	 */
	default boolean removeIf(Predicate<? super E> filter) {
		Objects.requireNonNull(filter);
		boolean removed = false;
		final Iterator<E> each = iterator();
		while (each.hasNext()) {
			if (filter.test(each.next())) {
				each.remove();
				removed = true;
			}
		}
		return removed;
	}
```
### 예기치 못한 상황
__`org.apache.commons.collections4.collection.SynchronizedCollection`__
```java
	public boolean removeIf(Predicate<? super E> filter) {
		synchronized(this.lock) {
			return this.decorated().removeIf(filter);
		}
	}
```
* 여러 스레드가 공유하는 환경에서 removeIf를 호출하면 ConcurrentModificationException이 발생하거나 다른 예상치 못한 결과로 이어질 수 있다.
* 디폴트 메서드는 컴파일에 성공하더라도 기존 구현체에 런타임 오류를 일으킬 수 있다.
* 디폴트 메서드가 생겼더라도, 인터페이스 설계할 때는 여전히 주의를 기울여야한다. 
* 인터페이스를 릴리즈한 후라도 결함을 수정하는 것이 가능할 수는 있지만, 절대 그 가능성에 기대서는 안된다.

---------------------------------------------------------------
[[TOC]](#목차)

## item 22. 인터페이스는 타입을 정의하는 용도로만 사용해라
`인터페이스는 타입`을 정의하는 용도로만 사용해야하며, `상수 공개용 수단`으로 사용하면 안됨

### __상수 공개용으로 적합한 수단__
* __`Enum 타입`으로 공개__
	```java
	public enum Day{ MON, TUE, WED, THU, FRI, SAT, SUN};
	```

* __인스턴스화 할 수 없는 `유틸리티 클래스`로 공개__
	```java
	public class PysicalConstants{
		private PysicalConstants(){}; // 인스턴스화 방지
		public static final double AVOGARDROS_NUMBER = 6.022_140_857e23;
		public static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
		public static final double ELECTRON_MASS = 9.109_383_56e-3;
	}
	```


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