package ie.gmit.sw;

/**
 * @author John Healy
 * @category Language
 * @version 1.0
 * 
 *           LanguageEntry - Gets and sets k-mer size, frequency of occurrence of
 *           k-mer in language, and rank of the k-mer in terms of its frequency
 */
public class LanguageEntry implements Comparable<LanguageEntry> {
	private int kmer; // Contiguous substring of text of size n
	private int frequency; // Frequency of the occurrence of a k-mer in a language
	private int rank; // Ranking of the k-mer in terms of its frequency

	/**
	 * @param kmer      - k-mer size the text substring of size n
	 * @param frequency - Frequency of the occurrence of a k-mer in a language
	 */
	public LanguageEntry(int kmer, int frequency) {
		super();

		this.kmer = kmer;
		this.frequency = frequency;
	}

	/**
	 * Gets the k-mer size
	 * 
	 * @return an int, kmer
	 */
	public int getKmer() {
		return kmer;
	}

	/**
	 * Sets the k-mer size
	 * 
	 * @param kmer
	 */
	public void setKmer(int kmer) {
		this.kmer = kmer;
	}

	/**
	 * Gets the frequency of the occurrence of a k-mer in a language
	 * 
	 * @return an int, frequency
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * Sets the frequency of the occurrence of a k-mer in a language
	 * 
	 * @param frequency
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * Gets the rank of the k-mers in terms of their frequency
	 * 
	 * @return an int, rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * Sets the rank of the k-mers in terms of their frequency
	 * 
	 * @param rank
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * Compare one language entry (k-mer) to another by their frequency in
	 * descending order
	 * 
	 * @param next - Next k-mer to be compared
	 */
	@Override
	public int compareTo(LanguageEntry next) {
		return -Integer.compare(frequency, next.getFrequency());
	}

	/**
	 * Prints k-mer, frequency, and rank
	 */
	@Override
	public String toString() {
		return "[" + kmer + "/" + frequency + "/" + rank + "]";
	}
}