package ie.gmit.sw;

public class LanguageRequest {
	private String query;
	private int jobNum;
	
	public LanguageRequest(String query, int jobNum) {
		super();
		this.query = query;
		this.jobNum = jobNum;
	}

	public String getQuery() {
		return query;
	}

	public int getJobNum() {
		return jobNum;
	}
}
