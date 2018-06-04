package SPARQLSon;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExecService {

	public static void main(String[] args) throws JSONException, Exception {
		// TODO Auto-generated method stub

		String TDBdirectory = "/put/your/directory/here";
		DatabaseWrapper dbw = new DatabaseWrapper(TDBdirectory);
		JSONObject input = new JSONObject(args[0]);
		JSONArray strategies = input.getJSONObject("values").getJSONArray("strategies");
		ArrayList<GetJSONStrategy> strategies_query = new ArrayList<>();
		ArrayList<HashMap<String, String>> params_query = new ArrayList<HashMap<String,String>>();
		if (strategies.length() > 0) {
			for (Object strategy : strategies) {
				// System.out.println((JSONObject) strategy);
				if (((JSONObject) strategy).getString("strategy").equals("1")) {
					GetJSONStrategy strategy_basic = new BasicStrategy();
					strategies_query.add(strategy_basic);
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("replace_string", "_");
					params_query.add(params);
				}
				else if (((JSONObject) strategy).getString("strategy").equals("2")) {
					GetJSONStrategy strategy_oauth = new OAuthStrategy();
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("replace_string", "_");
					params.put("consumerKey", ((JSONObject) strategy).getString("ck"));
					params.put("consumerSecret", ((JSONObject) strategy).getString("cs"));
					params.put("token", ((JSONObject) strategy).getString("tk"));
					params.put("tokenSecret", ((JSONObject) strategy).getString("ts"));
					strategies_query.add(strategy_oauth);
					params_query.add(params);
				}
			}
			dbw.evaluateSPARQLSon(input.getJSONObject("values").getString("query"),
					strategies_query,
					params_query);
		}
		else {
			dbw.execQuery(input.getJSONObject("values").getString("query"));
		}


	}

}
