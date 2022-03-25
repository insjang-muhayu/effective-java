package study.effective.ch07;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class I47_PowerSet {

	public static final <E> Collection<Set<E>> of(Set<E> s) {
		List<E> src = new ArrayList<>(s);
		if (src.size() > 30) 
			throw new IllegalArgumentException("Set too big (MAX:30) : " + s);
		
		return new AbstractList<Set<E>>() {
			@Override public int size() {
				return 1 << src.size(); // 2 to the power srcSize
			}
			@Override public boolean contains(Object o) {
				return o instanceof Set && src.containsAll((Set)o);
			}
			@Override public Set<E> get(int index) {
				Set<E> result = new HashSet<>();
				for (int i = 0; index != 0; i++, index >>= 1) {
					if ((index & 1) == 1) result.add(src.get(i));
				}
				return result;
			}
		};
	}

	public static void main(String[] args) {
		Set<String> dev2u = new HashSet(Arrays.asList("장인순", "이민승", "최혜환", "이규명"));

		System.out.println(dev2u);

		Collection<Set<String>> memberJoins = I47_PowerSet.of(dev2u);		
		for (Set<String> member : memberJoins) {
			System.out.println(member);
		}
	}

}