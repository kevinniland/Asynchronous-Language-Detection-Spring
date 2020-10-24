package ie.gmit.sw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Kevin Niland, John Healy
 * @category Database
 * @version 1.0
 *
 *          Proxy Pattern implementation. Acts as a proxy to RealDatabase.java
 */
public class ProxyDatabase implements Database {
	// Maps languages to their k-mers and frequency of occurrence
	private Map<Language, Map<Integer, LanguageEntry>> proxyDatabase = new ConcurrentHashMap<>();
	private RealDatabase realDatabase = RealDatabase.getInstance();
	private boolean isExecuted = false;
//	private AtomicBoolean isDisplayed = new AtomicBoolean();
//	private volatile static ProxyDatabase proxyDatabase;

	/**
	 * @param subjectText - Text from data set
	 * @param lang        - Language of each accompanying paragraph of text
	 */
	@Override
	public void add(CharSequence subjectText, Language lang) {
		int kmer = subjectText.hashCode();
		Map<Integer, LanguageEntry> langDb = getLanguageEntries(lang);

		int frequency = 1;

		if (langDb.containsKey(kmer)) {
			frequency += langDb.get(kmer).getFrequency();
		}

		// Displays message that the subject text and it's language has been added to the database
		if (!isExecuted) {
			realDatabase.add(subjectText, lang);
			isExecuted = true;
		}

		langDb.put(kmer, new LanguageEntry(kmer, frequency));
	}

	/**
	 * @param lang - Language name
	 * 
	 * @return a Map, langDb
	 */
	private Map<Integer, LanguageEntry> getLanguageEntries(Language lang) {
		Map<Integer, LanguageEntry> langDb = null;

		if (proxyDatabase.containsKey(lang)) {
			langDb = proxyDatabase.get(lang);
		} else {
			langDb = new TreeMap<Integer, LanguageEntry>();

			proxyDatabase.put(lang, langDb);
		}

		return langDb;
	}

	/**
	 * @param max - Size database will be resized to
	 */
	@Override
	public void resize(int max) {
		Set<Language> keys = proxyDatabase.keySet();

		for (Language lang : keys) {
			Map<Integer, LanguageEntry> top = getTop(max, lang);

			proxyDatabase.put(lang, top);
		}

		// Displays message that the database has been resized
		if (!isExecuted) {
			realDatabase.resize(max);
			isExecuted = true;
		}
	}

	/**
	 * @param max
	 * @param lang
	 */
	@Override
	public Map<Integer, LanguageEntry> getTop(int max, Language lang) {
		Map<Integer, LanguageEntry> temp = new TreeMap<>();
		List<LanguageEntry> les = new ArrayList<>(proxyDatabase.get(lang).values());
		Collections.sort(les);

		int rank = 1;

		for (LanguageEntry le : les) {
			le.setRank(rank);
			temp.put(le.getKmer(), le);

			if (rank == max) {
				break;
			}

			rank++;
		}

		if (!isExecuted) {
			realDatabase.getTop(max, lang);
			isExecuted = true;
		}

		return temp;
	}

	/**
	 * Gets the language of the query text the user has entered
	 * 
	 * @param query - Query text
	 */
	@Override
	public synchronized Language getLanguage(Map<Integer, LanguageEntry> query) {
		TreeSet<OutOfPlaceMetric> oopm = new TreeSet<>();

		Set<Language> langs = proxyDatabase.keySet();

		for (Language lang : langs) {
			oopm.add(new OutOfPlaceMetric(lang, getOutOfPlaceDistance(query, proxyDatabase.get(lang))));
		}

		// Displays message that the application is getting the predicted language of the query text
		if (!isExecuted) {
			realDatabase.getLanguage(query);
			isExecuted = true;
		}
		
		return oopm.first().getLanguage();
	}

	/**
	 * Gets the distance between query text and subject text
	 * 
	 * @param query   - Query text map
	 * @param subject - Subject text map
	 * 
	 * @return distance - Distance between query text and subject text
	 */
	private int getOutOfPlaceDistance(Map<Integer, LanguageEntry> query, Map<Integer, LanguageEntry> subject) {
		int distance = 0;

		Set<LanguageEntry> les = new TreeSet<>(query.values());

		for (LanguageEntry q : les) {
			LanguageEntry s = subject.get(q.getKmer());

			if (s == null) {
				distance += subject.size() + 1;
			} else {
				distance += s.getRank() - q.getRank();
			}
		}

		return distance;
	}

	/**
	 * Displays the total amount of k-mers in the total number of languages
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		int langCount = 0;
		int kmerCount = 0;

		Set<Language> keys = proxyDatabase.keySet();

		for (Language lang : keys) {
			langCount++;
			stringBuilder.append(lang.name() + "->\n");

			Collection<LanguageEntry> m = new TreeSet<>(proxyDatabase.get(lang).values());
			kmerCount += m.size();

			for (LanguageEntry languageEntry : m) {
				stringBuilder.append("\t" + languageEntry + "\n");
			}
		}

		stringBuilder.append(kmerCount + " total k-mers in " + langCount + " languages");

		return stringBuilder.toString();
	}
}
