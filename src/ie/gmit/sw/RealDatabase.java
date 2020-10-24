package ie.gmit.sw;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author Kevin Niland, John Healy
 * @category Database
 * 
 *           Proxy Pattern and Singleton Pattern implementation. Acts as the real database
 */
public class RealDatabase implements Database {
	private volatile static RealDatabase realDatabase;

	private RealDatabase() {

	}

	/**
	 * @return realDatabase instance
	 */
	public static RealDatabase getInstance() {
		if (realDatabase == null) {
			synchronized (RealDatabase.class) {
				if (realDatabase == null) {
					return realDatabase = new RealDatabase();
				}
			}
		}

		return realDatabase;
	}

	@Override
	public void add(CharSequence subjectText, Language lang) {
//		int kmer = subjectText.hashCode();
//		Map<Integer, LanguageEntry> langDb = getLanguageEntries(lang);
//
//		int frequency = 1;
//
//		if (langDb.containsKey(kmer)) {
//			frequency += langDb.get(kmer).getFrequency();
//		}
//
//		langDb.put(kmer, new LanguageEntry(kmer, frequency));
		
		System.out.println("Adding to database...");
	}

	/**
	 * @param max - Resizes database to a certain (max) size
	 */
	@Override
	public void resize(int max) {
//		Set<Language> keys = db.keySet();
//
//		for (Language lang : keys) {
//			Map<Integer, LanguageEntry> top = getTop(max, lang);
//			
//			db.put(lang, top);
//		}

		System.out.println("Resizing database (" + max + ")...");
	}

	/**
	 * @param max  -
	 * @param lang -
	 */
	@Override
	public Map<Integer, LanguageEntry> getTop(int max, Language lang) {
//		Map<Integer, LanguageEntry> temp = new TreeMap<>();
//		List<LanguageEntry> les = new ArrayList<>(db.get(lang).values());
//		Collections.sort(les);
//
//		int rank = 1;
//
//		for (LanguageEntry le : les) {
//			le.setRank(rank);
//			temp.put(le.getKmer(), le);
//
//			if (rank == max) {
//				break;
//			}
//			
//			rank++;
//		}
//
//		return temp;
		System.out.println("Getting top...");
		return null;
	}

	/**
	 * @param queryMap -
	 */
	@Override
	public Language getLanguage(Map<Integer, LanguageEntry> query) {
//		TreeSet<OutOfPlaceMetric> oopm = new TreeSet<>();
//
//		Set<Language> langs = db.keySet();
//
//		for (Language lang : langs) {
//			oopm.add(new OutOfPlaceMetric(lang, getOutOfPlaceDistance(query, db.get(lang))));
//		}
//		
//		return oopm.first().getLanguage();
		System.out.println("Getting language...");
		return null;
	}
}