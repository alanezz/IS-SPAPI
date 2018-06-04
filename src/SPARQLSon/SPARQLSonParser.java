package SPARQLSon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SPARQLSonParser {
	/*
	 * FUNCTION: Parse a SPARQL query into the sections PREFIX, SELECT, FIRST, URL, PATH, ALIAS, LAST
	 * @param {String} queryString
	 * @param {Boolean} replace
	 * @return {HashMap<String, Object>}
	 */
	public static HashMap<String, Object> parseSPARQLSonQuery(String queryString, boolean replace) {
		String[] firstParse = getSelectSection(queryString, replace);
		HashMap<String, Object> querySections = getAPIServiceSection(firstParse[2]);
		querySections.put("PREFIX", firstParse[0]);
		querySections.put("SELECT", firstParse[1]);
		return querySections;
	}
	
	/*
	 * FUNCTION: Get the Prefix, Select, and PostSelect parts of the query 
	 * @param {String} queryString
	 * @param {Boolean} replace
	 * @return {String[]}
	 */
	public static String[] getSelectSection(String queryString, boolean replace) {
		// Eliminate the multi-spaces before and after the query
		String newQueryString = queryString.trim();
		if(replace) {
			//Eliminate the line breaks which are not quoted
			newQueryString = newQueryString.replaceAll("\\\\n(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)", " ");
			//Eliminate the multi-spaces which are not quoted into the request
			newQueryString = newQueryString.replaceAll("\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)", " ");
		}
		// Separate what is before the WHERE from what comes after
		int cutIndex = newQueryString.indexOf('{');
		String preSelectSection = newQueryString.substring(0, cutIndex + 1);
		String postSelectSection = newQueryString.substring(cutIndex + 1, newQueryString.length());
		
		// Separate the PREFIX section from the SELECT section
		String prefixSection = "";
		String selectSection = "";
		String navigation_string = "(.*)(SELECT.*)$";
		Pattern pattern_variables = Pattern.compile(navigation_string);
		Matcher m = pattern_variables.matcher(preSelectSection);
		if (m.find()) {
			prefixSection = m.group(1);
			selectSection = m.group(2);
		}
		else {
			// System.out.println("ERROR : No select querry");
		}
		String[] retArray = {prefixSection, selectSection, postSelectSection};
		return retArray;
	}
	
	/*
	 * FUNCTION: Get the sections of the PostSelect part of the query which are: FIRST, URL, PATH, ALIAS, LAST
	 * @param {String} postSelectSection
	 * @return {HashMap<String, Object>}
	 */
	public static HashMap<String, Object> getAPIServiceSection(String postSelectSection) {
		/* 
		 * Regex to match the format of a SERVICE call to an API:
		 * This format is: FIRST SERVICE <URL> {($.PATH1, $.PATH2) AS (ALIAS1, ALIAS 2)} LAST
		 * Matched groups are:	Group 1: URL 	Group 2: $.PATH1, $.PATH2) AS (ALIAS1, ALIAS 2)} LAST
		 * regexr.com:  +SERVICE +<([\w\-\%\?\&\=\.\{\}\:\/\,]+)> *\{ *\( *(\$.*$)
		 */													// LAST
		String api_url_string = " +SERVICE +<([\\w\\-\\%\\?\\&\\=\\.\\{\\}\\:\\/\\,]+)> *\\{ *\\( *(\\$.*$)";	
		Pattern pattern_variables = Pattern.compile(api_url_string);
		Matcher m1 = pattern_variables.matcher(postSelectSection);
		HashMap<String, Object> bindSections = new HashMap<String, Object>();
		// Divide the query to keep the FIRST section, which comes before the word 'SERVICE'
		String[] dividedQuery = postSelectSection.split(api_url_string, 2);
		bindSections.put("FIRST", dividedQuery[0]);
		
		if (m1.find()) {
			bindSections.put("URL", m1.group(1));
			/*
			 * Regex to match the sections which come after the word 'AS' which are ALIAS and LAST
			 * Matched groups are: Group 1: ALIAS1, ALIAS 2		Group 2: LAST
			 * regexr.com: \) *AS *\(((?: *\?[\d\w]+ *,* *)*)\) *\}(.*)$
			 */
			String post_api_url_string = "\\) *AS *\\(((?: *\\?[\\d\\w]+ *,* *)*)\\) *\\} *\\.*(.*)$";
			
			// Divide the Group 2 of m1 to keep only the PATH section of the query
			dividedQuery = m1.group(2).split(post_api_url_string, 2);
			String json_nav_string = dividedQuery[0];
			// Split the different paths which are separated by the unquoted chain of character: , $
			String[] json_navs = json_nav_string.split(", *\\$(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)");
			for (int i=1 ; i<json_navs.length ; i++) {
				json_navs[i] = "$"+json_navs[i];
			}
			bindSections.put("PATH", json_navs);
			
			pattern_variables = Pattern.compile(post_api_url_string);
			Matcher m2 = pattern_variables.matcher(postSelectSection);
			if (m2.find()) {
				String aliases_string = m2.group(1).trim();
				// Split the different aliases which are separated by a ,
				String[] aliases = aliases_string.split(", *");
				for (int i = 0; i < aliases.length; i++) {
					// Eliminate the ? which is in front of the alias
					aliases[i] = aliases[i].substring(1);
				}
				bindSections.put("ALIAS", aliases);
				String post_aliases_string = m2.group(2).trim();
				String options_regex = "(.*\\})([^\\}]*$)";
				String options_section = "";
				pattern_variables = Pattern.compile(options_regex);
				Matcher m = pattern_variables.matcher(post_aliases_string);
				if (m.find()) {
					bindSections.put("LAST", m.group(1));
					bindSections.put("OPTIONS", m.group(2));
				}
				/*
				 * Show the distinct sections of the query
				 */
				// System.out.println("Request to the URL: " + bindSections.get("URL"));
				// System.out.println("FIRST: "+ bindSections.get("FIRST"));
				// System.out.println("PATH: "+ json_nav_string);
				// System.out.println("ALIAS: "+ aliases_string);
				// System.out.println("LAST: "+ bindSections.get("LAST"));
			}
		}
		return bindSections;
	}
}

