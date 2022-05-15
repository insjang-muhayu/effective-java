# 8장. 메서드

## 목차

- [x] item 49. [매개변수가 유효한지 검사하라](#item-49-매개변수가-유효한지-검사하라)
- [x] item 50. [적시에 방어적 복사본을 만들라](#item-50-적시에-방어적-복사본을-만들라)
- [x] item 51. [메서드 시그니처를 신중히 설계하라](#item-51-메서드-시그니처를-신중히-설계하라)
- [x] item 52. [다중정의는 신중히 사용하라](#item-52-다중정의는-신중히-사용하라)
- [x] item 53. [가변인수는 신중히 사용하라](#item-53-가변인수는-신중히-사용하라)
- [x] item 54. [null이 아닌, 빈 컬렉션이나 배열을 반환하라](#item-54-null이-아닌-빈-컬렉션이나-배열을-반환하라)
- [x] item 55. [옵셔널 반환은 신중히 하라](#item-55-옵셔널-반환은-신중히-하라)
- [x] item 56. [공개된 API 요소에는 항상 문서화 주석을 작성하라](#item-56-공개된-api-요소에는-항상-문서화-주석을-작성하라)


---------------------------------------------------------------

## item 49. 매개변수가 유효한지 검사하라

[[TOC]](#목차)

### **public / protectd method**

`public` 과 `protected` 메서드는 매개변수 값이 잘못됐을 때 던지는 예외를 문서화 해야한다. (`@throws` 자바독 태그 이용)

```java
	/**
	 * (현재 값 mod m) 값을 반환
	 * 항상 음이 아닌 BigInteger를 반환한다는 점에서 remainder와 다름
	 *
	 * @param m 계수(양수)
	 * @return 현재 값 mod m
	 * @throws ArithmeticException m이 0 이하이면 발생
	 */
	public BigInteger mod(BigInteger m) {
		if (m.signum() <= 0) throw new ArithmeticException("계수(m)는 양수여야 합니다. " + m);
		return m;
	}
```
이렇게 발생하는 예외도 같이 문서화 해두면, 간단한 방법으로 API 사용자가 제약을 지킬 가능성이 높아진다. 
위 예제에서 NullPointException 에 대한 문서화를 하지 않은 이유는 BigInteger 클래스 수준에서 기술되어 있기 때문이다. 
클래스 수준 주석은 해당 클래스의 모든 public 메서드에 적용되므로, 각 메서드에 일일이 기술하지 않아도 된다.

#### **[ Objects.requireNonNull ]**
* java7에 추가
* `java.util.Objects.requireNonNull`
* null 검사를 수동으로 하지 않아도 된다.
* 원하는 예외 메세지도 지정할 수 있다.
* 입력 값을 그대로 반환해, 값을 사용하는 동시에 검사도 가능
```java
this.strategy = Objects.requireNonNull(strategy, "전략 값이 없습니다.");
```

#### **[ Java9 Objects 범위검사 ]**
Java9에서는 범위 검사 기능도 추가됐다.
* checkFromIndexSize
* checkFromToIndex
* checkIndex

위 메서드들은 null 검사 메서드만큼 유연하지는 않다.
* 예외 메세지 지정불가능
* 리스트와 배열 전용으로 설계
* 닫힌 범위는 다루지 못함


### **private method**
`private` 메서드의 경우 `assert` 을 사용해 매개변수 유효성을 검증 가능
```java
private static void sort(long a[], int offset, int length) {
	assert a != null;
	assert offset >= 0 && offset <= a.length;
	assert length >= 0 && length <= a.length - offset;
}
```
* 실패시 `AssertionError` 예외를 발생
	```java
	java.lang.AssertionError
		at ch8.dahye.item49.MethodTest.sort(MethodTest.java:39)
		...
	```
* 런타임에 아무런 효과와 성능 저하도 없음
* `-ea` or `--enableassertions` 플래그를 설정하면, 런타임시 영향을 줌

---------------------------------------------------------------

## item 50. 적시에 방어적 복사본을 만들라

[[TOC]](#목차)

### **gggg**

```java

```

```java

```

```java

```

```java

```


---------------------------------------------------------------

## item 51. 메서드 시그니처를 신중히 설계하라

[[TOC]](#목차)

### **gggg**

```java

```

```java

```

```java

```

```java

```


---------------------------------------------------------------

## item 52. 다중정의는 신중히 사용하라

[[TOC]](#목차)

### **gggg**

```java

```

```java

```

```java

```

```java

```


---------------------------------------------------------------

## item 53. 가변인수는 신중히 사용하라

[[TOC]](#목차)

### **gggg**

```java

```

```java

```

```java

```

```java

```


---------------------------------------------------------------

## item 54. null이 아닌, 빈 컬렉션이나 배열을 반환하라

[[TOC]](#목차)

### **gggg**

```java

```

```java

```

```java

```

```java

```


---------------------------------------------------------------

## item 55. 옵셔널 반환은 신중히 하라

[[TOC]](#목차)

### **gggg**

```java

```

```java

```

```java

```

```java

```


---------------------------------------------------------------

## item 56. 공개된 API 요소에는 항상 문서화 주석을 작성하라

[[TOC]](#목차)

### **gggg**

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


... 8장 끝 ...

[<<< 7장 [이전]](../ch07/README.md) ----- [[TOC]](#목차) -----  [[다음] 9장 >>>](../ch09/README.md)
