/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();

//    Each word that is read from the dictionary file should be stored in an ArrayList
//    (called wordList).
    private ArrayList<String> wordList = new ArrayList<String>();

//    A HashSet (called wordSet)
//      that will allow us to rapidly (in O(1))
//      verify whether a word is valid.
//    A HashMap (called lettersToWord)
//      that will allow us to group anagrams together.
//      We will do this by using the sortLetters version of a string as the key and storing an
//      ArrayList of the words that correspond to that key as our value. For example,
//      we may have an entry of the form: key: "opst" value: ["post", "spot", "pots", "tops", ...].
    private HashSet<String> wordSet = new HashSet<String>();
    private HashMap<String, ArrayList<String>> lettersToWord =
            new HashMap<String, ArrayList<String>>();

//    sizeToWords maps word length to an ArrayList of all words of that length
//    for example, you should be able to get all four-letter words in the dictionary by calling
//    sizeToWords.get(4)
    private HashMap<Integer, ArrayList<String>> sizeToWords = new HashMap<Integer, ArrayList<String>>();

//    Create wordLength and default it to DEFAULT_WORD_LENGTH. Then in pickGoodStarterWord,
//    restrict your search to the words of length wordLength, and once you're done,
//    increment wordLength (unless it's already at MAX_WORD_LENGTH) so that the next invocation
//    will return a larger word.
    private int wordLength = DEFAULT_WORD_LENGTH;

    public synchronized void addToList(String mapKey, String s) {
        ArrayList<String> anagramsList = lettersToWord.get(mapKey);

        // if list does not exist create it
        if(anagramsList == null) {
            anagramsList = new ArrayList<String>();
            anagramsList.add(s);
            lettersToWord.put(mapKey, anagramsList);
        } else {
            // add if item is not already in list
            if(!anagramsList.contains(s)) anagramsList.add(s);
        }
    }

    public synchronized void addToList(Integer mapKey, String s) {
        ArrayList<String> wordList = sizeToWords.get(mapKey);

        // if list does not exist create it
        if(wordList == null) {
            wordList = new ArrayList<String>();
            wordList.add(s);
            sizeToWords.put(mapKey, wordList);
        } else {
            // add if item is not already in list
            if(!wordList.contains(s)) wordList.add(s);
        }
    }

    public String sortLetters(String str) {
        // put the characters into an array
        Character[] chars = new Character[str.length()];
        for (int i = 0; i < chars.length; i++)
            chars[i] = str.charAt(i);

        // sort the array
        Arrays.sort(chars, new Comparator<Character>() {
            public int compare(Character c1, Character c2) {
                int cmp = Character.compare(
                        Character.toLowerCase(c1.charValue()),
                        Character.toLowerCase(c2.charValue())
                );
                if (cmp != 0) return cmp;
                return Character.compare(c1.charValue(), c2.charValue());
            }
        });

        // rebuild the string
        StringBuilder sb = new StringBuilder(chars.length);
        for (char c : chars) sb.append(c);
        str = sb.toString();
        return str;
    }

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);
            addToList(sortLetters(word), word);
            addToList(word.length(), word);
        }
    }

    public boolean isGoodWord(String word, String base) {
        if(wordSet.contains(word)) {
            if(!word.contains(base)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getAnagrams(String targetWord) {
        ArrayList<String> result = new ArrayList<String>();
        for (String s: wordList
                ) {
            if(s.length() == targetWord.length()) {
                if(sortLetters(s).equals(sortLetters(targetWord))) {
                    result.add(s);
                }
            }
        }
        return result;
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for(char alphabet = 'a'; alphabet <= 'z'; alphabet++) {
            if(lettersToWord.containsKey(sortLetters(word+alphabet))) {
                result.addAll(lettersToWord.get(sortLetters(word+alphabet)));
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        int numOfAnagrams = 0;
        String result = wordList.get(0);
        ArrayList<String> sameSizeList = sizeToWords.get(wordLength);
        while(numOfAnagrams < MIN_NUM_ANAGRAMS) {
            int x = random.nextInt(sameSizeList.size());
            result = sameSizeList.get(x);
            List<String> anagramsList = getAnagramsWithOneMoreLetter(result);
            numOfAnagrams = anagramsList.size();
        }
        if(wordLength < MAX_WORD_LENGTH) wordLength++;
        else wordLength = DEFAULT_WORD_LENGTH;
        return result;
    }
}
