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

## __item 26. Raw type은 사용하지 마라__

### __ClassCastException 경고 발생__

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

### __비한정적 와일드카드 타입(Unbounded wildcard type)__

* `제네릭타입<?>` : 제한없음 (타입파라미터를 대치하는 구체적타입으로 모든 클래스나 인터페이스 타입이 올 수 있다)
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

### __예외케이스__

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

### __@SuppressWarnings("unchecked")__
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

### __[배열을 사용한 코드를 제네릭으로 만드는 방법]__

* __방법1. 제네릭 배열 생성 금지 제약 우회__
	- `@SuppressWarnings("unchecked")` 을 추가하여, 경고문구가 발생을 숨김 
	- 명시적으로 형변환을 하지 않고도 `ClassCastException` 을 걱정없이 사용 가능

* __방법2. Object[]로 타입 변경__
	- `elems` 의 타입을 `Object[]`로 변경하는 방법
	- 제네릭 타입 안에서 리스트를 사용하는 것이 항상 가능한 것도, 좋은 것도 아니다.

```java
	public class Stack<E> { // GenericType
		// private으로 저장
		private E[] elems; // GenericType
		private int size = 0;
		private static final int INIT_CAPACITY = 16;

		@SuppressWarnings("unchecked") // [방법1] 어노테이션 추가 !!!
		public Stack() {
			// elems = new E[INIT_CAPACITY]; // Unchecked cast: 'Object[]' to 'E[]' 
			elems = (E[]) new E[INIT_CAPACITY]; // [방법1]
		}

		public void push(E e) {
			ensureCapacity(); 
			elems[size++] = e;
		}

		public E pop() {
			if (isEmpty()) throw new EmptyStackException();

			// E result = (E) elems[size--]; // Unchecked cast: 'Object' to 'E'
			@SuppressWarnings("unchecked") E result = (E) elems[size--]; // [방법2]
			elems[size] = null; // 다 쓴 참조 해제
			return result;
		}

		public boolean isEmpty() { return size == 0; }

		private void ensureCapacity() {
			if (elems.length == size) elems = Arrays.copyOf(elems, 2 * size + 1);
		}
	}
```

### __[제네릭 Stack을 사용하는 맛보기 프로그램]__

> 대부분 제네릭타입은 타입매개변수에 아무런 제약을 두지 않으며,  
> `Stack<Object>, Stack<int[]>, Stack<List<String>>` 등 어떤 참조타입으로도 생성 가능   
> __(단, 기본타입(`Stack<int>, Stack<double>`)은 사용할 수 없다)__

```java
	public static void main(String[] args) {
		Stack<String> stack = new Stack<>();
		for (String itm : args) stack.push(itm);
		while (!stack.isEmpty()) System.out.println(stack.pop().toUpperCase());
	}
```

### __[타입 매개변수에 제약을 두는 제네릭 타입]__

```java
	public class DelayQueue<E extends Delayed> implements BlockingQueue<E>
```

---------------------------------------------------------------
[[TOC]](#목차)

## item 30. 이왕이면 Generic Method로 만들어라

### __제네릭 메서드__
> __제네릭 메서드__ 로 바꿔주면 경고문구가 뜨지 않으며, 타입안전성도 지켜지는 것을 확인 가능

__[제네릭 메서드]__
```java
	public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
		Set<E> result = new HashSet<>(s1); // raw type
		result.addAll(s2);
		return result;
	}
```
__[제네릭 메서드를 활용하는 간단한 프로그램]__
```java
	public static void main(String[] args) {
		Set<String> guys = Set.of("톰", "딕", "해리");
		Set<String> stooges = Set.of("래리", "모에", "컬리");
		Set<String> aflCio = union(guys, stooges);
		System.out.println(aflCio); // [모에, 톰, 해리, 래리, 컬리, 딕]
	}
```

### __제네릭 싱글턴 팩터리__
> 때때로 불변객체를 여러 타입으로 활용할 수 있게 만들어야 하는 경우가 있다.   
> 요청한 타입매개변수에 맞게 매번 그 객체타입을 바꿔주는 __제네릭 싱글턴 팩터리__ 를 이용해 구현 가능

```java
	public class GenericMethodTest {
		private static UnaryOperator<Object> IDNTITY_FN = (t) -> t;

		@SuppressWarnings("unchecked")
		private static <T> UnaryOperator<T> identityFunction() {
			return (UnaryOperator<T>) IDNTITY_FN;
		}

		public static void main(String[] args) {
			String[] strings = {"faker", "keria", "teddy"};
			UnaryOperator<String> sameString = identityFunction();
			for (String s : strings) {
				System.out.println(sameString.apply(s));
			}

			Number[] numbers = {1, 2.0, 3L};
			UnaryOperator<Number> sameNumber = identityFunction();
			for (Number n : numbers) {
				System.out.println(sameNumber.apply(n));
			}
		}
	}
```



### __재귀적 타입 한정 (Recursive Type Bound)__
자기 자신이 들어간 표현식을 사용하여, 타입 매개변수의 허용범위를 한정하는 `Recursive Type Bound` 개념이 있다. `Recursive Type Bound`은 주로 타입의 순서를 정하는 `Comparable` 과 함께 사용한다.
```java
public interface Comparable<T> {
	public int compareTo(T o);
}
```
타입한정인 `<E extends Comparable<E>>`는 "모든 타입 E는 자신과 비교할 수 있다"라는 의미로 해석할 수 있으며, 상호 비교가 가능하다는 것을 의미
```java
public static <E extends Comparable<E>> E max(Collection<E> c) {
	if (c.isEmpty()) throw new IllegalArgumentException("collection is empty");

	E result = null;
	for (E e : c) {
		if (result == null || e.compareTo(result) > 0) 
			result = Objects.requireNonNull(e);
	}
	return result;
}
```


---------------------------------------------------------------
[[TOC]](#목차)

## item 31. 한정적 와일드카드를 사용해 API 유연성을 높여라

### __경계 와일드카드 (Bounded Wildcard)__
경계 와일드카드(`Bounded Wildcard`)를 이용해서 해결 예시
```java
//	public void pushAll(Iterable<E> src) { // [e1] compile error
//		==> upper bounded wildcard 로 해결
	public void pushAll(Iterable<? extends E> src) {
		for (E e : src) push(e);
	}

//	public void popAll(Collection<E> dst) { // [e2]
//		==> lower bounded wildcard 로 해결
	public void popAll(Collection<? super E> dst) { 
		while (!isEmpty()) dst.add(pop());
	}

	public static void main(String[] args) {
		Stack<Number> stack = new Stack<>();

		Iterable<Integer> integers = ...;
		// [e1] : (Iterable<Number> != Iterable<Integer>) compile error
		stack.pushAll(integers); 

		Collection<Object> objects = ...;
		// [e2] : (Collection<Object> != Collection<Number>) compile error
		stack.popAll(objects); 
	}
```

### __PECS__
어떤 와일드 카드를 사용해야 하는지 기억하는데 있어서 `PECS` 공식 활용

* __PECS : producer - extends, consumer - super__
* T가 생산자이면 상한경계와일드카드(`<? extends E>`)를 사용
* T가 소비자이면 하한경계와일드카드(`<? super E>`)를 사용
```java
//	public static <E extends Comparable<E>> E max(List<E> c) {
	public static <E extends Comparable<? super E>> E max(List<? extends E> c) {
		if (c.isEmpty()) throw new IllegalArgumentException("collection is empty");

		E result = null;
		for (E e : c) {
			if (result == null || e.compareTo(result) > 0) 
				result = Objects.requireNonNull(e);
		}

		return result;
	}
```
+ __`List<E>` :--> `List<? extends E>`__ : 입력매개변수는 `E`인스턴스를 생성
* __`Comparable<E>` :--> `Comparable<? super E>`__ : `Comparable`은 언제나 소비자 역할
* `Comparable`와 `Comparator`는 모두 소비자이다.
* 메서드선언에 타입 매개변수가 한번만 나오면 wildcard로 대체하는 것이 좋다.

### __private static helper 메서드__
컴파일러는 변수에 잘못된 타입의 값을 할당하고 있다고 믿는다는 것을 의미
```java
	public static void swap(List<?> list, int i, int j) {
	//	list.set(i, list.set(j, list.get(i)));
		swapHelper(list, i, j);
	};
	// Helper method created so that the wildcard can be captured through type inference.
	public static <E> void swapHelper(List<E> list, int i, int j) {
		list.set(i, list.set(j, list.get(i)));
	};
```


---------------------------------------------------------------
[[TOC]](#목차)

## item 32. 제네릭과 가변인수를 함께 쓸 때는 신중해라

### @SafeVarargs
* 제네릭 가변인수 메서드의 타입 안전이 확실히 보장될 때는  
	Java 7부터 @SafeVarargs 어노테이션으로 경고문구를 숨길 수 있다. 
* 메서드가 안전한 경우 :
	- varargs 매개변수 배열에 아무것도 저장하지 않는다.
	- 배열 혹은 복제본을 신뢰할 수 없는 코드에 노출하지 않는다.

* `varargs` 매개변수를 안전하게 사용하는 전형적인 예
	```java
	@SafeVarargs
	static <T> List<T> flatten(List<? extends T>... lists) {
		List<T> result = new ArrayList<>();
		for (List<? extends T> list : lists) result.addAll(list);
		return result;
	}
	```
### List.of
* 정적 팩터리 메서드인 List.of를 활용해 메서드에 임의의 인수를 넘길 수 있다. (List.of 에 @SafeVarargs가 있기때문에 가능)
* __장점__ 은 컴파일러가 메서드 타입 안정성을 검증할 수 있는데 있다.  
	프로그래머가 @SafeVarargs 어노테이션을 달지 않아도 되며,  
	실수로 안전하다고 판단할 걱정도 없다. 
* __단점__ 은 클라이언트 코드가 살짝 지저분해지고,  
	속도가 약간 느려질 수 있다는 점이다.
	```java
	public class VarargsTest {
		@Test
		void heapPollutionTest() {
			List<String> attrs = pickTwo("좋은", "빠른", "저렴한");
			System.out.println("attrs = " + Arrays.toString(attrs));
		}

		static <T> List<T> pickTwo(T a, T b, T c) {
			switch (ThreadLocalRandom.current().nextInt(3)) {
				case 0: return List.of(a, b);
				case 1: return List.of(a, c);
				case 2: return List.of(b, c);
			}
			throw new AssertionError();
		}
	}
	```


---------------------------------------------------------------
[[TOC]](#목차)

## item 33. 타입 안전 이종 컨테이너를 고려해라




---------------------------------------------------------------
[[TOC]](#목차)


... 5장 끝 ...

[<<< 4장 [이전]](../ch04/README.md) ----- [[TOC]](#목차) -----  [[다음] 6장 >>>](../ch06/README.md)