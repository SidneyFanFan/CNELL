package cnell.basic;

import java.util.HashMap;

public abstract class CNParam<T> {

	private HashMap<String, T> map = new HashMap<String, T>();

	protected void setParam(String attr, T value) {
		map.put(attr, value);
	}

	protected T getValue(String attr) {
		return map.get(attr);
	}
}
