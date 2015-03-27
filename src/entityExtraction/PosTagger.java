package entityExtraction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import util.FileUtil;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class PosTagger {

	public static final String CHINESE = "chinese-distsim.tagger";
	public static final String ENGLISH = "english-bidirectional-distsim.tagger";

	private MaxentTagger tagger;

	public PosTagger(String mode) {
		init(mode);
	}

	private void init(String mode) {
		tagger = new MaxentTagger("lib/stanford-postagger/models/" + mode);
		System.out.println("PosTagger is initialzed!");
	}

	public List<String> tagList(List<String> list) {
		String[] array = list.toArray(new String[list.size()]);
		return tagArray(array);
	}

	public List<String> tagArray(String[] array) {
		ArrayList<String> taggedList = new ArrayList<String>();
		String tagStr = "";
		List<HasWord> sent = Sentence.toWordList(array);
		List<TaggedWord> taggedSent = tagger.tagSentence(sent);

		String regex = "[A-Z]+";
		for (TaggedWord tw : taggedSent) {
			String tag = tw.tag();
			if (Pattern.matches(regex, tag) && !tag.equals("PU")) {
				tagStr = String.format("%s/%s", tw.word(), tw.tag());
				taggedList.add(tagStr);
			}
		}
//		System.out.println(taggedList);
		return taggedList;
	}

	public String tagEntity(String entity) {
		List<HasWord> sent = Sentence.toWordList(new String[] { entity });
		List<TaggedWord> taggedSent = tagger.tagSentence(sent);

		String tag = null;
		for (TaggedWord tw : taggedSent) {
			tag = tw.tag();
		}
		return tag;
	}

	public void tagSegFile(String filePath, String exportPath) {
		List<String> file = FileUtil.readFileByLine(filePath);
		List<String> list;
		try {
			FileWriter fw = new FileWriter(exportPath);
			for (String line : file) {
				if (!line.contains("[")) {
					// title
					fw.write(line);
					fw.write("\n");
				} else {
					line = line.substring(1, line.length() - 1);
					list = tagArray(line.split(" "));
					fw.write(list.toString());
					fw.write("\n");
				}
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String segDocPath = "data/newsData/segmentation/150118_seg_revise.txt";
		String taggedDocPath = "data/newsData/tagged/150118_tag_revise.txt";
		PosTagger tagger = new PosTagger(PosTagger.CHINESE);
		tagger.tagSegFile(segDocPath, taggedDocPath);
	}

}
