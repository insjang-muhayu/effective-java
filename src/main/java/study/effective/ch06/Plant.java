package study.effective.ch06;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Plant {
	enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }

	final String name;
	final LifeCycle lifeCycle;

	@Override public String toString() { return name; }

	public static void main(String[] args) {
		List<Plant> garden = new ArrayList<>();
		garden.add(new Plant("호도", LifeCycle.ANNUAL));
		garden.add(new Plant("땅콩", LifeCycle.ANNUAL));
		garden.add(new Plant("쌀", LifeCycle.PERENNIAL));
		// garden.add(new Plant("dd", LifeCycle.BIENNIAL));
		// garden.add(new Plant("ee", LifeCycle.BIENNIAL));

		@SuppressWarnings("unchecked") 
		Set<Plant>[] pSets = (Set<Plant>[]) new Set[Plant.LifeCycle.values().length];
		Map<Plant.LifeCycle, Set<Plant>> pMaps = new EnumMap<>(Plant.LifeCycle.class);

		for (int i = 0; i < pSets.length; i++) pSets[i] = new HashSet<>();
		for (Plant.LifeCycle lc : Plant.LifeCycle.values()) pMaps.put(lc, new HashSet<>());

		for (Plant p : garden) pSets[p.lifeCycle.ordinal()].add(p); // 비검사 형변환 오류
		for (Plant p : garden) pMaps.get(p.lifeCycle).add(p);

		System.out.println("======================== ordinal 기반 배열 인덱싱");
		for (int i = 0; i < pSets.length; i++) System.out.printf("%s: %s%n", Plant.LifeCycle.values()[i], pSets[i]);
		
		System.out.println("======================= EnumMap을 사용해 매핑");
		System.out.println(pMaps);

		System.out.println("======================= [Stream] EnumMap 미사용");
		System.out.println(garden.stream().collect(
			Collectors.groupingBy(p -> p.lifeCycle)
		));
	
		System.out.println("======================= [Stream] EnumMap 사용");
		System.out.println(garden.stream().collect(
			Collectors.groupingBy(
					p -> p.lifeCycle,
					() -> new EnumMap<>(LifeCycle.class), // 원하는 맵 구현체 명시
					Collectors.toSet()
				)
		));
	
	}
}
