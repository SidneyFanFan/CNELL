package basic;

import java.util.ArrayList;
import java.util.List;

/**
 * CNCategory class<br>
 * Each CNCategory has a list of category name
 * 
 * @author Sidney Fan
 *
 */
public class CNCategory {

	private List<String> cateList;

	public CNCategory(List<String> cateList) {
		super();
		this.cateList = cateList;
	}

	public List<String> getCateList() {
		return cateList;
	}

	public void setCateList(List<String> cateList) {
		this.cateList = cateList;
	}

	/**
	 * Parse category string with format:<br>
	 * #c1#c2...#cn
	 * 
	 * @param str
	 * @return
	 */
	public static CNCategory parse(String str) {
		String[] strSplit = str.split("#");
		List<String> cateList = new ArrayList<String>();

		if (strSplit.length < 2) {
			// illegal string format
			return null;
		}
		for (int i = 1; i < strSplit.length; i++) {
			cateList.add(strSplit[i]);
		}
		return new CNCategory(cateList);
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		for (String c : cateList) {
			str.append("#");
			str.append(c);
		}
		return str.toString();
	}

}
