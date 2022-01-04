# 2장 객체 생성과 파괴

### ITEM-01. [생성자] Static Factory Method를 고려하라.
------------------------------------------------------------------------------------
1. __객체의 특성에 적합한 작명 가능__
	* `BigInteger(int, int, Random)` -> `BigInteger.probablePrime`
2. __인스턴스를 새로 생성하지 않아도 됨__
	* 인스턴스 캐슁하여 재활용 : 불필요한 객체생성 회피, 반복 호출 시 성능향상 기여
	* 통제된 인스턴스 클래스 (instance-controlled) : Singleton(ITEM-03), Noninstantiable(ITEM-04) 가능
	* `Boolean.valueOf(boolean)`
3. __하위 타입 객체 반환(return) 가능__
	* 구체적인 예가 없어 이해안됨 -> skip (ITEM-20 에서 파악 필요)
	* 자바8 부터 인터페이스에 public static method 가능
4. __입력 매개변수에 따라 다른 클래스 객체 반환 가능__
5. __작성 시점에는 반환할 객체의 클래스 부재 가능__
	* Service Provider Framework 근간 : 대표적 사례 JDBC Provider
		* Service Interface : `Connection`
		* Privider Registration API : `DriverManager.registerDriver`
		* Service Access API : `DriverManager.getConnection`
		* *Service Provider Interface*
	* 자바6 부터 범용 Service Provider Framework 제공 : `java.util.ServiceLoader`
	* 단점 :
		* 하위 클래스 생성 불가
		* API 설명 힌트 지원 안됨 (주석 잘 달아야 함)
	* 대표 명명 방식들
		* **from** :  
			`Date dt = Date.from(instant);`
		* **of** :  
			`Set<Rank> faceCards = EnumSet.of(JACK, QUEEN, KING);`
		* **valueOf** :  
			`BigInteger prime = BigInteger.valueOf(Integer.MAX_VALUE);`
		* **instance** :  
			`StackWalker luke = StackWalker.instance(options);`
		* **create** :  
			`Object newArray = Array.create(classObject, arrayLen);`
		* **get{Type}** :  
			`FileStore fs = Files.getFileStore(path)`
		* **new{Type}** :  
			`BufferedReader br = Files.newBufferedReader(path);`
		* **{Type}** :  
			`List<Complaint> litany = Collectins.list(legacyLitany);`


### ITEM-02. [생성자: 많은 매개변수] Builder를 고려하라.
------------------------------------------------------------------------------------
* 점층적 생성자 패턴 (Telescoping Constructor Pattern)
	* 매개변수의 개수/타입로 생성자를 구분하는 방식
	* 생성자에 필요한 매개변수가 단순할 때 사용 (가장 기초적 생성자 패턴)
		``` java
		public abstract class Stock02 {
			private final int A, B, C;		
			public Stock02(int a) { 
				this(a, 0); 
			}
			public Stock02(int a, int b) { 
				this(a, b, 0); 
			}
			public Stock02(int a, int b, int c) {
				this.A = a; this.B = b; this.C = c;
			}
		}
		```

* 자바빈즈 패턴 (JavaBeans Pattern)
	* MVC의 Model 클래스와 비슷 (Setter/Getter 함수로 이루어짐)
	* 단점 : 
		* 객체 하나 만들기 위해 setter method 를 여러번 호출해야 함 
		* 객체 생성 완료 전까지 일관성 깨질수 있음 (심각한 버그 유발)
		* 일관성 유지를 위해 **Freezing Method** 필요
		
* 빌더 패턴 (Builder Pattern)
	* 안전성과 가독성 겸비
	* 

	