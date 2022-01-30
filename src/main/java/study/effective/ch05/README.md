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

### __Array(Covariant)와 List(Invariant) 차이__

* __Array : 공변 (Covariant)__
	> 자기 자신과 자식 객체로 타입 변환을 허용해주는 것
	```java
		Object[] objects = new Long[1];

		objects[0] = "ArrayStoreException : 타입이다름"; // 오류!!
		Assertions.assertThrows(ArrayStoreException.class,
			() -> objects[0] = "ArrayStoreException : 타입이다름");
	```
	- `Array(공변)`는 타입이 다른 경우 런타임 `ArrayStoreException` 오류 발생


* __List : 불공변 (Invariant)__
	> __`List<String> != List<Object>`__ : 두 타입은 전혀 관련이 없다는 의미
	```java
		public class Test {
			public static void test(List<Object> list) { }
			public static void main(String[] args) {
				List<String> list = new ArrayList<>();
				list.add("Gyunny");
				test(list);   // 컴파일 에러
			} 
		}
	```
	- `List(불공변)`는 컴파일 오류 발생

### __타입__
> 배열은 구체화(reify)가 되고, 제네릭은 비구체화(non-reify)가 된다.

* __구체화 타입 (Reifiable Type)__
	> 자신의 타입정보를 런타임에도 알고 있는 것

* __비구체화 타입 (Non-Reifiable Type)__
	- 비구체화 타입은 런타임 시에 소거 되기 때문에 런타임에는 컴파일타임보다 타입정보를 적게 가짐
	- `E`, `List<E>`, `List<String>` 과 같은 타입을 실체화 불가 타입이라고 함

* __제네릭 타입 소거 (Generic Type Erasure)__
	> 컴파일타임에만 타입 제약 조건을 정의하고, 런타임에는 타입을 제거
	- __Unbounded Type(`<?>`, `<T>`)은 `Object`로 변환__
		```java
		// 컴파일 시 (타입 소거 전)
		public static <T> int count(T[] anArray, T elem) {
			int cnt = 0;
			for (T e : anArray) { if (e.equals(elem)) ++cnt; }
			return cnt;
		}
		```
		```java
		// 런타임 시 (타입 소거 후)
		public static int count(Object[] anArray, Object elem) {
			int cnt = 0;
			for (Object e : anArray) { if (e.equals(elem)) ++cnt; }
			return cnt;
		}
		```
	- __Bound Ttype(`<E extends Comparable>`)의 경우는 `Comparable`로 변환__
		```java
		// 컴파일 시 (타입 소거 전)
		public static <T extends Shape> void draw(T shape) { /* ... */ }
		```
		```java
		// 런타임 시 (타입 소거 후)
		public static void draw(Shape shape) { /* ... */ }
		```

	- 제네릭 타입을 사용할 수 있는 일반 클래스, 인터페이스, 메소드에만 소거 규칙을 적용
	- 타입 안정성 보존을 위해 필요시 type casting
	- __확장된 제네릭 타입에서 다형성을 보존하기위해 `bridge method` 생성__
		```java
		public class Node<T> {
			public T data;
			public Node(T data) { this.data = data; }
			public void setData(T data) {
				System.out.println("Node.setData");
				this.data = data;
			}
		}

		public class MyNode extends Node<Integer> {
			public MyNode(Integer data) { super(data); }

			public void setData(Integer data) {
				System.out.println("MyNode.setData");
				super.setData(data);
			}
		}
		```
		```java
		MyNode mn = new MyNode(5);
		Node n = (MyNode)mn;	// A raw type - compiler throws an unchecked warning
		n.setData("Hello");		// Causes a ClassCastException to be thrown.
		Integer x = (String)mn.data; // ClassCastException 오류 발생
		```

### __Array -> Generic__

* __Array__
	```java
	public class ChooserArray {
		private final Object[] choiceArray;

		public ChooserArray(Collection choices) {
			this.choiceArray = choices.toArray();
		}

		// 컬렉션안의 원소 중 하나를 무작위로 선택해 반환
		// 반환된 Object를 원하는 타입으로 형변환 필요
		// -> 타입이 다른게 들어가 있는 경우 런타임 오류 발생
		public Object choose() {
			Random rnd = ThreadLocalRandom.current();
			return choiceArray[rnd.nextInt(choiceArray.length)];
		}
	}
	```
	- 위 클래스의 경우 `choose()`를 호출할 때 반환된 `Object`를 원하는 타입으로 형변환 해야하며,
	- 이때 다른 타입의 원소가 들어 있다면 런타임 오류가 발생함
	- 이 경우 런타임 오류가 발생하지 않도록 `Generic`으로 변경 해주는 것이 좋음

* __Generic__
	```java
	package ch5.dahye.item28;
	import java.util.Collection;

	public class ChooserGeneric<T> {
		private final T[] choiceArray;

		public ChooserGeneric(Collection<T> choices) {
			this.choiceArray = (T[]) choices.toArray();
			// incompatible types 오류 -> (T[])로 형변환 필요
			// [경고발생] Unchecked cast: 'java.lang.Object[]' to 'T[]'
		}
	}
	```
	- __`T`__ 가 무슨타입인지 모르므로, 컴파일러는 형변환이 런타임에도 안전한지 보장 못함
	- (Generic은 원소의 타입정보가 소거되어 런타임시 타입을 알 수 없음)

* __비검사 형검사 경고 제거__
	```java
	public class Chooser<T> {
		private final List<T> choiceList;

		public Chooser(Collection<T> choices) {
			this.choiceList = new ArrayList<>(choices);
		}

		public T choose() {
			Random rnd = ThreadLocalRandom.current();
			return choiceList.get(rnd.nextInt(choiceList.size()));
		}
	}
	```
	- `Array` 대신 `List`를 사용하여, 형변환 경고를 제거 가능
	- __Runtime__ 시에 `ClassCastException`이 발생할 일도 없음

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