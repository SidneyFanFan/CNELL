package cnell.relationLinking;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cnell.basic.extraction.Relation;
import cnell.util.FileUtil;

public class RelationLinking {
	public static void main(String[] args) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document document = db.parse(new File(
					"data/relation/result.owl.xml"));
			NodeList list = ((Element) document.getElementsByTagName("rdf:RDF")
					.item(0)).getElementsByTagName("owl:ObjectProperty");
			for (int i = 0; i < list.getLength(); i++) {
				Element element = (Element) list.item(i);
				System.out.println("rdf:about="
						+ element.getAttribute("rdf:about"));
				NodeList content = element.getElementsByTagName("rdfs:label");
				for (int j = 0; j < content.getLength(); j++) {
					Element label = (Element) content.item(j);
					if (label.getAttribute("xml:lang").equals("cn")) {
						linkRelation(label.getFirstChild().getNodeValue());
					}
				}
				System.out.println("--------------------------------------");
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void linkRelation(String label) {
		String filePath = "data/newsData/relationExtraction/pattern/merge/2015-1_ORG-PERSON.txt";
		List<String> nl = FileUtil.readFileByLine(filePath);

		for (String string : nl) {
			Relation rp = Relation.parse(string);
			if (rp.getPattern().contains(label)
					&& !rp.getPattern().contains("DEG")) {
				System.out.println(rp);
			}
		}

	}
}
