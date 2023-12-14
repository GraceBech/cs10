import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViterbiTagging extends viterbi {
//    public String currState;
    private Object start;
    private Map<String, Double> currScore ;
    private Map<String, String> backtrack;
    private Map<String, String> currState;



    public ViterbiTagging(){
        currScore = new HashMap<>((Integer) (start = 0));
        backtrack = new HashMap<>();
        currState = new HashMap<>();


    }






//    currStates = { start }
//    currScores = map { start=0 }
//for i from 0 to # observations - 1
//    nextStates = {}
//    nextScores = empty map
//  for each currState in currStates
//    for each transition currState -> nextState
//    add nextState to nextStates
//    nextScore = currScores[currState] +                       // path to here
//    transitionScore(currState -> nextState) +     // take a step to there
//    observationScore(observations[i] in nextState) // make the observation there
//      if nextState isn't in nextScores or nextScore > nextScores[nextState]
//    set nextScores[nextState] to nextScore
//    remember that pred of nextState @ i is curr
//            currStates = nextStates
//    currScores = nextScores

}
