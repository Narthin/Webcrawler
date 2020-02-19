package pa1;

import api.Graph;
import api.Util;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Implementation of a basic web crawler that creates a graph of some
 * portion of the world wide web.
 *
 * @author Jordan Fox
 */
public class Crawler
{
	private String seedUrl;
	private int maxDepth;
	private int maxPages;
  /**
   * Constructs a Crawler that will start with the given seed url, including
   * only up to maxPages pages at distance up to maxDepth from the seed url.
   * @param seedUrl
   * @param maxDepth
   * @param maxPages
   */
  public Crawler(String seedUrl, int maxDepth, int maxPages)
  {
    this.seedUrl = seedUrl;
    this.maxDepth = maxDepth;
    this.maxPages = maxPages;
  }
  
  /**
   * Creates a web graph for the portion of the web obtained by a BFS of the 
   * web starting with the seed url for this object, subject to the restrictions
   * implied by maxDepth and maxPages.  
   * @return
   *   an instance of Graph representing this portion of the web
   */
  public Graph<String> crawl()
  {
	  // TODO
	  Web<String> web = new Web<String>(seedUrl,maxPages);
	  ArrayList<ArrayList<String>> layer = new ArrayList<>();
	  int layerCounter = 0;
	  int numRequests = 0;
	  Document doc = null;
	  Elements links = null;
	  String v = null;
	  layer.add(layerCounter,new ArrayList<>());
	  layer.get(layerCounter).add(seedUrl);
	  while(!layer.get(layerCounter).isEmpty() && layerCounter <= maxDepth){
		layer.add(layerCounter+1,new ArrayList<>());
		for(String s : layer.get(layerCounter)){
			doc = null;
			try {
				doc = Jsoup.connect(s).get();
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
				//no exceptions from connecting to link s
				links = doc.select("a[href]");
				for(Element link : links){
					v = link.attr("abs:href");
					if(!Util.ignoreLink(s, v)){
						//valid link from s to v
						if(!web.contains(v) && layerCounter != maxDepth && web.addVertex(v)){
							//if v can be added to web ie web not full
							layer.get(layerCounter+1).add(v);
						}
						web.addEdge(s, v);
					}
				}
			}
		}
		layerCounter++;
		//System.out.println("layer: " + layerCounter + "\n" + web.toString());
	  }
	  
	  return web;
  }
}
