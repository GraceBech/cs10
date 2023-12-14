import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.*;

/**
 * Library for graph analysis
 *Author : Grace Bech
 *
 *
 */

public class BaconGame {
    //instant variables
    Boolean gameOn = true;
    static String universeCentre;
    //initializing graph containing all vertices
    static Graph<String, Set<String>> graphVertices = new AdjacencyMapGraph<>();
    List<String> sharedMovies;  //list with all actors
    static Map<String, String> actors;
    static Map<String, String> movies;
    static Map<String, Set<String>> movieActors;
    static Graph<String, Set<String>> graph1;

    public static void readActors(String filename) throws IOException {
        Map<String, String> actors = new HashMap<>();
        BufferedReader input = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = input.readLine()) != null) {
            String res[] = line.split("\\|");
            actors.put(res[0], res[1]);
        }
        input.close();
        BaconGame.actors = actors;
    }

    public static void readMovies(String filename) throws IOException {
        Map<String, String> movies = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = in.readLine()) != null) {
            String res[] = line.split("\\|");
            movies.put(res[0], res[1]);
        }
        in.close();
        BaconGame.movies = movies;
    }

    public static void readMoviesActor(String filename) throws IOException {
        Map<String, Set<String>> moviesActors = new HashMap<>();
        //actors as vertices, movienames as edges, each edge label is a set of movies that the two actors have costared in.
        //for every movie in movie actor, get the name

        BufferedReader inside = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = inside.readLine()) != null) {
            String res[] = line.split("\\|");
            Set<String> actors = new HashSet<>();

            if (moviesActors.containsKey(res[0])) {
                actors = moviesActors.get(res[0]);
            }
            actors.add(res[1]);
            moviesActors.put(res[0], actors);
        }
        inside.close();
        BaconGame.movieActors = moviesActors;
    }
    public static void computeMoviesGraph() {
        Graph<String, Set<String>> baconGraph = new AdjacencyMapGraph<>();

        if (movieActors == null || actors == null || movies == null) {
            System.err.println("null movies / actors maps");
            return;
        }
        for (String movieID : movieActors.keySet()) {  // every movie in movies
            for (String actorID : movieActors.get(movieID)) {  // iterate through every actor in a movie's actor set
                for (String otherActorID : movieActors.get(movieID)) {  // iterate through other actors in the same movie's actor set
                    if (!actorID.equals(otherActorID)) {  // if the two actors are different

                        // convert IDs to names
                        String firstActor = actors.get(actorID);
                        String secondActor = actors.get(otherActorID);
                        String movieName = movies.get(movieID);

                        // insert actors into graph
                        if (!baconGraph.hasVertex(firstActor)) baconGraph.insertVertex(firstActor);
                        if (!baconGraph.hasVertex(secondActor)) baconGraph.insertVertex(secondActor);

                        Set<String> labels = new HashSet<>();

                        if (!baconGraph.hasEdge(firstActor, secondActor)) {
                            labels = baconGraph.getLabel(firstActor, secondActor);
                            if (labels == null) labels = new HashSet<>();
                        }
                        labels.add(movieName);  // add the movie to the set of labels
                        baconGraph.insertUndirected(firstActor, secondActor, labels);
                    }
                }
            }
        }
        graphVertices = baconGraph;
    }
    public static Map<String, Double> computeAverageSeparations() {
        Map<String, Double> averagesMap = new HashMap<>();

        for (String actor : graphVertices.vertices()) {
            // center them
            Graph<String, Set<String>> centeredGraph = graphLibrary.bfs(graphVertices, actor);

            // compute average separation on centered graph
            double avgSeparation = graphLibrary.averageSeparation(centeredGraph, actor);

            averagesMap.put(actor, avgSeparation);
        }
        return averagesMap;
    }

    public static void main(String[] args) throws IOException {

        String ActorsFile = "/Users/pacifique/Documents/IdeaProjects/cs10/PS4/actors.txt";
        String MoviesFile = "/Users/pacifique/Documents/IdeaProjects/cs10/PS4/movies.txt";
        String MoviesActorFile = "/Users/pacifique/Documents/IdeaProjects/cs10/PS4/movie-actors.txt";

        BaconGame.readActors(ActorsFile);
        BaconGame.readMovies(MoviesFile);
        BaconGame.readMoviesActor(MoviesActorFile);
        BaconGame.computeMoviesGraph();

        System.out.println("Enter Key");

        List<Character> keys = new ArrayList<>();
            keys.add('q');
            keys.add('i');
            keys.add('c');
            keys.add('u');
            keys.add('d');
            keys.add('p');
            keys.add('s');

        Scanner in = new Scanner(System.in);
        String line = in.nextLine().toLowerCase();
        char cha = line.charAt(0);

        Map<String, Double> separations = null;
        List<String> orderedList = null;
        while (keys.contains(cha)) {

            //u <name>: make <name> the center of the universe
            if (cha == 'u') {
                Scanner name = new Scanner(System.in);
                universeCentre = name.nextLine();

                //check if actor is in the mapu

                if (graphVertices.hasVertex(universeCentre)) {
                    System.out.println("u " + universeCentre);

                    graph1 = graphLibrary.bfs(graphVertices, universeCentre);
                }
            }

            //c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
            if (cha == 'c') {
                //how to implement
                //get the list of vertices in the graph
                //array list to keep track of
                //sort the list using a comparator according to their average separation
                //with the sorted list, if val is >0 print top val, if < 0 print bottom val
                //c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
                Scanner num = new Scanner(System.in);
                String number = num.nextLine();

                int object = Integer.parseInt(number);
                boolean printFromBack = object < 0;
                object = Math.abs(object);

                if (separations == null) {
                    separations = BaconGame.computeAverageSeparations();

                    orderedList = new ArrayList<>(separations.keySet());
                    Map<String, Double> finalSeparations = separations;
                    orderedList.sort((actor1, actor2) -> {
                        double actor1Separation = finalSeparations.get(actor1);
                        double actor2Separation = finalSeparations.get(actor2);
                        if (actor1Separation > actor2Separation) return 1;
                        else if (actor1Separation < actor2Separation) return -1;
                        else return 0;
                    });
                }
                for (int i = 0; i < object; i++) {
                    int index = (printFromBack) ? orderedList.size() - (i + 1) : i;
                    String currentActor = orderedList.get(index);
                    double separation = separations.get(currentActor);
                    System.out.println(currentActor + " with average separation " + separation);
                }
            }

            //d <low> <high>: list actors sorted by degree, with degree between low and high
            if (cha == 'd') {
                //get the list of vertices sort them by in degree
                //check that high is greater than low

                Scanner num = new Scanner(System.in);
                String number = num.nextLine();
                int low = Integer.parseInt(number);

                Scanner num1 = new Scanner(System.in);
                String number1 = num.nextLine();
                int high = Integer.parseInt(number1);

                //checking if the user has inputted in the numbers right order
                if (low > high) {
                    System.out.println("The numbers not well ordered, try again.");
                } else {
                    //list to store vertices within range of low and high
                    List<String> verticesRange = new ArrayList<>();
                    //looping through all vertices in main graph
                    for (String vertices : graphVertices.vertices()) {
                        //checks if vertex separation is within range
                        if (graphVertices.inDegree(vertices) <= high && graphLibrary.numOfCostars(graphVertices, vertices) < high) {
                            //adds vertex to the list if qualified
                            verticesRange.add(vertices);
                        }
                    }
                    System.out.println("Print" + verticesRange);
                }
            }

            //i: list actors with infinite separation from the current center
            if (cha == 'i') {
                Set<String> missingVertices = graphLibrary.missingVertices(graphVertices, graph1);
                System.out.println(" These are the missing vertices" + missingVertices);
            }

            //p <name>: find path from <name> to current center of the universe
            if (cha == 'p') {
                //finds path from object name to the centre of the universe
                Scanner object = new Scanner(System.in);
                String objectName = object.nextLine();
                //check if the value is present in the actor list
                if (!actors.containsKey(objectName)) {
                    System.out.println("Error");
                }

                List<String> path1 = graphLibrary.getPath( graph1, objectName);

                System.out.println("The Path is :" + path1);

            }

            //s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high
            if (cha == 's'){
                //check if separation is different from average separation
                //sort the list
                //if high is less than low
                //use the for loop tp get the numbers within the range

                Scanner object3 = new Scanner(System.in);
                String low = object3.next();
                int lowNum = Integer.parseInt(low);

                //create object for high class

                Scanner object4 = new Scanner(System.in);
                String high = object3.nextLine();
                int highNum = Integer.parseInt(high);

                List<String> list1 = new ArrayList<>();
                Map<String, Double> storeSeparations = new HashMap<>();
                for(String k : actors.keySet()) {
                    Graph<String, Set<String>> graph2 = graphLibrary.bfs(graphVertices, k);
                    Double result = graphLibrary.averageSeparation(graph2, k);
                    storeSeparations.put(k, result);
                    list1.add(k);
                }

//                    Collections.sort(list1, (actor1, acter2))->
//
                Collections.sort(list1, (a, b) -> {
                    double aSeparation = storeSeparations.get(a);
                    double bSeparation = storeSeparations.get(b);
                    if (aSeparation > bSeparation) return 1;
                    else if (aSeparation < bSeparation) return -1;
                    else return 0;
                });
            }

            //quit game
            if (cha == 'q') {
                System.out.println("GAME OVER");
                break;
            }

            System.out.println("Enter Key");
            Scanner object5 = new Scanner(System.in);
            String high = object5.nextLine();

            cha = high.charAt(0);

            }
        }
    }








