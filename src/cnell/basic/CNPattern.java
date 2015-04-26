package cnell.basic;

/**
 * Pattern interface
 * 
 * @author Sidney Fan
 *
 * @param <M>
 */
public interface CNPattern<M> {
	/**
	 * Match a pattern<br>
	 * If matched, return M type arguments.<br>
	 * Else, return null.<br>
	 * 
	 * @param str
	 * @return
	 */
	public M match(String str);
}
