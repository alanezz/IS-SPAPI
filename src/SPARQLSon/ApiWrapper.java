package SPARQLSon;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.QuerySolution;
import org.json.*;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;



public class ApiWrapper {
	
	
	public static Object getJSON(String urlString, HashMap<String,String> params, GetJSONStrategy strategy) throws JSONException, Exception {
		JSONObject json = new JSONObject(strategy.readURL(urlString));
		Object document = Configuration.defaultConfiguration().jsonProvider().parse(json.toString());
		return document;
	}
	
	// Function to insert a part of the result of the SPARQL request into the API request
	public static String insertValuesURL(String apiUrlRequest, QuerySolution rb, String replace_string) {
		String url = apiUrlRequest;
		Pattern pattern_variables = Pattern.compile("\\{(\\w*?)\\}"); // "\\{" allows to match { as \ and { are meta characters
		Matcher m = pattern_variables.matcher(url);
		while (m.find()) {
		    String s = m.group(1); // s is equal to the first subsequence of the url which matches the pattern 
		    String value = rb.get(s).asLiteral().getValue().toString().replaceAll("[\\ ]+", replace_string);
		    url = m.replaceFirst(value);
		    m = pattern_variables.matcher(url);
		}
		return url;
	}
	


}

