/**
 * Author: Grace Bech
 * Graph Library for the Kevin Bacon Game
 * Date : October 30th
 */

import java.util.*;

public class graphLibrary {

    /**
     *
     * @param g
     * @param source
     * @return
     * @param <V>
     * @param <E>
     */
    public static <V, E> Graph<V, E> bfs(Graph<V, E> g, V source) {  // Create a bfs graph
        Graph<V, E> treePath = new AdjacencyMapGraph<>();
        Queue<V> queue = new LinkedList<V>();  // queue to implement BFS

        //if there's no vertex return null otherwise make it the start point
        if (!g.hasVertex(source)) {
            return null;
        }
        queue.add(source); //enqueue start vertex
        treePath.insertVertex(source);

        //looping until there are  no more vertices
        while (!queue.isEmpty()) {
            V u = queue.remove(); //dequeue

            //looping over out neighbors
            for (V v : g.outNeighbors(u)) {
                if (!treePath.hasVertex(v)) {
                    queue.add(v); //enqueue neighbor
                    treePath.insertVertex(v);
                    treePath.insertDirected(v, u, g.getLabel(u, v));
                }
            }
        }
        return treePath;
    }

    /**
     *
     * @param tree
     * @param v
     * @return
     * @param <V>
     * @param <E>
     */
    //path from one vertex to another
      public static <V, E> List<V> getPath(Graph<V, E> tree, V v) {
        //list to store the path
        ArrayList<V> listPath = new ArrayList<>();
        if (!tree.hasVertex(v))
            return null;

        listPath.add(v);

       //iterate over the outNeighbors of the current vertex and adding them to the list
          Iterator<V> iterator = tree.outNeighbors(v).iterator();
          while(iterator.hasNext()){
              V vertex = iterator.next();
              listPath.add(vertex);
              iterator = tree.outNeighbors(vertex).iterator();
        }
        return listPath;
    }

    /**
     * create a set of all the missing vertices
     * @param graph
     * @param subgraph
     * @return
     * @param <V>
     * @param <E>
     */
    public static <V, E> Set<V> missingVertices(Graph<V, E> graph, Graph<V, E> subgraph) {
        Set<V> verticesMissed = new HashSet<>();
        //compare main graph and our subGraph  and
        // if the vertex is not common to both graphs, add it to the set
        for (V v : graph.vertices()) {
            if (!subgraph.hasVertex(v)) {
                verticesMissed.add(v);
            }
        }
        return verticesMissed;

    }

    /**
     * Method for average separation
     * @param tree
     * @param root
     * @return
     * @param <V>
     * @param <E>
     */
    //The average separation method
    public static <V, E> double averageSeparation(Graph<V, E> tree, V root) {
        //call the separationHelper method to calculate the total separation starting from the root
        //and calculating the total separation
        int totalSeperation = averageSeperationHelper(tree, root, 0);
        return (double) totalSeperation / (tree.numVertices());
    }

    /**
     *
     * @param tree
     * @param node
     * @param sep
     * @return
     * @param <V>
     * @param <E>
     */
    //A helper method that recursively calculates the total separation in a tree like graph
    public static <V, E> int averageSeperationHelper(Graph<V, E> tree, V node, int sep) {
        int total = sep;
        //Iterate over the inNeighbours of the current node
        // and recursively call separation Helper to update the separation value
        for (V v: tree.inNeighbors(node)) {
            total += averageSeperationHelper(tree, v, sep + 1);
        }
        //return the total separation
        return total;
    }
    //Calculating the number of inNeighbours of a vertex

    /**
     *
     * @param graph
     * @param v
     * @return
     * @param <V>
     * @param <E>
     */
    public static <V, E> int numOfCostars(Graph<V, E> graph, V v){
        return graph.inDegree(v);
    }
}



