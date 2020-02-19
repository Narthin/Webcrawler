package pa1;

import api.TaggedVertex;
import java.util.ArrayList;

public class Test {

	public static void main(String[] args) {
		
		int max = 50;
		int depth = 10;
		String seed = "http://web.cs.iastate.edu/~smkautz/cs311f19/temp/a.html";
		Crawler c = new Crawler(seed,depth,max);
		Web<String> web = (Web<String>) c.crawl();
		//System.out.println(web.toString());
		
		for(TaggedVertex<String> t : web.vertexDataWithIncomingCounts()){
			System.out.println(t.getVertexData() + " : " + t.getTagValue());
		}
		System.out.println();
		ArrayList<TaggedVertex<String>> t = web.vertexDataWithIncomingCounts();
		for(int x = 0; x < t.size(); x++){
			System.out.print(x + " : \nIndegree: " + t.get(x).getTagValue() + "\nNeighbors: ");
			for(Integer i : web.getNeighbors(x)){
				System.out.print(i.intValue() + " ");
			}
			System.out.print("\nIncoming: ");
			for(Integer i : web.getIncoming(x)){
				System.out.print(i.intValue() + " ");
			}
			System.out.println("");
		}
		System.out.println();
		Index dex = new Index(t);
		dex.makeIndex();
		for(TaggedVertex<String> s : dex.search("soup")){
			System.out.println(s.getVertexData() + " : " + s.getTagValue());
		}
	}

}
