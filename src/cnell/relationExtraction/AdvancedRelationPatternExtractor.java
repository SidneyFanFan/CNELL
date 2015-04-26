package cnell.relationExtraction;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cnell.basic.extraction.PatternExtractionParam;
import cnell.structure.Triple;
import cnell.util.FileUtil;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class AdvancedRelationPatternExtractor {

	private LexicalizedParser lp;
	private TreebankLanguagePack tlp;
	private GrammaticalStructureFactory gsf;
	private PatternExtractionParam param;

	public static final String regex = "(<(.*?)>(.*?)</(.*?)>)";
	public static final String regexFormat = "(<(%s)>(.*?)</(%s)>)";

	public static void main(String[] args) {
		String nerPath = "data/newsData/ner/2015-1/150101_ner.txt";
		String patternPath = "data/newsData/relationExtraction/pattern/2015-1/150101_pattern.txt";
		PatternExtractionParam pep = new PatternExtractionParam(new String[] {
				"ORG", "ORG" }, new String[] { "NN", "NR" }, 10,
				PatternExtractionParam.INTERVENINGMODE_ONEOFTYPES,
				PatternExtractionParam.EXTRACTIONMODE_PREPOSITION);
		AdvancedRelationPatternExtractor rextor = new AdvancedRelationPatternExtractor(
				pep);
		rextor.extractRoughRelationPatternXML(nerPath, patternPath);

	}

	public AdvancedRelationPatternExtractor(PatternExtractionParam pep) {
		String grammar = "edu/stanford/nlp/models/lexparser/chinesePCFG.ser.gz";
		lp = LexicalizedParser.loadModel(grammar);
		tlp = lp.getOp().langpack();
		gsf = tlp.grammaticalStructureFactory();
		param = pep;
	}

	public void extractRoughRelationPatternXML(String filePath,
			String patternPath) {
		List<String> originLines = FileUtil.readFileByLine(filePath);
		if (originLines == null) {
			System.out.println("empty file");
			return;
		}
		try {
			FileUtil.createFile(patternPath);
			FileWriter fw = new FileWriter(patternPath);
			HashMap<String, Integer> patternMap = new HashMap<String, Integer>();
			for (String line : originLines) {
				if (line.isEmpty() || line.endsWith("html")) {
					continue;
				} else {
					handleSentence(patternMap, line);
				}
			}
			for (Entry<String, Integer> en : patternMap.entrySet()) {
				fw.write(en.toString());
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void handleSentence(HashMap<String, Integer> patternMap, String line) {
		// window size = 2
		// bi-relation
		List<String> split = splitXml(line);
		List<Triple<String, String, String>> list = new ArrayList<Triple<String, String, String>>();
		String prev, middle, next;
		for (int i = 1; i < split.size() - 1; i++) {
			prev = split.get(i - 1);
			middle = split.get(i);
			next = split.get(i + 1);
			if (middle.matches(regex)) {
				prev = prev.matches(regex) ? "" : prev;
				next = next.matches(regex) ? "" : next;
				list.add(new Triple<String, String, String>(prev, middle, next));
			}
		}
		// Analyze pair
		Triple<String, String, String> entity1, entity2;
		for (int i = 0; i < list.size() - 1; i++) {
			entity1 = list.get(i);
			entity2 = list.get(i + 1);
			String type1 = param.getEntityTypes()[0];
			String type2 = param.getEntityTypes()[1];
			if (entity1.getArg2().matches(
					String.format(regexFormat, type1, type1))
					&& entity2.getArg2().matches(
							String.format(regexFormat, type2, type2))) {
				String pattern = null;
				pattern = interveningDependencyAnalyse(entity1.getArg2()
						.replaceAll("<(.*?)>", ""), entity1.getArg3(), entity2
						.getArg2().replaceAll("<(.*?)>", ""), entity2.getArg3());

				// switch (param.getExtractionMode()) {
				// case PatternExtractionParam.EXTRACTIONMODE_INTERVENING:
				// pattern = interveningDependencyAnalyse(entity1.getArg2()
				// .replaceAll("<(.*?)>", ""), entity1.getArg3(),
				// entity2.getArg2().replaceAll("<(.*?)>", ""));
				// break;
				// case PatternExtractionParam.EXTRACTIONMODE_PREPOSITION:
				// pattern = prepositionDependencyAnalyse(entity1.getArg2()
				// .replaceAll("<(.*?)>", ""), entity1.getArg3(),
				// entity2.getArg2().replaceAll("<(.*?)>", ""),
				// entity2.getArg3());
				// break;
				// }
				// String pattern = String
				// .format(patternFormat, entity1.getArg1(),
				// entity2.getArg1(), entity2.getArg3(),
				// entity1.getArg2(), entity2.getArg2());
				// System.out.println(pattern);
				// patternList.add(pattern);
				if (pattern == null) {
					continue;
				} else {
					Integer integer = patternMap.get(pattern);
					if (integer == null) {
						integer = 1;
					} else {
						integer++;
					}
					patternMap.put(pattern, integer);
				}
			}
		}
	}

	private List<String> splitXml(String str) {
		Pattern p = Pattern.compile(regex);
		Matcher matcher = p.matcher(str);
		List<String> list = new ArrayList<String>();
		int start = 0, end = 0;
		String entity;
		while (matcher.find(start)) {
			entity = matcher.group(1);
			end = str.indexOf(entity, start);
			list.add(str.substring(start, end).trim());
			list.add(entity.replace(" ", ""));
			start = end + entity.length();
		}
		list.add(0, "");
		list.add("");
		return list;
	}

	public void extractRoughRelationPattern(String filePath, String patternPath) {
		List<String> originLines = FileUtil.readFileByLine(filePath);
		if (originLines == null) {
			System.out.println("empty file");
			return;
		}
		String prevEntity = "", prevEntityType = "", prevIntervening = "";
		String nextEntity = "", nextEntityType = "", nextIntervening = "";
		String patternStr = "";
		HashMap<String, Integer> patternMap = new HashMap<String, Integer>();
		for (String line : originLines) {
			if (line.isEmpty() || line.endsWith("html")) {
				continue;
			}
			String[] split = line.split("\t");
			if (split.length != 3) {
				prevEntityType = "";
				nextEntityType = "";
				continue;
			}
			nextEntity = split[0].replace(" ", "");
			nextEntityType = split[1];
			nextIntervening = split[2];
			String t1 = param.getEntityTypes()[0];
			String t2 = param.getEntityTypes()[1];

			if (prevEntityType.equals(t1) && nextEntityType.equals(t2)) {
				patternStr = String.format("%s/%s %s %s/%s %s", prevEntity,
						prevEntityType, prevIntervening, nextEntity,
						nextEntityType, nextIntervening);
				System.out.println(patternStr);
				String pattern = null;
				switch (param.getExtractionMode()) {
				case PatternExtractionParam.EXTRACTIONMODE_INTERVENING:
					pattern = interveningDependencyAnalyse(prevEntity,
							prevIntervening, nextEntity, nextIntervening);
					break;
				case PatternExtractionParam.EXTRACTIONMODE_PREPOSITION:
					pattern = prepositionDependencyAnalyse(prevEntity,
							prevIntervening, nextEntity, nextIntervening);
					break;
				}
				System.out.println();
				if (pattern == null) {
					continue;
				} else {
					Integer i = patternMap.get(pattern);
					if (i == null) {
						i = 1;
					} else {
						i++;
					}
					patternMap.put(pattern, i);
				}
			}
			prevEntity = nextEntity;
			prevEntityType = nextEntityType;
			prevIntervening = nextIntervening;
		}

		try {
			FileWriter fw = new FileWriter(patternPath);
			for (Entry<String, Integer> en : patternMap.entrySet()) {
				fw.write(en.toString());
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String interveningDependencyAnalyse(String entity1,
			String intervening, String entity2, String post) {
		String sent = String.format("%s %s %s", entity1, intervening, entity2);
		System.out.println(sent);
		if (!isValidPatternFormat(intervening)) {
			return null;
		}

		// Use the default tokenizer for this TreebankLanguagePack
		Tokenizer<? extends HasWord> toke = tlp.getTokenizerFactory()
				.getTokenizer(new StringReader(sent));
		List<? extends HasWord> sentence = toke.tokenize();

		Tree parse = lp.parse(sentence);
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		System.out.println(tdl);

		// parse.pennPrint();
		System.out.println(parse.taggedYield());
		System.out.println();

		// divide tagged list
		List<TaggedWord> tagged = parse.taggedYield();
		int pos1 = 0;
		int pos2 = tagged.size() - 1;
		TaggedWord tEntity1 = tagged.get(pos1);
		TaggedWord tEntity2 = tagged.get(pos2);
		String middlePattern = tagged.subList(pos1 + 1, pos2).toString();
		// check type
		boolean hasType = false;
		for (String type : param.getInterveningTypes()) {
			if (middlePattern.contains(type)) {
				hasType = true;
				break;
			}
		}
		// check preposition
		if (middlePattern.contains("/P")) {
			return prepositionDependencyAnalyse(entity1, intervening, entity2,
					post);
		}

		if (hasType) {
			String patternStr = String.format("%s(%s/%s, %s/%s)",
					middlePattern, entity1, tEntity1.tag(), entity2,
					tEntity2.tag());
			return patternStr;
		} else {
			return null;
		}
	}

	private String prepositionDependencyAnalyse(String entity1, String middle,
			String entity2, String post) {
		String sent = String.format("%s %s %s %s", entity1, middle, entity2,
				post);
		System.out.println("Preposition parse " + sent);
		System.out.println(String.format("%s; %s; %s; %s", entity1, middle,
				entity2, post));
		// check format
		if (!isValidPatternFormat(middle) || !isValidPatternFormat(post)) {
			return null;
		}

		// Use the default tokenizer for this TreebankLanguagePack
		Tokenizer<? extends HasWord> toke = tlp.getTokenizerFactory()
				.getTokenizer(new StringReader(sent));
		List<? extends HasWord> sentence = toke.tokenize();

		Tree parse = lp.parse(sentence);
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
		System.out.println(tdl);

		parse.pennPrint();
		System.out.println(parse.taggedYield());
		System.out.println();

		// divide tagged list
		List<TaggedWord> tagged = parse.taggedYield();
		int pos1 = 0;
		int pos2 = 0;
		TaggedWord tw;
		TaggedWord tEntity1 = null;
		TaggedWord tEntity2 = null;
		for (int i = 0; i < tagged.size(); i++) {
			tw = tagged.get(i);
			if (tw.word().equals(entity1)) {
				pos1 = i;
				tEntity1 = tw;
				for (int j = i + 1; j < tagged.size(); j++) {
					tw = tagged.get(j);
					if (tw.word().equals(entity2)) {
						pos2 = j;
						tEntity2 = tw;
						break;
					}
				}
				break;
			}
		}

		String middlePattern = tagged.subList(pos1 + 1, pos2).toString();
		middlePattern = middlePattern.substring(1, middlePattern.length() - 1);

		String postPattern = tagged.subList(pos2 + 1, tagged.size()).toString();
		postPattern = postPattern.substring(1, postPattern.length() - 1);

		// check type
		boolean hasType = false;
		for (String type : param.getInterveningTypes()) {
			if (postPattern.contains(type)) {
				hasType = true;
				break;
			}
		}
		if (hasType) {
			String patternStr = String.format("[%s; %s](%s/%s, %s/%s)",
					middlePattern, postPattern, entity1, tEntity1.tag(),
					entity2, tEntity2.tag());
			return patternStr;
		} else {
			return null;
		}
	}

	private boolean isValidPatternFormat(String candidate) {
		if (candidate
				.matches("^(.*)(！|，|。|（|）|：|・|、|；|-|\\?|“|”|`|'|【|】|《|》|\\.)(.*)$")) {
			return false;
		}
		if (candidate.split(" ").length > param.getPatternMaxLength()) {
			return false;
		}
		if (candidate.split(" ").length == 0) {
			return false;
		}
		return true;
	}

}
