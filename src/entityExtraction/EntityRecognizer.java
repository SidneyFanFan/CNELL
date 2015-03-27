package entityExtraction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import util.FileUtil;

public class EntityRecognizer {

	private AbstractSequenceClassifier<CoreLabel> classifier;

	public EntityRecognizer() {
		String serializedClassifier = "lib/stanford-ner-2015-01-30/classifiers/chinese.misc.distsim.crf.ser.gz";
		try {
			classifier = CRFClassifier.getClassifier(serializedClassifier);
		} catch (ClassCastException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String segPath = "data/newsData/segmentation/2015-1/150101_seg.txt";
		String nerPath = "data/newsData/ner/2015-1/150101_ner.txt";

		EntityRecognizer rdp = new EntityRecognizer();
		rdp.recogEntityInSegFile(segPath, nerPath);
	}

	public void recogEntityInSegFile(String filePath, String exportPath) {
		List<String> file = FileUtil.readFileByLine(filePath);
		try {
			FileWriter fw = new FileWriter(exportPath);
			String xml = "";
			for (String line : file) {
				if (!line.contains("[")) {
					// title
					fw.write(line);
					fw.write("\n");
				} else {
					line = line.substring(1, line.length() - 1);
					// fw.write(classifier.classifyToString(line,
					// "tabbedEntities", false));
					xml = classifier.classifyWithInlineXML(line);
					if (xml.contains("<")) {
						fw.write(xml);
						fw.write("\n");
					}

				}
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
