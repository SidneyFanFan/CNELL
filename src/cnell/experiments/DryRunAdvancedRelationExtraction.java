package cnell.experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cnell.basic.extraction.PatternExtractionParam;
import cnell.relationExtraction.AdvancedRelationPatternExtractor;
import cnell.util.DataUtil;
import cnell.util.FileUtil;

public class DryRunAdvancedRelationExtraction {

	static String month = "2015-1";
	static String segPathRoot = String.format("data/newsData/segmentation/%s",
			month);
	static String nerPathRoot = "data/newsData/ner";
	static String patternPathRoot = "data/newsData/relationExtraction/pattern";

	public static void main(String[] args) {
		String[] types = new String[] { "ORG", "PERSON", "LOC", "GPE" };
		for (int i = 0; i < types.length; i++) {
			for (int j = 0; j < types.length; j++) {
				start(types[i], types[j]);
			}
		}
	}

	private static void start(String type1, String type2) {
		PatternExtractionParam pep = new PatternExtractionParam(new String[] {
				type1, type2 }, new String[] { "NN", "NR" }, 5,
				PatternExtractionParam.INTERVENINGMODE_ONEOFTYPES,
				PatternExtractionParam.EXTRACTIONMODE_INTERVENING);

		AdvancedRelationPatternExtractor rextor = new AdvancedRelationPatternExtractor(
				pep);

		String name, nerPath, patternPath;
		File file = new File(segPathRoot);

		if (file.isDirectory()) {
			File[] dirFile = file.listFiles();
			for (File f : dirFile) {
				if (f.isFile()) {
					name = f.getName().split("_seg.txt")[0];
					nerPath = String.format("%s/%s/%s_ner.txt", nerPathRoot,
							month, name);
					patternPath = String.format("%s/%s/%s_%s_pattern.txt",
							patternPathRoot, month, name,
							pep.entityTypeString());
					try {
						// rdp.recogEntityInSegFile(segPath, nerPath);
						rextor.extractRoughRelationPatternXML(nerPath,
								patternPath);
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		}

		// merge
		file = new File(String.format("%s/%s", patternPathRoot, month));
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		if (file.isDirectory()) {
			File[] dirFile = file.listFiles();
			for (File f : dirFile) {
				if (f.isFile()
						&& f.getName().endsWith(
								pep.entityTypeString() + "_pattern.txt")) {
					try {
						List<String> lines = FileUtil.readFileByLine(f
								.getPath());
						for (String line : lines) {
							String[] split = line.split("=");
							Integer i = map.get(split[0]);
							if (i == null) {
								i = Integer.valueOf(split[1]);
							} else {
								i = Integer.valueOf(split[1]) + i;
							}
							map.put(split[0], Integer.valueOf(split[1]));
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		}
		List<Entry<String, Integer>> list = DataUtil
				.convertMapToSortedList(map);
		try {
			FileWriter fw = new FileWriter(String.format("%s/%s/%s_%s.txt",
					patternPathRoot, "merge", month, pep.entityTypeString()));
			for (Entry<String, Integer> en : list) {
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
