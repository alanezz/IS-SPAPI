package SPARQLSon;


import java.util.HashMap;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class OAuthStrategy implements GetJSONStrategy {

	OAuthService service;
	Token accessToken;
	String consumerKey;
	String consumerSecret;
	String token;
	String tokenSecret;

	public OAuthStrategy() {
		
	}

	public OAuthRequest createOAuthRequest(String url) {
		OAuthRequest request = new OAuthRequest(Verb.GET, url);
		return request;
	}

	private String sendRequestAndGetResponse(OAuthRequest request) {
		// System.out.println("Service: " + this.service);
		// System.out.println("Querying " + request.getSanitizedUrl() + " ...");
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();
		// System.out.println(response.getBody());
		return response.getBody();
	}

	public String readURL(String url) {
		// System.out.println("Read URL: " + url);
		OAuthRequest request = createOAuthRequest(url);
		return sendRequestAndGetResponse(request);
	}

	@Override
	public void set_params(HashMap<String, String> params) {
		this.consumerKey = params.get("consumerKey");
		this.consumerSecret = params.get("consumerSecret");
		this.token = params.get("token");
		this.tokenSecret = params.get("tokenSecret");
		
		this.service =
				new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(this.consumerKey)
				.apiSecret(this.consumerSecret).build();
		this.accessToken = new Token(this.token, this.tokenSecret);
	}

}
