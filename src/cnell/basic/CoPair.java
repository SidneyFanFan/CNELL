package cnell.basic;

public class CoPair {
	private String p, q;
	private int count;

	public CoPair(String p, String q, int count) {
		super();
		this.p = p;
		this.q = q;
		this.count = count;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return String.format("(%s,%s)=%d", p, q, count);
	}

}