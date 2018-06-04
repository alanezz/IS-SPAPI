package SPARQLSon;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.json.JSONException;

public class ExperimentsReal {

	public static void main(String[] args) throws JSONException, Exception {

		// Database loading
		String TDBdirectory = "/put/your/directory/here";
		DatabaseWrapper dbw = new DatabaseWrapper(TDBdirectory);

		// Examples of query using the BIND_API function


		String test_use_case = "PREFIX ex: <http://example.org/> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT * WHERE {?product rdfs:label ?label .  "
				+ "?product rdf:type <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType1> . "
				+ "SERVICE <http://localhost:5000/features/{label}>{"
				+ "  ($.values[*]) AS (?v1)"
				+ "} "
				+ "FILTER(?v1 = \"noisemakers\") "
				+ "SERVICE <http://localhost:5000/features/{label}>{"
				+ "  ($.values[*]) AS (?v2)"
				+ "} "
				+ "FILTER(?v2 = \"caskets\") "
				+ "SERVICE <http://localhost:5000/numeric/{label}>{"
				+ "  ($.p1) AS (?v3)"
				+ "} "
				+ "FILTER (?v3 > 30)"
				+ "}";

		String test_use_case_1 = "SELECT * WHERE{?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/City> . " +
				"?y <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/City> . " +
				"?x <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lo . " +
				"?y <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lo2 . " +
				"?x <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?la . " +
				"?y <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?la2 . " +
				"?x <http://www.w3.org/2000/01/rdf-schema#label> ?x2 . " +
				"?y <http://www.w3.org/2000/01/rdf-schema#label> ?y2 " +
				"FILTER((?la <= -?la2 + 1) && (?la >= -?la2 - 1) && " +
				"(?lo <= -((?lo2/abs(?lo2))*(180 - abs(?lo2))) + 1) && (?lo >= -((?lo2/abs(?lo2))*(180 - abs(?lo2))) - 1)) "
				+ "SERVICE <http://api.openweathermap.org/data/2.5/weather?q={x2}&appid=be84c20688b078837610d2010e2cd564>{"
				+ "  ($.[\"main\"][\"temp\"]) AS (?t)"
				+ "} "
				+ "SERVICE <http://api.openweathermap.org/data/2.5/weather?q={y2}&appid=be84c20688b078837610d2010e2cd564>{"
				+ "  ($.[\"main\"][\"temp\"]) AS (?t2)"
				+ "} "
				+ "}";


		String test_use_case_2 = "SELECT * WHERE {?x <http://www.wikidata.org/prop/direct/P31> <http://www.wikidata.org/entity/Q8502> . "
				+ "?x <http://www.w3.org/2000/01/rdf-schema#label> ?l . "
				+ "?x <http://www.wikidata.org/prop/direct/P131> <http://www.wikidata.org/entity/Q22> "
				+ "SERVICE <http://api.openweathermap.org/data/2.5/weather?q={l},GB&appid=be84c20688b078837610d2010e2cd564>{"
				+ "  ($.[\"weather\"][0][\"description\"]) AS (?t)"
				+ "} "
				+ "FILTER (?t = \"clear sky\")"
				+ "}";

		String test_use_case_3 =  "SELECT * WHERE {?x ?y <http://dbpedia.org/ontology/Museum> . "
				+ "?x <http://dbpedia.org/ontology/location> <http://dbpedia.org/resource/London> . "
				+ "?x <http://www.w3.org/2000/01/rdf-schema#label> ?l "
				+ "SERVICE <https://api.yelp.com/v2/search?term={l}&location=London&radius_filter=40000>{"
				+ "  ($.[\"businesses\"][0][\"is_closed\"]) AS (?isclosed)"
				+ "} "
				+ "SERVICE <https://api.twitter.com/1.1/search/tweets.json?q={l}&result_type=recent>{"
				+ "($.statuses[0:3].text) AS (?tw)"
				+ "}"
				+ "}";


		String test_use_case_4 = "SELECT * WHERE {?x <http://yago-knowledge.org/resource/isLocatedIn> <http://yago-knowledge.org/resource/Chile> . "
				+ "?x <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://yago-knowledge.org/resource/wikicat_Communes_of_Chile> . "
				+ "?x <http://www.w3.org/2000/01/rdf-schema#label> ?n "
				+ "SERVICE <https://api.yelp.com/v2/search?term=Burguers&location={n}&sort=2>{"
				+ "  ($.[\"businesses\"][0][\"name\"], $.[\"businesses\"][0][\"rating\"]) AS (?b, ?r)"
				+ "} "
				+ "} ";


		// Definition of parameters
		HashMap<String, String> params1 = new HashMap<String, String>();
		HashMap<String, String> params2 = new HashMap<String, String>();



		params1.put("replace_string", "_");
		params1.put("cache", "true");
		params1.put("distinct", "false");
		params1.put("min_api_call", "false");
		params1.put("consumerKey", "put_consumer_key");
		params1.put("consumerSecret", "put_consumer_secret");
		params1.put("token", "put_token");
		params1.put("tokenSecret", "put_token_secret");

		params2.put("replace_string", "_");
		params2.put("cache", "true");
		params2.put("distinct", "false");
		params2.put("min_api_call", "false");
		params2.put("consumerKey", "put_consumer_key");
		params2.put("consumerSecret", "put_consumer_secret");
		params2.put("token", "put_token");
		params2.put("tokenSecret", "put_token_secret");


		GetJSONStrategy strategy_oauth = new OAuthStrategy();
		GetJSONStrategy strategy_oauth2 = new OAuthStrategy();


		// Storage of strategies and parameters to call the API(s)

		ArrayList<GetJSONStrategy> strategy = new ArrayList<>();
		ArrayList<HashMap<String, String>> params = new ArrayList<HashMap<String,String>>();

		strategy.add(strategy_oauth);
		strategy.add(strategy_oauth2);



		params.add(params1);
		params.add(params2);



		// Execution of the query
		String selected_query = test_use_case;

		// System.out.println("QUERYING: \n" + selected_query);
		long start = System.nanoTime();
		ResultSet rs = dbw.evaluateSPARQLSon(selected_query, strategy, params, false);
		// MappingSet ms = new MappingSet(rs);
		// System.out.println(ms.serializeAsValues());
		// rs = dbw.evaluateSPARQLSon("SELECT * WHERE {" + ms.serializeAsValues() + "}", strategy, params, false);
		ResultSetFormatter.outputAsTSV(rs);
		long elapsedTime = System.nanoTime() - start;
		dbw.qexec.close();
		dbw.dataset.close();
		System.out.println("Total Time: " + elapsedTime / 1000000000.0);
		System.out.println("API Time: " + dbw.apiOptimizer.timeApi / 1000000000.0);
		// printStatistics();




	}

	public static void printStatistics() {
		System.out.println("API Calls: " + Experiments.API_CALLS);
		System.out.println("Cached Calls: " + Experiments.CACHED_CALLS);
		int count = 0;
		for(int[] stats: Experiments.REPEATED_CALLS) {
			System.out.println("SERVICE " + count + ": " + stats[0] + " - " + stats[1]);
			count += 1;
		}
	}

}
