package cnell.basic.extraction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cnell.basic.CNWord;

public class RelationInstance {

	private CNWord arg1;
	private CNWord arg2;

	private int weight;

	public RelationInstance(CNWord arg1, CNWord arg2, int weight) {
		super();
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.weight = weight;
	}

	public CNWord getArg1() {
		return arg1;
	}

	public CNWord getArg2() {
		return arg2;
	}

	public int getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", arg1.toString(), arg2.toString());
	}

	public static RelationInstance parse(String str) {
		String regex = "^\\[(.*)\\]\\((.*), (.*)\\)=(.*)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		boolean ret = matcher.find();
		if (ret) {
			CNWord arg1 = CNWord.parse(matcher.group(2));
			CNWord arg2 = CNWord.parse(matcher.group(3));
			int weight = Integer.parseInt(matcher.group(4));
			return new RelationInstance(arg1, arg2, weight);
		}
		return null;
	}

	public HashSet<String> match(String line) {
		int pos1, pos2;
		String wordWithTag, word;
		String[] wordWithTagSplit;
		String patternStr = null;
		HashSet<String> patternSet = new HashSet<String>();
		String[] lineSplit = line.split(", ");
		// System.out.println("check: " + line);
		pos1 = -1;
		pos2 = -1;
		for (int i = 0; i < lineSplit.length; i++) {
			wordWithTag = lineSplit[i];
			wordWithTagSplit = wordWithTag.split("/");
			if (wordWithTagSplit.length != 2) {
				continue;
			}
			word = wordWithTagSplit[0];
			if (word.equals(arg1.getWord())) {
				pos1 = i;
				if (pos2 != -1) {
					// export a pair
					patternStr = exportPair(pos1, pos2, lineSplit);
					pos1 = -1;
					// pos2 = -1;
				}
			}
			if (word.equals(arg2.getWord())) {
				pos2 = i;
				if (pos1 != -1) {
					// export a pair
					patternStr = exportPair(pos1, pos2, lineSplit);
					// pos1 = -1;
					pos2 = -1;
				}
			}
			if (patternStr != null) {
				patternSet.add(patternStr);
			}
		}

		return patternSet;
	}

	private String exportPair(int pos1, int pos2, String[] lineSplit) {
		int before, after;
		String tmpWord;
		String sentence = null;
		List<String> tokens = new ArrayList<String>();
		boolean hasNR = false;
		boolean hasNN = false;
		boolean hasVV = false;

		before = pos1 < pos2 ? pos1 : pos2;
		after = pos1 < pos2 ? pos2 : pos1;

		// contains no more than 5 tokens
		// has a content word
		// has an uncapitalized word, for Chinese is NR
		// and has at least one non-noun.
		for (int i = before + 1; i < after; i++) {
			tmpWord = lineSplit[i];
			if (tmpWord.contains("NR")) {
				hasNR = true;
			}
			if (tmpWord.contains("NN")) {
				hasNN = true;
			}
			if (tmpWord.contains("VV")) {
				hasVV = true;
			}
			tokens.add(tmpWord);
		}
		if ((hasNR || hasNN || hasVV) && tokens.size() <= 5) {
			sentence = String.format("%s(%s, %s)", tokens.toString(),
					lineSplit[before], lineSplit[after]);
			System.out.println("Extract relationPattern: " + sentence);
			return sentence;
		}
		return null;
	}

}
