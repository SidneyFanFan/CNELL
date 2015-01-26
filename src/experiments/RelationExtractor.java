package experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import util.FileUtil;

public class RelationExtractor {

	private List<String> lines;

	public static void main(String[] args) {
		String taggedDocPath = "data/newsData/taggedNews.txt";
		RelationExtractor rextor = new RelationExtractor();
		rextor.loadFile(taggedDocPath);
		rextor.countRelation();
		// rextor.findRelationOf("朱立伦", "主席");
	}

	public void findRelationOf(String noun1, String noun2) {
		int pos1, pos2;
		String wordWithTag, word;
		String[] wordWithTagSplit;
		for (String line : lines) {
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
				// tag = wordWithTagSplit[1];
				if (word.equals(noun1)) {
					pos1 = i;
					if (pos2 != -1) {
						// export a pair
						exportPair(pos1, pos2, lineSplit);
						pos1 = -1;
						pos2 = -1;
					}
				}
				if (word.equals(noun2)) {
					pos2 = i;
					if (pos1 != -1) {
						// export a pair
						exportPair(pos1, pos2, lineSplit);
						pos1 = -1;
						pos2 = -1;
					}
				}
			}
		}
	}

	private void exportPair(int pos1, int pos2, String[] lineSplit) {
		int before, after;
		boolean hasMiddle = false;
		String sentence;
		before = pos1 < pos2 ? pos1 : pos2;
		after = pos1 < pos2 ? pos2 : pos1;
		sentence = lineSplit[before] + " ";
		for (int i = before + 1; i < after; i++) {
			if (lineSplit[i].contains("VV") || lineSplit[i].contains("NR")) {
				hasMiddle = true;
				sentence += lineSplit[i] + " ";
			}
		}
		sentence += lineSplit[after] + " ";
		if (hasMiddle) {
			System.out.println(sentence);
		}

	}

	public void countRelation() {
		HashMap<String, HashMap<String, Integer>> bigmap = new HashMap<String, HashMap<String, Integer>>();
		HashMap<String, Integer> map;
		String[] taggedWords;
		for (String line : lines) {
			taggedWords = line.split(", ");
			for (String taggedWord : taggedWords) {
				if (taggedWord.isEmpty()) {
					continue;
				}
				int splitIndex = taggedWord.lastIndexOf("/");
				String word = taggedWord.substring(0, splitIndex);
				String tag = taggedWord.substring(splitIndex + 1);
				if (bigmap.containsKey(tag)) {
					map = bigmap.get(tag);
					if (map.containsKey(word)) {
						map.put(word, map.get(word) + 1);
					} else {
						map.put(word, 1);
					}
				} else {
					bigmap.put(tag, new HashMap<String, Integer>());
				}
			}
		}
		for (Entry<String, HashMap<String, Integer>> en : bigmap.entrySet()) {
			System.out.println(en.getKey() + " : " + en.getValue().size());
		}
		// sort by count
		map = bigmap.get("NR");
		List<Entry<String, Integer>> sortedList = new ArrayList<Entry<String, Integer>>();
		for (Entry<String, Integer> en : map.entrySet()) {
			if (sortedList.size() == 0) {
				sortedList.add(en);
			} else {
				int i = 0;
				for (; i < sortedList.size(); i++) {
					if (en.getValue() > sortedList.get(i).getValue()) {
						break;
					}
				}
				sortedList.add(i, en);
			}
		}

		// for each v check c1 and c2
		for (int i = 0; i < 100; i++) {
			System.out.println(sortedList.get(i).toString());
			// if (sortedList.get(i).getKey().length() > 2) {
			// checkConceptsOfVerb(sortedList.get(i).getKey() + "/VV");
			// }
		}

	}

	protected void checkConceptsOfVerb(String verb) {
		String[] taggedWords;
		String prev = null, next = null;
		for (String line : lines) {
			if (line.contains(verb)) {
				taggedWords = line.split(", ");
				for (int i = 0; i < taggedWords.length; i++) {
					if (taggedWords[i].contains(verb)) {
						// check backward
						prev = null;
						next = null;
						int j = i - 1;
						int k = i + 1;
						while (j > 0) {
							prev = taggedWords[j];
							if (prev.contains("N")) {
								break;
							} else {
								j--;
							}
						}
						// check forward
						while (k < taggedWords.length) {
							next = taggedWords[k];
							if (next.contains("N")) {
								break;
							} else {
								k++;
							}
						}
						if (prev != null && next != null) {
							System.out.printf("%s(%s,%s)\n", verb, prev, next);
						}
					}
				}
			}
		}
	}

	public void loadFile(String filePath) {
		lines = new ArrayList<String>();
		List<String> originLines = FileUtil.readFileByLine(filePath);
		for (String line : originLines) {
			if (line.contains("[")) {
				lines.add(line.substring(1, line.length() - 1));
			}
		}
	}

}
