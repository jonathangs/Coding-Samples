import java.util.Iterator;
import java.util.ArrayList;

/**
 * Created by Jonathan Shustov
 * 
 * EdgeCollection is designed to store a list of edges for any multigraph 
 * implementation, although it could also be applicable to any other construct
 * that requires a list of Strings that are treated like edges. This version
 * uses generics so that we can use any comparable data type.
 * 
 */

public class EdgeCollection<T extends Comparable<? super T>> {
    //Stores a list that contains the edges in a specific format
    private ArrayList<T> edges;
    
    // Abstraction Function:
    //   edges = [edge1:edge2:edge3:...:edgeN], where edgeN is 
    //   EdgeCollection.size(), and 1,2,3,...,N represent 
    //   alphabetical orderings. If EdgeCollection is empty,
    //   edges = [].

    // Representation invariant for every EdgeCollection:
    //   edges != null.
    //
    //   The generic object stored in edges, edge1 through edgeN are 
    //   stored.
    //
    //   There are no two instances of the same generic object.
      
    
    /**
     * Constructor for the edge collection that stores all the edges 
     * from one node to another. 
     * 
     * @return a new EdgeCollection that starts empty
     */
    public EdgeCollection(){
        edges = new ArrayList<T>();
    }
    
    /**
     * Constructor for the edge collection that store all the edges 
     * from one node to another. 
     * 
     * @param edge = edge to be added to EdgeCollection
     * @return a new EdgeCollection that contains one edge
     */
    public EdgeCollection(T edge){
        edges = new ArrayList<T>();
        this.add(edge);
    }
    
    /**
     * Adds an edge to the edge collection unless the edge is
     * already contained. Edges are placed in sorted ordering.
     * @param <E>
     * 
     * @param edge = edge to be added to edge collection
     * @modifies edges contained in this by addition
     * @return true if add successful. false otherwise.
     */
    public boolean add(T edge){
        //checkRep();
        if(edges.contains(edge))
            return false;
        //sorted insertion
        for(int i = 0; i < edges.size(); i++){
            if(edge.compareTo(edges.get(i))<0){
                edges.add(i, edge);
                checkRep();
                return true;
            }
        }
        //add to end of list (sorted)
        edges.add(edge);
        //checkRep();
        return true;
    }
    
    /**
     * Returns true if the edge collection contains edge
     * 
     * @return true if edge is contained in this. false otherwise.
     */
    public boolean contains(T edge){
        //checkRep();
        return edges.contains(edge); 
    }
    
    /**
     * Removes the specified edge from the edge collection.
     * 
     * @param edge = edge to be removed
     * @requires edge be contained in this 
     * @modifies edges contained in this by removal
     * @return true if removed from this. 
     *            false otherwise.    
     */
    public boolean remove(T edge){
        boolean removed = edges.remove(edge);
        //checkRep();
        return removed;
    }
    
    /**
     * Returns the number of edges in the edge collection.
     * 
     * @return the number of edges in this
     */
    public int size(){
        return edges.size();
    }
    
    /**
     * Returns an iterator of a copy of the strings in sorted order 
     * @return an iterator with all of the edges in alphabetical order
     */
    public Iterator<T> iterator(){
        return edges.iterator(); 
                
    }
    
    /**
     * Checks to ensure that the invariant holds.
     */
    private void checkRep(){
        assert(this.edges != null);
        //ensure that strings are sorted in alphabetical order 
        //and that no two of the same instances occur 
        for(int i = 0; i < edges.size(); i++){
            if(i < edges.size()-1)
                assert(edges.get(i).compareTo(edges.get(i+1)) < 0);
            
            T edge = edges.get(i);
            for(int j = i + 1; j < edges.size(); j++){
                assert(!edge.equals(edges.get(j)));
            }
        }
    }

}
