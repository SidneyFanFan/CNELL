package experiments;

import relationExtraction.AdvancedRelationPatternExtractor;
import basic.extraction.PatternExtractionParam;
import entityExtraction.EntityRecognizer;
import entityExtraction.PosTagger;
import entityExtraction.Segmentor;

public class DryRunDaily {

	public static String date = "150301";
	public static String month = "2015-3";

	public static String rawDocPathTpl = "data/newsData/raw/%s/%s.json";
	public static String segDocPathTpl = "data/newsData/segmentation/%s/%s_seg.txt";
	public static String taggedDocPathTpl = "data/newsData/tagged/%s/%s_tag.txt";
	public static String nerPathTpl = "data/newsData/ner/%s/%s_ner.txt";
	public static String patternPathRoot = "data/newsData/relationExtraction/pattern";

	public static void main(String[] args) {
		Segmentor segmentor = new Segmentor();
		PosTagger tagger = new PosTagger(PosTagger.CHINESE);
		EntityRecognizer rdp = new EntityRecognizer();

		String rawPath = String.format(rawDocPathTpl, month, date);
		String segPath = String.format(segDocPathTpl, month, date);
		String tagPath = String.format(taggedDocPathTpl, month, date);
		String nerPath = String.format(nerPathTpl, month, date);

		try {
			System.out.println("Segmenting " + date + "...");
			segmentor.segment(rawPath, segPath);
			System.out.println("Tagging " + date + "...");
			tagger.tagSegFile(segPath, tagPath);
			System.out.println("Recorgnizing " + date + "...");
			rdp.recogEntityInSegFile(segPath, nerPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Extracting " + date + "...");
		String[] types = new String[] { "ORG", "PERSON", "LOC", "GPE" };
		for (int i = 0; i < types.length; i++) {
			for (int j = 0; j < types.length; j++) {
				start(types[i], types[j], nerPath);
			}
		}
	}

	public static void start(String type1, String type2, String nerPath) {
		PatternExtractionParam pep = new PatternExtractionParam(new String[] {
				type1, type2 }, new String[] { "NN", "NR" }, 5,
				PatternExtractionParam.INTERVENINGMODE_ONEOFTYPES,
				PatternExtractionParam.EXTRACTIONMODE_INTERVENING);
		AdvancedRelationPatternExtractor rextor = new AdvancedRelationPatternExtractor(
				pep);
		String patternPath = String.format("%s/%s/%s_%s_pattern.txt",
				patternPathRoot, month, date, pep.entityTypeString());
		try {
			rextor.extractRoughRelationPatternXML(nerPath, patternPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
