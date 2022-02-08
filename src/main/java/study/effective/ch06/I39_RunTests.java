package study.effective.ch06;

import java.lang.reflect.*;

public class I39_RunTests {
	public static void main(String[] args) 
		throws ClassNotFoundException, InvocationTargetException, IllegalAccessException 
	{
		int tests = 0; int passed = 0;
		Class<?> testClass = Class.forName("study.effective.ch06.I39_Sample"); //(args[0]);
		for (Method m : testClass.getDeclaredMethods()) {
			if (m.isAnnotationPresent(I39_Test.class)) {
				tests++;
				try {
					m.invoke(null);
					passed++;
				} catch (InvocationTargetException wrappedExc) {
					Throwable exc = wrappedExc.getCause();
					System.out.println(m + "실패: " + exc);
				} catch (Exception exception) {
					System.out.println("잘못 사용한 @I39_Test: " + m);
				}
			}
		}
		System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
	}
}