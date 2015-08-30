import java.io.File;
import java.lang.Integer;
import java.lang.String;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

public class MP1 {
    Random generator;
    String userName;
    String inputFileName;
    String delimiters = " \t,;.?!-:@[](){}_*/";
    String[] stopWordsArray = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
            "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its",
            "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that",
            "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having",
            "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before",
            "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each",
            "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};

    void initialRandomGenerator(String seed) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(seed.toLowerCase().trim().getBytes());
        byte[] seedMD5 = messageDigest.digest();

        long longSeed = 0;
        for (int i = 0; i < seedMD5.length; i++) {
            longSeed += ((long) seedMD5[i] & 0xffL) << (8 * i);
        }

        this.generator = new Random(longSeed);
    }

    Integer[] getIndexes() throws NoSuchAlgorithmException {
        Integer n = 10000;
        Integer number_of_lines = 50000;
        Integer[] ret = new Integer[n];
        this.initialRandomGenerator(this.userName);
        for (int i = 0; i < n; i++) {
            ret[i] = generator.nextInt(number_of_lines);
        }
        return ret;
    }

    public MP1(String userName, String inputFileName) {
        this.userName = userName;
        this.inputFileName = inputFileName;
    }

    public String[] process() throws Exception {
        String[] ret = new String[20];

        Path path = Paths.get(inputFileName);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<String> stopWords = new ArrayList<String>(Arrays.asList(stopWordsArray));

        Integer[] indexes = getIndexes();

        Hashtable<String, Integer> freq = new Hashtable<String, Integer>();

        for(Integer index : indexes)
        {
            String line = lines.get(index);
            StringTokenizer tokenizer = new StringTokenizer(line, delimiters, false);
            while(tokenizer.hasMoreTokens())
            {
                String word = tokenizer.nextToken();
                String cleanedWord = word.toLowerCase().trim();

                if(!stopWords.contains(cleanedWord)){
                    if(freq.containsKey(cleanedWord)){
                        int count = freq.get(cleanedWord);
                        count++;
                        freq.put(cleanedWord, count);
                    }
                    else{
                        freq.put(cleanedWord, 1);
                    }
                }
            }
        }

        Map<String, Integer> sorted = sortByValue(freq);

        String[] result = sorted.keySet().toArray(new String[0]);

        for(int i=0; i<20; i++){
            ret[i] = result[i];
        }

        return ret;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1){
            System.out.println("MP1 <User ID>");
        }
        else {
            String userName = args[0];
            String inputFileName = "./input.txt";
            MP1 mp = new MP1(userName, inputFileName);
            String[] topItems = mp.process();
            for (String item: topItems){
                System.out.println(item);
            }
        }
    }

    public Map<String, Integer> sortByValue( Map<String, Integer> map )
    {
        List<Map.Entry<String, Integer>> list = new LinkedList<>( map.entrySet() );

        Collections.sort(list, new Comparator<Map.Entry<String,Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int retValue = (o1.getValue()).compareTo(o2.getValue()) * -1;
                if(retValue == 0){
                    return o1.getKey().compareTo(o2.getKey());
                }
                return retValue;
            }
        });

        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}