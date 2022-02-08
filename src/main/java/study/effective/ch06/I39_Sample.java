package study.effective.ch06;

public class I39_Sample {
	@I39_Test public static void m1() { } // 설공
	public static void m2() { }
	@I39_Test public static void m3() { throw new RuntimeException("실패"); } // 실패
	public static void m4() { }
	@I39_Test public void m5() { } // 잘못 사용한 예: 정적 메서드가 아니다.
	public static void m6() { }
	@I39_Test public static void m7() { throw new RuntimeException("실패"); } // 실패
	public static void m8() { }
}
