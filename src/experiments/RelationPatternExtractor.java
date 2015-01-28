package experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import basic.CNews;
import basic.CNews.CoPair;
import util.FileUtil;
import util.LoggerUtil;

public class RelationPatternExtractor {

	private List<CNews> fileContent;
	private Logger logger;

	public static void main(String[] args) {
		String taggedPath = "data/newsData/tagged/150120_tag.txt";
		String relationPath = "data/newsData/relationExtraction/150120_pattern.txt";
		String cooccPath = "data/newsData/entityExtraction/150120/co-occurrenceLineByLine.txt";
		RelationPatternExtractor rextor = new RelationPatternExtractor();
		// rextor.countRelation();
		rextor.findPatternBetweenEntities(taggedPath, cooccPath, relationPath);
	}

	public RelationPatternExtractor() {
		fileContent = new ArrayList<CNews>();
		logger = Logger.getLogger("RelationExtractor");
		try {
			LoggerUtil.setLogingProperties(logger);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void findPatternBetweenEntities(String taggedPath, String cooccPath,
			String relationPath) {
		loadTaggedFile(taggedPath);
		loadCoFile(cooccPath);
		HashSet<String> patternSet;
		HashSet<String> localPatternSet = null;
		for (CNews cNews : fileContent) {
			List<CoPair> coPairs = cNews.getCoPairs();
			patternSet = new HashSet<String>();
			for (CoPair coPair : coPairs) {
				System.out.println(coPair.toString());
				localPatternSet = findRelationOf(coPair.getP(), coPair.getQ(),
						cNews.getBody());
				patternSet.addAll(localPatternSet);
			}
			cNews.setPatternSet(patternSet);
		}
		// export pattern
		exportPattern(relationPath);
	}

	private void exportPattern(String relationPath) {
		try {
			FileWriter fw = new FileWriter(relationPath);
			for (CNews cNews : fileContent) {
				fw.write(cNews.getLink());
				fw.write("\n");
				for (String pattern : cNews.getPatternSet()) {
					fw.write(pattern);
					fw.write("\n");
				}
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void loadTaggedFile(String filePath) {
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
					fileContent.add(new CNews(link, lines));
					logger.log(Level.INFO, String.format("load news: %s", link));
				}
				lines = new ArrayList<String>();
				link = line;
			} else {
				lines.add(line.substring(1, line.length() - 1));
			}
		}
		logger.log(Level.INFO, String.format("Finish load file: %s", filePath));

	}

	private void loadCoFile(String filePath) {
		List<String> originLines = FileUtil.readFileByLine(filePath);
		if (originLines == null) {
			logger.log(Level.WARNING,
					String.format("Failed to load file: %s", filePath));
			return;
		}

		CNews localNews = null;
		for (String line : originLines) {
			if (line.endsWith("html")) {
				localNews = getNewsByLink(line);
			} else {
				if (localNews == null) {
					logger.log(Level.WARNING,
							String.format("Cant find local news: %s", filePath));
					return;
				}
				localNews.addCoOccurrencePair(line);
			}
		}
		logger.log(Level.INFO,
				String.format("Finish load cooccurrence file: %s", filePath));

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
						pattern = exportPair(pos1, pos2, lineSplit);
						pos1 = -1;
						// pos2 = -1;
					}
				}
				if (word.equals(noun2)) {
					pos2 = i;
					if (pos1 != -1) {
						// export a pair
						pattern = exportPair(pos1, pos2, lineSplit);
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

	private String exportPair(int pos1, int pos2, String[] lineSplit) {
		int before, after;
		String sentence = null;
		String tmpWord;
		List<String> tokens = new ArrayList<String>();
		boolean hasNR = false;
		boolean hasNN = false;

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
			tokens.add(tmpWord);
		}

		if ((hasNR || hasNN) && tokens.size() <= 5) {
			sentence = String.format("%s(%s,%s)", tokens.toString(),
					lineSplit[before], lineSplit[after]);
			System.out.println(sentence.toString());
			return sentence.toString();
		}
		return null;
	}

	private CNews getNewsByLink(String link) {
		if (fileContent == null) {
			return null;
		}
		for (CNews cNews : fileContent) {
			if (cNews.getLink().equals(link)) {
				return cNews;
			}
		}
		return null;
	}

}
