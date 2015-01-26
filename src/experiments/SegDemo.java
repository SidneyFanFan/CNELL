package experiments;

public class SegDemo {

	public static void main(String[] args){
		String jsonPath = "data/newsData/news.json";
		String segDocPath = "data/newsData/newsSeg.txt";
		Segmentor segmentor = new Segmentor();
		segmentor.segment(jsonPath, segDocPath);
	}
}
