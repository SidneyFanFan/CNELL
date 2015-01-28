package experiments;

public class DryRun {

	public static void main(String[] args) {
		String jsonPath = "data/newsData/source/150122.json";
		String segDocPath = "data/newsData/segmentation/150122_seg.txt";
		String taggedDocPath = "data/newsData/tagged/150122_tag.txt";
		String categoryPath = "data/baidubaike/category_simple.txt";
		String entityPath = "data/newsData/entityExtraction/";

		Segmentor segmentor = new Segmentor();
		segmentor.segment(jsonPath, segDocPath);

		PosTagger tagger = new PosTagger(PosTagger.CHINESE);
		tagger.tagSegFile(segDocPath, taggedDocPath);

		// EntityExtractor rextor = new EntityExtractor(categoryPath);
		// rextor.extractEntities(taggedDocPath, entityPath);

	}

}
