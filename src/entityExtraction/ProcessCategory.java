package entityExtraction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class ProcessCategory {

	public static void main(String[] args) {
		String categoryPath = "data/baidubaike/category.txt";
		String simpleCategoryPath = "data/baidubaike/category_simple.txt";

		HashMap<String, String> simpleCateMap = new HashMap<String, String>();
		BufferedReader br = null;
		FileWriter fw = null;
		try {
			br = new BufferedReader(new FileReader(categoryPath));
			fw = new FileWriter(simpleCategoryPath);
			PosTagger tagger = new PosTagger(PosTagger.CHINESE);
			String line, entity, cates, tag;
			String[] categroup;
			while (br.ready()) {
				line = br.readLine();
				categroup = line.split("\t");
				if (categroup.length != 4) {
					System.out.println(line);
					continue;
				}
				entity = categroup[0];
				tag = tagger.tagEntity(entity);
				if (!tag.contains("N")) {
					continue;
				}
				cates = simpleCateMap.get(categroup[0]);
				if (cates == null) {
					cates = "#" + categroup[2];
					simpleCateMap.put(entity, cates);
				} else {
					cates = String.format("%s#%s", cates, categroup[2]);
					simpleCateMap.put(entity, cates);
				}
			}
			for (Entry<String, String> en : simpleCateMap.entrySet()) {
				fw.write(String.format("%s\t%s\n", en.getKey(), en.getValue()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
