package experiments;

import java.io.File;

import entityExtraction.PosTagger;
import entityExtraction.Segmentor;

public class DryRunEntityExtraction {

	public static String segDocPathRoot = "data/newsData/segmentation/2015-1/%s_seg.txt";
	public static String taggedDocPathRoot = "data/newsData/tagged/2015-1/%s_tag.txt";
	public static String categoryPath = "data/baidubaike/category_simple.txt";
	public static String entityPath = "data/newsData/entityExtraction/2015-1/";

	public static void main(String[] args) {
		String name, jsonPath, segDocPath, taggedDocPath;
		Segmentor segmentor = new Segmentor();
		PosTagger tagger = new PosTagger(PosTagger.CHINESE);

		File file = new File("data/newsData/raw/2015-1");
		if (file.isDirectory()) {
			File[] dirFile = file.listFiles();
			for (File f : dirFile) {
				if (f.isFile()) {
					name = f.getName().split(".json")[0];
					System.out.println("Processing " + name + "...");
					jsonPath = f.getPath();
					segDocPath = String.format(segDocPathRoot, name);
					taggedDocPath = String.format(taggedDocPathRoot, name);
					try {
						segmentor.segment(jsonPath, segDocPath);
						tagger.tagSegFile(segDocPath, taggedDocPath);
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		}

		// EntityExtractor rextor = new EntityExtractor(categoryPath);
		// rextor.extractEntities(taggedDocPath, entityPath);

	}

}
