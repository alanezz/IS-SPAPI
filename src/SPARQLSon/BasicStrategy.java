package SPARQLSon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

public class BasicStrategy implements GetJSONStrategy {

	@Override
	public String readURL(String url) throws IOException {
		BufferedReader reader = null;
	    try {
	        URL url_object = new URL(url);
	        reader = new BufferedReader(new InputStreamReader(url_object.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        return buffer.toString();
	    } 
	    finally {
	        if (reader != null)
	            reader.close();
	    }
	}

	@Override
	public void set_params(HashMap<String, String> params) {
		return;
	}
	
}
