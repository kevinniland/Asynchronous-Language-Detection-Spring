package ie.gmit.sw;

/**
 * @author Kevin Niland, John Healy
 * @category Database
 * @version 1.0
 * 
 * OutOfPlaceMetric - Determines the distance between the language and text i.e. how similar the text is to a certain language
 */
public class OutOfPlaceMetric implements Comparable<OutOfPlaceMetric> {
	private Language lang;
	private int distance;

	/**
	 * @param lang - The language name
	 * @param distance - Distance between language and text
	 */
	public OutOfPlaceMetric(Language lang, int distance) {
		super();
		
		this.lang = lang;
		this.distance = distance;
	}

	/**
	 * Gets the language name
	 * 
	 * @return a Language, lang
	 */
	public Language getLanguage() {
		return lang;
	}

	/**
	 * Gets the distance between the language and the text
	 * 
	 * @return Math.abs(distance)
	 */
	public int getAbsoluteDistance() {
		return Math.abs(distance);
	}

	/**
	 * Compares each distance and will return them in ascending order
	 */
	@Override
	public int compareTo(OutOfPlaceMetric outOfPlaceMetric) {
		return Integer.compare(this.getAbsoluteDistance(), outOfPlaceMetric.getAbsoluteDistance());
	}

	/**
	 * Prints language and distance between language and text
	 */
	@Override
	public String toString() {
		return "[lang=" + lang + ", distance=" + getAbsoluteDistance() + "]";
	}
}
