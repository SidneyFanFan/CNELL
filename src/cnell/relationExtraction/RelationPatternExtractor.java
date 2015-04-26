package cnell.relationExtraction;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import cnell.basic.CNews;
import cnell.basic.CoPair;
import cnell.basic.extraction.RelationInstance;
import cnell.util.FileUtil;
import cnell.util.LoggerUtil;

public class RelationPatternExtractor {

	private Logger logger;

	public static void main(String[] args) {
		// String taggedPath = "data/newsData/tagged/150120_tag.txt";
		// String relationPath =
		// "data/newsData/relationExtraction/150120_pattern.txt";
		// String cooccPath =
		// "data/newsData/entityExtraction/150120/co-occurrenceLineByLine.txt";
		// String instancePath =
		// "data/newsData/instanceExtraction/150122_tag-by-150120_pattern.txt";
		// String reRelationPath =
		// "data/newsData/relationExtraction/150120_pattern_150122_re.txt";
		// RelationPatternExtractor rextor = new RelationPatternExtractor();
		// rextor.findPatternBetweenEntities(taggedPath, instancePath,
		// reRelationPath);
		// rextor.findPatternBetweenSeedCo(taggedPath, cooccPath, relationPath);
	}

	public RelationPatternExtractor() {
		logger = Logger.getLogger("RelationExtractor");
		try {
			LoggerUtil.setLogingProperties(logger);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void findPatternBetweenInstances(String taggedPath,
			String instancePath, String relationPatternPath) {
		// load corpus
		List<CNews> corpus = new ArrayList<CNews>();
		loadTaggedFile(taggedPath, corpus);
		// load instances
		HashSet<RelationInstance> instancesSet = new HashSet<RelationInstance>();
		loadInstances(instancePath, instancesSet);
		// extract pattern for each instance
		HashMap<String, Integer> relationPatternStrMap = new HashMap<String, Integer>();
		extractPatternWithInstances(instancesSet, corpus, relationPatternStrMap);
		// export relation pattern
		exportRelationPattern(relationPatternStrMap, relationPatternPath);
	}

	public void findPatternBetweenSeed(String taggedPath, String seedPath,
			String relationPatternPath) {
		// load corpus
		List<CNews> corpus = new ArrayList<CNews>();
		loadTaggedFile(taggedPath, corpus);
		// load seed
		HashSet<CoPair> seedSet = new HashSet<CoPair>();
		loadSeeds(seedPath, seedSet);
		// extract pattern for each instance
		HashMap<String, Integer> relationPatternStrMap = new HashMap<String, Integer>();
		extractPatternWithSeeds(seedSet, corpus, relationPatternStrMap);
		// export relation pattern
		exportRelationPattern(relationPatternStrMap, relationPatternPath);
	}

	private void extractPatternWithSeeds(HashSet<CoPair> seedSet,
			List<CNews> corpus, HashMap<String, Integer> relationPatternStrMap) {
		for (CoPair ri : seedSet) {
			extractPatternWithSeed(ri, corpus, relationPatternStrMap);
		}
	}

	private void extractPatternWithSeed(CoPair coPair, List<CNews> corpus,
			HashMap<String, Integer> relationPatternStrMap) {
		HashSet<String> localPatternSet;
		for (CNews cNews : corpus) {
			localPatternSet = findRelationOf(coPair.getP(), coPair.getQ(),
					cNews.getBody());
			for (String patternStr : localPatternSet) {
				if (relationPatternStrMap.containsKey(patternStr)) {
					int old = relationPatternStrMap.get(patternStr);
					relationPatternStrMap.put(patternStr, old + 1);
				} else {
					relationPatternStrMap.put(patternStr, 1);
				}
			}
		}

	}

	private void extractPatternWithInstances(
			HashSet<RelationInstance> instancesSet, List<CNews> corpus,
			HashMap<String, Integer> relationPatternStrMap) {
		for (RelationInstance ri : instancesSet) {
			extractPatternWithInstance(ri, corpus, relationPatternStrMap);
		}
	}

	private void extractPatternWithInstance(RelationInstance ri,
			List<CNews> corpus, HashMap<String, Integer> relationPatternStrMap) {
		HashSet<String> localPatternStrSet = new HashSet<String>();
		for (CNews cNews : corpus) {
			for (String line : cNews.getBody()) {
				localPatternStrSet = ri.match(line);
				for (String patternStr : localPatternStrSet) {
					if (relationPatternStrMap.containsKey(patternStr)) {
						int old = relationPatternStrMap.get(patternStr);
						relationPatternStrMap.put(patternStr, old + 1);
					} else {
						relationPatternStrMap.put(patternStr, 1);
					}
				}
			}
		}
	}

	private void loadTaggedFile(String filePath, List<CNews> corpus) {
		List<String> lines = null;
		List<String> originLines = FileUtil.readFileByLine(filePath);
		if (originLines == null) {
			logger.log(Level.WARNING,
					String.format("Failed to load file: %s", filePath));
			return;
		}
		String link = null;
		for (String line : originLines) {
			if (line.endsWith("html")) {
				if (lines != null && link != null) {
					corpus.add(new CNews(link, lines));
					// logger.log(Level.INFO, String.format("load news: %s",
					// link));
				}
				lines = new ArrayList<String>();
				link = line;
			} else {
				lines.add(line.substring(1, line.length() - 1));
			}
		}
		logger.log(Level.INFO, String.format("Finish load file: %s", filePath));

	}

	private void loadSeeds(String seedPath, HashSet<CoPair> seedSet) {
		List<String> seedFileLines = FileUtil.readFileByLine(seedPath);
		if (seedFileLines == null) {
			logger.log(Level.WARNING,
					String.format("Failed to load file: %s", seedPath));
			return;
		}
		int leftPar, rightPar, equation;
		for (String pairStr : seedFileLines) {
			if (!pairStr.endsWith("html")) {
				leftPar = pairStr.indexOf("(");
				rightPar = pairStr.lastIndexOf(")");
				equation = pairStr.lastIndexOf("=");
				String[] ws = pairStr.substring(leftPar + 1, rightPar).split(
						",");
				int count = Integer.valueOf(pairStr.substring(equation + 1)
						.trim());
				seedSet.add(new CoPair(ws[0], ws[1], count));
			}
		}
		logger.log(Level.INFO,
				String.format("Finish load seed file: %s", seedPath));
	}

	private void loadInstances(String instancePath,
			HashSet<RelationInstance> instancesSet) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(instancePath));
			String line;
			RelationInstance ri = null;
			while (br.ready()) {
				line = br.readLine();
				ri = RelationInstance.parse(line);
				if (ri != null) {
					instancesSet.add(ri);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private HashSet<String> findRelationOf(String noun1, String noun2,
			List<String> list) {
		int pos1, pos2;
		String wordWithTag, word;
		String[] wordWithTagSplit;
		String pattern = null;
		HashSet<String> patternSet = new HashSet<String>();

		for (String line : list) {
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
				if (word.equals(noun1)) {
					pos1 = i;
					if (pos2 != -1) {
						// export a pair
						pattern = extractInterveningSequence(pos1, pos2,
								lineSplit);
						pos1 = -1;
						// pos2 = -1;
					}
				}
				if (word.equals(noun2)) {
					pos2 = i;
					if (pos1 != -1) {
						// export a pair
						pattern = extractInterveningSequence(pos1, pos2,
								lineSplit);
						// pos1 = -1;
						pos2 = -1;
					}
				}
				if (pattern != null) {
					patternSet.add(pattern);
				}
			}
		}
		return patternSet;
	}

	private String extractInterveningSequence(int pos1, int pos2,
			String[] lineSplit) {
		int before, after;
		String sentence = null;
		String tmpWord;
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
			return sentence.toString();
		}
		return null;
	}

	private void exportRelationPattern(
			HashMap<String, Integer> relationPatternStrMap,
			String relationPatternPath) {
		FileWriter fw;
		try {
			fw = new FileWriter(relationPatternPath);
			for (Entry<String, Integer> en : relationPatternStrMap.entrySet()) {
				fw.write(en.toString());
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
