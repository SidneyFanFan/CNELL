package relationExtract;

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

import extractElement.RelationPattern;
import basic.CNews;
import structure.Tuple;
import util.FileUtil;
import util.LoggerUtil;

public class RelationInstanceExtractor {

	private Logger logger;

	public static void main(String[] args) {
		String extractSourcePath = "data/newsData/tagged/150122_tag.txt";
		String relationPatternPath = "data/newsData/relationExtraction/150120_seedPattern.txt";
		String relationInstancePath = "data/newsData/instanceExtraction/150122_tag-by-150120_pattern.txt";
		RelationInstanceExtractor rextor = new RelationInstanceExtractor();
		rextor.findInstancesOfPattern(extractSourcePath, relationPatternPath,
				relationInstancePath);
	}

	public RelationInstanceExtractor() {
		logger = Logger.getLogger("RelationExtractor");
		try {
			LoggerUtil.setLogingProperties(logger);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void findInstancesOfPattern(String taggedPath,
			String relationPatternPath, String relationInstancePath) {
		// load corpus
		List<CNews> corpus = new ArrayList<CNews>();
		loadTaggedFile(taggedPath, corpus);
		// load pattern
		HashSet<RelationPattern> patternSet = new HashSet<RelationPattern>();
		loadPatterns(relationPatternPath, patternSet);
		// extract instances for each pattern
		HashMap<String, Integer> relationInstancesStrMap = new HashMap<String, Integer>();
		extractInstanceWithPatterns(patternSet, corpus, relationInstancesStrMap);
		// export relation pattern
		exportRelationInstances(relationInstancesStrMap, relationInstancePath);

	}

	private void extractInstanceWithPatterns(
			HashSet<RelationPattern> patternSet, List<CNews> corpus,
			HashMap<String, Integer> relationInstancesStrMap) {
		for (RelationPattern rp : patternSet) {
			extractInstanceWithPattern(rp, corpus, relationInstancesStrMap);
		}

	}

	private void extractInstanceWithPattern(RelationPattern rp,
			List<CNews> corpus, HashMap<String, Integer> relationInstancesStrMap) {
		String instanceStr;
		for (CNews cNews : corpus) {
			for (String line : cNews.getBody()) {
				Tuple<String, String> args = rp.match(line);
				if (args != null) {
					instanceStr = String.format("[%s]%s", rp.getPattern(),
							args.toString());
					System.out.println("Extract instances: " + instanceStr);
					if (relationInstancesStrMap.containsKey(instanceStr)) {
						int old = relationInstancesStrMap.get(instanceStr);
						relationInstancesStrMap.put(instanceStr, old + 1);
					} else {
						relationInstancesStrMap.put(instanceStr, 1);
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

	private void loadPatterns(String relationPatternPath,
			HashSet<RelationPattern> relationPatternSet) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					relationPatternPath));
			String line;
			RelationPattern rp = null;
			while (br.ready()) {
				line = br.readLine();
				if (!line.endsWith("html")) {
					rp = RelationPattern.parse(line);
					if (rp != null) {
						relationPatternSet.add(rp);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exportRelationInstances(
			HashMap<String, Integer> relationInstancesStrMap,
			String relationInstancePath) {
		FileWriter fw;
		try {
			fw = new FileWriter(relationInstancePath);
			for (Entry<String, Integer> en : relationInstancesStrMap.entrySet()) {
				fw.write(en.toString());
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
