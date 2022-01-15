package study.effective.ch02;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EJTestItem04 {
	@Test
	void testBuild1() {
		// reflection
		Constructor<?>[] constructors = StockUtils.class.getDeclaredConstructors();
		for (Constructor<?> constructor : constructors) {
			constructor.setAccessible(true);
			Assertions.assertThrows(InvocationTargetException.class, constructor::newInstance);
		}
	}

	public static void main(String[] args) {
		String url = "https://m.nocafe.com/";
		String html = StockUtils.getHtml(url);
		System.out.println("[\n"+html+"\n]");
	}
}
