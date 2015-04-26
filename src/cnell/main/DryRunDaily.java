package cnell.main;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Calendar;

import cnell.basic.extraction.PatternExtractionParam;
import cnell.entityExtraction.EntityRecognizer;
import cnell.entityExtraction.PosTagger;
import cnell.entityExtraction.Segmentor;
import cnell.relationExtraction.AdvancedRelationPatternExtractor;
import cnell.util.Spider;

public class DryRunDaily {

	public static String logTpl = "log/%s.txt";
	public static String urlTpl = "http://218.193.131.249/ljq/news/%s/";
	public static String rawDocPathTpl = "data/cnell/raw/%s/%s/%s/%s.json";
	public static String segDocPathTpl = "data/cnell/segmentation/%s/%s/%s/%s_seg.txt";
	public static String taggedDocPathTpl = "data/cnell/tagged/%s/%s/%s/%s_tag.txt";
	public static String nerPathTpl = "data/cnell/ner/%s/%s/%s/%s_ner.txt";
	public static String patternPathTpl = "data/cnell/relationExtraction/pattern/%s/%s/%s/%s_%s_pattern.txt";

	Segmentor segmentor = new Segmentor();
	PosTagger tagger = new PosTagger(PosTagger.CHINESE);
	EntityRecognizer rdp = new EntityRecognizer();
	
	PrintStream systemOut = System.out;

	public static void main(String[] args) {
		// year, month, date, rootPath
		if (args.length < 4) {
			System.out.println("please input format \"year month date path\" ");
			return;
		}
		DryRunDaily d = new DryRunDaily(args[3]);
		d.run(args[0], args[1], args[2]);
	}

	public DryRunDaily(String rootPath) {
		rawDocPathTpl = rootPath + rawDocPathTpl;
		segDocPathTpl = rootPath + segDocPathTpl;
		taggedDocPathTpl = rootPath + taggedDocPathTpl;
		nerPathTpl = rootPath + nerPathTpl;
		patternPathTpl = rootPath + patternPathTpl;
		logTpl = rootPath + logTpl;
	}

	public void runToday(String root) {
		String pattern = "00";
		DecimalFormat df = new DecimalFormat(pattern);
		Calendar date = Calendar.getInstance();
		String year = String.valueOf(date.get(Calendar.YEAR) % 100);
		String month = df.format(date.get(Calendar.MONTH) + 1);
		String day = df.format(date.get(Calendar.DATE));
		String dateStr = String.valueOf(date.get(Calendar.YEAR) % 100) + month
				+ day;
		String[] args = new String[] { year, month, dateStr, root };
		run(args[0], args[1], args[2]);
	}

	public void run(String year, String month, String date) {
		String logPath = String.format(logTpl, date);
		String urlStr = String.format(urlTpl, date);
		String rawPath = String.format(rawDocPathTpl, year, month, date, date);
		String segPath = String.format(segDocPathTpl, year, month, date, date);
		String tagPath = String.format(taggedDocPathTpl, year, month, date,
				date);
		String nerPath = String.format(nerPathTpl, year, month, date, date);

		try {
			
			systemOut.println("Set log: " + logPath);
			System.setOut(new PrintStream(logPath));
			System.setErr(new PrintStream(logPath));
			systemOut.println("Fecth " + urlStr + "...");
			Spider.getPage(urlStr, rawPath);
			systemOut.println("Segmenting " + date + "...");
			segmentor.segment(rawPath, segPath);
			systemOut.println("Tagging " + date + "...");
			tagger.tagSegFile(segPath, tagPath);
			systemOut.println("Recorgnizing " + date + "...");
			rdp.recogEntityInSegFile(segPath, nerPath);
			systemOut.println("Extracting " + date + "...");
			String[] types = new String[] { "ORG", "PERSON", "LOC", "GPE" };
			for (int i = 0; i < types.length; i++) {
				for (int j = 0; j < types.length; j++) {
					start(types[i], types[j], nerPath, year, month, date);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void start(String type1, String type2, String nerPath,
			String year, String month, String date) {
		PatternExtractionParam pep = new PatternExtractionParam(new String[] {
				type1, type2 }, new String[] { "NN", "NR" }, 5,
				PatternExtractionParam.INTERVENINGMODE_ONEOFTYPES,
				PatternExtractionParam.EXTRACTIONMODE_INTERVENING);
		AdvancedRelationPatternExtractor rextor = new AdvancedRelationPatternExtractor(
				pep);
		String patternPath = String.format(patternPathTpl, year, month, date,
				date, pep.entityTypeString());
		try {
			rextor.extractRoughRelationPatternXML(nerPath, patternPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
