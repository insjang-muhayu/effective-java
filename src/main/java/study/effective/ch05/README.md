# 5장. 제네릭

## 목차

- [x] item 26. [Raw type은 사용하지 마라](#item-26-raw-type은-사용하지-마라)
- [x] item 27. [Unchecked Warning을 제거해라](#item-27-unchecked-warning을-제거해라)
- [x] item 28. [Array보다는 List를 사용해라](#item-28-array보다는-list를-사용해라)
- [x] item 29. [이왕이면 Generic Type으로 만들어라](#item-29-이왕이면-generic-type으로-만들어라)
- [x] item 30. [이왕이면 Generic Method로 만들어라](#item-30-이왕이면-generic-method로-만들어라)
- [x] item 31. [한정적 와일드카드를 사용해 API 유연성을 높여라](#item-31-한정적-와일드카드를-사용해-api-유연성을-높여라)
- [x] item 32. [제네릭과 가변인수를 함께 쓸 때는 신중해라](#item-32-제네릭과-가변인수를-함께-쓸-때는-신중해라)
- [x] item 33. [타입 안전 이종 컨테이너를 고려해라](#item-33-타입-안전-이종-컨테이너를-고려해라)

---------------------------------------------------------------
[[TOC]](#목차)

## item 26. Raw type은 사용하지 마라

| 한글                     | 영어                    | 예                                 |
| ------------------------ | ----------------------- | ---------------------------------- |
| __로 타입__              | __Raw type__            | `List`                             |
| 매개변수화 타입          | parameterized type      | `List<String>`                     |
| 실제타입 매개변수        | actual type parameter   | `String`                           |
| __제네릭 타입__          | __Generic type__        | `List<E>`                          |
| 정규타입 매개변수        | formal type parameter   | `E`                                |
| 비한정적 와일드카드 타입 | unbounded wildcard type | `List<?>`                          |
| 한정적 타입 매개변수     | bounded type parameter  | `<E extends Number>`               |
| 재귀 타입 한정           | recursive type bound    | `<T extends Comparable<T>>`        |
| 한정적 와일드카드 타입   | bounded wildcard type   | `List<? extends Number>`           |
| 제네릭 메서드            | Generic method          | `static <E> List<E> asList(E[] a)` |
| 타입 토큰                | type token              | `String.class`                     |


* __Raw type : runtime 오류__
	```java
	public class RawTypeTest {
		static class Coin {
			public void cancle() { System.out.println("Coin.cancle"); }
		}
		static class Stamp {
			public void cancle() { System.out.println("Stamp.cancle"); }
		}
		public static void main(String[] args) {
			// Stamp 인스턴스만 취급 (Raw Type 선언)
			Collection stamps = new ArrayList(); 
			// 실수로 Coin 추가
			stamps.add(new Stamp()); stamps.add(new Coin());
			// 조회시 ClassCastException 발생
			for (Iterator i = stamps.iterator(); i.hasNext();) {
				Stamp stamp = (Stamp) i.next();
				stamp.cancle();
			}
		}
	}
	```
	> Exception in thread "main" java.lang.ClassCastException: ...

* __Raw type List : runtime 오류__
	```java
	public class ListRawTypeTest {
		// Raw type List를 매개변수로 사용
		private static void unsafeAdd(List list, Object o) { list.add(o); }

		public static void main(String[] args) {
			List<String> strings = new ArrayList<>();
			unsafeAdd(strings, Integer.valueOf(50));
			String s = strings.get(0); // ClassCastException 오류 발생
		}
	}
	```
	> Exception in thread "main" java.lang.ClassCastException :  
	> ... class java.lang.Integer cannot be cast to class java.lang.String

* __Generic parameter type : compile 오류__
	```java
	public class RawTypeTest {
		static class Coin {
			public void cancle() { System.out.println("Coin.cancle"); }
		}
		static class Stamp {
			public void cancle() { System.out.println("Stamp.cancle"); }
		}

		public static void main(String[] args) {
			// stamps는 Stamp 인스턴스만 취급
			Collection<Stamp> stamps = new ArrayList();
			// 컴파일시 오류발생
			stamps.add(new Stamp()); stamps.add(new Coin());
			// 조회시 ClassCastException 발생
			for (Iterator i = stamps.iterator(); i.hasNext();) {
				Stamp stamp = (Stamp) i.next();
				stamp.cancle();
			}
		}
	}
	```

### 비한정적 와일드카드 타입(Unbounded wildcard type)
* `제네릭타입<?>` : 제한없음(타입 파라미터를 대치하는 구체적 타입으로 모든 클래스나 인터페이스 타입이 올 수 있다)
	```java
	// 비한정적 와일드카드 타입을 사용하라 - 타입 안전하며 유연
	static int numElemnetsInCommon(Set<?> s1, Set<?> s2) {
		int result = 0;
		for (Object o1 : s1) {
			if (s2.contains(o1)) result++;
		}
		return result;
	}
	```

### 예외케이스

* __class 리터럴에는 Raw type을 사용해야한다.__
	- 자바 명세는 class 리터럴에 매개변수화 타입을 사용하지 못하게 했다.(배열과 기본 타입은 허용)
	- `List.class`, `String[].class`, `int.class` 는 허용하지만, `List<String>.class`, `List<?>.class` 는 허용하지 않는다.

* __instanceof 연산__
	- 런타임에는 제네릭 타입 정보가 지워지므로 `instanceof` 연산자는  
	  `Unbounded wildcard type` 이외의 매개변수화 타입에는 적용할 수 있다. 
	- `Raw type`과 `Unbounded wildcard type` 이 완전히 동일하게 동작한다. 
	- 그러므로 `Raw type`을 쓰는 편이 더 깔끔하다.

	```java
	if (o instanceof Set) { // Raw type을 써도 좋은 예
		Set<?> s = (Set<?>) o;
	}
	```


---------------------------------------------------------------
[[TOC]](#목차)

## item 27. Unchecked Warning을 제거해라
할 수 있는 한 모든 Unchecked Warning을 제거하라
```java
	// Warning!! : unchecked conversion
	Set<Lark> set = new HashSet(); 

	// (자바7부터 지원) 다아아몬드 연산자(<>)로 해결
	Set<Lark> set = new HashSet<>(); 
```

### @SuppressWarnings("unchecked")
* 타입이 안전하다고 판단되면 Annotation을 활용해 경고를 숨김
* `@SuppressWarnings`은 가능한 좁은 범위에 적용하라
* `@SuppressWarnings("unchecked")` 사용시 경고를 무시해도 되는 사유를 주석 작성
```java
	public <T> T[] toArray(T[] a) {
		if (a.length < size) {
			// 생성배열과 매개변수로 받은 배열타입이 T[]와 같으므로 바른 형변환
			@SuppressWarnings("unchecked") T[] result = 
				(T[]) Arrays.copyOf(elements, size, a.getClass());
			return result;
		}
		System.arraycopy(elements, 0, a, 0, size);
		if (a.length > size) a[size] = null;

		return a;
	}
```

---------------------------------------------------------------
[[TOC]](#목차)

## item 28. Array보다는 List를 사용해라





---------------------------------------------------------------
[[TOC]](#목차)

## item 29. 이왕이면 Generic Type으로 만들어라





---------------------------------------------------------------
[[TOC]](#목차)

## item 30. 이왕이면 Generic Method로 만들어라





---------------------------------------------------------------
[[TOC]](#목차)

## item 31. 한정적 와일드카드를 사용해 API 유연성을 높여라





---------------------------------------------------------------
[[TOC]](#목차)

## item 32. 제네릭과 가변인수를 함께 쓸 때는 신중해라





---------------------------------------------------------------
[[TOC]](#목차)

## item 33. 타입 안전 이종 컨테이너를 고려해라




---------------------------------------------------------------
[[TOC]](#목차)


... 5장 끝 ...

[<<< 4장 [이전]](../ch04/README.md) ----- [[TOC]](#목차) -----  [[다음] 6장 >>>](../ch06/README.md)