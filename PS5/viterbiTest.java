import java.util.*;

/**
 * Author : Grace Bech
 * Date: 6th November, 2023
 * viterbi parts of speech trainer hard coded example
 * used a reference of the recitation example
 */
public class viterbiTest  {
    public static void main(String[] args) {
        Map<String, Map<String, Double>> transitions = new HashMap<>();
        Map<String, Map<String, Double>> observations = new HashMap<>();

// Define transition probabilities
        transitions.put("#", new HashMap<>());
        transitions.get("#").put("NP", 3.0);
        transitions.get("#").put("N", 7.0);


        transitions.put("N", new HashMap<>());
        transitions.get("N").put("CNJ", 2.0);
        transitions.get("N").put("V", 8.0);

        transitions.put("V", new HashMap<>());
        transitions.get("V").put("CNJ", 2.0);
        transitions.get("V").put("N", 4.0);
        transitions.get("V").put("NP", 4.0);

        transitions.put("NP", new HashMap<>());
        transitions.get("NP").put("CNJ", 2.0);
        transitions.get("NP").put("V", 8.0);

        transitions.put("CNJ", new HashMap<>());
        transitions.get("CNJ").put("V", 4.0);
        transitions.get("CNJ").put("N", 4.0);
        transitions.get("CNJ").put("NP", 2.0);

// Test the observations
        observations.put("N", new HashMap<>());
        observations.get("N").put("watch", 2.0);
        observations.get("N").put("cat", 4.0);
        observations.get("N").put("dog", 4.0);

        observations.put("V", new HashMap<>());
        observations.get("V").put("chase", 3.0);
        observations.get("V").put("watch", 6.0);
        observations.get("V").put("get", 1.0);

        observations.put("NP", new HashMap<>());
        observations.get("NP").put("chase", 10.0);

        observations.put("CNJ", new HashMap<>());
        observations.get("CNJ").put("and", 10.0);
        ArrayList<String> testWords = new ArrayList<>();
        testWords.add("The ");
        testWords.add("lazy ");
        testWords.add("cat");
        testWords.add("loves");
        testWords.add("playing");


        ArrayList<String> testWords2 = new ArrayList<>();
        testWords2.add("She");
        testWords2.add("is");
        testWords2.add("beautiful");
        testWords2.add("and");
        testWords2.add("very");
        testWords2.add("kind");
        testWords2.add("hardworking");
// create an instance of the viterbi
        viterbi m = new viterbi(transitions, observations);
        System.out.println(testWords);
        System.out.println(m.viterb(testWords));
        System.out.println(testWords2);
        System.out.println(m.viterb(testWords2));

    }


}