package ie.gmit.sw;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @author Kevin Niland, John Healy
 * @category ServiceHandler
 * @version 1.0
 *
 *          ServiceHanlder
 */
public class ServiceHandler extends HttpServlet {
	ProxyDatabase proxyDatabase = new ProxyDatabase();

	/**
	 * From: https://www.baeldung.com/java-executor-service-tutorial
	 * 
	 * ExecutorService is a framework provided by the JDK which simplifies the
	 * execution of tasks in asynchronous mode. Generally speaking, ExecutorService
	 * automatically provides a pool of threads and API for assigning tasks to it.
	 * 
	 * Used to dispatch a thread to handle the language detection after something is
	 * added to the queue
	 */
	private ExecutorService executorService = Executors.newFixedThreadPool(50);
	private File file;

	// This variable is shared by all HTTP requests for the servlet
	private String languageDataSet = null;

	// The number of the task in the async queue
	private long jobNumber = 0;

	// Set initial k-mer size - can then be changed depending on the option the user
	// sets
	private int kmer = 4;

	private static final long serialVersionUID = 1L;

	static BlockingQueue<Job> blockingQueue = new ArrayBlockingQueue<Job>(20);

	// Concurrent hash map - Checks for finished job
	static Map<CharSequence, CharSequence> outQueue = new ConcurrentHashMap<CharSequence, CharSequence>();

	// init is called for every instance of the servlet
	public void init() throws ServletException {
		ServletContext ctx = getServletContext(); // Get a handle on the application context

		// Reads the value from the <context-param> in web.xml
		languageDataSet = ctx.getInitParameter("LANGUAGE_DATA_SET");
//		System.out.println(System.getProperty("user.dir"));

		/*
		 * You can start to build the subject database at this point. The init() method
		 * is only ever called once during the life cycle of a servlet build the query
		 * database
		 */
		file = new File(languageDataSet);

		// Build the database
		DatabaseBuilder databaseBuilder = new DatabaseBuilder(proxyDatabase, file, kmer);

		Thread thread = new Thread(databaseBuilder);

		/**
		 * Use this thread for each browser - allows the adding and removing from the
		 * queue
		 */
		thread.start();

		/**
		 * Wait for the database to fully built first - Previously just started the
		 * thread and then resized it immediately, leading to inaccurate language
		 * predictions
		 */
		try {
			thread.join();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		// Resize the database
		proxyDatabase.resize(300);
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 * 
	 * @param req  - Extends the ServletRequest interface to provide request
	 *             information for HTTP servlets
	 * @param resp - Extends the ServletResponse interface to provide HTTP-specific
	 *             functionality in sending a response
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html"); // Output the MIME type

		PrintWriter out = resp.getWriter(); // Write out text. We can write out binary too and change the MIME type...

		/**
		 * Initialise some request variables with the submitted form info. These are
		 * local to this method and thread safe
		 */
		String option = req.getParameter("cmbOptions");
		String queryText = req.getParameter("query");
		String taskNumber = req.getParameter("frmTaskNumber");

		// Used to display the predicted language
		CharSequence language = req.getParameter("languagePrediction");

		// Display some text while the predicted language is being determined
		language = "Predicting language...";

		out.print("<html><head><title>AOOP - Asynchronous Language Detection (G00342279)</title>");
		out.print("</head>");
		out.print("<body>");

		if (taskNumber == null) {
			taskNumber = new String("T" + jobNumber);
			jobNumber++;

			// Add job to blocking queue
			if (queryText != null) {
				try {
					System.out.println("Adding job to the queue...");

					buildBlockingQueue(taskNumber, queryText);

					System.out.println("Done");
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}
		} else {
			if (outQueue.containsKey(taskNumber)) {
				language = outQueue.get(taskNumber);

				outQueue.remove(taskNumber);
			}
		}

		out.print("<H1>Processing request for Job#: " + taskNumber + "</H1>");
		out.print("<div id=\"r\"></div>");
		out.print("<font color=\"#993333\"><b>");
		out.print("Language Dataset is located at " + languageDataSet + " and is <b><u>" + file.length()
				+ "</u></b> bytes in size");
		out.print("<br>Option: " + option);
		out.print("<br>Query Text: " + queryText);
		out.print("<br>Language: " + language);
		out.print("</font><p/>");
		out.print(
				"<br>This servlet should only be responsible for handling client request and returning responses. Everything else should be handled by different objects. ");
		out.print(
				"Note that any variables declared inside this doGet() method are thread safe. Anything defined at a class level is shared between HTTP requests.");
		out.print("</b></font>");
		out.print("<P> Next Steps:");
		out.print("<OL>");
		out.print(
				"<LI>Generate a big random number to use a a job number, or just increment a static long variable declared at a class level, e.g. jobNumber.");
		out.print("<LI>Create some type of an object from the request variables and jobNumber.");
		out.print("<LI>Add the message request object to a LinkedList or BlockingQueue (the IN-queue)");
		out.print(
				"<LI>Return the jobNumber to the client web browser with a wait interval using <meta http-equiv=\"refresh\" content=\"10\">. The content=\"10\" will wait for 10s.");
		out.print("<LI>Have some process check the LinkedList or BlockingQueue for message requests.");
		out.print(
				"<LI>Poll a message request from the front of the queue and pass the task to the language detection service.");
		out.print("<LI>Add the jobNumber as a key in a Map (the OUT-queue) and an initial value of null.");
		out.print(
				"<LI>Return the result of the language detection system to the client next time a request for the jobNumber is received and the task has been complete (value is not null).");
		out.print("</OL>");
		out.print("<form method=\"POST\" name=\"frmRequestDetails\">");
		out.print("<input name=\"cmbOptions\" type=\"hidden\" value=\"" + option + "\">");
		out.print("<input name=\"query\" type=\"hidden\" value=\"" + queryText + "\">");
		out.print("<input name=\"frmTaskNumber\" type=\"hidden\" value=\"" + taskNumber + "\">");
		out.print("</form>");
		out.print("</body>");
		out.print("</html>");
		out.print("<script>");
		out.print("var wait=setTimeout(\"document.frmRequestDetails.submit();\", 10000);");
		out.print("</script>");
	}

	/**
	 * Builds the blocking queue - Creates a new Job from the task number and query
	 * text
	 * 
	 * @param taskNumber 
	 * @param queryText  
	 */
	private void buildBlockingQueue(String taskNumber, String queryText) throws InterruptedException {
		// Add new job to the in queue - pass in the task number and query text
		blockingQueue.put(new Job(taskNumber, queryText));

		executorService.execute(new LanguageDetection(proxyDatabase, kmer));
	}

	/**
	 * @param req  
	 * @param resp 
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}