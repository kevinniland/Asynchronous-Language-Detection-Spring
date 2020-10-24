package ie.gmit.sw;

/**
 * @author Kevin Niland
 * @category Job/Task
 * @version 1.0
 *
 * Job class - Allows for the adding and removing of jobs to the queue
 */
public class Job {
	private String task;
	private String queryText;
	
	/**
	 * Constructor for Job - takes in task and query text 
	 * 
	 * @param task
	 * @param queryText
	 */
	public Job(String task, String queryText) {
		super();
		
		this.task = task;
		this.queryText = queryText;
	}

	/**
	 * Returns the task 
	 * 
	 * @return a String - task
	 */
	public String getTask() {
		return task;
	}

	/**
	 * Returns the query text
	 * 
	 * @return a String - queryText
	 */
	public String getQueryText() {
		return queryText;
	}
}
