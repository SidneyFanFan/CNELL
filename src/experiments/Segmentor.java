package experiments;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import gvjava.org.json.JSONArray;
import gvjava.org.json.JSONException;
import gvjava.org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import util.FileUtil;

public class Segmentor {

	private HashMap<String, List<List<String>>> segmentMap;

	private CRFClassifier<CoreLabel> segmenter;

	public Segmentor() {
		init();
	}

	private void init() {
		String basedir = "lib/stanford-segmenter/data";
		try {
			System.setOut(new PrintStream(System.out, true, "utf-8"));
			Properties props = new Properties();
			props.setProperty("sighanCorporaDict", basedir);
			props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
			props.setProperty("inputEncoding", "UTF-8");
			props.setProperty("sighanPostProcessing", "true");

			segmenter = new CRFClassifier<CoreLabel>(props);
			segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("Segmentor is initialzed!");
	}

	public void segment(String jsonPath, String exportPath) {
		// "data/newsData/news.json"
		// "data/newsData/newsSeg.txt"
		segmentMap = new HashMap<String, List<List<String>>>();
		// import document
		String news = FileUtil.readFile(jsonPath);
		FileWriter fw;
		JSONArray ja;
		String title, textObj, text;
		JSONObject jobj;
		ArrayList<List<String>> list;
		try {
			ja = new JSONArray(news);
			fw = new FileWriter(exportPath);
			for (int i = 0; i < ja.length(); i++) {
				title = ja.getJSONArray(i).getString(0);
				textObj = ja.getJSONArray(i).getString(1);
				jobj = new JSONObject(textObj);
				text = jobj.getString("text");

				list = new ArrayList<List<String>>();
				fw.write(title);
				fw.write("\n");
				String[] texts = text.split("\n");
				for (String seg : texts) {
					List<String> segmented = segmenter.segmentString(seg);
					list.add(segmented);
					// fw.write(segmented.toString().replace(",", ""));
					fw.write(segmented.toString());
					fw.write("\n");
				}
				segmentMap.put(title, list);
			}
			fw.flush();
			fw.close();
			System.out.println("Totally has items:" + ja.length());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public HashMap<String, List<List<String>>> getSegmentMap() {
		return segmentMap;
	}

	public static void main(String[] args) {
		String jsonPath = "data/newsData/source/150118.json";
		String segDocPath = "data/newsData/segmentation/150118_seg.txt";
		Segmentor segmentor = new Segmentor();
		segmentor.segment(jsonPath, segDocPath);
	}

}
