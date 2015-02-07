package basic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CNews {
	private String link;
	private List<String> body;
	private HashSet<String> allEntities;
	private HashSet<String> patternSet;
	private HashSet<String> instanceSet;
	/* map: line=[set of entity] */
	private HashMap<String, HashSet<String>> entitiesInLine;

	public CNews(String link, List<String> body) {
		this.link = link;
		this.body = body;
		this.allEntities = new HashSet<String>();
		this.entitiesInLine = new HashMap<String, HashSet<String>>();
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public List<String> getBody() {
		return body;
	}

	public void setBody(List<String> body) {
		this.body = body;
	}

	public HashSet<String> getAllEntities() {
		return allEntities;
	}

	public void setAllEntities(HashSet<String> allEntities) {
		this.allEntities = allEntities;
	}

	public HashMap<String, HashSet<String>> getEntitiesInLine() {
		return entitiesInLine;
	}

	public void setEntitiesInLine(
			HashMap<String, HashSet<String>> entitiesInLine) {
		this.entitiesInLine = entitiesInLine;
	}

	public HashSet<String> getPatternSet() {
		return patternSet;
	}

	public void setPatternSet(HashSet<String> patternSet) {
		this.patternSet = patternSet;
	}

	public HashSet<String> getInstanceSet() {
		return instanceSet;
	}

	public void setInstanceSet(HashSet<String> instanceSet) {
		this.instanceSet = instanceSet;
	}

}