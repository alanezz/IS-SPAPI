package SPARQLSon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.jsonpath.JsonPath;


public class DatabaseWrapper {
	/*
	 * PROPERTIES
	 */
	
	ApiOptimizer apiOptimizer;
	int mappingCount;
	String TDBdirectory;
	QueryExecution qexec;
	Dataset dataset;
	static final int RESULTS_MAX = 100000; // Maximum of results processed by the pipeline method if LIMIT operator is not used.
	static final boolean FUSEKI_ENABLED = true;
	static final boolean LOG = true;

	static final Class[] no_quote_types = {Boolean.class, Integer.class, Double.class, Float.class};

	/*
	 * CONSTRUCTORS
	 */
	
	public DatabaseWrapper(String _directory) {
		this.TDBdirectory = _directory;
		this.apiOptimizer = new ApiOptimizer();
		this.mappingCount = 0;
	}
	
	/*
	 * METHODS
	 */

	/*
	 * FUNCTION: Create the dataset to query on from the source file
	 * @param {String} source
	 * @param {String} format
	 * @return {}
	 */
	public void createDataset(String source, String format) {
		dataset = TDBFactory.createDataset(this.TDBdirectory);
		Model tdb = dataset.getDefaultModel();
		FileManager.get().readModel(tdb, source, format);
		dataset.close();
	}
	
	/*
	 * FUNCTION: Evaluate if the query includes an API service section, execute the query according to the adequate method
	 * @param {String} queryString
	 * @param {ArrayList<GetJSONStrategy>} strategy
	 * @param {ArrayList<HashMap<String, String>>} params
	 * @param {boolean} replace (optional)
	 * @return {}
	 */
	public ResultSet evaluateSPARQLSon(String queryString, ArrayList<GetJSONStrategy> strategy, ArrayList<HashMap<String, String>> params) throws JSONException, Exception {
		return evaluateSPARQLSon(queryString, strategy, params, true);
	}
	
	public ResultSet evaluateSPARQLSon(String queryString, ArrayList<GetJSONStrategy> strategy, ArrayList<HashMap<String, String>> params, boolean replace) throws JSONException, Exception {
		strategy.get(0).set_params(params.get(0));
		HashMap<String, Object> parsedQuery = SPARQLSonParser.parseSPARQLSonQuery(queryString, replace);
		if (parsedQuery.get("URL") == null) {
			// Applies the sparql execution method
			return execQuery(queryString);
		}
		else {
			int limit = retrieve_limit((String) parsedQuery.get("OPTIONS"), params.get(0));
			// Applies the pipeline execution method for query including API service
			if(params.get(0).containsKey("pipeline") && params.get(0).get("pipeline").equals("true")) {
				MappingSet ms = execQueryPipeURL(parsedQuery, strategy, params, limit);
				return execPostBindQuery((String) parsedQuery.get("PREFIX") + " " + (String) parsedQuery.get("SELECT"),
								  "}" + (String) parsedQuery.get("OPTIONS"),
								  ms);
			}
			// Applies the generic execution method for query including API service
			else {
				MappingSet ms = execQueryGenURL(parsedQuery, strategy.get(0), params.get(0));
				// Recursive condition is that the LAST section of parsedQuery includes another API service section
				String api_url_string = " +SERVICE +<([\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+)> *\\{ *\\( *(\\$.*$)";	
				Pattern pattern_variables = Pattern.compile(api_url_string);
				Matcher m = pattern_variables.matcher(" " + (String) parsedQuery.get("LAST"));
				if (m.find()) {
					String recursive_query_string = ((String) parsedQuery.get("PREFIX")) + ((String) parsedQuery.get("SELECT")) + ms.serializeAsValues() + (String) parsedQuery.get("LAST") + (String) parsedQuery.get("OPTIONS");
					return evaluateSPARQLSon(recursive_query_string, new ArrayList<GetJSONStrategy>(strategy.subList(1, strategy.size())), new ArrayList<HashMap<String,String>>(params.subList(1, params.size())), false);
				}
				else {
					return execPostBindQuery((String) parsedQuery.get("PREFIX") + (String) parsedQuery.get("SELECT"), 
							(String) parsedQuery.get("LAST") + (String) parsedQuery.get("OPTIONS"), ms);
				}

			}
		}
	}
	
	/*
	 * FUNCTION: Get the results' limit from the query or applies the static constant of the dbw
	 * @param {String} options
	 * @param {ArrayList<HashMap<String, String>>} params
	 * @return {int}
	 */
	public int retrieve_limit(String options, HashMap<String, String> params) {
		String limit_regex = "LIMIT *(\\d+)";
		Pattern pattern_limit = Pattern.compile(limit_regex);
		Matcher m_limit = pattern_limit.matcher(options);
		int limit = RESULTS_MAX;
		if (m_limit.find()) {
			// Force the use of the pipeline execution method
			params.put("pipeline", "true");
			limit = Integer.parseInt(m_limit.group(1));
		}
		return limit;
	}
	
	/*
	 * FUNCTION: Execute a simple sparql query and print it in a JSON format
	 * @param {String} source
	 * @param {String} format
	 * @return {}
	 */
	/* public void execQuery(String queryString) {
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec;
		Dataset dataset;
		if(!FUSEKI_ENABLED) {
			dataset = TDBFactory.createDataset(this.TDBdirectory);
			qexec = QueryExecutionFactory.create(query, dataset);
		}
		else {
			qexec = QueryExecutionFactory.sparqlService("http://localhost:3030/ds", query);
		}
		try {
			// Assumption: it's a SELECT query.
			ResultSet rs = qexec.execSelect();
			JSONObject jsonResponse = new JSONObject();
			jsonResponse.put("results", new JSONArray());
			jsonResponse.put("vars", rs.getResultVars());
			// The order of results is undefined. 
			for ( ; rs.hasNext() ; ) {
				JSONObject mappingToJSON = new JSONObject();
				QuerySolution rb = rs.nextSolution() ;
				// Get title - variable names do not include the '?' (or '$')
				for (String v: rs.getResultVars()) {	
					mappingToJSON.put(v, rb.get(v));
				}
				((JSONArray)jsonResponse.get("results")).put(mappingToJSON);
				this.mappingCount++;
			}
			System.out.println(jsonResponse.toString());
		}
		finally
		{
			// QueryExecution objects should be closed to free any system resources 
			qexec.close() ;
			if (!FUSEKI_ENABLED) {
				dataset.close();
			}
		}
	}*/
	
	public ResultSet execQuery(String queryString) {
		Query query = QueryFactory.create(queryString);
		if(!FUSEKI_ENABLED) {
			dataset = TDBFactory.createDataset(this.TDBdirectory);
			qexec = QueryExecutionFactory.create(query, dataset);
		}
		else {
			qexec = QueryExecutionFactory.sparqlService("http://localhost:3030/ds", query);
		}
		try {
			// Assumption: it's a SELECT query.
			ResultSet rs = qexec.execSelect();
			if (LOG) {
				printResultSet(rs);
			}
			return rs;
		}
		finally
		{
			// QueryExecution objects should be closed to free any system resources 
			// qexec.close() ;
			if (!FUSEKI_ENABLED) {
				// dataset.close();
			}
		}
	}

	/*
	 * FUNCTION: Map the JSON values from the API call with all the values from the previous sparql query
	 * @param {HashMap<String, Object>} parsedQuery
	 * @param {GetJSONStrategy} strategy
	 * @param {HashMap<String, String>} params
	 * @param {int} limit
	 * @return {MappingSet}
	 */
	public MappingSet execQueryGenURL(HashMap<String, Object> parsedQuery, 
			GetJSONStrategy strategy, HashMap<String, String> params) 
					throws JSONException, Exception {
		String firstQuery = retrieve_firstQuery (parsedQuery, params);
		// System.out.println(firstQuery);
		String[] bindName = (String[])parsedQuery.get("ALIAS");
		String[] jpath = (String[])parsedQuery.get("PATH");
		Query query = QueryFactory.create(firstQuery);
		if(!FUSEKI_ENABLED) {
			dataset = TDBFactory.createDataset(this.TDBdirectory);
			qexec = QueryExecutionFactory.create(query, dataset);
		}
		else {
			qexec = QueryExecutionFactory.sparqlService("http://localhost:3030/ds", query);
		}
		MappingSet ms = new MappingSet();
		ArrayList<String> ms_varnames = new ArrayList<String>();

		try {
			// Assumption: it's a SELECT query.
			ResultSet rs = qexec.execSelect() ;
			List<String> vars_name =  rs.getResultVars();
			for (String vn: vars_name) {
				ms_varnames.add(vn);
			}
			for (String bn: bindName) {
				ms_varnames.add(bn);
			}
			ms.set_var_names(ms_varnames);
			
			ArrayList<String> requested_URIs = new ArrayList<String>();
			int repeated_values = 0;
			int total_values = 0;
			
			// The order of results is undefined. 
			for ( ; rs.hasNext() ; ) {
				QuerySolution rb = rs.nextSolution() ;
				HashMap<String, String> mapping = mappQuerySolution(rb, vars_name);				
				String url_req = ApiWrapper.insertValuesURL((String)parsedQuery.get("URL"), rb, params.get("replace_string"));
				Object json = null;
				try {
					long start = System.nanoTime();
					if (requested_URIs.contains(url_req)) {
						repeated_values += 1;
					}
					else {
						requested_URIs.add(url_req);
					}
					total_values += 1;
					
					json = this.apiOptimizer.retrieve_json(url_req, params, strategy);
					long stop = System.nanoTime();
					this.apiOptimizer.timeApi += (stop - start);
				}
				catch (Exception name) {
					// System.out.println("ERROR: " + name);
					for (int i = 0; i < bindName.length; i++) {
						mapping.put(bindName[i], "UNDEF");
					}
				}
				if (json != null) {
					ArrayList<HashMap<String, String>> mapping_array = new ArrayList<HashMap<String, String>>();
					for (int i = 0; i < bindName.length; i++) {
						try {						
							Object value = JsonPath.parse(json).read(jpath[i]);
							mapping_array = updateMappingArray(mapping_array, value, bindName[i], i, mapping);					
						}
						catch (Exception name) {
							// System.out.println("ERROR: " + name);
							// CASE 0.A: json_nav = first argument
							if(i==0){
								mapping.put(bindName[i], "UNDEF");
								mapping_array.add(mapping); // the ArrayList has a size=1				
							}
							// CASE 0.B: json_nav = next arguments
							else {
								for (int k=0; k<mapping_array.size(); k++){
									mapping_array.get(k).put(bindName[i], "UNDEF");
								}
							}
						}
					}
					// Add all the mappings relative to the result rb to the MappingSet to return
					for (int k=0; k<mapping_array.size(); k++){
						ms.addMapping(mapping_array.get(k));
					}
				}
				// json = null
				else {
					for (int i = 0; i < bindName.length; i++) {
						mapping.put(bindName[i], "UNDEF");
						ms.addMapping(mapping);
					}				
				}
			}
			int[] stats = {repeated_values, total_values};
			Experiments.REPEATED_CALLS.add(stats);
		}
		finally
		{
			// QueryExecution objects should be closed to free any system resources 
			qexec.close();
			if(!FUSEKI_ENABLED) {
				// dataset.close();
			}
		}
		return ms;	
	}
	
	/*
	 * FUNCTION: One mapping after the other, map the JSON data from the API with the RDF data from the complete sparql query
	 * @param {HashMap<String, Object>} parsedQuery
	 * @param {ArrayList<GetJSONStrategy>} strategy
	 * @param {ArrayList<HashMap<String, String>>} params
	 * @param {int} limit
	 * @return {MappingSet}
	 */
	public MappingSet execQueryPipeURL(HashMap<String, Object> parsedQuery, 
			ArrayList<GetJSONStrategy> strategy, ArrayList<HashMap<String, String>> params, int limit) 
			throws JSONException, Exception {
		String firstQuery = retrieve_firstQuery (parsedQuery, params.get(0));
		// System.out.println(firstQuery);
		Boolean distinctQuery = retrieve_distinct((String)parsedQuery.get("SELECT"));
		String[] bindName = (String[])parsedQuery.get("ALIAS");
		String[] jpath = (String[])parsedQuery.get("PATH");
		Query query = QueryFactory.create(firstQuery);
		if(!FUSEKI_ENABLED) {
			dataset = TDBFactory.createDataset(this.TDBdirectory);
			qexec = QueryExecutionFactory.create(query, dataset);
		}
		else {
			qexec = QueryExecutionFactory.sparqlService("http://localhost:3030/ds", query);
		}
		MappingSet ms = new MappingSet();
		ArrayList<String> ms_varnames = new ArrayList<String>();
		try {
			// Assumption: it's a SELECT query.
			ResultSet rs = qexec.execSelect() ;
			// Materialize the results to be able to free the system resources for the next query executions
		    rs = ResultSetFactory.copyResults(rs) ;
			// QueryExecution objects should be closed to free any system resources 
		    qexec.close();
		    
			List<String> vars_name =  rs.getResultVars();
			for (String vn: vars_name) {
				ms_varnames.add(vn);
			}
			for (String bn: bindName) {
				ms_varnames.add(bn);
			}
			// The order of results is undefined.
			while (rs.hasNext() && this.mappingCount < limit) {
				MappingSet ms_temp = new MappingSet();
				ms_temp.set_var_names(ms_varnames);
				
				QuerySolution rb = rs.nextSolution() ;
				HashMap<String, String> mapping = mappQuerySolution(rb, vars_name);
				String url_req = ApiWrapper.insertValuesURL((String)parsedQuery.get("URL"), rb, params.get(0).get("replace_string"));
				Object json = null;
				try {
					long start = System.nanoTime();
					json = this.apiOptimizer.retrieve_json(url_req, params.get(0), strategy.get(0));
					long stop = System.nanoTime();
					this.apiOptimizer.timeApi += (stop - start);
				}
				catch (Exception name) {
					// System.out.println("ERROR: " + name);
					for (int i = 0; i < bindName.length; i++) {
						mapping.put(bindName[i], "UNDEF");
					}
				}
				if (json != null) {
					ArrayList<HashMap<String, String>> mapping_array = new ArrayList<HashMap<String, String>>();
					for (int i = 0; i < bindName.length; i++) {
						try {				
							Object value = JsonPath.parse(json).read(jpath[i]);
							mapping_array = updateMappingArray(mapping_array, value, bindName[i], i, mapping);									
						}
						catch (Exception name) {
							// System.out.println("ERROR: " + name);
							// CASE 0.A: json_nav = first argument
							if(i==0){
								mapping.put(bindName[i], "UNDEF");
								mapping_array.add(mapping); // the ArrayList has a size=1				
							}
							// CASE 0.B: json_nav = next arguments
							else {
								for (int k=0; k<mapping_array.size(); k++){
									mapping_array.get(k).put(bindName[i], "UNDEF");
								}
							}
						}
					}
					// Add all the mappings relative to the result rb to the MappingSet to return
					for (int k=0; k<mapping_array.size(); k++){
						ms_temp.addMapping(mapping_array.get(k));
					}
				}
				// Recursive condition is that the LAST section of parsedQuery includes another API service section
				String api_url_string = " +SERVICE +<([\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+)> *\\{ *\\( *(\\$.*$)";	
				Pattern pattern_variables = Pattern.compile(api_url_string);
				Matcher m = pattern_variables.matcher(" " + (String) parsedQuery.get("LAST"));
				String recursive_query_string = ((String) parsedQuery.get("PREFIX")) + ((String) parsedQuery.get("SELECT")) + ms_temp.serializeAsValues() + (String) parsedQuery.get("LAST") + (String) parsedQuery.get("OPTIONS");
				if (m.find()) {
					strategy.get(1).set_params(params.get(1));
					ms_temp = execQueryPipeURL(SPARQLSonParser.parseSPARQLSonQuery(recursive_query_string, false), 
							new ArrayList<GetJSONStrategy>(strategy.subList(1, strategy.size())),
							new ArrayList<HashMap<String,String>>(params.subList(1, params.size())),
							limit);
				}
				else {
					ms_temp = mappPostBindQuery(recursive_query_string, limit);
				}
				ms.set_var_names(ms_temp.var_names);
				if(distinctQuery) {
					int ms_size = ms.mappings.size();
					ms.addDistinctMappingsFromMappingSet(ms_temp);
					// We reduce the mappingCount as the no-distinct mappings are not added
					this.mappingCount += ms.mappings.size() - ms_size - ms_temp.mappings.size();
				}
				else {
					for (int k=0; k<ms_temp.mappings.size(); k++){
						ms.addMapping(ms_temp.mappings.get(k));
					}
				}
			}
		}
		finally
		{
			if(!FUSEKI_ENABLED) {
				// dataset.close();
			}
		}
		return ms;
	}
	
	/*
	 * FUNCTION: Retrieve a SELECT DISTINCT query
	 * @param {HashMap<String, Object>} parsedQuery
	 * @param {HashMap<String, String>} params
	 * @param {String} selectQuery
	 * @return {Boolean}
	 */
	public Boolean retrieve_distinct(String selectQuery) {
		String regex = "SELECT DISTINCT";
		Pattern pattern_variables = Pattern.compile(regex);
		Matcher m = pattern_variables.matcher(selectQuery);
		if (m.find()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/*
	 * FUNCTION: Get the first query to execute before mapping the JSON values
	 * @param {HashMap<String, Object>} parsedQuery
	 * @param {HashMap<String, String>} params
	 * @param {int} limit
	 * @return {String}
	 */
	public String retrieve_firstQuery (HashMap<String, Object> parsedQuery, HashMap<String, String> params) {
		String firstQuery = (String) parsedQuery.get("PREFIX") + " SELECT * WHERE { " + (String) parsedQuery.get("FIRST") + " } ";
		// Optimize the parsed query to call the API a minimum amount of times
		if(params.containsKey("min_api_call") && params.get("min_api_call").equals("true")) {
			parsedQuery = this.apiOptimizer.minimizeAPICall(parsedQuery);
			if (parsedQuery.get("VARS").toString().length()>0) {
				firstQuery = (String) parsedQuery.get("PREFIX") + " SELECT DISTINCT " + parsedQuery.get("VARS") + " WHERE {" + (String) parsedQuery.get("FIRST") + "}";
			}
			else {
				firstQuery = (String) parsedQuery.get("PREFIX") + " SELECT * " + " WHERE {" + (String) parsedQuery.get("FIRST") + "}";
			}
		}
		return firstQuery;
	}
	
	/*
	 * FUNCTION: Transform a sparql QuerySolution into a mapping
	 * @param {QuerySolution} rb
	 * @param {List<String>} vars_name
	 * @return {HashMap<String, String>}
	 */
	public HashMap<String, String> mappQuerySolution(QuerySolution rb, List<String> vars_name) {
		HashMap<String, String> mapping = new HashMap<String, String>();
		for(String var: vars_name) {
			if (rb.contains(var))
			{
				if (rb.get(var).isLiteral())
				{
					if(!rb.get(var).asLiteral().getDatatypeURI().equals("http://www.w3.org/2001/XMLSchema#string") && 
					   !rb.get(var).asLiteral().getDatatypeURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString")) {
						mapping.put(var, rb.get(var).asLiteral().getString());
					}
					else {
						if (!rb.get(var).asLiteral().getLanguage().equals("")) {
							mapping.put(var, "\"" + rb.get(var).asLiteral().getValue().toString() + "\"@" + rb.get(var).asLiteral().getLanguage());
						}
						// Fixed escape issue
						else {
							mapping.put(var, "\"" + StringEscapeUtils.escapeJava(rb.get(var).asLiteral().getValue().toString()) + "\"");
						}
					}
				}
				else 
				{
					mapping.put(var, "<" + rb.get(var).toString() + ">");
				}
			}
			else {
				mapping.put(var, "UNDEF");
			}
		}
		return mapping;
	}
	
	/*
	 * FUNCTION: Serialize a JSONObject value
	 * @param {Object} value
	 * @return {String}
	 */
	public static String serializeValue(Object value) {
		for (Class c: no_quote_types) {
			if (value.getClass().equals(c)) {
				return value.toString();
			}
		}
		return "\"" + StringEscapeUtils.escapeJava(value.toString().replace('\n', ' ')) + "\"";
	}
	
	/*
	 * FUNCTION: Update a mapping_array by mapping a new jsonValue
	 * @param {ArrayList<HashMap<String, String>>} mapping_array
	 * @param {Object} jsonValue
	 * @param {String} bindName
	 * @param {int} bindName_index
	 * @param {HashMap<String, String>} initial_mapping
	 * @return {ArrayList<HashMap<String, String>>}
	 */
	public ArrayList<HashMap<String, String>> updateMappingArray(ArrayList<HashMap<String, String>> mapping_array, Object jsonValue, String bindName, int bindName_index, HashMap<String, String> initial_mapping) throws Exception {
		int mapping_array_size = mapping_array.size();
		// CASE 1: value.class = Array of Elements
		if (jsonValue.getClass().equals(net.minidev.json.JSONArray.class)){
			if(((net.minidev.json.JSONArray)jsonValue).isEmpty()) {
				throw new NullPointerException("Empty Array");
			}
			else {
				for (int j=0; j<((net.minidev.json.JSONArray)jsonValue).size(); j++){		
					// CASE 1.A: json_nav = first argument
					if(bindName_index==0){
						Object mapping_clone = initial_mapping.clone();
						mapping_array.add((HashMap<String, String>)mapping_clone); // I initiate by cloning the mapping I had built into all the mapping_array mappings
						mapping_array.get(mapping_array.size()-1).put(bindName, serializeValue(((net.minidev.json.JSONArray)jsonValue).get(j))); // I add to the mapping_array mappings the relative JSON of the JSONArray
					}
					// CASE 1.B: json_nav = next arguments
					else {
						// I assign to each element of mapping_array the first value of the new argument
						if(j==0){
							for (int k=0; k<mapping_array.size(); k++){
								mapping_array.get(k).put(bindName, serializeValue(((net.minidev.json.JSONArray)jsonValue).get(j))); // I add to the mapping_array mappings the relative JSON of the JSONArray
							}
						}
						// For each next values, I first "duplicate" the original mapping_array and then assign the value to the duplicate
						else {
							for (int k=0; k<mapping_array_size; k++){
								Object mapping_clone = mapping_array.get(k).clone();
								mapping_array.add((HashMap<String, String>)mapping_clone);
								mapping_array.get(mapping_array.size()-1).put(bindName, serializeValue(((net.minidev.json.JSONArray)jsonValue).get(j)));
							}
						}
					}
				}
			}
		}
		// CASE 2: value.class = Single Element
		else {
			// CASE 2.A: json_nav = first argument
			if(bindName_index==0){
				initial_mapping.put(bindName, serializeValue(jsonValue));
				mapping_array.add(initial_mapping); // the ArrayList has a size=1				
			}
			// CASE 2.B: json_nav = next arguments
			else {
				for (int k=0; k<mapping_array.size(); k++){
					mapping_array.get(k).put(bindName, serializeValue(jsonValue));
				}
			}
		}
		return mapping_array;
	}
	
	/*
	 * FUNCTION: Transform a sparql queryString into a MappingSet
	 * @param {String} queryString
	 * @param {int} limit
	 * @return {MappingSet}
	 */
	public MappingSet mappPostBindQuery(String queryString, int limit) {
		// System.out.println(queryString);
		Query query = QueryFactory.create(queryString);
		if(!FUSEKI_ENABLED) {
			dataset = TDBFactory.createDataset(this.TDBdirectory);
			qexec = QueryExecutionFactory.create(query, dataset);
		}
		else {
			qexec = QueryExecutionFactory.sparqlService("http://localhost:3030/ds", query);
		}
		MappingSet ms = new MappingSet();
		ArrayList<String> ms_varnames = new ArrayList<String>();
		try {
			// Assumption: it's a SELECT query.
			ResultSet rs = qexec.execSelect() ;
		    rs = ResultSetFactory.copyResults(rs) ;
			// QueryExecution objects should be closed to free any system resources 
		    qexec.close();
		    
			List<String> vars_name =  rs.getResultVars();
			for (String vn: vars_name) {
				ms_varnames.add(vn);
			}
			ms.set_var_names(ms_varnames);
			// The order of results is undefined.
			while (rs.hasNext()) {				
				QuerySolution rb = rs.nextSolution() ;	
				// System.out.println("--DEBUG-- "+rb);
				HashMap<String, String> mapping = mappQuerySolution(rb, vars_name);
				ms.addMapping(mapping);
				this.mappingCount += 1;
			}
		}
		finally
		{
			if(!FUSEKI_ENABLED) {
				// dataset.close();
			}
		}
		return ms;
	}
	
	/*
	 * FUNCTION: Execute a sparql query serializing a MappingSet
	 * @param {String} selectSection
	 * @param {String} bodySection
	 * @param {MappingSet} ms
	 * @return {ResultSet}
	 */
	public ResultSet execPostBindQuery(String selectSection, String bodySection, MappingSet ms) {
		ResultSet rs = null;
		String queryString = selectSection + ms.serializeAsValues() + bodySection;
		if(!FUSEKI_ENABLED) {
			dataset = TDBFactory.createDataset(this.TDBdirectory);
			// System.out.println(queryString);
			qexec = QueryExecutionFactory.create(queryString, dataset);
		}
		else {
			qexec = QueryExecutionFactory.sparqlService("http://localhost:3030/ds", queryString);
		}
		try {
			rs = qexec.execSelect() ;
			if (LOG) {
				printResultSet(rs);
			}
		}
		finally
		{
			// QueryExecution objects should be closed to free any system resources 
			// qexec.close();
			if (!FUSEKI_ENABLED) {
				// dataset.close();
			}
		}
		return rs;
	}
	
	/*
	 * FUNCTION: Print the results in a JSON format
	 * @param {ResultSet} rs
	 * @return {}
	 */
	public void printResultSet(ResultSet rs) {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("results", new JSONArray());
		jsonResponse.put("vars", rs.getResultVars());
		this.mappingCount = 0;
		for ( ; rs.hasNext() ; ) {
			JSONObject mappingToJson = new JSONObject();
			QuerySolution rb = rs.nextSolution() ;
			// Get title - variable names do not include the '?' (or '$')
			Iterator<String> names = rb.varNames();
			while (names.hasNext()) {	
				String v = names.next();
				mappingToJson.put(v, StringEscapeUtils.unescapeJson(rb.get(v).toString()));
			}
			((JSONArray)jsonResponse.getJSONArray("results")).put(mappingToJson);
			this.mappingCount++;
		}
		System.out.println(jsonResponse.toString());
	}
		
}
