package pa1;

import java.util.List;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.ArrayList;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

import api.TaggedVertex;
import api.Util;

/**
 * Implementation of an inverted index for a web graph.
 * 
 * @author Jordan Fox
 */
public class Index
{
	private List<TaggedVertex<String>> urls;//url paired with indegree
	private Hashtable<String,ArrayList<TaggedVertex<String>>> invIndex;//word map to list of urls paired with num occurances
	private Hashtable<String,Integer> map; //url map to index
	
  /**
   * Constructs an index from the given list of urls.  The
   * tag value for each url is the indegree of the corresponding
   * node in the graph to be indexed.
   * @param urls
   *   information about graph to be indexed
   */
  public Index(List<TaggedVertex<String>> urls)
  {
    this.urls = urls;
    invIndex = new Hashtable<>();
    map = new Hashtable<>();
  }
  
  /**
   * Creates the index.
   */
  public void makeIndex()
  {
	  int numRequests = 0;
	  int index = 0;
	  Document doc = null;
	  Hashtable<String,Integer> tally = null;
	  String body = null;
	  Scanner s = null;
	  String word = null;
	  ArrayList<String> words = null;
	  for(TaggedVertex<String> v : urls){
		  map.put(v.getVertexData(), index);
		  index++;
		  doc = null;
		  try {
			  doc = Jsoup.connect(v.getVertexData()).get();
		  } catch (UnsupportedMimeTypeException e){
		  } catch (HttpStatusException  e) {
		  } catch (IOException e) {
		  }
		  numRequests++;
		  if(numRequests >= 50){
			  try{
				  Thread.sleep(3000);
			  } catch (InterruptedException e){}
			  numRequests -= 50;
		  }
		  if(doc != null){
			  //valid link
			  body = doc.body().text();
			  if(body != null){
				  tally = new Hashtable<>();
				  words = new ArrayList<>();
				  s = new Scanner(body);
				  while(s.hasNext()){
					  word = s.next();
					  word = Util.stripPunctuation(word);
					  if(!Util.isStopWord(word)){
						  //System.out.println(word);
						  if(tally.containsKey(word))
							  tally.put(word, tally.get(word)+1);
						  else{
							  tally.put(word, 1);
							  words.add(word);
						  }
					  }
				  }
				  s.close();
				  for(String w : words){
					  //System.out.println(v.getVertexData()+" "+w+": "+tally.get(w));
					  if(!invIndex.containsKey(w)){
						  invIndex.put(w, new ArrayList<>());
						  //System.out.println("new word " + w);
					  }
					  invIndex.get(w).add(new TaggedVertex<>(v.getVertexData(),tally.get(w)));
				  }
			  }
		  }
	  }
  }
  
  /**
   * Searches the index for pages containing keyword w.  Returns a list
   * of urls ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of the keyword multiplied by the indegree of its url in
   * the associated graph.  No pages with rank zero are included.
   * @param w
   *   keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> search(String w)
  {
	  if(invIndex.containsKey(w)){
		  ArrayList<TaggedVertex<String>> a = new ArrayList<>();
		  for(TaggedVertex<String> v : invIndex.get(w)){
			  a.add(new TaggedVertex<>(v.getVertexData(),v.getTagValue()*urls.get(map.get(v.getVertexData())).getTagValue()));
		  }
		  return mergeSort(a);
	  } else 
		  return new ArrayList<>();
  }


  /**
   * Searches the index for pages containing both of the keywords w1
   * and w2.  Returns a list of qualifying
   * urls ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of w1 plus number of occurrences of w2, all multiplied by the 
   * indegree of its url in the associated graph.
   * No pages with rank zero are included.
   * @param w1
   *   first keyword to search for
   * @param w2
   *   second keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> searchWithAnd(String w1, String w2)
  {
	  if(invIndex.containsKey(w1) && invIndex.containsKey(w2)){
		  Hashtable<String,Integer> q1 = new Hashtable<>();
		  for(TaggedVertex<String> v : invIndex.get(w1)){
			  q1.put(v.getVertexData(), v.getTagValue()*urls.get(map.get(v.getVertexData())).getTagValue());
		  }
		  ArrayList<TaggedVertex<String>> a = new ArrayList<>();
		  for(TaggedVertex<String> v : invIndex.get(w2)){
			  if(q1.containsKey(v.getVertexData())){
				  a.add(new TaggedVertex<>(v.getVertexData(),q1.get(v.getVertexData())+v.getTagValue()*urls.get(map.get(v.getVertexData())).getTagValue()));
			  }
		  }
		  return mergeSort(a);
	  } else 
		  return new ArrayList<>();
  }
  
  /**
   * Searches the index for pages containing at least one of the keywords w1
   * and w2.  Returns a list of qualifying
   * urls ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of w1 plus number of occurrences of w2, all multiplied by the 
   * indegree of its url in the associated graph.
   * No pages with rank zero are included.
   * @param w1
   *   first keyword to search for
   * @param w2
   *   second keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> searchWithOr(String w1, String w2)
  {
	  int index = 0;
	  ArrayList<TaggedVertex<String>> a = new ArrayList<>();
	  Hashtable<String,Integer> mapping = new Hashtable<>();
	  if(invIndex.containsKey(w1)){
		  for(TaggedVertex<String> v : invIndex.get(w1)){
			  a.add(index,new TaggedVertex<>(v.getVertexData(),v.getTagValue()*urls.get(map.get(v.getVertexData())).getTagValue()));
			  mapping.put(v.getVertexData(),index);
			  index++;
			  //System.out.println(index);
		  }
	  }
	  if(invIndex.containsKey(w2)){
		  for(TaggedVertex<String> v : invIndex.get(w2)){
			  if(mapping.containsKey(v.getVertexData())){
				  a.set(mapping.get(v.getVertexData()),new TaggedVertex<>(v.getVertexData(),a.get(mapping.get(v.getVertexData())).getTagValue()+v.getTagValue()*urls.get(map.get(v.getVertexData())).getTagValue()));
			  } else {
				  a.add(index,new TaggedVertex<>(v.getVertexData(),v.getTagValue()*urls.get(map.get(v.getVertexData())).getTagValue()));
				  mapping.put(v.getVertexData(),index);
				  index++;
			  }
		  }
	  }
    return mergeSort(a);
  }
  
  /**
   * Searches the index for pages containing keyword w1
   * but NOT w2.  Returns a list of qualifying urls
   * ordered by ranking (largest to smallest).  The tag 
   * value associated with each url is its ranking.  
   * The ranking for a given page is the number of occurrences
   * of w1, multiplied by the 
   * indegree of its url in the associated graph.
   * No pages with rank zero are included.
   * @param w1
   *   first keyword to search for
   * @param w2
   *   second keyword to search for
   * @return
   *   ranked list of urls
   */
  public List<TaggedVertex<String>> searchAndNot(String w1, String w2)
  {
	  Hashtable<String,Integer> mapping = new Hashtable<>();
	  ArrayList<TaggedVertex<String>> a = new ArrayList<>();
	  if(invIndex.containsKey(w2)){
		  for(TaggedVertex<String> v : invIndex.get(w2)){
			  mapping.put(v.getVertexData(), 1);
		  }
	  }
	  if(invIndex.containsKey(w1)){
		  for(TaggedVertex<String> v : invIndex.get(w1)){
			  if(!mapping.containsKey(v.getVertexData()))
				  a.add(new TaggedVertex<>(v.getVertexData(),v.getTagValue()*urls.get(map.get(v.getVertexData())).getTagValue()));
		  }
		 
	  } 
	  return mergeSort(a);
  }
  
  private ArrayList<TaggedVertex<String>> mergeSort (ArrayList<TaggedVertex<String>> whole){
	  ArrayList<TaggedVertex<String>> left = new ArrayList<>();
	  ArrayList<TaggedVertex<String>> right = new ArrayList<>();
	  int center;
	  if(whole.size() == 1)
		  return whole;
	  else {
		  center = whole.size()/2;
		  for(int i = 0; i < center; i++)
			  left.add(whole.get(i));
		  for(int i = center; i < whole.size(); i++)
			  right.add(whole.get(i));
		  left = mergeSort(left);
		  right = mergeSort(right);
		  merge(left,right,whole);
			 
	  }
	  return whole;
  }
  
  private void merge(ArrayList<TaggedVertex<String>> left, ArrayList<TaggedVertex<String>> right, ArrayList<TaggedVertex<String>> whole){
	  int leftIndex = 0;
	  int rightIndex = 0;
	  int wholeIndex = 0;
	  while(leftIndex < left.size() && rightIndex < right.size()){
		  if(left.get(leftIndex).getTagValue() > right.get(rightIndex).getTagValue()){
			  whole.set(wholeIndex, left.get(leftIndex));
			  leftIndex++;
		  } else {
			  whole.set(wholeIndex, right.get(rightIndex));
			  rightIndex++;
		  }
		  wholeIndex++;
	  }
	  ArrayList<TaggedVertex<String>> rest;
	  int restIndex;
	  if(leftIndex >= left.size()){
		  rest = right;
		  restIndex = rightIndex;
	  } else {
		  rest = left;
		  restIndex = leftIndex;
	  }
	  for(int i = restIndex;i<rest.size();i++){
		  whole.set(wholeIndex, rest.get(i));
		  wholeIndex++;
	  }
  }
  

}
