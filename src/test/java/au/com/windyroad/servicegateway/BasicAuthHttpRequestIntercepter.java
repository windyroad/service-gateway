package au.com.windyroad.servicegateway;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class BasicAuthHttpRequestIntercepter implements
		ClientHttpRequestInterceptor {

	private String password;
	private String name;

	public BasicAuthHttpRequestIntercepter(String name, String password) {
		this.name = name;
		this.password = password;
	}

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body,
			ClientHttpRequestExecution execution) throws IOException {
		String auth = name + ":" + password;
		byte[] encodedAuth = org.apache.commons.codec.binary.Base64
				.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
		request.getHeaders().add("Authorization",
				"Basic " + new String(encodedAuth));
		return execution.execute(request, body);
	}

}
