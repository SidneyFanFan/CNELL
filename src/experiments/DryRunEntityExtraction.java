package experiments;

public class DryRunEntityExtraction {

	public static String jsonPath = "data/newsData/source/150122.json";
	public static String segDocPath = "data/newsData/segmentation/150122_seg.txt";
	public static String taggedDocPath = "data/newsData/tagged/150122_tag.txt";
	public static String categoryPath = "data/baidubaike/category_simple.txt";
	public static String entityPath = "data/newsData/entityExtraction/";

	public static void main(String[] args) {

		Segmentor segmentor = new Segmentor();
		segmentor.segment(jsonPath, segDocPath);

		PosTagger tagger = new PosTagger(PosTagger.CHINESE);
		tagger.tagSegFile(segDocPath, taggedDocPath);

		// EntityExtractor rextor = new EntityExtractor(categoryPath);
		// rextor.extractEntities(taggedDocPath, entityPath);

	}

}
