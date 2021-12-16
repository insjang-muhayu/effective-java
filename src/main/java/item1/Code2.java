package item1;

// 생성자 대신 정적 팩터리 메서드를 고려하라
// 2. 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.
public class Code2 {

	static Code2 instance;

	static Code2 getInstance() {
		if (instance == null) {
			instance = new Code2();
		}

		return instance;
	}

	public static void main(String[] args) {
		Code2 code = Code2.getInstance();
	}
}
