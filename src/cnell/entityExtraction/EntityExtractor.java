package cnell.entityExtraction;

import java.io.BufferedReader;
import java.io.File;
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
import cnell.util.DataUtil;
import cnell.util.FileUtil;
import cnell.util.LoggerUtil;

public class EntityExtractor {

	private List<CNews> fileContent;
	private Logger logger;
	private String categoryPath;

	public static void main(String[] args) {
		String taggedDocPath = "data/newsData/tagged/150120_tag.txt";
		String categoryPath = "data/baidubaike/category_simple.txt";
		String entityPath = "data/newsData/entityExtraction";

		EntityExtractor rextor = new EntityExtractor(categoryPath);

		// extract and make co-occurrence
		rextor.extractEntities(taggedDocPath, entityPath);
		// merge files
		// rextor.mergeFiles(entityPath);
	}

	public EntityExtractor(String categoryPath) {
		this.categoryPath = categoryPath;
		logger = Logger.getLogger("EntityExtractor");
		try {
			LoggerUtil.setLogingProperties(logger);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void extractEntities(String taggedPath, String entityPath) {
		String link;
		List<String> lines;
		String line;
		HashSet<String> entitySet;

		// load tagged file
		loadFile(taggedPath);
		// mkdir
		entityPath = makeDirectory(taggedPath, entityPath);

		if (fileContent == null) {
			logger.log(Level.WARNING, "No file loaded");
			return;
		}
		// for each piece of news
		for (CNews cnews : fileContent) {
			link = cnews.getLink(); // news link
			lines = cnews.getBody(); // news body
			// for each line in body
			for (int i = 0; i < lines.size(); i++) {
				line = lines.get(i);
				entitySet = new HashSet<String>();
				checkEntityInLine(entitySet, line);
				cnews.getEntitiesInLine().put(line, entitySet);
				cnews.getAllEntities().addAll(entitySet);
			}
			logger.log(Level.INFO, String.format(
					"Extracts %d entities from %s", cnews.getAllEntities()
							.size(), link));
			// export entity
			try {
				// write entities news by news
				writeEntitiesNewsByNews(cnews, entityPath);
				// write entities line by line
				writeEntitiesLineByLine(cnews, entityPath);
				// write co-occurrence line by line
				writeCoOccurrenceLineByLine(cnews, entityPath);
				// merge
				mergeFiles(entityPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.gc();
		}
	}

	private void loadFile(String filePath) {
		fileContent = new ArrayList<CNews>();
		List<String> lines = null;
		List<String> originLines = FileUtil.readFileByLine(filePath);
		if (originLines == null) {
			logger.log(Level.WARNING,
					String.format("Failed to load file: %s", filePath));
			return;
		}

		int count = 0;
		int startFrom = 0;
		int endAt = 1;
		String link = null;
		for (String line : originLines) {
			if (line.endsWith("html") || line.endsWith("htm")) {
				if (lines != null && link != null) {
					if (count > startFrom) {
						fileContent.add(new CNews(link, lines));
						logger.log(Level.INFO,
								String.format("load news: %s", link));
					}
					if (count >= endAt) {
						break;
					}
				}
				count++;
				lines = new ArrayList<String>();
				link = line;
			} else {
				lines.add(line.substring(1, line.length() - 1));
			}
		}
		logger.log(Level.INFO, String.format("Finish load file: %s", filePath));
	}

	private String makeDirectory(String taggedPath, String entityPath) {
		// parse
		int pos = taggedPath.indexOf("_tag");
		String name = taggedPath.substring(pos - 6, pos);
		String dir = String.format("%s/%s", entityPath, name);
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(String.format("%s/newsByNews", dir));
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(String.format("%s/lineByLine", dir));
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(String.format("%s/co-occurrence", dir));
		if (!file.exists()) {
			file.mkdirs();
		}
		return dir;
	}

	private void mergeFiles(String entityPath) {
		// merge line by line directory
		String lineByLineDirectoryPath = String.format("%s/lineByLine",
				entityPath);
		String lineByLineExport = String.format("%s/sentitiesLineByLine.txt",
				entityPath);
		FileUtil.mergeDirectoryToSingleFile(lineByLineDirectoryPath,
				lineByLineExport);

		String cooccurrencelineByLineDirectoryPath = String.format(
				"%s/co-occurrence", entityPath);
		String cooccurrencelineByLineExportPath = String.format(
				"%s/co-occurrenceLineByLine.txt", entityPath);
		FileUtil.mergeDirectoryToSingleFile(
				cooccurrencelineByLineDirectoryPath,
				cooccurrencelineByLineExportPath);

	}

	private void writeEntitiesNewsByNews(CNews cnews, String entityPath)
			throws IOException {
		FileWriter fw = new FileWriter(String.format("%s/newsByNews/%s.txt",
				entityPath, cnews.getLink()));
		fw.write(cnews.getLink());
		fw.write("\n");
		fw.write(cnews.getAllEntities().toString());
		fw.write("\n");
		fw.flush();
		fw.close();

	}

	private void writeEntitiesLineByLine(CNews cnews, String entityPath)
			throws IOException {
		FileWriter fw = new FileWriter(String.format("%s/lineByLine/%s.txt",
				entityPath, cnews.getLink()));
		fw.write(cnews.getLink());
		fw.write("\n");
		for (String line : cnews.getBody()) {
			fw.write(line);
			fw.write("\n");
			if (cnews.getEntitiesInLine().containsKey(line)) {
				fw.write(cnews.getEntitiesInLine().get(line).toString());
				fw.write("\n");
			}
		}
		fw.flush();
		fw.close();
	}

	private void writeCoOccurrenceLineByLine(CNews cnews, String entityPath)
			throws IOException {
		FileWriter fw = new FileWriter(String.format("%s/co-occurrence/%s.txt",
				entityPath, cnews.getLink()));
		HashMap<String, Integer> cooccurrenceMap = new HashMap<String, Integer>();
		fw.write(cnews.getLink());
		fw.write("\n");
		for (String line : cnews.getBody()) {
			HashMap<String, Integer> localCoMap = generateCoOccurrence(cnews
					.getEntitiesInLine().get(line));
			// merge
			for (Entry<String, Integer> en : localCoMap.entrySet()) {
				String key = en.getKey();
				Integer localValue = en.getValue();
				Integer oldValue = cooccurrenceMap.get(key);
				if (oldValue != null) {
					cooccurrenceMap.put(key, localValue + oldValue);
				} else {
					cooccurrenceMap.put(key, localValue);
				}
			}
		}
		// convert to list
		List<Entry<String, Integer>> coList = DataUtil
				.convertMapToSortedList(cooccurrenceMap);
		int num = coList.size() > 5 ? 5 : coList.size();
		for (int i = 0; i < num; i++) {
			// only top 5
			fw.write(coList.get(i).toString());
			fw.write("\n");
		}
		fw.flush();
		fw.close();
	}

	private HashMap<String, Integer> generateCoOccurrence(
			HashSet<String> entitySet) {
		String[] entities = new String[entitySet.size()];
		entitySet.toArray(entities);
		return generateCoOccurrence(entities);
	}

	private HashMap<String, Integer> generateCoOccurrence(String[] entities) {
		HashMap<String, Integer> cooccurrenceMap = new HashMap<String, Integer>();
		String ett1, ett2;
		String pair;
		Integer times;
		for (int i = 0; i < entities.length; i++) {
			ett1 = entities[i];
			for (int j = i + 1; j < entities.length; j++) {
				ett2 = entities[j];
				if (ett1.contains(ett2) || ett2.contains(ett1)) {
					// it is possible that AB and A are extracted
					// but AB contains A
					continue;
				}
				if (ett1.compareTo(ett2) < 0) {
					pair = String.format("(%s,%s)", ett1, ett2);
				} else {
					pair = String.format("(%s,%s)", ett2, ett1);
				}
				// check map
				times = cooccurrenceMap.get(pair);
				if (times != null) {
					cooccurrenceMap.put(pair, times + 1);
				} else {
					cooccurrenceMap.put(pair, 1);
				}
			}
		}
		return cooccurrenceMap;
	}

	private void checkEntityInLine(HashSet<String> entitySet, String line) {
		String[] lineSplit = line.split(", ");
		int entityCount = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(categoryPath));
			logger.log(Level.INFO,
					String.format("Start to check line: %s", line));
			while (br.ready()) {
				entityCount++;
				String category = br.readLine();
				String[] cate = category.split("\t");
				if (cate.length != 2) {
					logger.log(Level.WARNING, String.format(
							"Split failed for category: %s", category));
					break;
				} else {
					String entity = cate[0];
					// String cates = cate[1];
					/*
					 * Example: entity = AB CD E, word = CD, wordPosInEntity =
					 * 2, leftWindowSize = 2, rightWindowSize = 5-2-2 = 1
					 * 
					 * However, currently the leftWindowSize is not usable
					 * because we check words in line from left to right.
					 */
					// int leftWindowSize;
					if (entitySet.contains(entity) || entity.length() == 1) {
						continue;
					} else {
						boolean find = findCombinedWord(lineSplit, entity);
						if (find) {
							String logStr = String.format(
									"Find (%s@%d) in:\t%s\n", entity,
									entityCount, line);
							logger.log(Level.INFO, logStr);
							entitySet.add(entity);
						}
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

	private boolean findCombinedWord(String[] lineSplit, String entity) {
		int rightWindowSize;
		String windowWord;
		String combinedWord = null;

		// start to check words in a line
		for (int j = 0; j < lineSplit.length; j++) {
			if (lineSplit[j].isEmpty()) {
				continue;
			}
			String[] wordWithTag = lineSplit[j].split("/");
			String word = wordWithTag[0];
			String tag = wordWithTag[1];
			if (word.isEmpty()) {
				continue;
			}
			if (entity.equals(word) && (tag.equals("NR") || tag.equals("NN"))) {
				return true;
			}
			// start to recognize word
			int wordPosInEntity = entity.indexOf(word);
			if (wordPosInEntity == -1) {
				// not exist
			} else if (wordPosInEntity == 0) {
				// Here we assume check from left to right
				// So CD in ABCDE must be found at AB
				/*
				 * Left side code: leftWindowSize = wordPosInEntity; while
				 * (leftWindowSize > 0) { // check left side words for (int k =
				 * j; k >= 0; k--) { windowWordWithTag = lineSplit[j]
				 * .split("/"); windowWord = wordWithTag[0]; windowTag =
				 * wordWithTag[1]; } }
				 */
				combinedWord = word;
				rightWindowSize = entity.length() - word.length()
						- wordPosInEntity;
				if (rightWindowSize >= 0) {
					// check right side words
					for (int k = j + 1; k < lineSplit.length; k++) {
						windowWord = lineSplit[k].split("/")[0];
						combinedWord += windowWord;
						rightWindowSize -= windowWord.length();
						if (rightWindowSize <= 0) {
							if (entity.equals(combinedWord)) {
								return true;
							} else {
								return false;
							}
						}
					}
				}
			}

		}
		return false;
	}

}
