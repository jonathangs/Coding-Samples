import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;


/**
 * Created by Jonathan Shustov
 * 
 * Multigraph is a data structure used to represent directional graphs
 * in which nodes are allowed to have multiple edges from one node to 
 * another. Nodes are represented by any comparable data type, giving 
 * each a unique ID. Rather than numerical weighting, the edges are 
 * Objects specified by the client. This idea can be used to model a 
 * social network of shared interests, activities, and so on.
 * 
 */

public class Multigraph <E extends Comparable<? super E>, T extends Comparable<? super T>> {
    
    protected HashMap<E, HashMap<E, EdgeCollection<T>>> nodeMap;
    
    // Abstraction Function:
    //   nodeMap = [node1(node1(EdgeCollection1)...nodeN(EdgeCollectionN)),
    //   node2(node1(EdgeCollection1)...nodeN(EdgeCollectionN)), ..., 
    //   nodeN(node1(EdgeCollection1)...nodeN(EdgeCollectionN)], where 
    //   nodeN is Multigraph.size()th node, EdgeCollectionX represents a 
    //   collection of edges from outer nodeA to any inner nodeB.
    //   Every node is an object that is a key to a HasMap of HashMaps.
    
    // Representation invariant for every Multigraph:
    //   nodeMap != null.
    //
    //   Every node is represented by a unique Object (no duplicates allowed).
    //
    //   If node1 -> node2 EdgeCollection2 contains some edge from node1 to node2.

    /**
     * Create a new Multigraph. Starts empty.
     * 
     * @return new Multigraph
     */
    public Multigraph(){
        nodeMap = new HashMap<E, HashMap<E, EdgeCollection<T>>>();
    }

    /**
     * Adds a node to the graph.
     * @param node
     * @modifies this by adding to the graph if node is not 
     *              already contained.  
     *              node by adding a way to find connections.
     * @return true if successfully added. false otherwise.
     */
    public boolean addNode(E node){
        //checkRep();
        if(nodeMap.containsKey(node))
            return false;
        nodeMap.put(node, new HashMap<E, EdgeCollection<T>>());
        //checkRep();
        return true;
    }

    /**
     * Adds a directed edge from one node to another. Nodes are represented 
     * as unique objects.
     * @requires node1 and node2 are within nodeMap && edge not contained in EdgeCollection1
     * @modifies this by adding edges
     * @return true if added successfully. false otherwise.
     */
    public boolean addEdgeDirected(E node1, E node2, T edge){
        //checkRep();
        if((!nodeMap.containsKey(node1) || !nodeMap.containsKey(node2)))
            return false;
        
        //Create edge collection to HashMap in HashMap if none exist
        if(!nodeMap.get(node1).containsKey(node2)){
            HashMap<E, EdgeCollection<T>> node2Map = nodeMap.get(node1); 
            node2Map.put(node2, new EdgeCollection<T>(edge));
        }else{ //does reference node2
            HashMap<E, EdgeCollection<T>> node2Map = nodeMap.get(node1);
            EdgeCollection<T> node2Edges = node2Map.get(node2);
            //in case of duplicate, EdgeCollection will take care of this
            node2Edges.add(edge); 
        }
        //checkRep();
            return true;
    }

    /**
     * Adds one or two directed edge from one node to another. 
     * @requires node1 and node2 are within nodeMap && edge not contained in 
     *              (EdgeCollection1 and EdgeCollection2)
     * @modifies this by adding edges
     * @return true if either added successfully. false otherwise. 
     */
    public boolean addEdgesCircular(E node1, E node2, T edge){
        boolean added1;
        boolean added2;
        added1 = addEdgeDirected(node1, node2, edge); 
        added2 = addEdgeDirected(node2, node1, edge); //both are true
        
        return added1 || added2;
    }

    /**
     * Deletes one directed edge from node1 to node2.
     * @param node1, node2, edge 
     * @requires node1 and node2 are within nodeMap and 
     *              that the two are connected by edge
     * @modifies this by deleting edge
     * @return true if contained and deleted. false otherwise. 
     */
    public boolean deleteEdgeDirected(E node1, E node2, T edge){
        //checkRep();
        if((!nodeMap.containsKey(node1) || !nodeMap.containsKey(node2)))
            return false;
        //Create edge collection to HashMap in HashMap if none exist
        if(nodeMap.get(node1).containsKey(node2)){
            HashMap<E, EdgeCollection<T>> node2Map = nodeMap.get(node1);
            //Remove link to node2 if size edge will be 0
            boolean removed = false;
            removed = node2Map.get(node2).remove(edge);
            if(node2Map.get(node2).size() == 0){
                //remove link from node1 to node2
                node2Map.remove(node2);
            }
            //checkRep();
            return removed;
        }else{        
            return false;
        }
    }

    /**
     * Deletes a up to two directed edges involving node1 and node2. 
     * @param node1, node2, edge
     * @requires node1 and node2 are within nodeMap && edge contained 
     *              contained in (EdgeCollection1 or EdgeCollection2)
     * @modifies this by deleting edges
     * @return true if all contained are deleted successfully. false otherwise. 
     */
    public boolean deleteEdgeOccurrences(E node1, E node2, T edge){
        boolean added1;
        boolean added2;
        added1 = deleteEdgeDirected(node1, node2, edge); 
        added2 = deleteEdgeDirected(node2, node1, edge); 
        
        return added1 || added2;
    }

    /**
     * Deletes a node from the graph, including all edges referencing the node.
     * @param node
     * @requires node is contained in this
     * @modifies this by deleting a node and all occurrences of its edges
     * @return true if successfully deleted node and edge occurrences
     */
    public boolean deleteNode(E node){
        //checkRep();
        Iterator<Entry<E, HashMap<E, EdgeCollection<T>>>> itr = this.iterator();
        while(itr.hasNext()){
            Entry<E, HashMap<E, EdgeCollection<T>>> nodeEntries = itr.next();
            if(nodeEntries.getValue().containsKey(node))
                nodeEntries.getValue().remove(node);
        }
        //checkRep();
        return nodeMap.remove(node) != null;
                
    }

    /**
     * Return the EdgeCollection associated with any given node.
     *  
     * @param node
     * @requires hasNode(node) = true
     * @return copy of EdgeCollection associated with node. 
     *            null if there are no links (requires not respected)
     * 
     */
    public EdgeCollection<T> findEdges(E node1, E node2){
        EdgeCollection<T> result = new EdgeCollection<T>();
        //checkRep();
        if(this.hasNode(node1) && nodeMap.get(node1).containsKey(node2)){
            //returns the edge collection
            Iterator<T> itr = nodeMap.get(node1).get(node2).iterator(); 
            while(itr.hasNext())
                result.add(itr.next()); 
        }else{
            result = null;
        }
        //checkRep();
        return result;
    }

    /**
     * Return true if the node is in the graph
     * @param node
     * @return true if this has node. false otherwise. 
     * 
     */
    public boolean hasNode(E node){
        return nodeMap.containsKey(node);
        
    }

    /**
     * Return the number of nodes in the Multigraph
     * @return int representing number of nodes in graph
     */
    public int size(){
        return nodeMap.size();
    }

    /**
     * Returns an iterator to examine the nodes in the Multigraph
     * 
     */
    private Iterator<Entry<E, HashMap<E, EdgeCollection<T>>>> iterator(){
        //checkRep();
        return nodeMap.entrySet().iterator();
    }

    /**
     * @return a String with the nodes listed in alphabetical order 
     *            or an empty String if there are no nodes. 
     */
    public String listNodes(){
        List<E> nodes = sortedObjectList(this.nodeMap);
        
        String result = "";
        for(E s : nodes)
            result = result + " " + s;
        
        return result;    
    }

    /**
     * @return a String with the children of node listed in alphabetical 
     *         order or an empty String if there are no children. 
     *  
     */
    public String listChildren(E node){
        //checkRep();
        if(!this.hasNode(node))
            return "";
        List<E> childrenNodes = sortedObjectList(nodeMap.get(node));
        String result = "";
        for(int i = 0; i < childrenNodes.size(); i++){
            //get edge collection for every child
            Iterator<T> edgeItr = nodeMap.get(node).get(childrenNodes.get(i)).iterator();
            
            //update result with every node in its proper format
            while(edgeItr.hasNext()){
                result += " " + childrenNodes.get(i) + "(" + edgeItr.next() + ")";
            }
        }
                
        return result;    
    }

    /**
     * @return a sorted list with objects representing 
     * all of the nodes contained in the graph.
     */
    public List<E> nodeList(){
        return sortedObjectList(this.nodeMap);
    }

    /**
     * @requires node is contained in graph
     * @param node: the node whose children we're looking for
     * @return a sorted list with Objects representing 
     * all of the children of node.
     */
    public List<E> nodeChildrenList(E node){
        return sortedObjectList(this.nodeMap.get(node));
    }

    /**
     * Helper method to return sorted object list in alphabetical order 
     * that represent the nodes in the first or second hashmap
     * 
     * Returns unmodifiable list to protect rep. invariant.
     * @param <U>
     */
    @SuppressWarnings("unchecked")
    private <U> List<E> sortedObjectList(HashMap<E,U> map){
        if(map != null){
            List<E> nodes = new ArrayList<E>(); 
            nodes.addAll(map.keySet());
            Collections.sort(nodes); //since the result must be alphabetically sorted
            return Collections.unmodifiableList(nodes);
        }
        else{
            return null;
        }
        
    }

    /**
     * Checks to ensure that the invariant holds.
     * 
     */
    private void checkRep(){
        assert(nodeMap != null);
        List<E> nodeArray = this.nodeList();
        for(int i = 0; i < nodeArray.size(); i++){
            for(int j = i; j < nodeArray.size(); j++){
                //check edges are contained
                if(nodeMap.get(nodeArray.get(i)).containsKey(nodeArray.get(j)))
                    assert(nodeMap.get(nodeArray.get(i)).get(nodeArray.get(j)).size() > 0);
                //check that there are no duplicates
                if(j != i)
                    assert(!nodeArray.get(i).equals(nodeArray.get(j)));
            }
        }
    }
}
