package basic;

/**
 * Chinese word class<br>
 * CWord has a Chinese string word and a POS tag
 * 
 * @author Sidney Fan
 *
 */
public class CNWord {

	private String word;
	private String tag;

	public CNWord(String word, String tag) {
		super();
		this.word = word;
		this.tag = tag;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Parse word with format:<br>
	 * word/tag
	 * 
	 * @param str
	 * @return
	 */
	public static CNWord parse(String str) {
		String[] strSplit = str.split("/");
		if (strSplit.length != 2) {
			// illegal string format
			return null;
		}
		return new CNWord(strSplit[0], strSplit[1]);
	}

	@Override
	public String toString() {
		return String.format("%s/%s", word, tag);
	}

}
