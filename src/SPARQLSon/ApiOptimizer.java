package SPARQLSon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;

public class ApiOptimizer {
	
	/*
	 * PROPERTIES
	 */
	
	int apiCalls;
	long timeApi;
	ArrayList<String> cacheKeys;
	HashMap<String, Object> cache;
	static final int CACHE_SIZE = Integer.MAX_VALUE;
	
	/*
	 * CONSTRUCTORS
	 */
	
	public ApiOptimizer() {
		this.cacheKeys = new ArrayList<String>();
		this.cache = new HashMap<>();
		this.timeApi = 0;
		this.apiCalls = 0;
	}
	
	/*
	 * METHODS
	 */
	
	/*
	 * FUNCTION: Apply the cache option to retrieve the JSON response
	 * @param {String} url_req
	 * @param {HashMap<String, String>} params
	 * @param {GetJSONStrategy} strategy
	 * @return {Object}
	 */
	public Object retrieve_json(String url_req, HashMap<String,String> params, GetJSONStrategy strategy) throws JSONException, Exception {
		Experiments.API_CALLS += 1;

		if (params.containsKey("cache") && params.get("cache").equals("true")) {
			if (cache.containsKey(url_req)) {
				Experiments.CACHED_CALLS += 1;
				return cache.get(url_req);
			}
			else {
				this.apiCalls += 1;
				long start = System.nanoTime();
				Object json =  ApiWrapper.getJSON(url_req, params, strategy);
				if (cacheKeys.size() < CACHE_SIZE) {
					cacheKeys.add(url_req);
					cache.put(url_req, json);
				}
				else {
					String removed_key = cacheKeys.remove(0);
					cache.remove(removed_key);
					cache.put(url_req, json);
				}
				long elapsedTime = System.nanoTime() - start;
				Experiments.API_TIME += elapsedTime;
				return json;
			}
		}
		else {
			this.apiCalls += 1;
			return ApiWrapper.getJSON(url_req, params, strategy);
		}
	}

	/*
	 * FUNCTION: Transform the parsed query to minimize the number of calls to API by Service
	 * @param {HashMap<String, Object>} parsedQuery
	 * @return {HashMap<String, Object>}
	 */
	public HashMap<String, Object> minimizeAPICall(HashMap<String, Object> parsedQuery) {
		// Look for inserted variables in the URL of the API-Service call and keep them
		String variable_in_URL = "(.*)=\\{([^\\}]+)\\}.*$";
		ArrayList<String> inserted_variables = new ArrayList<String>();
		Pattern pattern_variables = Pattern.compile(variable_in_URL);
		Matcher m = pattern_variables.matcher((String)parsedQuery.get("URL"));
		while(m.find()){
			inserted_variables.add(m.group(2));
			m = pattern_variables.matcher(m.group(1));
		}
		for (int i=0; i<inserted_variables.size(); i++) {
			inserted_variables.set(i, "?" + inserted_variables.get(i));
		}
		
		// Separate an eventual values_section from the rest of the FIRST section
		String values_regex ="(.*)( *VALUES *\\([^\\)]*\\) *\\{[^\\}]* *\\} *)(.*$)";
		String values_section = "";
		pattern_variables = Pattern.compile(values_regex);
		m = pattern_variables.matcher((String)parsedQuery.get("FIRST"));
		while(m.find()){
			values_section += m.group(2);
			parsedQuery.put("FIRST", m.group(1) + " " + m.group(3));
			m = pattern_variables.matcher((String)parsedQuery.get("FIRST"));
		}
		
		// Parse the first section into a list of TripletParsers corresponding to the relative SPARQL blocks
		ArrayList<TripletParser> triplets_to_put_first = TripletParser.getParsedSPARQLBlocks((String)parsedQuery.get("FIRST"));
		ArrayList<TripletParser> triplets_to_put_last = new ArrayList<TripletParser>();
		
		if (inserted_variables.size()>0) {
			
			// Eject the triplets constraining the inserted variables (no other variable in the triplet)
			triplets_to_put_last = ejectConstrainingTriplets(inserted_variables, triplets_to_put_first);
			
			// Eject the triplets which are independent from the selected_variables from the first query part
			ejectIndependantTriplets(inserted_variables, triplets_to_put_first);
			
			// Remove from the first part of the query the useless triplets which are called later
			parsedQuery.put("FIRST", values_section + " " + TripletParser.reverseParsedSPARQLBlocks(triplets_to_put_first));
			// Add to the last part of the query the triplets which are needed to Select the other variables later
			parsedQuery.put("LAST", values_section + " " + TripletParser.reverseParsedSPARQLBlocks(triplets_to_put_last) + (String)parsedQuery.get("LAST"));
		}
		else {
			// Put all the FIRST section after the API call
			triplets_to_put_last = triplets_to_put_first;
			parsedQuery.put("FIRST", "");
			parsedQuery.put("LAST", values_section + " " + TripletParser.reverseParsedSPARQLBlocks(triplets_to_put_last) + (String)parsedQuery.get("LAST"));
		}
		// Stock in a String the variables to Select before calling the API
		String needed_variables_string = "";
		for (String var: inserted_variables) {
			needed_variables_string += var + " ";
		}
		// Add to the parsed Query the variables to Select before calling the API through Service
		parsedQuery.put("VARS", needed_variables_string);
		return parsedQuery;
	}
	
	
	
	/*
	 * FUNCTION: Eject the triplets constraining the specified variables (no other variable in the triplet)
	 * @param {ArrayList<String>} vars
	 * @param {ArrayList<TripletParser>} list_parsed_triplets
	 * @return {ArrayList<TripletParser>}
	 */
	public ArrayList<TripletParser> ejectConstrainingTriplets(ArrayList<String> vars, ArrayList<TripletParser> list_parsed_triplets) {
		ArrayList<TripletParser> selected_triplets = new ArrayList<TripletParser>();
		// Iterate over the linked variables
		for (String var: vars) {
			// Iterate over the (basic and SPARQL-Service) sections of the first part of the query
			for (int section=0; section< list_parsed_triplets.size(); section++) {
				// Iterate over the list of triplets in the section
				for (int triplet=0; triplet<list_parsed_triplets.get(section).triplets.size(); triplet++) {
					int element = 0;
					boolean constraint_triplet = false;
					// Iterate over the elements of the triplet
					while (element<3) {
						if(var.equals(list_parsed_triplets.get(section).triplets.get(triplet)[element])) {
							constraint_triplet = true;
							for (int other_element=0; other_element<3; other_element++) {
								if(other_element!=element && list_parsed_triplets.get(section).triplets.get(triplet)[other_element].startsWith("?")) {
									// The linked variable is linked to another variable in the triplet
									constraint_triplet = false;							
								}
							}
							element = 3;
						}
						else {
							element +=1;
						}
					}
					// The triplet does not contain only the linked variable as a variable
					if (!constraint_triplet) {
						TripletParser.addTripletToParsedQuery(selected_triplets, list_parsed_triplets.get(section), triplet);							
					}
				}
			}
		}
		return selected_triplets;
	}
	
	/*
	 * FUNCTION: Eject the triplets which are not linked in the data graph with the specified variables
	 * @param {ArrayList<String>} vars
	 * @param {ArrayList<TripletParser>} list_parsed_triplets
	 * @return {ArrayList<TripletParser>}
	 */
	public void ejectIndependantTriplets(ArrayList<String> vars, ArrayList<TripletParser> list_parsed_triplets) {
		// Create a local list of variables
		ArrayList<String> local_vars = new ArrayList<String>();
		local_vars.addAll(vars);
		// Initialize the loop
		HashMap<String, Object> opt = ejectIncludingTriplets(local_vars, list_parsed_triplets);
		ArrayList<TripletParser> triplets_to_eject = new ArrayList<TripletParser>();
		boolean loop = true;
		// Loop which excludes the triplets linking variables to anterior linked variables from the triplets to eject
		while (loop) {
			triplets_to_eject = (ArrayList<TripletParser>)opt.get("selected_triplets");
			if (!local_vars.containsAll((ArrayList<String>)opt.get("linked_variables"))) {
				local_vars.addAll((ArrayList<String>)opt.get("linked_variables"));
				opt = ejectIncludingTriplets((ArrayList<String>)opt.get("linked_variables"), triplets_to_eject);
			}
			else {
				loop = false;
			}
		}
		for (int section=0; section< triplets_to_eject.size(); section++) {
			for (int triplet=0; triplet<triplets_to_eject.get(section).triplets.size(); triplet++) {
				for (int s=0; s<list_parsed_triplets.size(); s++) {
					for (int t=0; t<list_parsed_triplets.get(s).triplets.size(); t++) {
						if (list_parsed_triplets.get(s).triplets.get(t)==triplets_to_eject.get(section).triplets.get(triplet)
								&& list_parsed_triplets.get(s).service_uri==triplets_to_eject.get(section).service_uri) {
							list_parsed_triplets.get(s).triplets.remove(t);
						}
					}
				}
			}
		}

	}
	
	/*
	 * FUNCTION: Eject the triplets including the specified variables and stock the other variables linked by those triplets
	 * @param {ArrayList<String>} vars
	 * @param {ArrayList<TripletParser>} list_parsed_triplets
	 * @return {HashMap<String, Object>}
	 */
	public HashMap<String, Object> ejectIncludingTriplets(ArrayList<String> vars, ArrayList<TripletParser> list_parsed_triplets) {
		ArrayList<String> linked_variables = new ArrayList<String>();
		ArrayList<TripletParser> selected_triplets = new ArrayList<TripletParser>();
		// Iterate over the inserted variables
		for (String var: vars) {
			// Iterate over the (basic and SPARQL-Service) sections of the first part of the query
			for (int section=0; section< list_parsed_triplets.size(); section++) {
				// Iterate over the list of triplets in the section
				for (int triplet=0; triplet<list_parsed_triplets.get(section).triplets.size(); triplet++) {
					int element = 0;
					boolean include = false;
					// Iterate over the elements of the triplet
					while (element<3) {
						if(var.equals(list_parsed_triplets.get(section).triplets.get(triplet)[element])) {
							for (int other_element=0; other_element<3; other_element++) {
								if(other_element!=element && list_parsed_triplets.get(section).triplets.get(triplet)[other_element].startsWith("?")) {
									// Stock the variables linked to inserted variables by a triplet
									linked_variables.add(list_parsed_triplets.get(section).triplets.get(triplet)[other_element]);
								}
							}
							element=3;
							include = true;
						} else { element +=1; }
					}
					// Add the triplet which doesn't contain an inserted variable to the selected triplets
					if (!include) {
						TripletParser.addTripletToParsedQuery(selected_triplets, list_parsed_triplets.get(section), triplet);

					}
				}
			}
		}
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("linked_variables", linked_variables);
		result.put("selected_triplets", selected_triplets);
		return result;
	}
}
