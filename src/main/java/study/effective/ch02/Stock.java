package ch02;

public abstract class Stock02 {
	private final int A, B, C;
	
	public Stock02() { this(0); }
	public Stock02(int a) { this(a, 0); }
	public Stock02(int a, int b) { this(a, b, 0);	}
	public Stock02(int a, int b, int c) {
		this.A = a;
		this.B = b;
		this.C = c;
	}
}

class Stock {
	public static void main(String[] args) {
		Stock aaa = new Stock02(1);
		System.out.println("---------------");
	}
}
