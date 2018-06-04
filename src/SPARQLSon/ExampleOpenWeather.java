package SPARQLSon;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.json.JSONException;

public class ExampleOpenWeather {

	public static void main(String[] args) throws JSONException, Exception {

		// Database loading, change it for your folder
		String TDBdirectory = "/Users/adriansotosuarez/Desktop/miniYago";
		DatabaseWrapper dbw = new DatabaseWrapper(TDBdirectory);

		// Examples of query using the BIND_API function


		String test_use_case = "PREFIX ex: <http://example.org/> "
				+ "SELECT * WHERE {?x ex:label ?label .  "
				+ "SERVICE <http://api.openweathermap.org/data/2.5/weather?q={label}&appid=be84c20688b078837610d2010e2cd564>{"
				+ "  ($.[\"main\"][\"temp\"]) AS (?t)"
				+ "} "
				+ "}";

		HashMap<String, String> params1 = new HashMap<String, String>();
		


		params1.put("replace_string", "_");
		params1.put("cache", "true");
		params1.put("distinct", "false");
		params1.put("min_api_call", "false");
		
		// This params are key for an OAuth authenticaton, maybe are my keys for Twitter or Yelp
		
		// params1.put("consumerKey", "fqBmHXJ6DeeTHQsyBvPWFw");
		// params1.put("consumerSecret", "h7OS1xMz1Nf7HPKlJQkCdH6zszw");
		// params1.put("token", "HT_BrNwG6PGWgKGjxSYy34-H1HlVug_O");
		// params1.put("tokenSecret", "DRvZvXShinHhcK93LV2ZnNd1JK0");

	


		GetJSONStrategy strategy_oauth = new BasicStrategy();


		// Storage of strategies and parameters to call the API(s)

		ArrayList<GetJSONStrategy> strategy = new ArrayList<>();
		ArrayList<HashMap<String, String>> params = new ArrayList<HashMap<String,String>>();

		strategy.add(strategy_oauth);



		params.add(params1);



		// Execution of the query
		String selected_query = test_use_case;

		// System.out.println("QUERYING: \n" + selected_query);
		long start = System.nanoTime();
		ResultSet rs = dbw.evaluateSPARQLSon(selected_query, strategy, params, false);
		// MappingSet ms = new MappingSet(rs);
		// System.out.println(ms.serializeAsValues());
		// rs = dbw.evaluateSPARQLSon("SELECT * WHERE {" + ms.serializeAsValues() + "}", strategy, params, false);
		ResultSetFormatter.outputAsTSV(rs);
		dbw.qexec.close();
		dbw.dataset.close();
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
