import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Author : Grace Bech
 * Date: 6th November, 2023
 * viterbi parts of speech trainer
 */
public class viterbi {
    private String start = "#";
    private Set<String> tags;
    Map<String, Map<String, Double>> transitions;
    Map<String, Map<String, Double>> observation;
    private double notFoundPenalty = -100.00;

    public viterbi() {
        tags = new HashSet<>();
        transitions = new HashMap<>();
        observation = new HashMap<>();

    }

    /**
     *
     * @param transitions
     * @param observation
     */
    public viterbi(Map<String, Map<String, Double>> transitions, Map<String, Map<String, Double>> observation) {
        this.observation = observation;
        this.transitions = transitions;
    }

    /**
     * @param words
     * @return
     */
    public List<String> viterb(List<String> words) {
        start = "#"; // start at hash
        Map<String, Double> currScores = new HashMap<>();
        currScores.put(start, 0.00);  // set the current score to be the score at the start (0)
        List<Map<String, String>> backTrackList = new ArrayList<>();  // create a list of a map to backtrack to where each part of speech came from

//        System.out.println(transitions.keySet());
        for (int i = 0; i < words.size(); i++) {  // loop through the observation map
            Map<String, Double> nextScores = new HashMap<>(); // create a map to keep count of the scores in the next transitions
            Map<String, String> backTrack = new HashMap<>(); // create a map to backtrack to where you started

            for (String currState : currScores.keySet()) {  // Loop through all the states and check its next states
                for (String nextState : transitions.get(currState).keySet()) { // loop through all the next states and add them to the next states
//                    double currScore = currScores.get(currState);
                    if (!transitions.containsKey(nextState))
                        continue;

                    double score = currScores.get(currState) + transitions.get(currState).get(nextState);
                    if (observation.get(nextState).containsKey(words.get(i)))
                        score += observation.get(nextState).get(words.get(i));

                    else
                        score += notFoundPenalty;

                    if (!nextScores.containsKey(nextState) || score > nextScores.get(nextState)) {
                        nextScores.put(nextState, score);
                        backTrack.put(nextState, currState);
//                            System.out.println(backTrack);  // backtrack seems to be working

                    }
                }

            }
            backTrackList.add(backTrack);
            currScores = nextScores;
        }
        // Find the best path
        String bestState = null;
        double maximumScore = Double.NEGATIVE_INFINITY;
        for (String state : currScores.keySet()) {
            double score = currScores.get(state);
            if (score > maximumScore) {
                bestState = state;
                maximumScore = score;
            }
        }

        // Backtrack to get the best path
        List<String> bestPath = new ArrayList<>();
        String currentState = bestState;
        for (int i = backTrackList.size() - 1; i >= 0; i--) {
            bestPath.add(0, currentState);
            currentState = backTrackList.get(i).get(currentState);

        }

        return bestPath;
    }

    /**
     *
     * @param sentences
     * @param tags
     */

    public void train(List<List<String>> sentences, List<List<String>> tags) {
        Map<String, Double> tagCount = new HashMap<>(); // Create a map of the tagCount
        Map<String, Map<String, Double>> transitionCount = new HashMap<>(); // Create a map of the transitionCount
        Map<String, Map<String, Double>> emissionCount = new HashMap<>();  // create a map with the different emmissionCounts

        // Iterate through each sentence and its corresponding tags
        for (int i = 0; i < sentences.size(); i++) {
            List<String> sentence = sentences.get(i);
            List<String> tagSequence = tags.get(i);

            // Update tag counts
            for (String tag : tagSequence) {
                tagCount.put(tag, tagCount.getOrDefault(tag, 0.0) + 1);
            }

            String currentTag = "#";
            // Update transition counts
            for (int j = 0; j < tagSequence.size(); j++) {
                String nextTag = tagSequence.get(j);

                transitionCount.computeIfAbsent(currentTag, k -> new HashMap<>());  // if the transitionCount is empty, then pass in the
                // current tag and create a new map
                transitionCount.get(currentTag).put(nextTag, transitionCount.get(currentTag).getOrDefault(nextTag, 0.0) + 1);
                currentTag = nextTag;  // update the currentTag to be the nextTag
            }

            // Update emission counts
            for (int j = 0; j < tagSequence.size(); j++) {  // loop through the tagSequence
                String tag = tagSequence.get(j);  // for every tag in the tagSequence, get the tagSequence
                String word = sentence.get(j);  // Store the word in word

                emissionCount.computeIfAbsent(tag, k -> new HashMap<>());  // if the emmission is empty, then add the tag and set the key to be a new map
                emissionCount.get(tag).put(word, emissionCount.get(tag).getOrDefault(word, 0.0) + 1);
            }
        }

         //Convert counts to probabilities
        double smoothingFactor = 0.1;
        for (String currentTag : transitionCount.keySet()) {  // loop through the currentTags in the transitionCount map and get the keys
            Map<String, Double> transitionMap = transitionCount.get(currentTag);
            Map<String, Double> transitionProbabilities = new HashMap<>();

            double total = 0;  // initialize the count to 0
            for (String nextTag : transitionMap.keySet()) {  // loop through the next tags in the transition map
                total += transitionMap.get(nextTag);  // increment the taotal with the count in the transition map
            }
            // loop through the tansition map and get the keys
            for (String nextTag : transitionMap.keySet()) {
                double probability = transitionMap.get(nextTag) / total;   // divide the tags by the totals
                transitionProbabilities.put(nextTag, Math.log(probability));  // covert the probabilities into logs
            }
//

            transitions.put(currentTag, transitionProbabilities);
        }

        // loop through the currentTags in the emmission count, and then create  a map  of the emmissions and another map of their probabilities
        for (String currentTag : emissionCount.keySet()) {
            Map<String, Double> emissionMap = emissionCount.get(currentTag);
            Map<String, Double> emissionProbabilities = new HashMap<>();
// loop through every word in the emissionMap and get the keys
            for (String word : emissionMap.keySet()) {
                double probability = (double) emissionMap.get(word) / tagCount.get(currentTag);    // convert the probabiities into smaller ones by dividing bey the tagcount in the current tag
                emissionProbabilities.put(word, probability);  // add the word as key and the probabilities as values in the emissionProbabilities map
            }

            observation.put(currentTag, emissionProbabilities);  // add the currentTag and the emissionProbabilities into the observation Map
        }

    }
    /**
     * @param predictedTags
     * @param actualTags
     * @return
     */
    public double calculateAccuracy(List<List<String>> predictedTags, List<List<String>> actualTags) {
        double totalWords = 0.0;  // initialize the total words to 0.0
        double correctTags = 0.0; // initialize the number of correct tags to 0.0

        for (int i = 0; i < predictedTags.size(); i++) {  // loop through thr list of predicted tags
            List<String> predicted = predictedTags.get(i);  //store every prediction in the predicted list
            List<String> actual = actualTags.get(i);  // store the actual tags in the actual list

            if (predicted.size() != actual.size()) {  // so long as you have not check all the tags in the actual list
                throw new IllegalArgumentException("Number of predicted and actual tags must be the same.");
            }

            for (int j = 0; j < predicted.size(); j++) {    // loop through the list
                totalWords++;  // update the total number of words

                if (predicted.get(j).equals(actual.get(j))) {
                    correctTags++;
                }
            }

        }
        Map<String, Double> accuracyTally = new HashMap<>();  // create a map with the accuracy tallies
        accuracyTally.put("Correct words", correctTags);
        accuracyTally.put("incorrect words ", totalWords - correctTags);

        return (double) correctTags / totalWords * 100.0;
    }

    /**
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static List<List<String>> readFile(String filename) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(filename));  // reader from the file
        String line = reader.readLine();   // reader line by line from the file
        List<List<String>> sentences = new ArrayList<>();  // create a list of lists to store the sentences
        while (line != null) {   // as long as there is a line to read
            String[] words = line.strip().split(" ");  // strip and split that word
            sentences.add(new ArrayList<>(List.of(words)));
            line = reader.readLine();   // reader every line

        }
//
        return sentences;
    }

    /**
     *
     * @param MODEL
     * @param transitions
     * @param observation
     */
    public static void readFromKeyboad(viterbi MODEL, Map<String, Map<String, Double>> transitions, Map<String, Map<String, Double>> observation) {

        Scanner keyboard = new Scanner(System.in);  // read from the keyboard
        System.out.println("Enter your input:");
        String inputSentence = keyboard.nextLine();  // Use nextLine() to get the entire line

        List<String> inputWords = new ArrayList<>();  // create a list of the inputs you get from the console
        String[] wordsArray = inputSentence.strip().split("\\s+");

        // Loop through all the words in the array of words
        for (String word : wordsArray) {
            inputWords.add(word);  // add the word inputWords
        }

        List<String> resultTags = MODEL.viterb(inputWords);

        System.out.println("Tags for the input sentence:");
        for (String str : inputWords) { // loop through all the words in the input and then print them out
            System.out.println(str + " : " + resultTags.get(inputWords.indexOf(str)));
        }
        keyboard.close();
    }

    /**
     *
     * @param args
     * @throws Exception
     */

    public static void main(String[] args) throws Exception {


        viterbi MODEL = new viterbi();  // create an instance of the viterbi class
        MODEL.transitions.put("#", new HashMap<>()); // Add transitions data
        MODEL.observation.put("#", new HashMap<>()); // Add observation data
        Map<String, Double> observations = new HashMap<>(); // Add your observations

        List<List<String>> words = readFile("Pset5/texts 2/brown-train-sentences.txt");
        List<List<String>> tags = readFile("Pset5/texts 2/brown-train-tags.txt");
        MODEL.train(words, tags);  // call the training on the words and the tags
         readFromKeyboad(MODEL, MODEL.transitions, MODEL.observation);

        List<List<String>> testSentences = readFile("Pset5/texts 2/brown-test-sentences.txt");  // create a list of the sentences you're testing
        List<List<String>> actualTags = readFile("Pset5/texts 2/brown-test-tags.txt");  // create a list of the actual tags

        List<List<String>> predictedTags = new ArrayList<>();   // create a list of expected / predicted tags

        for (List<String> sentence : testSentences) {  // loop through every string in the test sentences
            List<String> resultTags = MODEL.viterb(sentence);
            predictedTags.add(resultTags);  // add the results to the list of expected tags
        }

// this will print the number of correct words vs number of incorrect words

        double accuracy = MODEL.calculateAccuracy(predictedTags, actualTags);
        System.out.println(" This is the Accuracy: " + accuracy + "%");
        System.out.println("This got this number of words correct:" + accuracy + "This is the percentage of inaccurate words: " + (100 - accuracy));

    }
}






