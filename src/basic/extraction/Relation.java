package basic.extraction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basic.CNWord;

public class Relation {

	private CNWord arg1;
	private CNWord arg2;
	private String pattern;
	private int weight;

	public Relation(CNWord arg1, CNWord arg2, String pattern, int weight) {
		super();
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.pattern = pattern;
		this.weight = weight;
	}

	public CNWord getArg1() {
		return arg1;
	}

	public CNWord getArg2() {
		return arg2;
	}

	public String getPattern() {
		return pattern;
	}

	public int getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		String ret = String.format("[%s](%s, %s)=%d", pattern, arg1.toString(),
				arg2.toString(), weight);
		return ret;
	}

	public String getInstances() {
		String ret = String
				.format("(%s, %s)", arg1.toString(), arg2.toString());
		return ret;
	}

	public static Relation parse(String str) {
		String regex = "^\\[(.*)\\]\\((.*), (.*)\\)=(.*)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		boolean ret = matcher.find();
		if (ret) {
			String p = matcher.group(1);
			CNWord arg1 = CNWord.parse(matcher.group(2));
			CNWord arg2 = CNWord.parse(matcher.group(3));
			int weight = Integer.parseInt(matcher.group(4));
			return new Relation(arg1, arg2, p, weight);
		}
		return null;
	}

}
