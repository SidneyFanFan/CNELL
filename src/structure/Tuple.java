package structure;

public class Tuple<A, B> {
	private A arg1;
	private B arg2;

	public Tuple(A arg1, B arg2) {
		super();
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	public A getArg1() {
		return arg1;
	}

	public B getArg2() {
		return arg2;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", arg1.toString(), arg2.toString());
	}

}
