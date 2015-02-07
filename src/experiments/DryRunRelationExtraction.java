package experiments;

public class DryRunRelationExtraction {

	public static String seedEntityPath = "data/newsData/entityExtraction/150120/co-occurrenceLineByLine.txt";
	public static String seedRelationPath = "data/newsData/relationExtraction/150120_seedPattern.txt";

	public static String taggedPath = "data/newsData/tagged/150120_tag.txt";
	public static String relationPath = "data/newsData/relationExtraction/150120_pattern.txt";
	public static String reRelationPath = "data/newsData/relationExtraction/150120_pattern_150122_re.txt";

	public static String extractSourcePath = "data/newsData/tagged/150122_tag.txt";
	public static String instancePath = "data/newsData/instanceExtraction/150122_tag-by-150120_pattern.txt";

	public static void main(String[] args) {

		// RelationPatternExtractor prextor = new RelationPatternExtractor();
		// prextor.findPatternBetweenSeed(taggedPath, seedEntityPath,
		// seedRelationPath);
		// prextor.findPatternBetweenInstances(taggedPath, instancePath,
		// reRelationPath);

		RelationInstanceExtractor irextor = new RelationInstanceExtractor();
		irextor.findInstancesOfPattern(extractSourcePath, seedRelationPath,
				instancePath);

	}

}
