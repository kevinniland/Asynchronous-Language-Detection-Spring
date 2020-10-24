package ie.gmit.sw;

import java.util.Map;

/**
 * @author Kevin Niland
 * @category Database
 * @version 1.0
 * 
 * Database interface
 */
public interface Database {
	public void add(CharSequence subjectText, Language lang);
	public void resize(int max);
	public Map<Integer, LanguageEntry> getTop(int max, Language lang);
	public Language getLanguage(Map<Integer, LanguageEntry> query);
}
