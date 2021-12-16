package item1;

// 생성자 대신 정적 팩터리 메서드를 고려하라
// 1. 이름을 가질 수 있다.
public class Code1 {

    private String name;
    private String code;

    public Code1() {}

//  생성 불가
//    public Code1(String name) {
//        this.name = name;
//    }
//
//    public Code1(String code) {
//        this.code = code;
//    }

    static Code1 getName(String name) {
        Code1 code1 = new Code1();
        code1.name = name;

        return code1;
    }

    static Code1 getCode(String code) {
        Code1 code1 = new Code1();
        code1.code = code;

        return code1;
    }
}
