import java.util.*;

public class viterbiPos {
    private String start = "#";
    private Set<String> endStates;
    Map<String, Map<String, Double>> transitions;
    Map<String, Map<String, Double>> emission;
    private double notFoundPenalty = -100.00;

    public viterbiPos() {
        endStates = new HashSet<>();
        transitions = new HashMap<>();
        emission = new HashMap<>();
    }

    public void train(List<List<String>> sentences, List<List<String>> tags) {
        // Implement training logic here based on your specific dataset
        // This could involve estimating transition and emission probabilities
    }

    public List<String> viterbi(List<String> observations) {
        List<Map<String, Double>> scoresList = new ArrayList<>();
        List<Map<String, String>> backTrackList = new ArrayList<>();

        // Initialization step
        Map<String, Double> initialScores = new HashMap<>();
        initialScores.put(start, 0.0);
        scoresList.add(initialScores);
        backTrackList.add(new HashMap<>());

        // Viterbi algorithm
        for (int i = 0; i < observations.size(); i++) {
            Map<String, Double> scores = new HashMap<>();
            Map<String, String> backTrack = new HashMap<>();

            for (String currentState : scoresList.get(i).keySet()) {
                for (String nextState : transitions.getOrDefault(currentState, new HashMap<>()).keySet()) {
                    double score = scoresList.get(i).get(currentState);
                    double transitionScore = transitions.get(currentState).get(nextState);
                    double emissionScore = emission.getOrDefault(nextState, new HashMap<>()).getOrDefault(observations.get(i), notFoundPenalty);
                    double newScore = score + transitionScore + emissionScore;

                    if (!scores.containsKey(nextState) || newScore > scores.get(nextState)) {
                        scores.put(nextState, newScore);
                        backTrack.put(nextState, currentState);
                    }
                }
            }

            scoresList.add(scores);
            backTrackList.add(backTrack);
        }

        // Find the best path
        String bestState = null;
        double maximumScore = 0.0;
        for (String state : scoresList.get(observations.size()).keySet()) {
            double score = scoresList.get(observations.size()).get(state);
            if (score > maximumScore) {
                bestState = state;
                maximumScore = score;
            }
        }

        // Backtrack to get the best path
        List<String> bestPath = new ArrayList<>();
        String currentState = bestState;
        for (int i = observations.size(); i >= 0; i--) {
            bestPath.add(0, currentState);
            currentState = backTrackList.get(i).get(currentState);
            System.out.println(bestPath);
        }

        return bestPath;
    }

    public static void main(String[] args) {
        viterbiPos posTagger = new viterbiPos();

        // Example training data (replace with actual training data)
        List<List<String>> sentences = Arrays.asList(
                Arrays.asList("The", "cat", "chases", "the", "mouse"),
                Arrays.asList("She", "sings", "a", "song")
        );

        List<List<String>> tags = Arrays.asList(
                Arrays.asList("DT", "NN", "VBZ", "DT", "NN"),
                Arrays.asList("PRP", "VBZ", "DT", "NN")
        );

        posTagger.train(sentences, tags);

        // Example test data (replace with actual test data)
        List<String> testSentence = Arrays.asList("Mom", "chases", "the", "mouse");
        List<String> bestPath = posTagger.viterbi(testSentence);

        System.out.println("Best POS Tags:");
        System.out.println(bestPath);
    }
}