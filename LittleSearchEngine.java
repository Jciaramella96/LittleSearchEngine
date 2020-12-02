package lse;
 
import java.io.*;



import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		
		Scanner sc = new Scanner(new File(docFile));
		HashMap<String, Occurrence> words = new HashMap<String, Occurrence>();
		  
		while(sc.hasNext()){
		String key = getKeyword(sc.next());
		if(key != null){
		if(words.containsKey(key)){
		words.get(key).frequency += 1;
		}
		else{
		words.put(key, new Occurrence(docFile, 1));
		}
		}
		}
		  
		return words;
		}
	
		
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		
		for(String key : kws.keySet()){
			if(keywordsIndex.containsKey(key)){
			keywordsIndex.get(key).add(kws.get(key));
			insertLastOccurrence(keywordsIndex.get(key));
			}
			else{
			ArrayList<Occurrence> temp = new ArrayList<Occurrence>();
			temp.add(kws.get(key));
			  
			keywordsIndex.put(key, temp);
			}
			}
		}
			  
	

	
	
	
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		
		word = word.toLowerCase();
		  
		if(!Character.isLetter(word.charAt(0))){
		return null;
		}
		  
		  
		String[] pieces = word.split("[.,\\?:;!]");
		  
		if(pieces.length != 1){
		return null;
		}
		  
		word = pieces[0];
		for(int i = 0; i < word.length(); i++){
		if(!Character.isLetter(word.charAt(i))){
		return null;
		}
		}
		  
		if(noiseWords.contains(word)){
		return null;
		}
		  
		return word;
		}
	
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		
		int freq = occs.get(occs.size() - 1).frequency;
		String docFile = occs.get(occs.size() - 1).document;
		ArrayList<Integer> mids = new ArrayList<Integer>();
		  
		int hi = 0;
		int lo = occs.size() - 2;
		int mid = 0;
		  
		while(lo >= hi){
		mid = (lo + hi)/2;
		mids.add(mid);
		  
		if(freq == occs.get(mid).frequency){
		break;
		}
		else if(freq > occs.get(mid).frequency){
		lo = mid - 1;
		}
		else if(freq < occs.get(mid).frequency){
		hi = mid + 1;
		}
		}
		  
		if(freq < occs.get(mid).frequency){
		int j = occs.size() - 2;
		for(; j > mid; j--){
		occs.set(j + 1, occs.get(j));
		}
		j++;
		occs.set(j, new Occurrence(docFile, freq));
		}
		else{
		int j = occs.size() - 2;
		for(; j >= mid; j--){
		occs.set(j + 1, occs.get(j));
		}
		j++;
		occs.set(j, new Occurrence(docFile, freq));
		}
		  
		return mids;
	}
	

	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		
		ArrayList<String> docs = new ArrayList<String>();
		  
		if(keywordsIndex.get(kw1) == null && keywordsIndex.get(kw2) == null){
		return null;
		}
		else if(keywordsIndex.get(kw1) == null || keywordsIndex.get(kw2) == null){
		ArrayList<Occurrence> temp = (keywordsIndex.get(kw1) != null) ? keywordsIndex.get(kw1) : keywordsIndex.get(kw2);
		  
		for(int i = 0; i < temp.size() && i < 4; i++){
		docs.add(temp.get(i).document);
		}
		  
		return docs;
		}
		  
		int k1ptr = 0;
		int k2ptr = 0;
		while(k1ptr < keywordsIndex.get(kw1).size() && k2ptr < keywordsIndex.get(kw2).size()){
		if(keywordsIndex.get(kw1).get(k1ptr).frequency >= keywordsIndex.get(kw2).get(k2ptr).frequency){
		if(!docs.contains(keywordsIndex.get(kw1).get(k1ptr).document)){
		docs.add(keywordsIndex.get(kw1).get(k1ptr).document);
		}
		k1ptr++;
		}
		else{
		if(!docs.contains(keywordsIndex.get(kw2).get(k2ptr).document)){
		docs.add(keywordsIndex.get(kw2).get(k2ptr).document);
		}
		k2ptr++;
		}
		}
		  
		for(; k1ptr < keywordsIndex.get(kw1).size(); k1ptr++){
		if(!docs.contains(keywordsIndex.get(kw1).get(k1ptr).document)){
		docs.add(keywordsIndex.get(kw1).get(k1ptr).document);
		}
		}
		for(; k2ptr < keywordsIndex.get(kw2).size(); k2ptr++){
		if(!docs.contains(keywordsIndex.get(kw2).get(k2ptr).document)){
		docs.add(keywordsIndex.get(kw2).get(k2ptr).document);
		}
		}
		  
		if(docs.size() >= 5){
		for(int i = docs.size() - 1; i > 4; i--){
		docs.remove(i);
		}
		}
		  
		return docs;
	}
	  
	}

		
		

