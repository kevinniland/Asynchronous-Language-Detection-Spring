package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @author Kevin Niland
 * @category Database
 * @version 1.0
 * 
 *           DatabaseBuilder -
 * 
 *           Taken from the 'first half' of the Parser class in this video:
 *           https://web.microsoftstream.com/video/c12997b2-2e7f-4047-8763-b73d0ac0712c?referrer=https:%2F%2Flearnonline.gmit.ie%2Fcourse%2Fview.php%3Fid%3D945
 */
public class DatabaseBuilder implements Runnable {
	private ProxyDatabase proxyDatabase;
	private File file;
	private int k;
	
	public DatabaseBuilder() {
		
	}
	
	/**
	 * @param database - Proxy database
	 * @param file     - Data set file
	 * @param k        - kmer size
	 */
	public DatabaseBuilder(ProxyDatabase proxyDatabase, File file, int k) {
		super();

		this.proxyDatabase = proxyDatabase;
		this.file = file;
		this.k = k;
	}

	/**
	 * @see java.lang.Runnable#run()
	 * 
	 *      Reads in language data set. Splits the file into two "distinct" parts -
	 *      The contents of the file are stored into the array 'fileRecord' as
	 *      follows: Index 0 of fileRecord stores the paragraphs of each language.
	 *      Index 1 of fileRecord stores the names of each of the language
	 */
	@Override
	public void run() {
		System.out.println("Reading WiLI language dataset...");

		// Read in the language dataset
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileStr)));
			String line = null;

			System.out.println("Done");
			System.out.println("Parsing file...");

			// Split the file and trim it
			while ((line = bufferedReader.readLine()) != null) {
				String[] fileRecord = line.trim().split("@");

				if (fileRecord.length != 2) {
					continue;
				}

				parse(fileRecord[0].toLowerCase(), fileRecord[1]);
			}

			System.out.println("Done");
			bufferedReader.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Parses the text and languages of the subject file. Creates kmers from the
	 * subject text and add the kmer and language the kmer is in to the database
	 * 
	 * @param text - Subject text from the data set file
	 * @param lang - Language of subject text
	 */
	private void parse(String text, String lang, int... ks) {
		Language language = Language.valueOf(lang);

		/**
		 * Create the k-mers
		 * 
		 * Add the k-mer and language to the proxy database
		 */
		for (int i = 0; i <= text.length() - k; i++) {
			CharSequence kmer = text.substring(i, i + k);
			proxyDatabase.add(kmer, language);
		}
	}
	
	public static void main(String[] args) {
		new DatabaseBuilder().run();
	}
}
