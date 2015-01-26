package experiments;

public class TagDemo {
	public static void main(String[] args) {
		String segDocPath = "data/newsData/newsSeg.txt";
		String taggedDocPath = "data/newsData/taggedNews.txt";
		PosTagger tagger = new PosTagger(PosTagger.CHINESE);
		tagger.tagSegFile(segDocPath, taggedDocPath);
	}
}
