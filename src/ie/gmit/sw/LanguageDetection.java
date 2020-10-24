package ie.gmit.sw;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Kevin Niland, John Healy
 * @category Language
 * @version 1.0
 * 
 *          LanguageDetection
 *
 *          Taken from the 'second half' of the Parser class found in this
 *          video:
 *          https://web.microsoftstream.com/video/c12997b2-2e7f-4047-8763-b73d0ac0712c?referrer=https:%2F%2Flearnonline.gmit.ie%2Fcourse%2Fview.php%3Fid%3D945
 */
public class LanguageDetection implements Runnable {
	private ProxyDatabase proxyDatabase;
	private int k;
	private boolean keepRunning = true;

	/**
	 * @param proxyDatabase - Database instance
	 * @param k             - k-mer size
	 */
	public LanguageDetection(ProxyDatabase proxyDatabase, int k) {
		super();

		this.proxyDatabase = proxyDatabase;
		this.k = k;
	}

	/**
	 * @see java.lang.Runnable#run()
	 * 
	 *      Assigns a job from the blocking queue. Adds it to the out queue (gets
	 *      the task and analyses the query text)
	 */
	@Override
	public void run() {
		while (keepRunning) {
			try {
				System.out.println("Assigning job from queue...");

				// Create and take a job from the blocking queue in the ServiceHandler
				Job languageDetection = ServiceHandler.blockingQueue.take();
//				Job addTask = new Job(languageDetection.getTask(), analyseQuery(languageDetection.getQueryText()).name());
//				
//				ServiceHandler.outQueue.add(addTask);

				// Add the job to the out queue
				ServiceHandler.outQueue.put(languageDetection.getTask(),
						analyseQuery(languageDetection.getQueryText()).name());

				System.out.println("Done");
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	/**
	 * Analyses the query text. Creates k-mers from the query text (the size of the
	 * k-mers are based on the k-mer size defined in ServiceHandler). The query text
	 * is then predicted by passing the query map into the getLanguage function of
	 * the database. This is done in a similar way to that of the parse function in
	 * DatabaseBuilder.java
	 * 
	 * @param queryText - Text the user specifies to be processed
	 */
	public Language analyseQuery(String queryText) {
		System.out.println("Analysing query text...");

		// Create a new map
		Map<Integer, LanguageEntry> queryMap = new ConcurrentHashMap<>();
		int kmer, frequency = 1;

		// Create k-mers from the query text
		for (int i = 0; i <= queryText.length() - k; i++) {
			CharSequence query = queryText.substring(i, i + k);
			kmer = query.hashCode();

			if (queryMap.containsKey(kmer)) {
				frequency += queryMap.get(kmer).getFrequency();
			}

			queryMap.put(kmer, new LanguageEntry(kmer, frequency));
		}

		System.out.println("Done");
//		System.out.println(database.getLanguage(queryMap));
		return proxyDatabase.getLanguage(queryMap);
	}
}
