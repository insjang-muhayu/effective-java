package item1;

// 생성자 대신 정적 팩터리 메서드를 고려하라
// 3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.
// 4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
public abstract class Code3 {

	abstract String getCode();

	static Code3 getNewInstance(String code) {
		Code3 code3;

		if (code.equals("wrong")) {
			code3 = new WrongCode();
		} else {
			code3 = new RightCode();
		}

		return code3;
	}
}

class WrongCode extends Code3 {

	@Override
	String getCode() {
		return "wrong code";
	}
}

class RightCode extends Code3 {
	@Override
	String getCode() {
		return "right code";
	}
}
