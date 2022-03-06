package study.effective.ch07;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

public class I46_Stream {
	public static void main(String[] args) throws FileNotFoundException {
		File file = new File("D:/temp/README.txt");

		Map<String, Long> freq;
		try (Stream<String> words = new Scanner(file).tokens()) {
			freq = words.collect(groupingBy(String::toLowerCase, counting()));
		}
		System.out.println(freq);
		System.out.println("\n---------------------------------------\n");
		freq.keySet().stream().forEach(System.out::println);
		System.out.println("\n---------------------------------------\n");

		List<String> topTen = freq.keySet().stream() 
			.sorted(comparing(freq::get).reversed()) 
			.limit(10) 
			.collect(toList());
		topTen.stream().forEach(System.out::println);

	}
}