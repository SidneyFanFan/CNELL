package relationExtract;

import java.util.ArrayList;
import java.util.List;

public class DryRunRelationExtraction {

	public static String seedEntityPath = "data/newsData/entityExtraction/150120/co-occurrenceLineByLine.txt";
	public static String seedRelationPath = "data/newsData/relationExtraction/pattern/150120_seedPattern.txt";

	public static String taggedPath = "data/newsData/tagged/150120_tag.txt";
	public static String relationPath = "data/newsData/relationExtraction/pattern/150120_pattern.txt";
	public static String reRelationPath = "data/newsData/relationExtraction/pattern/150120_pattern_150122_re.txt";

	public static String extractSourcePath = "data/newsData/tagged/150118_tag.txt";
	public static String instancePath = "data/newsData/relationExtraction/instance/150118_tag-by-150120_pattern.txt";

	public static List<String> patternPathList = new ArrayList<String>();
	public static String mergedPath = "data/newsData/relationExtraction/instance/mergedInstance_by_150120.txt";

	public static RelationPatternExtractor patternExtractor = new RelationPatternExtractor();
	public static RelationInstanceExtractor instanceExtractor = new RelationInstanceExtractor();

	public static void main(String[] args) {

		// patternExtractor.findPatternBetweenSeed(taggedPath, seedEntityPath,
		// seedRelationPath);
		// patternExtractor.findPatternBetweenInstances(taggedPath,
		// instancePath,
		// reRelationPath);
		patternExtractor.findPatternBetweenInstances(taggedPath, mergedPath,
				reRelationPath);

		// instanceExtractor.findInstancesOfPattern(extractSourcePath,
		// seedRelationPath, instancePath);
		//
		// patternPathList
		// .add("data/newsData/relationExtraction/instance/150118_tag-by-150120_pattern.txt");
		// patternPathList
		// .add("data/newsData/relationExtraction/instance/150122_tag-by-150120_pattern.txt");
		// FileUtil.mergeFiles(patternPathList, mergedPath);
	}

}
