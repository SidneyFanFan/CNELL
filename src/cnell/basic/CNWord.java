package cnell.basic;

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
		int pos = str.lastIndexOf("/");
		return new CNWord(str.substring(0, pos), str.substring(pos + 1));
	}

	@Override
	public String toString() {
		return String.format("%s/%s", word, tag);
	}

}
