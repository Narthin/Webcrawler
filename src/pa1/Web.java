package pa1;

import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;

import api.Graph;
import api.TaggedVertex;

public class Web<E> implements Graph<E>{
	
	private ArrayList<E> vertices; //map index to url
	private Hashtable<E,Integer> index; //map url to index in vertices
	private ArrayList<ArrayList<Integer>> adjList; //adjacency list
	private int numVert;
	private int max;
	private ArrayList<TaggedVertex<E>> vertInc; //url with indegree
	private Hashtable<E,ArrayList<Integer>> incoming; //nodes with incoming edges
	private boolean[][] adjMatrix;
	
	public Web(E seedUrl, int max) {
		vertices = new ArrayList<>();
		index = new Hashtable<>();
		adjList = new ArrayList<>();
		adjMatrix = new boolean[max][max];
		numVert = 0;
		vertInc = new ArrayList<>();
		incoming = new Hashtable<>();
		this.max = max;
		addVertex(seedUrl);
		vertInc.set(0,new TaggedVertex<E>(vertInc.get(0).getVertexData(),vertInc.get(0).getTagValue()+1));
	}
	
	public boolean addVertex(E url){
		if(!index.containsKey(url) && numVert < max){
			vertices.add(numVert,url);
			index.put(url, numVert);
			adjList.add(numVert,new ArrayList<>());
			vertInc.add(numVert,new TaggedVertex<>(url,0));
			incoming.put(url, new ArrayList<>());
			numVert++;
			return true;
		} else 
			return false;
	}
	
	public void addEdge(int src, int dst){
		if(src >= 0 && src < numVert && dst >= 0 && dst < numVert && !adjMatrix[src][dst]){
			adjList.get(src).add(dst);
			adjMatrix[src][dst] = true;
			vertInc.set(dst,new TaggedVertex<E>(vertInc.get(dst).getVertexData(),vertInc.get(dst).getTagValue()+1));
			incoming.get(vertices.get(dst)).add(src);
		}
	}
	
	public void addEdge(E src, E dst){
		if(index.containsKey(src) && index.containsKey(dst)){
			addEdge(index.get(src),index.get(dst));
		}
	}
	
	public int get (E vert){
		if(index.containsKey(vert))
			return index.get(vert);
		else
			return -1;
	}
	
	public E get (int index){
		if (index >= 0 && index < numVert){
			return vertices.get(index);
		} else {
			return null;
		}
	}
	
	public boolean contains(int vert) {
		return vert >= 0 && vert < numVert;
	}
	
	public boolean contains(E vert){
		return index.containsKey(vert);
	}
	
	public boolean isFull(){
		return numVert >= max;
	}
	
	public ArrayList<Integer> getEdges(int vert){
		if(vert >= 0 && vert < numVert)
			return adjList.get(vert);
		else
			return null;
	}
	
	public ArrayList<Integer> getEdges(E vert){
		if(index.containsKey(vert))
			return adjList.get(index.get(vert));
		else
			return null;
	}
	
	public int getNumVert(){
		return numVert;
	}
	
	public ArrayList<E> vertexData(){
		return vertices;
	}
	
	public String toString(){
		String s = "";
		for(int x = 0; x < numVert; x++){
			s = s + x + " : " + vertices.get(x) + "\n";
		}
		s = s + "\n";
		for(int i = 0; i < numVert; i++){
			s = s + i + " : ";
			for(Integer t : adjList.get(i)){
				s = s + t.intValue() + " ";
			}
			s = s + "\n";
		}
		return s;
	}
	
	public ArrayList<TaggedVertex<E>> vertexDataWithIncomingCounts(){
		return vertInc;
	}
	
	public List<Integer> getNeighbors(int index){
		return adjList.get(index);
	}
	
	public List<Integer> getIncoming(int index){
		return incoming.get(vertices.get(index));
	}
}
