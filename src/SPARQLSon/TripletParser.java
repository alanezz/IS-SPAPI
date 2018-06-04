package SPARQLSon;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TripletParser {
	
	/*
	 * PROPERTIES
	 */
	public String section_type;
	public ArrayList<String[]> triplets;
	public String service_uri;
	public String options;
	
	/*
	 * CONSTRUCTORS
	 */
	
	public TripletParser(String service_uri, String[] triplet, String options) {
		if(service_uri!=null) {
			this.section_type = "sparql_service";
			this.service_uri = service_uri;
		}
		else {
			this.section_type = "basic";
			this.service_uri = null;
		}
		this.triplets = new ArrayList<String[]>();
		this.triplets.add(triplet);
		this.options = options;
	}
	
	public TripletParser(String service_uri, String queryPart) {
		if(service_uri!=null) {
			this.section_type = "sparql_service";
			this.service_uri = service_uri;
		}
		else {
			this.section_type = "basic";
			this.service_uri = null;
		}
		this.triplets = new ArrayList<String[]>();
		// Match ?a ?b ?c ; ?d ?e . and transform into ?a ?b ?c . ?a ?d ?e .
		String triplet_regex = "((<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+)) *;(.*$)";
		Pattern pattern_triplet = Pattern.compile(triplet_regex);
		Matcher matcherTriplet = pattern_triplet.matcher(queryPart);
		while(matcherTriplet.find()) {
			queryPart = matcherTriplet.group(1) + " . " + matcherTriplet.group(2) + " " + matcherTriplet.group(5).trim();
			matcherTriplet = pattern_triplet.matcher(queryPart);
		}
		// Match the triplets add them to the TripletParser
		triplet_regex = "(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) *\\.*(.*$)";
		pattern_triplet = Pattern.compile(triplet_regex);
		matcherTriplet = pattern_triplet.matcher(queryPart);
		while(matcherTriplet.find()) {
			String[] triplets = new String[]{matcherTriplet.group(1), matcherTriplet.group(2), matcherTriplet.group(3)};
			this.triplets.add(triplets);
			queryPart = matcherTriplet.group(4);
			matcherTriplet = pattern_triplet.matcher(queryPart);
		}
		this.options = queryPart.trim();
	}
	
	public TripletParser(String queryPart) {
		this.section_type = "basic";
		this.service_uri = null;
		this.triplets = new ArrayList<String[]>();
		// Match ?a ?b ?c ; ?d ?e . and transform into ?a ?b ?c . ?a ?d ?e .
		String triplet_regex = "((<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+)) *;(.*$)";
		Pattern pattern_triplet = Pattern.compile(triplet_regex);
		Matcher matcherTriplet = pattern_triplet.matcher(queryPart);
		while(matcherTriplet.find()) {
			queryPart = matcherTriplet.group(1) + " . " + matcherTriplet.group(2) + " " + matcherTriplet.group(5).trim();
			matcherTriplet = pattern_triplet.matcher(queryPart);
		}
		// Match the triplets add them to the TripletParser
		triplet_regex = "(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) *\\.*(.*$)";
		pattern_triplet = Pattern.compile(triplet_regex);
		matcherTriplet = pattern_triplet.matcher(queryPart);
		while(matcherTriplet.find()) {
			String[] triplets = new String[]{matcherTriplet.group(1), matcherTriplet.group(2), matcherTriplet.group(3)};
			this.triplets.add(triplets);
			queryPart = matcherTriplet.group(4);
			matcherTriplet = pattern_triplet.matcher(queryPart);
		}
		this.options = queryPart.trim();
	}
	
	/*
	 * METHODS
	 */
	
	/* 
	 * FUNCTION: Add a triplet
	 * @param {String[]} triplet
	 * @return {}
	 */
	public void addTriplet(String[] triplet) {
		this.triplets.add(triplet);
	}
	
	/* 
	 * FUNCTION: Parse SPARQL Query constraints into a list of TripletParsers corresponding to the relative SPARQL blocks
	 * @param {String} sparqlQuerySection
	 * @return {ArrayList<TripletParser>}
	 */
	public static ArrayList<TripletParser> getParsedSPARQLBlocks(String sparqlQuerySection) {
		// Match ?a ?b ?c ; ?d ?e . and transform into ?a ?b ?c . ?a ?d ?e .
		String triplet_regex = "(.*)((<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+)) *;(.*$)";
		Pattern pattern_triplet = Pattern.compile(triplet_regex);
		Matcher matcherTriplet = pattern_triplet.matcher(sparqlQuerySection);
		while(matcherTriplet.find()) {
			sparqlQuerySection = matcherTriplet.group(1) + matcherTriplet.group(2) + " . " + matcherTriplet.group(3) + " " + matcherTriplet.group(6).trim();
			matcherTriplet = pattern_triplet.matcher(sparqlQuerySection);
		}
		
		ArrayList<TripletParser> parsedFirstQuery = new ArrayList<TripletParser>();
		String api_url_string = "(.*) *SERVICE +<([\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+)> *\\{([^\\}]*)\\} *(.*$)";
		Pattern pattern_variables = Pattern.compile(api_url_string);
		triplet_regex = "(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) +(<[\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+>|\\?\\w+|\\w+:\\w+) *\\.*(.*$)";
		pattern_triplet = Pattern.compile(triplet_regex);
		String query_string = sparqlQuerySection;
		Matcher m = pattern_variables.matcher(query_string);
		while(m.find()) {
			matcherTriplet = pattern_triplet.matcher(m.group(4));
			if(matcherTriplet.find()) {
				TripletParser basic_section = new TripletParser(m.group(4));
				parsedFirstQuery.add(0, basic_section);
			}
			TripletParser sparql_service_section = new TripletParser(m.group(2),  m.group(3));
			parsedFirstQuery.add(0, sparql_service_section);
			
			query_string = m.group(1);
			m = pattern_variables.matcher(query_string);
		}
		matcherTriplet = pattern_triplet.matcher(query_string);
		if(matcherTriplet.find()) {
			TripletParser basic_section = new TripletParser(query_string);
			parsedFirstQuery.add(0, basic_section);
		}
		return parsedFirstQuery;
	}
	
	/* 
	 * FUNCTION: Add a triplet to a list of TripletParsers representing a query
	 * @param {String} sparqlQuerySection
	 * @return {ArrayList<TripletParser>}
	 */
	public static void addTripletToParsedQuery(ArrayList<TripletParser> list_parsed_triplets, TripletParser parsed_triplets, int index_triplet) {
		// The triplet is added to a new section
		if (list_parsed_triplets.size()==0) {
			TripletParser new_section = new TripletParser(parsed_triplets.service_uri, parsed_triplets.triplets.get(index_triplet), parsed_triplets.options);
			list_parsed_triplets.add(new_section);
		}
		else if (parsed_triplets.service_uri != list_parsed_triplets.get(list_parsed_triplets.size()-1).service_uri) {
			TripletParser new_section = new TripletParser(parsed_triplets.service_uri, parsed_triplets.triplets.get(index_triplet), parsed_triplets.options);
			list_parsed_triplets.add(new_section);
		}
		// The triplet is added to the last created section
		else {
			list_parsed_triplets.get(list_parsed_triplets.size()-1).addTriplet(parsed_triplets.triplets.get(index_triplet));
		}
	}
	
	/* 
	 * FUNCTION: Transform a list of TripletParsers into a SPARQL Query section
	 * @param {ArrayList<TripletParser>} parsedTriplets
	 * @return {String}
	 */
	public static String reverseParsedSPARQLBlocks(ArrayList<TripletParser> parsedTriplets) {
		String query= "";
		for (int i=0; i<parsedTriplets.size();i++) {
			String triplets_string = "";
			for (int j=0; j< parsedTriplets.get(i).triplets.size(); j++) {
				for (int k=0; k<3; k++) {
					triplets_string += parsedTriplets.get(i).triplets.get(j)[k] + " ";
				}
				triplets_string += ". ";
			}
			if(parsedTriplets.get(i).section_type == "sparql_service" && triplets_string!="") {
				query += "SERVICE <" + parsedTriplets.get(i).service_uri + "> {" + triplets_string + parsedTriplets.get(i).options + "} " ;
			}
			else if (triplets_string!=""){
				query += triplets_string + parsedTriplets.get(i).options;
			}
		}
		return query;
	}

	/*
	 * TESTS
	 */
	public static void main(String[] args) throws Exception {
		
		String test =
				  "  ?place ?link <http://dbpedia.org/resource/Chile> ."
				+ "  <http://dbpedia.org/resource/Chile> geo:lat blabla:coucou ."
				+ "  place:l geo:long plouf:opoe ."
				+ "  SERVICE <http://dbpedia.org/sparql> {"
				+ "    ?place ?yipo ?label ."
				+ "	   <http://dbpedia.org/resource/Chile> <http://dbpedia.org/resource/Chile> <http://dbpedia.org/resource/Chile>"
    			+ "  }"
				+ "  ?place geo:lat ?lat ;"
				+ "  	 geo:long ?long ."
				+ "  SERVICE <http://dbpedia.org/sparql> {"
				+ "    ?place rdfs:label ?label ;"
				+ "    		 ?rdfs:label ?label ."
				+ "	   FILTER(lang(?label) = 'es') ."
				+ "	   pojfiunfo;lkco,cin;^zù"
				+ "}";
		
		ArrayList<TripletParser> result = getParsedSPARQLBlocks(test);
		for (int i=0; i< result.size(); i++) {
			System.out.println("-- SECTION n°"+i+" --");
			System.out.println("TYPE: " + result.get(i).section_type);
			for (int j=0; j<result.get(i).triplets.size(); j++) {
				System.out.println("TRIPLET n°"+j+": ");
				for (int k=0; k<result.get(i).triplets.get(j).length; k++) {
					System.out.println(" "+result.get(i).triplets.get(j)[k]);
				}
			}
		}
	}
}
