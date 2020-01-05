package ie.gmit.sw;

/**
* Supplied OutOfPlaceMetric class that is used to get the OutOfPlaceMetric distance between the query and database maps.
* 
* @author John Healy
*/
public class OutOfPlaceMetric implements Comparable<OutOfPlaceMetric>{
	private Language lang;
	private int distance;
	
	public OutOfPlaceMetric(Language lang, int distance) {
		super();
		this.lang = lang;
		this.distance = distance;
	}

	public Language getLanguage() {
		return lang;
	}

	public int getAbsoluteDistance() {
		return Math.abs(distance);
	}

	@Override
	public int compareTo(OutOfPlaceMetric o) {
		return Integer.compare(this.getAbsoluteDistance(), o.getAbsoluteDistance());
	}
}
