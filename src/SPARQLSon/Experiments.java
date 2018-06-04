package SPARQLSon;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.json.JSONException;

public class Experiments {

	public static int API_CALLS = 0;
	public static int API_TIME = 0;
	public static int CACHED_CALLS = 0;
	public static ArrayList<int[]> REPEATED_CALLS = new ArrayList<int[]>();

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

		String test_use_case_1 = "PREFIX ex: <http://example.org/> "
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
				+ "?product bsbm:productPropertyNumeric1 ?v3 "
				+ "FILTER (?v3 > 500) "
				+ "}";

		String test_use_case_2 = "PREFIX ex: <http://example.org/> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT * WHERE {"
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer7/Product286> rdfs:label ?label . "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer7/Product286> rdfs:comment ?comment . "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer7/Product286> bsbm:producer ?p . "
				+ "?p rdfs:label ?producer . "
				+ "SERVICE <http://localhost:5000/features/{label}>{"
				+ "  ($.values[*]) AS (?f)"
				+ "} "
				+ "SERVICE <http://localhost:5000/textual/{label}>{"
				+ "  ($.p1, $.p2, $.p3) AS (?propertyTextual1, ?propertyTextual2, ?propertyTextual3) "
				+ "} "
				+ "SERVICE <http://localhost:5000/numeric/{label}>{"
				+ "  ($.p1, $.p2) AS (?propertyNumeric1, ?propertyNumeric2)"
				+ "} "
				+ "}";

		String test_use_case_2_2 = "PREFIX ex: <http://example.org/> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT * WHERE {"
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product250> rdfs:label ?label . "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product250> rdfs:comment ?comment . "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product250> bsbm:producer ?p . "
				+ "?p rdfs:label ?producer . "
				+ "SERVICE <http://localhost:5000/features/{label}>{"
				+ "  ($.values[*]) AS (?f)"
				+ "} "
				+ "SERVICE <http://localhost:5000/textual/{label}>{"
				+ "  ($.p1, $.p2, $.p3) AS (?propertyTextual1, ?propertyTextual2, ?propertyTextual3) "
				+ "} "
				+ "SERVICE <http://localhost:5000/numeric/{label}>{"
				+ "  ($.p1, $.p2) AS (?propertyNumeric1, ?propertyNumeric2)"
				+ "} "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product250> bsbm:productPropertyTextual4 ?p4 . " // moved
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product250> bsbm:productPropertyTextual5 ?p5 " // moved
				+ "}";


		String test_use_case_3 = "PREFIX ex: <http://example.org/> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT * WHERE {"
				+ "?product rdfs:label ?label . "
				+ "?product rdf:type <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType1> . "
				+ "SERVICE <http://localhost:5000/features/{label}>{"
				+ "  ($.values[*]) AS (?f)"
				+ "} "
				+ "FILTER(?f = \"servilely\") "
				+ "SERVICE <http://localhost:5000/numeric/{label}>{"
				+ "  ($.p1, $.p2) AS (?p1, ?p2)"
				+ "} "
				+ "FILTER ( ?p1 > 100) "
				+ "FILTER (?p2 < 1500 ) "
				+ "}";

		String test_use_case_3_2 = "PREFIX ex: <http://example.org/> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT * WHERE {"
				+ "?product rdfs:label ?label . "
				+ "?product rdf:type <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType1> . "
				+ "SERVICE <http://localhost:5000/features/{label}>{"
				+ "  ($.values[*]) AS (?f)"
				+ "} "
				+ "FILTER(?f = \"servilely\") "
				+ "?product bsbm:productPropertyNumeric1 ?p1 . " // moved
				+ "?product bsbm:productPropertyNumeric2 ?p2 " // moved
				+ "FILTER ( ?p1 > 100) "
				+ "FILTER (?p2 < 500 ) "
				+ "}";

		String test_use_case_4 = "PREFIX ex: <http://example.org/> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT * WHERE {?product rdfs:label ?label .  "
				+ "?product rdf:type <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType1> . "
				+ "SERVICE <http://localhost:5000/features/{label}>{"
				+ "  ($.values[*]) AS (?v1)"
				+ "} "
				+ "FILTER(?v1 = \"tiller\") "
				+ "SERVICE <http://localhost:5000/features/{label}>{"
				+ "  ($.values[*]) AS (?v2)"
				+ "} "
				+ "FILTER((?v2 = \"steering\" || ?v2 = \"upsilons\")) "
				+ "SERVICE <http://localhost:5000/numeric/{label}>{"
				+ "  ($.p1) AS (?p1)"
				+ "} "
				+ "FILTER (?p1 > 100)"
				+ "}";

		String test_use_case_4_2 = "PREFIX ex: <http://example.org/> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "SELECT * WHERE {?product rdfs:label ?label .  "
				+ "?product rdf:type <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType1> . "
				+ "?product bsbm:productPropertyNumeric1 ?p1 . " // moved
				+ "FILTER (?p1 > 500) "
				+ "SERVICE <http://localhost:5000/features/{label}>{"
				+ "  ($.values[*]) AS (?v1)"
				+ "} "
				+ "FILTER(?v1 = \"tiller\") "
				+ "SERVICE <http://localhost:5000/features/{label}>{"
				+ "  ($.values[*]) AS (?v2)"
				+ "} "
				+ "FILTER((?v2 = \"steering\" || ?v2 = \"upsilons\")) "

				+ "}";

		String test_use_case_5 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX ex: <http://example.org/> "
				+ "PREFIX rev: <http://purl.org/stuff/rev#> "
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "SELECT * WHERE {?product rdfs:label ?label . "
				+ "?product rdf:type <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType1> . "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer7/Product286> rdfs:label ?label2 "
				+ "FILTER (<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer7/Product286> != ?product) "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer7/Product286> bsbm:productFeature ?f . "
				+ "?product bsbm:productFeature ?f "
				+ "SERVICE <http://localhost:5000/numeric/{label}>{"
				+ "  ($.p1) AS (?simp1)"
				+ "} "
				+ "SERVICE <http://localhost:5000/numeric/{label2}>{"
				+ "  ($.p1) AS (?origp1)"
				+ "} "
				+ "FILTER (?simp1 < (?origp1 + 120) && ?simp1 > (?origp1 - 120)) "
				+ "SERVICE <http://localhost:5000/numeric/{label}>{"
				+ "  ($.p2) AS (?simp2)"
				+ "} "
				+ "SERVICE <http://localhost:5000/numeric/{label2}>{"
				+ "  ($.p2) AS (?origp2)"
				+ "} "
				+ "FILTER (?simp2 < (?origp2 + 500) && ?simp2 > (?origp2 - 500)) "
				+ "}";

		String test_use_case_6 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX ex: <http://example.org/> "
				+ "PREFIX rev: <http://purl.org/stuff/rev#> "
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "SELECT * WHERE {<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product250> rdfs:label ?label . "
				+ "?offer bsbm:product <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product250> . "
				+ "?offer ex:id ?id "
				+ "SERVICE <http://localhost:5000/offer/{id}>{"
				+ "  ($.price, $.vendor, $.country) AS (?pr, ?vendor, ?country)"
				+ "} "
				+ "FILTER(?country = \"http://downlode.org/rdf/iso-3166/countries#GB\") "
				+ "?review bsbm:reviewFor <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product250> . "
				+ "?review ex:id ?id2 . "
				+ "?review bsbm:rating1 ?rating1 . ?review bsbm:rating2 ?rating2 " // moved
				+ "SERVICE <http://localhost:5000/review/{id2}>{"
				+ "  ($.revName, $.revTitle) AS (?revName, ?revTitle)"
				+ "} "
				+ "}";

		String test_use_case_7 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX ex: <http://example.org/> "
				+ "PREFIX rev: <http://purl.org/stuff/rev#> "
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "SELECT * WHERE {<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer5/Product201> rdfs:label ?label . "
				+ "?review bsbm:reviewFor <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer5/Product201> . "
				+ "?review ex:id ?id2 . "
				+ "?review bsbm:rating1 ?rating1 . " // moved
				+ "?review bsbm:rating2 ?rating2 . " // moved
				+ "?review bsbm:rating3 ?rating3 . " // moved
				+ "?review bsbm:rating4 ?rating4 " // moved
				+ "SERVICE <http://localhost:5000/review/{id2}>{"
				+ "  ($.revName, $.revTitle, $revText) AS (?revName, ?revTitle, ?revText)"
				+ "} "
				+ "}";

		String test_use_case_8 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX ex: <http://example.org/> "
				+ "PREFIX rev: <http://purl.org/stuff/rev#> "
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "SELECT * WHERE {<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product250> rdfs:label ?label . "
				+ "?offer bsbm:product <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product250> . "
				+ "?offer ex:id ?id . "
				+ "?offer bsbm:deliveryDays ?devDays . " // moved
				+ "FILTER(?devDays < 3) "
				+ "SERVICE <http://localhost:5000/offer/{id}>{"
				+ "  ($.price, $.vendor, $.country) AS (?price, ?vendor, ?country)"
				+ "} "
				+ "}";

		String test_use_case_9 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				+ "PREFIX ex: <http://example.org/> "
				+ "PREFIX rev: <http://purl.org/stuff/rev#> "
				+ "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "SELECT * WHERE { "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor5/Offer9220> bsbm:product ?p . "
				+ "?p rdfs:label ?label . "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor5/Offer9220> ex:id ?id "
				+ "SERVICE <http://localhost:5000/offer/{id}>{"
				+ "  ($.price, $.vendor) AS (?price, ?vendor)"
				+ "} "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor5/Offer9220> bsbm:deliveryDays ?devDays . "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor5/Offer9220> bsbm:offerWebpage ?offerURL . "
				+ "<http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromVendor5/Offer9220> bsbm:validTo ?validTo "
				+ "}";


		// Definition of parameters
		HashMap<String, String> params1 = new HashMap<String, String>();


		params1.put("replace_string", "_");
		params1.put("cache", "true");
		params1.put("distinct", "false");
		params1.put("min_api_call", "false");



		GetJSONStrategy strategy_basic = new BasicStrategy();

		// Storage of strategies and parameters to call the API(s)

		ArrayList<GetJSONStrategy> strategy = new ArrayList<>();
		ArrayList<HashMap<String, String>> params = new ArrayList<HashMap<String,String>>();

		strategy.add(strategy_basic);
		strategy.add(strategy_basic);
		strategy.add(strategy_basic);
		strategy.add(strategy_basic);



		params.add(params1);
		params.add(params1);
		params.add(params1);
		params.add(params1);



		// Execution of the query
		String selected_query = test_use_case_9;

		// System.out.println("QUERYING: \n" + selected_query);
		long start = System.nanoTime();
		ResultSet rs = dbw.evaluateSPARQLSon(selected_query, strategy, params, false);
		// MappingSet ms = new MappingSet(rs);
		// System.out.println(ms.serializeAsValues());
		// rs = dbw.evaluateSPARQLSon("SELECT * WHERE {" + ms.serializeAsValues() + "}", strategy, params, false);
		// ResultSetFormatter.outputAsTSV(rs);
		long elapsedTime = System.nanoTime() - start;
		dbw.qexec.close();
		dbw.dataset.close();
		System.out.println("Total Time: " + elapsedTime / 1000000000.0);
		System.out.println("API Time: " + dbw.apiOptimizer.timeApi / 1000000000.0);
		printStatistics();

		dbw = new DatabaseWrapper(TDBdirectory);

		selected_query = test_use_case_8;

		// System.out.println("QUERYING: \n" + selected_query);
		start = System.nanoTime();
		rs = dbw.evaluateSPARQLSon(selected_query, strategy, params, false);
		elapsedTime = System.nanoTime() - start;
		dbw.qexec.close();
		dbw.dataset.close();
		System.out.println("Total Time: " + elapsedTime / 1000000000.0);
		System.out.println("API Time: " + dbw.apiOptimizer.timeApi / 1000000000.0);
		printStatistics();



	}

	public static void printStatistics() {
		System.out.println("API Calls: " + API_CALLS);
		System.out.println("Cached Calls: " + CACHED_CALLS);
		int count = 0;
		for(int[] stats: REPEATED_CALLS) {
			System.out.println("SERVICE " + count + ": " + stats[0] + " - " + stats[1]);
			count += 1;
		}
	}

}
