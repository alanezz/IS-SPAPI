package SPARQLSon;

import java.io.IOException;
import java.util.HashMap;

public interface GetJSONStrategy {
	
	public String readURL(String url) throws IOException;
	public void set_params(HashMap<String, String> params);
	
}
