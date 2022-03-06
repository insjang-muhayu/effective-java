package study.effective.ch07;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class I45_StreamPL {
	public static void main(String[] args) {
		// filter()
		IntStream sub1 = IntStream.of(1, 2, 3, 4, 5).filter(val -> val > 3);
		System.out.println(sub1.count());	// 2

		// map()
		Stream inp2 = Stream.of("Welcome", "To", "java", "blog");
		Stream sub2 = inp2.map(str -> { return (str.equals("java"))? "Insjang" : str; });
		System.out.println(sub2.collect(Collectors.toList())); // [Welcome, To, Insjang, blog]

		// flatMap()
		String[] arr = {"I study hard", "You study JAVA", "I am hungry"};
		Stream<String> inp3 = Arrays.stream(arr);
		inp3.flatMap(s -> Stream.of(s.split(" "))).distinct().forEach(System.out::println);
		System.out.println("\n---------------------------------------\n");

		// Stream sorted()
		Stream.of("[tomoto]", "[Green Chilli]", "[Pototo]", "[Beet root]")
			.sorted().forEach(System.out::print); // [Beet root][Green Chilli][Pototo][tomoto]
		System.out.println("\n---------------------------------------\n");
			
		// Stream limit​(long maxSize)
		Stream.of("[one]", "[two]", "[three]", "[four]")
			.limit(2).forEach(System.out::print); // [one][two]
		System.out.println("\n---------------------------------------\n");
		
		// Stream skip​(long n)
		Stream.of("[one]", "[two]", "[three]", "[four]", "[five]")
			.skip(2).forEach(System.out::print); // [three][four][five]

			
		System.out.println("\n================================================\n");

		Stream.of("[넷]", "[둘]", "[셋]", "[하나]").forEach(System.out::print);
		System.out.println();

		Stream.of("[넷]", "[둘]", "[셋]", "[하나]")
			.reduce((s1, s2) -> s1 + "++" + s2).ifPresent(System.out::print);
		System.out.println();
	
		System.out.println(Stream.of("[넷]", "[둘]", "[셋]", "[하나]").reduce("시작", (s1, s2) -> s1 + "++" + s2));
		System.out.println("---------------------------------------");
	
		System.out.println(IntStream.of(30, 90, 70, 10).anyMatch(n -> n > 80)); // true
		System.out.println(IntStream.of(30, 90, 70, 10).allMatch(n -> n > 80)); // false
		System.out.println("---------------------------------------");
	
		System.out.println(IntStream.of(30, 90, 70, 10).count()); // 4
		System.out.println(IntStream.of(30, 90, 70, 10).sum()); // 200
		System.out.println("---------------------------------------");
	
		System.out.println(IntStream.of(30, 90, 70, 10).max().getAsInt()); // 90
		System.out.println(DoubleStream.of(30.3, 90.9, 70.7, 10.1).average().getAsDouble()); // 50.5
		System.out.println("---------------------------------------");
	
		Map<Boolean, List<String>> part = Stream.of("HTML", "CSS", "JAVA", "PHP")
						.collect(Collectors.partitioningBy(s -> (s.length() % 2) == 0));
		List<String> oddLengthList = part.get(false); System.out.println(oddLengthList);
		List<String> evenLengthList = part.get(true); System.out.println(evenLengthList);
	}
}