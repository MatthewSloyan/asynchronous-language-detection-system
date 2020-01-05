package ie.gmit.sw;

/**
* Object which is added to the inQueue.
* 
* @author Matthew Sloyan
*/
public class LanguageRequest {
	private String query;
	private String taskNum;
	
	/**
	* Constructor for class
	* 
	* @param query user input string.
	* @param taskNum unique tasknumber for job in queue.
	*/
	public LanguageRequest(String query, String taskNum) {
		super();
		this.query = query;
		this.taskNum = taskNum;
	}

	// Get values only (immutable)
	public String getQuery() {
		return query;
	}

	public String getTaskNum() {
		return taskNum;
	}
}
