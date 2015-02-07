package extractElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import structure.Tuple;
import basic.CNPattern;
import basic.CNRelation;

public class RelationPattern implements CNRelation,
		CNPattern<Tuple<String, String>> {
	private String pattern;
	private String c1;
	private String c2;

	public RelationPattern(String pattern, String tag1, String tag2) {
		super();
		this.pattern = pattern;
		this.c1 = tag1;
		this.c2 = tag2;
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

	@Override
	public String toString() {
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
		String regex = "^\\[(.*)\\]\\((((.*)\\/(.*)),((.*)\\/(.*)))\\)=(.*)$";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(str);
		boolean ret = matcher.find();
		if (ret) {
			pattern = matcher.group(1);
			c1 = matcher.group(5);
			c2 = matcher.group(8);
			return new RelationPattern(pattern, c1, c2);
		}
		return null;
	}

}
