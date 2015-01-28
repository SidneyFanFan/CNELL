package basic;

public class RelationPattern {
	private String pattern;
	private String tag1; // TODO should be category
	private String tag2; // TODO should be category

	public RelationPattern(String pattern, String tag1, String tag2) {
		super();
		this.pattern = pattern;
		this.tag1 = tag1;
		this.tag2 = tag2;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getTag1() {
		return tag1;
	}

	public void setTag1(String tag1) {
		this.tag1 = tag1;
	}

	public String getTag2() {
		return tag2;
	}

	public void setTag2(String tag2) {
		this.tag2 = tag2;
	}

	@Override
	public String toString() {
		return String.format("[%s](%s,%s)", pattern, tag1, tag2);
	}
}
