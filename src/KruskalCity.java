import sun.security.provider.certpath.Vertex;

import java.util.*;

/**
 * Created by Karl on 5/8/2015.
 */
public class KruskalCity {
    public static void main (String [] args){
        /**
         * 1. Create a new List of Edges
         * 2. Add each edge
         * 3. Create a new Graph to Store those Edges
         * 4. Run our modified Dijkstra's on the Graph
         * 5. Print each Path, combined with Autobahn calculations
         */
        List<Graph.Edge> edges = new LinkedList();
        edges.add(new Graph.Edge('A','B',10,true));
        edges.add(new Graph.Edge('B','C',10,true));
        edges.add(new Graph.Edge('D','A',1,false));
        edges.add(new Graph.Edge('E','B',5,false));
        edges.add(new Graph.Edge('F','C',1,false));
        edges.add(new Graph.Edge('D','E',10,false));
        edges.add(new Graph.Edge('E','F',10,false));
        Graph g = new Graph(edges);
        g.Dijkstra('A');
        g.printPath('A','B');
        g.Dijkstra('D');
        g.printPath('D','E');
        g.Dijkstra('F');
        g.printPath('F','D');
    }

     static class Graph {
        private final Map<Character, Vertex> graph;
         private final List<Edge> originalEdges;

        public static class Edge {
            public final char start, end;
            public final int distance;
            public final boolean isAutobahn;

            /**
             * Constructor for each edge including autobahn status
             * @param start Initial Vertex
             * @param end Final Vertex
             * @param distance In kilometers
             * @param isAutobahn Records as boolean if the road is on the Autobahn
             */
            public Edge (char start, char end, int distance, boolean isAutobahn){
                this.start=start;
                this.end=end;
                this.distance=distance;
                this.isAutobahn=isAutobahn;
            }
        }

        public class Vertex implements Comparable<Vertex>{
            public final char name;
            public final boolean isAutobahn;
            public int dist = Integer.MAX_VALUE;
            public Vertex previous = null;
            public final Map<Vertex, Integer> neighbours = new HashMap<Vertex, Integer>();

            /**
             * Constructor for each individual vertex
             * @param name Character representing each road
             * @param isAutobahn Boolean representing if we are on the Autobahn
             */
            public Vertex(char name, boolean isAutobahn){
                this.name=name;
                this.isAutobahn=isAutobahn;
            }

            /**
             * Utilizing compare function for distance of edges
             * @param o Other Vertex
             * @return Comparison function
             */
            @Override
            public int compareTo(Vertex o) {
                return Integer.compare(dist,o.dist);
            }

            private void printPath() {
                if (this == this.previous) {
                    System.out.printf("%s", this.name);
                } else if (this.previous == null) {
                    System.out.printf("%s(unreached)", this.name);
                } else {
                    this.previous.printPath();
                    System.out.printf(" -> %s(%d)", this.name, this.dist);
                }
            }

            /**
             * Recursive method that calculates the total distance on Autobahn thus far
             * Base: Starting point
             * Recursive: Goes through edges and if the edge is not an Autobahn, subtracts the distance
             * @param soFarAutobahn Total distance thus far on Autobahn
             */
            private int totalAutobahn (int soFarAutobahn, char endChar){
                if(this == this.previous){
                    //Original node. There will be no Edges to check for distance
                    return soFarAutobahn;
                }else if (this.previous == null){
                    //Error: node has no previous node
                    //System.out.println(this.name + "has no prior node");
                    return 0;
                }else{
                    //Iterate through every Edge
                    for(Edge e : originalEdges){
                        if((e.start==this.previous.name && e.end==endChar)||(e.start==endChar&&e.end==this.previous.name)){
                            return this.previous.totalAutobahn(this.isAutobahn ? soFarAutobahn : soFarAutobahn - e.distance,this.previous.name);
                        }
                    }
                    //This point should never be reached as we made the Graph with the Edges
                    System.out.println("No edge exists for the path");
                    return 0;
                }
            }
        }

        /**
         * Set up your Graph from a List of Edges
         * @param edges List of Edges present
         */
        public Graph (List<Edge> edges){
            graph = new HashMap<Character, Vertex>(edges.size());
            originalEdges=edges;
            //Perform a single pass to find every Vertex possible
            for(Edge e: edges){
                if(!graph.containsKey(e.start)) graph.put(e.start,new Vertex(e.start,e.isAutobahn));
                if(!graph.containsKey(e.end)) graph.put(e.end,new Vertex (e.end,e.isAutobahn));
            }
            //Perform a second pass to set neighboring vertices
            for (Edge e : edges){
                graph.get(e.start).neighbours.put(graph.get(e.end),e.distance);
                graph.get(e.end).neighbours.put(graph.get(e.start),e.distance);
            }
        }

        /**
         * Public method for Dijkstra shortest path
         * @param startVertex Character of the starting Vertex
         */
        public void Dijkstra(char startVertex){
            //Sets the starting Vertex as the Vertex where your first character originates
            final Vertex source=graph.get(startVertex);
            NavigableSet<Vertex> vertexes = new TreeSet();
            //Set up your vertices
            for (Vertex v : graph.values()){
                v.previous = v == source ? source : null;
                v.dist = v == source ? 0 : Integer.MAX_VALUE;
                vertexes.add(v);
            }
            //Implement Dijkstra using a Binary Heap
            Vertex shortest,currentVertex;
            while(!vertexes.isEmpty()){
                //Find the shortest path thus far which is connected in our Binary Heap
                shortest=vertexes.pollFirst();
                //Should exit out at the very beginning
                if(shortest.dist == Integer.MAX_VALUE) break;
                //Compare distances from neighbors
                for(Map.Entry<Vertex, Integer> potentialEntry : shortest.neighbours.entrySet()){
                    currentVertex=potentialEntry.getKey();
                    //final int alternateDist = shortest.dist + potentialEntry.getValue();
                    final double alternateTrump = shortest.dist -  shortest.totalAutobahn(0,shortest.name)/2 + (potentialEntry.getKey().isAutobahn ? potentialEntry.getValue()/2 : potentialEntry.getValue());
                    final double currentTrump = currentVertex.dist - currentVertex.totalAutobahn(0,currentVertex.name)/2;
                    //Check for shorter path, trumping with Autobahn, taking into account Autobahn is twice as fast
                    if ((alternateTrump<currentTrump)||(alternateTrump==currentTrump && (currentVertex.totalAutobahn(0,currentVertex.name)<potentialEntry.getKey().totalAutobahn(0,potentialEntry.getKey().name)))){
                        vertexes.remove(currentVertex);
                        currentVertex.dist=shortest.dist + potentialEntry.getValue();
                        currentVertex.previous=shortest;
                        vertexes.add(currentVertex);
                    }
                }
            }
        }

         /**
          * Outputs the shortest path and calculates the time spent on the Autobahn
          * @param startchar Starting point (Could be coded out)
          * @param endchar Ending point (necessary)
          */
        public void printPath(char startchar, char endchar) {
            if (!graph.containsKey(endchar)) {
                System.err.printf("Graph doesn't contain end vertex \"%s\"\n", endchar);
                return;
            }
            System.out.println(startchar + " " + endchar + " " + graph.get(endchar).dist + " " + graph.get(endchar).totalAutobahn(graph.get(endchar).dist,endchar));
        }
     }
}

