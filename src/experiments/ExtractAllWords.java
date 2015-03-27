package experiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import util.FileUtil;

public class ExtractAllWords {
	public static void main(String args[]) {
		File file = new File("data/newsData/segmentation/2015-1");
		HashSet<String> wordset = new HashSet<String>();
		if (file.isDirectory()) {
			File[] dirFile = file.listFiles();
			for (File f : dirFile) {
				if (f.isFile()) {
					List<String> lines = FileUtil.readFileByLine(f.getPath());
					for (String line : lines) {
						if (line.contains("[")) {
							String[] split = line.substring(1, line.length())
									.split(" ");
							for (String string : split) {
								wordset.add(string);
							}
						}
					}
				}
			}
		}
		file = new File("data/newsData/segmentation/2015-2");
		if (file.isDirectory()) {
			File[] dirFile = file.listFiles();
			for (File f : dirFile) {
				if (f.isFile()) {
					List<String> lines = FileUtil.readFileByLine(f.getPath());
					for (String line : lines) {
						if (line.contains("[")) {
							String[] split = line.substring(1,
									line.length() - 1).split(" ");
							for (String string : split) {
								wordset.add(string);
							}
						}
					}
				}
			}
		}
		try {
			FileWriter fw = new FileWriter(
					"data/newsData/segmentation/allwords.txt");
			for (String string : wordset) {
				fw.write(string);
				fw.write("\n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
