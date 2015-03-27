package basic.extraction;

import basic.CNParam;

public class PatternExtractionParam extends CNParam<String[]> {

	public final static int INTERVENINGMODE_ALLTYPES = 1;
	public final static int INTERVENINGMODE_ONEOFTYPES = 2;

	public final static int EXTRACTIONMODE_INTERVENING = 1;
	public final static int EXTRACTIONMODE_PREPOSITION = 2;

	public static String ENTITY_TYPES = "ENTITY_TYPES";
	public static String INTERVENT_TYPES = "INTERVENT_TYPES";

	private int interveningMode;
	private int patternMaxLength;
	private int extractionMode;

	public PatternExtractionParam(String[] entityTypes,
			String[] interveningTypes, int patternMaxLength,
			int interveningMode, int extractionMode) {
		super();
		this.setParam(ENTITY_TYPES, entityTypes);
		this.setParam(INTERVENT_TYPES, interveningTypes);
		this.patternMaxLength = patternMaxLength;
		this.interveningMode = interveningMode;
		this.extractionMode = extractionMode;
	}

	public String[] getEntityTypes() {
		return this.getValue(ENTITY_TYPES);
	}

	public String[] getInterveningTypes() {
		return this.getValue(INTERVENT_TYPES);
	}

	public int getPatternMaxLength() {
		return patternMaxLength;
	}

	public int getInterveningMode() {
		return interveningMode;
	}

	public int getExtractionMode() {
		return extractionMode;
	}

	public String entityTypeString() {
		String[] types = getValue(ENTITY_TYPES);
		String ret = types[0];
		for (int i = 1; i < types.length; i++) {
			ret += "-" + types[i];
		}
		return ret;
	}

}
