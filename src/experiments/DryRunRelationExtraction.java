package experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import relationExtraction.RelationInstanceExtractor;
import relationExtraction.RelationPatternExtractor;
import util.DataUtil;
import util.FileUtil;

public class DryRunRelationExtraction {

	public static String seedEntityPath = "data/newsData/entityExtraction/150120/co-occurrenceLineByLine.txt";
	public static String seedRelationPath = "data/newsData/relationExtraction/pattern/2015-2/2015-2_pattern.txt";

	public static String instancePathRoot = "data/newsData/relationExtraction/instance/2015-1/%s_instance.txt";

	public static RelationPatternExtractor patternExtractor = new RelationPatternExtractor();
	public static RelationInstanceExtractor instanceExtractor = new RelationInstanceExtractor();

	public static void main(String[] args) {

		String name, tagSrcPath, instancePath;
		File file = new File("data/newsData/tagged/2015-1");
		if (file.isDirectory()) {
			File[] dirFile = file.listFiles();
			for (File f : dirFile) {
				if (f.isFile()) {
					name = f.getName().split("_tag.txt")[0];
					tagSrcPath = f.getPath();
					instancePath = String.format(instancePathRoot, name);
					try {
						instanceExtractor.findInstancesOfPattern(tagSrcPath,
								seedRelationPath, instancePath);
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		}

		// merge
		file = new File("data/newsData/relationExtraction/instance/2015-1");
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		if (file.isDirectory()) {
			File[] dirFile = file.listFiles();
			for (File f : dirFile) {
				if (f.isFile()) {
					name = f.getName().split("_instance.txt")[0];
					tagSrcPath = f.getPath();
					instancePath = String.format(instancePathRoot, name);
					try {
						List<String> lines = FileUtil
								.readFileByLine(instancePath);
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
			FileWriter fw = new FileWriter(
					"data/newsData/relationExtraction/instance/2015-1/2015-1_instance.txt");
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
