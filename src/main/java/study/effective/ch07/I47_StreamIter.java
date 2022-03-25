package study.effective.ch07;

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class I47_StreamIter {
	// Stream -> Iterable 어댑터
	public static <E> Iterable<E> iterableOf(Stream<E> stream) {
		return stream::iterator;
	}

	// Iterable -> Stream 어댑터
	public static <E> Stream<E> streamOf(Iterable<E> iterable){
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	public static void main(String[] args) {
		Stream<String> dev2u = Stream.of("장인순", "이민승", "최혜환", "이규명");

		System.out.println("\n===================== dev2u : Stream iterator 메소드 사용");
		for (String member : (Iterable<String>) dev2u::iterator) { // 형변환
			System.out.println(member);
		}

		// [빈번한 실수] 스트림은 재사용할 수 없다. 오직 한번만 사용
		// Stream dev2u 재사용하면 다음과 같은 오류 발생
		//	--> java.lang.IllegalStateException: stream has already been operated upon or closed
		Stream<String> dev4u = Stream.of("이강원", "최유빈");
		System.out.println("\n===================== dev4u : Stream -> Iterable 어댑터");
		for (String member : iterableOf(dev4u)) { // 어댑터 메소드 사용
			System.out.println(member);
		}

		Iterable<String> dev8u = Arrays.asList("장인순", "이민승", "최혜환", "이규명", "이강원", "최유빈");
		System.out.println("\n===================== dev8u : Iterable -> Stream 어댑터");
		streamOf(dev8u).forEach(System.out::println);
	}
}