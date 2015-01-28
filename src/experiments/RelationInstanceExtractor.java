package experiments;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basic.CNews;
import basic.RelationPattern;
import util.FileUtil;
import util.LoggerUtil;

public class RelationInstanceExtractor {

	private List<CNews> fileContent;
	private Logger logger;

	public static void main(String[] args) {
		String taggedPath = "data/newsData/tagged/150122_tag.txt";
		String relationPath = "data/newsData/relationExtraction/150120_pattern.txt";
		String instancePath = "data/newsData/instanceExtraction/150122_tag-by-150120_pattern.txt";
		RelationInstanceExtractor rextor = new RelationInstanceExtractor();
		// rextor.countRelation();
		rextor.findInstancesOfPattern(taggedPath, relationPath, instancePath);
	}

	public RelationInstanceExtractor() {
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

	public void findInstancesOfPattern(String taggedPath, String relationPath,
			String instancePath) {
		loadTaggedFile(taggedPath);
		try {
			BufferedReader br = new BufferedReader(new FileReader(relationPath));
			String line;
			HashSet<String> originalSet = new HashSet<String>();
			HashSet<String> dicoveredSet = new HashSet<String>();
			RelationPattern rp = null;
			while (br.ready()) {
				line = br.readLine();
				if (!line.endsWith("html")) {
					rp = parsePattern(line);
					originalSet.add(line);
					dicoveredSet.addAll(extractInstanceWithPattern(rp));
				}
			}
			br.close();
			// compare two set
			FileWriter fw = new FileWriter(instancePath);
			for (String instance : dicoveredSet) {
				if (!originalSet.contains(instance)) {
					System.out.println("New discovered instance: " + instance);
					fw.write(instance);
					fw.write("\n");
				}
			}
			fw.flush();
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private HashSet<String> extractInstanceWithPattern(RelationPattern rp) {
		int pos;
		String[] prevTokens, nextTokens;
		String prevWord, nextWord;
		String instance;
		HashSet<String> instanceSet = new HashSet<String>();

		for (CNews cNews : fileContent) {
			for (String line : cNews.getBody()) {
				pos = line.indexOf(rp.getPattern());
				if (pos >= 0) {
					// found instance
					prevTokens = line.substring(0, pos).split(", ");
					nextTokens = line.substring(pos + rp.getPattern().length())
							.split(", ");
					// the last one is empty
					if (prevTokens.length > 1 && nextTokens.length > 2) {
						prevWord = prevTokens[prevTokens.length - 1];
						nextWord = nextTokens[1];
						// check
						if (prevWord.contains(rp.getTag1())
								&& nextWord.contains(rp.getTag2())) {
							instance = String.format("[%s](%s,%s)",
									rp.getPattern(), prevWord, nextWord);
							instanceSet.add(instance);
						}
					}
				}
			}
		}
		return instanceSet;

	}

	private RelationPattern parsePattern(String line) {

		String tag1, tag2;
		String pattern;

		String regex = "^\\[(.*)\\]\\((((.*)\\/(.*)),((.*)\\/(.*)))\\)$";
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(line);
		boolean ret = matcher.find();
		if (ret) {
			pattern = matcher.group(1);
			tag1 = matcher.group(5);
			tag2 = matcher.group(8);
			return new RelationPattern(pattern, tag1, tag2);
		}
		return null;
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

}
