/**
 * Author: Grace Bech
 * KevinBacon Game
 */

import java.util.*;

public class kevinBaconGame {
    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        String actorsFile = "/Users/gracebech/IdeaProjects/CS10/CS10/PS4/actors.txt";
        String moviesFile = "/Users/gracebech/IdeaProjects/CS10/CS10/PS4/movies.txt";
        String moviesActorFile = "/Users/gracebech/IdeaProjects/CS10/CS10/PS4/movie-actors.txt";

        boolean gameOn = true;
        Graph<String, Set<String>> theActorsGraph= BuildGraph.createGraph(actorsFile, moviesFile, moviesActorFile);

        String universeCenter = "Kevin Bacon";

        Graph<String, Set<String>> BFSGraph = graphLibrary.bfs(theActorsGraph, universeCenter);

        System.out.println("Welcome to the Bacon game. Here are some rules to follow: \n"+
                "c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" +
                "d <low> <high>: list actors sorted by degree, with degree between low and high\n" +
                "i: list actors with infinite separation from the current center\n" +
                "p <name>: find path from <name> to current center of the universe\n" +
                "s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" +
                "u <name>: make <name> the center of the universe\n" +
                "q: quit game");


        while (gameOn){ // While playing the game, do the following
            // Reading from the Keyboard
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your command: ");
            String line = scanner.nextLine();  // Read from the scanner line by line until the end

            // p <name>: find path from <name> to current center of the universe
            for(int i = 0; i<line.length(); i++){
                try {
                    //CONDITION FOR U "u <name>: make <name> the center of the universe\n"
                if (line.charAt(i) == 'u') {
                    Scanner scanner2 = new Scanner(System.in);
                    System.out.println("Enter your new center: ");
                    String line2 = scanner2.nextLine();
                    BFSGraph = graphLibrary.bfs(theActorsGraph, line2);

                    if (BFSGraph !=null){
                        System.out.println(line2 + " is now the center of the acting universe, connected to " +
                                BFSGraph.numVertices() + "/" + theActorsGraph.numVertices() +
                                " actors with average separation " + graphLibrary.averageSeparation(BFSGraph,line2));}
                }


                else if(line.charAt(i) == 'p'){
                    String name =  line.substring(2);

                    //Check if the graph's vertex corresponds to the extracted name or not
                    if(!theActorsGraph.hasVertex(name)){
                        System.out.println("Person not among the movie co stars");
                    }
                    else { //if vertex found print the statement indicated
                        assert BFSGraph != null;
                        System.out.println("The path from " + name + " to the center of the universe:  " + graphLibrary.getPath(BFSGraph, name));
                        }
                    }

                //CONDITION FOR D "d <low> <high>: list actors sorted by degree, with degree between low and high\n"
                else if(line.charAt(i) == 'd') {
                    ArrayList<String> actorsByDegree = new ArrayList<>();
                    int low = Integer.parseInt(line.split(" ")[1]);
                    int high= Integer.parseInt(line.split(" ")[2]);

                    if (low > high) {
                        System.out.println("The input not well organized, try again.");
                    }
                    for (String actorNode : theActorsGraph.vertices()) {
                        int degree = theActorsGraph.outDegree(actorNode);
                        if ( degree >= low && degree <= high) {
                            actorsByDegree.add(actorNode);
                        }
                    }
                    actorsByDegree.sort(Comparator.comparingInt(theActorsGraph::outDegree));
                    System.out.println(actorsByDegree); // come up with a better printing format
                }

                // list actors with infinite separation from the current center
                else if(line.charAt(i) == 'i'){
                    Set<String> missingVertices = graphLibrary.missingVertices(theActorsGraph, BFSGraph);
                    System.out.println("Missing vertices: " + missingVertices);
                }

                //list actors sorted by non-infinite separation from the current center, with separation between low and high
                else if(line.charAt(i) =='s') {
                    int low2 = Integer.parseInt(line.split(" ")[1]);
                    int high2 = Integer.parseInt(line.split(" ")[2]);
                    List<Map.Entry<String, Integer>> sortedFinite = new ArrayList<>();

                    if (low2 > high2) {
                        System.out.println("The input not well organized, try again.");
                    }

                    Map<String, Integer> separationMap = new HashMap<>();  // Create a map for the separations
                    for(String vertex: theActorsGraph.vertices()){  // loop through all the vertices in the actor graph
                        assert BFSGraph != null;  // the bfs graph can never be null
                        List<String> shortPath = graphLibrary.getPath(BFSGraph, vertex);  // create a list with the shortest paths to tge vertices
                        if(shortPath != null) {
                            int pathSize  = shortPath.size();  // store the short path size
                            if(low2 <= pathSize && high2 >= pathSize){
                                separationMap.put(vertex, pathSize);
                        }

                    }
                    }
                    sortedFinite.addAll(separationMap.entrySet());
                    sortedFinite.sort(( s1, s2) -> (int) (separationMap.get(s1) - separationMap.get(s2)));
                    System.out.println(sortedFinite);

                }

                //c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
                else if (line.charAt(i) == 'c' && line.length() > 2){

                    List<Map.Entry<String, Integer>> sortedList = new ArrayList<>(); // create a list of the sorted actors using average separation
                    Map<String, Integer> theSeparation = new HashMap<>();
                    int intValue = Integer.parseInt(line.substring(1));

                    for(String vertex : theActorsGraph.vertices()){
                        int separation = (int) graphLibrary.averageSeparation(graphLibrary.bfs(theActorsGraph, vertex), vertex);
                        theSeparation.put(vertex, separation);
                    }
                    // sort the list in place
                    sortedList.addAll(theSeparation.entrySet());
                    sortedList.sort((s1, s2) -> theSeparation.get(s1) - theSeparation.get(s2));
                    while(i < Math.abs(intValue)){
                        if(intValue < 0){
                            System.out.println(sortedList.get(i));

                        }
                        else {
                            System.out.println(sortedList.get(sortedList.size() - i));
                        }
                        i += 1;

                    }

                }

                // quit game if q pressed
                else if(line.charAt(i) == 'q') {
                    gameOn = false;
                }
            }
                catch (Exception e){
                    System.out.println("Invalid Input format");
                }
            }
        }
    }
}
