# 3장. 모든 객체의 공통 메서드

## 목차

- [x] item 10. [Overriding equlas](#item-10-overriding-equlas)
- [x] item 11. [Overriding hashCode](#item-11-overriding-hashcode)
- [x] item 12. [Overriding toString](#item-12-overriding-tostring)
- [x] item 13. [Overriding clone judiciously](#item-13-overriding-clone-judiciously)
- [x] item 14. [Consider implementing comparable](#item-14-consider-implementing-comparable)


---------------------------------------------------------------
[[TOC]](#목차)

## item 10. Overriding equlas
`[재정의]` equals 는 일반 규약을 지켜 재정의하라
### equals 메서드 재정의
* `equals()` 재정의 하지 말아야 할 사항
	- 각 인스턴스가 본질적으로 고유한 경우
	- 인스턴스의 `logical equality`를 검사할 경우가 없는 경우
	- 상위 클래스에서 재정의한 equals()가 하위 클래스에도 적합한 경우
	- 클래스가 private or package-private이고 equals()를 호출할 일이 없는 경우


### equals 메서드 규약
* 반사성 (reflexivity)
	> null이 아닌 모든 참조값 __X__ 에 대해,  
	> `X.equals(X)`는 true
	- 객체는 자기 자신과 같아야 한다는 뜻
* 대치성 (symmetry)
	> null이 아닌 모든 참조값 __X, Y__ 에 대해,  
	> `X.equals(Y)`가 true 이면, `Y.equals(X)`도 true
	- 두 객체는 서로의 동치여부에 대해 똑같은 결과 반환

* 추이성 (transitivity)
	> null이 아닌 모든 참조값 __X, Y, Z__ 에 대해,  
	> `X.equals(Y)`가 true 이고 `Y.equals(Z)`가 true 이면,  
	> `X.equals(Z)`도 true
* 일관성 (consistency)
	> null이 아닌 모든 참조값 __X, Y__ 에 대해,  
	> `X.equals(Y)`를 반복 호출하면, 항상 같은 true or false를 반환
* null 아님
	> null이 아닌 모든 참조값 __X__ 에 대해,  
	> `X.equals(null)`은 false
```java
public class ColorPoint {
	private final Color color;
	private final Point point;

	public ColorPoint(int x, int y, Color color) {
		this.point = new Point(x,y);
		this.color = Objects.requireNonNull(color);
	}

	public Point asPoint() { return point; }

	@Override 
	public boolean equals(Object o) { // 입력타입은 Object 여야 한다
		if (!(o instanseof ColorPoint)) return false;
		ColorPoint obj = (ColorPoint) o;

		return obj.point.equals(point) && obj.color.equals(color);
	}
}
```

### equals 구현시 주의사항
1. `==` 연산자를 사용해 입력이 자신참조인지 확인 (Float, Double은 .compare()로 비교)
2. `instanceof` 연산자로 입력이 올바른 타입인지 확인
3. 입력을 올바른 타입으로 형변환
4. 입력 객체와 자기 자신의 대응되는 '핵심' 필드들이 모두 일치하는지 검사

### @AutoValue
__'AutoValue 프레임워크'__ 를 사용하면 `equals`와 `hashCode`를 작성해줌
```java
	// build.gradle :
	//	implementation("com.google.auto.value:auto-value:1.3")

	import com.google.auto.value.AutoValue;

	@AutoValue
	public abstract class Product {
		public abstract String name();
		public abstract String price();

		@AutoValue.Builder
		public abstract static class Builder {
			public abstract Builder name(String name);
			public abstract Builder price(String price);
			public abstract Product build();
		}

		public static Product.Builder builder() {
			return new AutoValue_Product.Builder();
		}
	}
```
-----------------------------------------------------------------
[[TOC]](#목차)

## item 11. Overriding hashCode
`[재정의]` equals 를 재정의하려거든 hashCode 도 재정의하라.  
* `equals`를 재정의한 클래스는 `hashCode`도 재정의 해야 함
* 미정의 시 HashMap, HashSet에서 원소로 사용하면 문제 발생

### Thread Safe 해시코드
```java
public final class PhoneNumber {
	private final short areaCode, prefix, lineNum;
	// ... 생략 ...

	private int hashCode;

	@Override public int hashCode() {
		int result = hashCode;
		if ( result == 0) {
			result = Short.hashCode(areaCode);
			// 31 * i == (i << 5) - i
			result = 31 * result + Short.hashCode(prefix);
			result = 31 * result + Short.hashCode(lineNum);
			hashCode = result;			
		}
		return result;
	}
}
```

-----------------------------------------------------------------
[[TOC]](#목차)

## item 12. Overriding toString
`[재정의]` toString 을 항상 재정의하라
* 사용성과 디버깅에 필요한 정보 전달을 위해 toString()을 재정의
* 특별하게 인상적인 내용이 없어 skip...


-----------------------------------------------------------------
[[TOC]](#목차)

## item 13. Overriding clone judiciously
`[재정의]` clone 재정의는 주의해서 진행하라

### Cloneable 인터페이스
```java
public interface Cloneable {
	// 메서드가 하나도 없음
}
```
* Cloneable 은 복제해도 되는 클래스임을 명시하는 용도
* Cloneable 인터페이스는 Object의 protected 메서드인 __clone()의 동작방식을 결정__
* `clone()`을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환
* 구현하지 않은 클래스의 인스턴스에서 호출하면 `CloneNotSupportException`
* __불변 클래스__ 는 굳이 clone을 제공하지 않는 것이 좋다.

```java
	public final class PhoneNumber implements Cloneable {
		private final short areaCode, prefix, lineNum;
		private int hashCode;

		@Override
		public PhoneNumber clone() {
			try {
				// (형변환)
				return (PhoneNumber) super.clone();
			} catch (CloneNotSupportedException e) {
				throw new AssertionError();
			}
		}
	}
```

### Deep Copy
```java
	public class HashTable implements Cloneable {
		private Entry[] buckets = new Entry[10];

		private static class Entry {
			final Object key;
			Object value;
			Entry next;

			Entry(Object key, Object value, Entry next){
				this.key = key; this.value = value; this.next = next;
			}

			Entry deepCopy(){
				// 엔트리가 가리키는 연결 리스트를 재귀적으로 복사 (스택오버플로우 발생위험 존재)
				// return new Entry(key, value, next == null ? null : next.deepCopy());

				// 스택오버플로우 문제를 피하기 위해 반복자를 사용
				Entry result = new Entry(key,value, next);
				for (Entry p = result; p.next != null; p = p.next)
					p.next = new Entry(p.next.key, p.next.value, p.next.next);
				return result;
			}
		}

		@Override
		public  HashTable clone() {
			try {
				HashTable result = (HashTable) super.clone();
				result.buckets = new Entry[buckets.length];
				for (int i = 0 ; i < buckets.length; i++) {
					if (buckets[i] != null) result.buckets[i] = buckets[i].deepCopy();
				}
				return result;
			} catch (CloneNotSupportedException e) {
				throw new AssertionError();
			}
		}
	}
```

### 복사생성자 & 복사팩터리 
```java
	// 복사 생성자
	public Yum(Yum yum) { ... };

	// 복사 팩터리
	public static Yum newInstance(Yum yum) { ... };
```
* 생성자를 쓰지 않는 방식의 객체 생성 메커니즘을 사용하지 않는다.
* 엉성하게 문서화된 규약에 기대지 않고, 정상적인 final 필드 용법과도 충돌하지 않는다.
* 불필요한 검사 예외를 던지지 않고, 형변환도 필요치 않는다.
* __해당 클래스가 구현한 인터페이스 타입의 인스턴스를 인수로 받을 수 있다.__
-----------------------------------------------------------------
[[TOC]](#목차)

## item 14. Consider implementing Comparable
`[비교]` Comparable 을 구현할지 고민하라
```java
	public interface Comparable<T> {
		//	자신과 주어진 객체의 순서를 비교
		//	비교 불가 타입의 객체는 ClassCastException 반환
		public int compareTo(T o);
	}
```
알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 `Comparable 인터페이스`를 구현하자.

* `Comparable`은 단순 동치비교와 순서비교를 할 수 있는 Generic 인터페이스
* `Comparable`을 구현한 클래스의 인스턴스에는 자연적인 순서가 있음
* `Comparable`을 구현한 객체들의 배열은 다음과 같이 쉽게 정렬할 수 있다.
	> `Arrays.sort(a);`
* String과 같이 자바의 모든 값 클래스와 열거타입이 `Comparable`을 구현함
	```java
	public final class String
		implements java.io.Serializable, Comparable<String>, CharSequence {
	...
	}
	```
### __compareTo__ 메서드 일반 규약
`SGN`은 __signum function__ 을 뜻하고, 값이 음수:-1, 양수:1 을 반환하도록 정의

* `SGN(X.compareTo(Y)) == -SGN(Y.compareTo(X))`
* `X.compareTo(Y) > 0 && Y.compareTo(Z) > 0` 이면 `X.compareTo(Z) > 0`
* `X.compareTo(Y) == 0` 이면 `SGN(X.compareTo(Z)) == SGN(Y.compareTo(Z))`
* `(X.compareTo(Y) == 0 ) == (X.equals(Y))`
	> _이 권고는 필수는 아니지만 꼭 지키는게 좋으며, 만약 지키지 않았다면 이 클래스의 순서는 equals 메서드와 일관되지 않는 다는 것을 명시해야 함_


### 정리
* 순서를 고려하는 값 클래스 작성시 `Comparable` 인터페이스를 구현해 해당 인스턴스를 쉽게 정렬, 검색, 비교할 수 있는 컬렉션과 어우러지도록 해야 한다.
* `compareTo()`에서 필드 값 비교시 <, > 연산자는 사용하지 말자
* 박싱된 기본 타입 클래스가 제공하는 정적 `compare()`나 `Compartor` 인터페이스가 제공하는 비교자 생성 메서드를 사용하자.
-----------------------------------------------------------------
[[TOC]](#목차)

... 3장 끝 ...

[다음 4장 >>>](../ch04/README.md)