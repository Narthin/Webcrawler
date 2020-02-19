package pa1;

import java.util.ArrayList;
import java.util.List;

import api.Graph;
import api.TaggedVertex;
import pa1.Crawler;
import pa1.Index;

public class ManIndexTest {

	public static void main(String[] args) {

		String url = "https://en.m.wikipedia.org/wiki/Mario_(franchise)?fbclid=IwAR3Plxr3O2U0bs10Qdjvmc93y1LdDIErpOPLN13mN7ZzQwDYHIP0dnhoXHA";
		Crawler crawler = new Crawler(url, 3, 40);
		Graph<String> graph = crawler.crawl();

		List<TaggedVertex<String>> urls = graph.vertexDataWithIncomingCounts();
		Index ind = new Index(urls);

		ind.makeIndex();

		List<TaggedVertex<String>> list = new ArrayList<TaggedVertex<String>>();
		list = ind.searchWithOr("video","game");
		for (TaggedVertex<String> s : list) {
			System.out.println(s.getVertexData() + ", rank: " + s.getTagValue());
		}
	}
}
