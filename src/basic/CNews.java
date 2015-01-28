package basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CNews {
	private String link;
	private List<String> body;
	private HashSet<String> allEntities;
	private List<CoPair> coPairs;
	private HashSet<String> patternSet;
	private HashSet<String> instanceSet;
	/* map: line=[set of entity] */
	private HashMap<String, HashSet<String>> entitiesInLine;

	public CNews(String link, List<String> body) {
		this.link = link;
		this.body = body;
		this.allEntities = new HashSet<String>();
		this.entitiesInLine = new HashMap<String, HashSet<String>>();
		this.coPairs = new ArrayList<CoPair>();
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

	public class CoPair {
		private String p, q;
		private int count;

		public CoPair(String p, String q, int count) {
			super();
			this.p = p;
			this.q = q;
			this.count = count;
		}

		public String getP() {
			return p;
		}

		public void setP(String p) {
			this.p = p;
		}

		public String getQ() {
			return q;
		}

		public void setQ(String q) {
			this.q = q;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		@Override
		public String toString() {
			return String.format("(%s,%s)=%d", p, q, count);
		}

	}

	/**
	 * @param pairStr
	 *            -format: (p,q)=number
	 */
	public void addCoOccurrencePair(String pairStr) {
		int leftPar = pairStr.indexOf("(");
		int rightPar = pairStr.lastIndexOf(")");
		int equation = pairStr.lastIndexOf("=");
		String[] ws = pairStr.substring(leftPar + 1, rightPar).split(",");
		int count = Integer.valueOf(pairStr.substring(equation + 1).trim());
		coPairs.add(new CoPair(ws[0], ws[1], count));
	}

	public List<CoPair> getCoPairs() {
		return coPairs;
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