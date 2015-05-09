import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Karl on 5/6/2015.
 */
public class GraphMUD {

    public static void main (String []args){
        /**
         1. Take input, list by list
         2. For each list, create a new GraphMUD with max_vertices equal to n
         3. Add edges for each line of input data
         4. Run testCase() and obtain an answer based on creation of Graph
         **/
        GraphMUD a = new GraphMUD(5);
        a.setEdge(1,5);
        a.setEdge(5, 2);
        a.setEdge(3,2);
        a.setEdge(4, 3);
        System.out.println(a.testCase());
        GraphMUD b = new GraphMUD(5);
        b.setEdge(3,1);
        b.setEdge(4,2);
        b.setEdge(1,5);
        b.setEdge(5,4);
        System.out.println(b.testCase());
        GraphMUD c = new GraphMUD(2);
        c.setEdge(1,2);
        c.setEdge(2,1);
        System.out.println(c.testCase());
    }

    private Map<Integer,List<Integer>> Adjacency_List;
    boolean isInfeasible = false;
    boolean canBeNonlinear = false;

    /**
     * Constructor to populate Adjacency List
     * @param max_vertices Maximum number of vertices currently in use. Determined by n
     */
    public GraphMUD(int max_vertices){
        Adjacency_List= new HashMap<Integer,List<Integer>>();
        for(int i=1;i<=max_vertices;i++){
            Adjacency_List.put(i,new LinkedList<Integer>());
        }
    }

    /**
     * Edge creation modified to check for  MUD conditions
     * @param source Source Vertex
     * @param destination Destination Vertex
     */
    public void setEdge(int source, int destination){

        //If there is another source vertex, it will be non-linear
        for(int i=1;i<=Adjacency_List.size();i++)
        if(getEdge(i,destination)){
            canBeNonlinear=true;
        }
        //Having a path both directions results in infeasible
        if(getEdge(destination,source)){
            isInfeasible=true;
        }
        Adjacency_List.get(source).add(destination);
    }

    /**
     * Check if an edge exists yet
     * @param source Source Vertex
     * @param destination Destination vertex
     * @return Boolean to indicate edge presence
     */
    public boolean getEdge(int source, int destination){
        if(source>Adjacency_List.size()) {
            isInfeasible=true;
            return false;
        }
        return Adjacency_List.get(source).size()!=0 ? Adjacency_List.get(source).contains(destination): false;
    }

    /**
     * Gives data about each MUD test case
     * @return String based on the type of Gameplay experienced
     */
    public String testCase(){
        if(isInfeasible){
            return "Infeasible game";
        }
        if (canBeNonlinear){
            return "Nonlinear gameplay possible";
        }
        return "Linear gameplay";
    }

}
