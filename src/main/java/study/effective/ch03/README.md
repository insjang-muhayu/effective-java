# 3장. 모든 객체의 공통 메서드

## 목차

- [x] item 10. [Overriding equlas](#item-01-static-factory-method)
- [x] item 11. [Overriding hashCode](#item-02-builder)
- [x] item 12. [Overriding toString](#item-03-singleton)
- [x] item 13. [Overriding clone judiciously](#item-04-private-constructor)
- [x] item 14. [Consider implementing comparable](#item-05-dependency-injection)


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

-----------------------------------------------------------------
[[TOC]](#목차)

## item 11. Overriding hashCode
`[재정의]` equals 를 재정의하려거든 hashCode 도 재정의하라
### hashCode 란

-----------------------------------------------------------------
[[TOC]](#목차)

## item 12. Overriding toString
`[재정의]` toString 을 항상 재정의하라
### [ Public Static Final 필드 방식 ]


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