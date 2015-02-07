package basic;

/**
 * Entity class<br>
 * Each entity has a category
 * 
 * @author Sidney Fan
 *
 */
public class CNEntity {

	private String word;
	private CNCategory category;

	public CNEntity(String word, CNCategory category) {
		this.word = word;
		this.category = category;
	}

	public CNCategory getCategory() {
		return category;
	}

	public void setCategory(CNCategory category) {
		this.category = category;
	}

	/**
	 * Parse entity with format:<br>
	 * word\t{#c}
	 * 
	 * @param str
	 * @return
	 */
	public static CNEntity parse(String str) {
		String[] strSplit = str.split("\t");
		if (strSplit.length != 2) {
			// illegal string format
			return null;
		}
		CNCategory cate = CNCategory.parse(strSplit[1]);
		return new CNEntity(strSplit[0], cate);
	}

	@Override
	public String toString() {
		return String.format("%s\t%s", word, category.toString());
	}
}
