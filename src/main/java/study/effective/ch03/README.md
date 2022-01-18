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

cloneable 은 복제해도 되는 클래스임을 명시하는 용도

-----------------------------------------------------------------
[[TOC]](#목차)

## item 14. Consider implementing comparable
`[비교]` Comparable 을 구현할지 고민하라

알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하자.


-----------------------------------------------------------------
[[TOC]](#목차)

... 3장 끝 ...

[다음 4장 >>>](../ch04/README.md)