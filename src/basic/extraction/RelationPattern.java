package basic.extraction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import structure.Tuple;
import basic.CNPattern;
import basic.CNRelation;

public class RelationPattern implements CNRelation,
		CNPattern<Tuple<String, String>> {
	protected String pattern;
	protected String c1;
	protected String c2;
	protected int weight;

	public RelationPattern(String pattern, String c1, String c2, int weight) {
		super();
		this.pattern = pattern;
		this.c1 = c1;
		this.c2 = c2;
		this.weight = weight;
	}

	public String getPattern() {
		return pattern;
	}

	public String getC1() {
		return c1;
	}

	public String getC2() {
		return c2;
	}

	public int getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return String.format("[%s](%s,%s)=%d", pattern, c1, c2, weight);
	}

	public String getPatternString() {
		return String.format("[%s](%s,%s)", pattern, c1, c2);
	}

	@Override
	public Tuple<String, String> match(String str) {
		int pos;
		String[] prevTokens, nextTokens;
		String prevWord, nextWord;
		// TODO after category should use CNEntity
		// CNEntity prevWord, nextWord;

		pos = str.indexOf(pattern);
		if (pos >= 0) {
			// found instance
			prevTokens = str.substring(0, pos).split(", ");
			nextTokens = str.substring(pos + pattern.length()).split(", ");
			// the last one is empty
			if (prevTokens.length > 1 && nextTokens.length > 2) {
				prevWord = prevTokens[prevTokens.length - 1];
				nextWord = nextTokens[1];
				// check
				if (prevWord.contains(c1) && nextWord.contains(c2)) {
					return new Tuple<String, String>(prevWord, nextWord);
				}
			}
		}
		return null;
	}

	public static RelationPattern parse(String str) {
		String c1, c2;
		String pattern;
		int weight;
		String regex = "^\\[(.*)\\]\\((((.*)\\/(.*)),((.*)\\/(.*)))\\)=(.*)$";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(str);
		boolean ret = matcher.find();
		if (ret) {
			pattern = matcher.group(1);
			c1 = matcher.group(5);
			c2 = matcher.group(8);
			weight = Integer.parseInt(matcher.group(9));
			return new RelationPattern(pattern, c1, c2, weight);
		}
		return null;
	}

}
