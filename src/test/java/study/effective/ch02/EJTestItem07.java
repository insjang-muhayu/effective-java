package study.effective.ch02;

import java.util.HashMap;
import java.util.WeakHashMap;

public class EJTestItem07 {
	public static void main(String[] args){
		Integer key1 = 1000; Integer key2 = 2000; Integer key3 = 3000;

		HashMap<Integer, String> hashMap = new HashMap<>();
		hashMap.put(key3, "test c");
		hashMap.put(key2, "test b");
		key3 = null;

		System.out.println("[HashMap GC 수행 전]");
		hashMap.entrySet().stream().forEach(el -> System.out.println("    "+el));

		System.out.println("[== GC 수행 ==]");
		System.gc(); // GC 수행을 해도 key3

		System.out.println("[HashMap GC 수행 후]");
		hashMap.entrySet().stream().forEach(el -> System.out.println("    "+el));

		System.out.println("========================================");

		WeakHashMap<Integer, String> weakMap = new WeakHashMap<>();
		weakMap.put(key1, "test a");
		weakMap.put(key2, "test b");
		key1 = null;

		System.out.println("[WeakHashMap GC 수행 전]");
		weakMap.entrySet().stream().forEach(el -> System.out.println("    "+el));

		System.out.println("[== GC 수행 ==]");
		System.gc(); // GC 수행

		System.out.println("[WeakHashMap GC 수행 후]");
		weakMap.entrySet().stream().forEach(el -> System.out.println("    "+el));

		// [output]
		// [HashMap GC 수행 전]
		//     2000=test b
		//     3000=test c
		// [== GC 수행 ==]
		// [HashMap GC 수행 후]
		//     2000=test b
		//     3000=test c
		// ========================================
		// [WeakHashMap GC 수행 전]
		//     1000=test a
		//     2000=test b
		// [== GC 수행 ==]
		// [WeakHashMap GC 수행 후]
		//     2000=test b
	}

}