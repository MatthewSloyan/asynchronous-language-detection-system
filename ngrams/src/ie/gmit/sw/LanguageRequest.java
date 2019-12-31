package ie.gmit.sw;

public class LanguageRequest {
	private String query;
	private String taskNum;
	
	public LanguageRequest(String query, String taskNum) {
		super();
		this.query = query;
		this.taskNum = taskNum;
	}

	public String getQuery() {
		return query;
	}

	public String getTaskNum() {
		return taskNum;
	}
}
