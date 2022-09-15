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
		Scanner scanner = new Scanner(new File(docFile));
		HashMap<String, Occurrence> current = new HashMap<String, Occurrence>();
		int count = 0;

		while(scanner.hasNext())
		{
			String word = scanner.next();
			word = getKeyword(word);
			if(word != null)
			{
				if(!current.containsKey(word)) //if the word is not already in the hashtable then add it in
				{
					count = 1;
					Occurrence hey = new Occurrence(docFile, count);
					current.put(word, hey);
				}
				else //otherwise just add to the frequency of the word by 1
				{
					current.get(word).frequency++;
				}
			}
		}
		scanner.close();
		// following line is a placeholder to make the program compile
		// you should modify it as needed when you write your code
		return current;
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
		for(String str : kws.keySet())
		{
			if(keywordsIndex.containsKey(str))
			{
				keywordsIndex.get(str).add(kws.get(str));
				insertLastOccurrence(keywordsIndex.get(str));
			}
			else
			{
				ArrayList<Occurrence> occs = new ArrayList<Occurrence>();
				occs.add(kws.get(str));
				keywordsIndex.put(str, occs);
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
		//if there is any punctuation at the end cut it off until there is none, and then makes the word null if there is a letter in the middle or it's not null yet but it is in noisewords
		while (word.length() != 0 && (word.charAt(word.length() - 1) == '?' || word.charAt(word.length() - 1) == ',' || word.charAt(word.length() - 1) == '.' || word.charAt(word.length() - 1) == '!' || word.charAt(word.length() - 1) == ';' || word.charAt(word.length() - 1) == ':')) 
		{	
			char end = word.charAt(word.length() - 1);
			if (end == '?' || end == ',' || end == '.' || end == '!' || end == ';' || end == ':') 
			{
			   word = word.substring(0, word.length() - 1);
			}
		 }
   
		 int i = 0;
		 while(i < word.length())
		 {
			char letter = word.charAt(i);
			if (!Character.isLetter(letter)) 
			{
			   word = null;
			   break;
			}
			i++;
		 }
   
		 if ((noiseWords.contains(word)) || (word != null && word.length() == 0)) 
		 {
			word = null;
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
		if(occs.size() == 1 || occs.size() == 0) return null;
		ArrayList<Integer> mids = new ArrayList<Integer>();
		int last = occs.get(occs.size()-1).frequency;
		Occurrence las = occs.remove(occs.size()-1);
		int mid = 0;
		int high = 0;
		int small = occs.size()-1;
		while(high <= small)
		{
			mid = high + (small - high) / 2;
			// System.out.println(mid);
			if(occs.get(mid).frequency == last)
			{
				occs.add(mid, las);
				mids.add(mid); //added this
				return mids;
			}
			if(occs.get(mid).frequency < last)
			{
				small = mid - 1;
				mids.add(mid); //took out the part where it added it at the first index
			}
			else
			{
				high = mid + 1;
				mids.add(mid); //took out the part where it added it at the first index
			}
		}
		occs.add(high, las);
		return mids;
		// for(int m : mids)
		// {
		// 	System.out.print(m + ", ");
		// }
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
		ArrayList<Occurrence> docs1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> docs2 = new ArrayList<Occurrence>();

		if(!keywordsIndex.containsKey(kw1) && !keywordsIndex.containsKey(kw2))
		{
			return null;
		}
		else if(!keywordsIndex.containsKey(kw1))
		{

			//do the same as you did for kw1 for kw2
			if(keywordsIndex.containsKey(kw2)) // if kw1 is in keywordsIndex
			{
				if(keywordsIndex.get(kw2).size() <= 5) //if the size of the arraylist of occurrences is less than or equal to 5 then add all of them to docs1
				{
					for(int i = 0; i < keywordsIndex.get(kw2).size(); i++)
					{
						docs2.add(keywordsIndex.get(kw2).get(i));
					}
				}
				else //if the size of arraylist of occurrences is greater than 5 then add only up to 5 docs from the occurrences
				{
					for(int i = 0; i < 5; i++)
					{
						docs2.add(keywordsIndex.get(kw2).get(i));
					}
				}
			}

			ArrayList<String> finalDocs2 = new ArrayList<String>();
			for(Occurrence occ : docs2)
			{
				finalDocs2.add(occ.document);
			}
			//now check to see if the finalNames arraylist of doc names is 5 or smaller or bigger than 5 and change it accordingly
			if(finalDocs2.size() > 5)
			{
				while(finalDocs2.size() > 5)
				{
					docs2.remove(5);
				}
				return finalDocs2;
			}
			else
			{
				return finalDocs2;
			}		
		}
		else if(!keywordsIndex.containsKey(kw2))
		{
			//do the same as you did for kw1 for kw2
			if(keywordsIndex.containsKey(kw1)) // if kw1 is in keywordsIndex
			{
				if(keywordsIndex.get(kw1).size() <= 5) //if the size of the arraylist of occurrences is less than or equal to 5 then add all of them to docs1
				{
					for(int i = 0; i < keywordsIndex.get(kw1).size(); i++)
					{
						docs2.add(keywordsIndex.get(kw1).get(i));
					}
				}
				else //if the size of arraylist of occurrences is greater than 5 then add only up to 5 docs from the occurrences
				{
					for(int i = 0; i < 5; i++)
					{
						docs2.add(keywordsIndex.get(kw1).get(i));
					}
				}
			}

			ArrayList<String> finalDocs1 = new ArrayList<String>();
			for(Occurrence occ : docs2)
			{
				finalDocs1.add(occ.document);
			}
			//now check to see if the finalNames arraylist of doc names is 5 or smaller or bigger than 5 and change it accordingly
			if(finalDocs1.size() > 5)
			{
				while(finalDocs1.size() > 5)
				{
					docs2.remove(5);
				}
				return finalDocs1;
			}
			else
			{
				return finalDocs1;
			}		
		}
		else
		{
			if(keywordsIndex.containsKey(kw1)) // if kw1 is in keywordsIndex
			{
				if(keywordsIndex.get(kw1).size() <= 5) //if the size of the arraylist of occurrences is less than or equal to 5 then add all of them to docs1
				{
					for(int i = 0; i < keywordsIndex.get(kw1).size(); i++)
					{
						docs1.add(keywordsIndex.get(kw1).get(i));
					}
				}
				else //if the size of arraylist of occurrences is greater than 5 then add only up to 5 docs from the occurrences
				{
					for(int i = 0; i < 5; i++)
					{
						docs1.add(keywordsIndex.get(kw1).get(i));
					}
				}
			}

			//do the same as you did for kw1 for kw2
			if(keywordsIndex.containsKey(kw2)) // if kw1 is in keywordsIndex
			{
				if(keywordsIndex.get(kw2).size() <= 5) //if the size of the arraylist of occurrences is less than or equal to 5 then add all of them to docs1
				{
					for(int i = 0; i < keywordsIndex.get(kw2).size(); i++)
					{
						docs2.add(keywordsIndex.get(kw2).get(i));
					}
				}
				else //if the size of arraylist of occurrences is greater than 5 then add only up to 5 docs from the occurrences
				{
					for(int i = 0; i < 5; i++)
					{
						docs2.add(keywordsIndex.get(kw2).get(i));
					}
				}
			}

			
			//combine docs1 and docs2 into one arraylist, and check if the document is already in finalDocs
			ArrayList<String> finalDocs = new ArrayList<String>();
			int length1 = 0;
			int length2 = 0;
			while(docs1.size() > length1 && docs2.size() > length2)
			{
				int uno = docs1.get(length1).frequency;
				int dos = docs2.get(length2).frequency;

				if(uno >= dos)
				{
					if(finalDocs.contains(docs1.get(length1).document))
					{
						length1++;
					}
					else
					{
						finalDocs.add(docs1.get(length1).document);
						length1++;
					}
				}
				else
				{
					if(finalDocs.contains(docs2.get(length2).document))
					{
						length2++;
					}
					else
					{
						finalDocs.add(docs2.get(length2).document);
						length2++;
					}
				}
			}
			//add leftovers from the arrays
			while(docs1.size() > length1)
			{
				if(!(finalDocs.contains(docs1.get(length1).document)))
				{
					finalDocs.add(docs1.get(length1).document);
					length1++;
				}
				length1++;
			}
			while(docs2.size() > length2)
			{
				if(!(finalDocs.contains(docs2.get(length2).document)))
				{
					finalDocs.add(docs2.get(length2).document);
					length2++;
				}
				length2++;
			}

			//now check to see if the finalNames arraylist of doc names is 5 or smaller or bigger than 5 and change it accordingly
			if(finalDocs.size() > 5)
			{
				while(finalDocs.size() > 5)
				{
					finalDocs.remove(5);
				}
				return finalDocs;
			}
			else
			{
				return finalDocs;
			}		
		}
	
	}
}
