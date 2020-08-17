package org.home;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Test1 {

    public static void main(String[] args) {
        String pattern = "....н";
        String letters = "знадвору";
        String wordsListStr = getWordsListByPattern(pattern);
        splitByWords(wordsListStr.toLowerCase().trim()).stream().filter(w -> wordSatisfiesLetters(w, letters.toLowerCase())).forEach(System.out::println);

        // words not included in above site
        String extraWordsList = "ласо какао тремоло подкаст погроза агро арго рогоза таран сліп спам таро авто табло мокко нетбол";
        System.out.println("-=-=-= extra -=-=-=-");
        splitByWords(extraWordsList.toLowerCase().trim()).stream().filter(w -> wordSatisfiesLetters(w, letters.toLowerCase())).forEach(System.out::println);
    }

    private static String getWordsListByPattern(String pattern) {
        try {
            URL url = new URL("http://www.senyk.poltava.ua/cgi-bin/ukr_words/fuw.cgi?tq=M&main=" + URLEncoder.encode(pattern, "windows-1251"));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Status code is not 200, but: " + status);
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "windows-1251"));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            final String startToken = "</B></H4>";
            final String endToken = "<H4>Всього";
            String result = content.substring(content.indexOf(startToken) + startToken.length(), content.indexOf(endToken));

            con.disconnect();
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String VOWELS_LOW = "aeiou";
    private static final String VOWELS_CAP = VOWELS_LOW.toUpperCase();
    private static final String VOWELS = VOWELS_LOW.concat(VOWELS_CAP);


    public static String disemvowel(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int begIndx = 0;
        for (int i = 0; i < str.length(); i++) {
            if (VOWELS.indexOf(str.charAt(i)) != -1) {
                if (begIndx != i) {
                    sb.append(str.substring(begIndx, i));
                }
                begIndx = i + 1;
            }
        }
        if (begIndx != str.length() - 1) {
            sb.append(str.substring(begIndx));
        }
        return sb.toString();
    }

    public static List<String> splitByWords(String listOfWordsSeparatedBySpace) {
        return Arrays.asList(listOfWordsSeparatedBySpace.split(" "));
    }

    private static Map<Character, Integer> prepareLettersMap(String letters) {
        Map<Character, Integer> lettersMap = new HashMap<>();
        for (int i = 0; i < letters.length(); i++) {
            Character c = letters.charAt(i);
            if (lettersMap.containsKey(c)) {
                lettersMap.put(c, lettersMap.get(c) + 1);
            } else {
                lettersMap.put(c, 1);
            }
        }
        return lettersMap;
    }

    public static boolean wordSatisfiesLetters(String word, String letters) {
        Map<Character, Integer> lettersMap = prepareLettersMap(letters);
        for (int i = 0; i < word.length(); i++) {
            Character c = word.charAt(i);
            if (!lettersMap.containsKey(c)) {
                return false;
            } else {
                int count = lettersMap.get(c);
                count--;
                if (count == 0) {
                    lettersMap.remove(c);
                } else {
                    lettersMap.put(c, count);
                }
            }
        }
        return true;
    }

}
