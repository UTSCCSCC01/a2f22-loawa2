package ca.utoronto.utm.mcs;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * the microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */

import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.OutputStream;    // Also given to you to send back your response
import java.util.HashMap;

public class RequestRouter implements HttpHandler {

	private HttpClient httpClient;
	public HashMap<Integer, String> errorMap;

	public RequestRouter() {
		httpClient = HttpClient.newHttpClient();
		errorMap = new HashMap<>();
		errorMap.put(200, "OK");
		errorMap.put(400, "BAD REQUEST");
		errorMap.put(401, "UNAUTHORIZED");
		errorMap.put(404, "NOT FOUND");
		errorMap.put(405, "METHOD NOT ALLOWED");
		errorMap.put(409, "CONFLICT");
		errorMap.put(500, "INTERNAL SERVER ERROR");
	}

	@Override
	public void handle(HttpExchange r) throws IOException {
		try {
			switch (r.getRequestMethod()) {
				case "GET":
					this.handleGet(r);
					break;
				case "POST":
					this.handlePut(r);
					break;
				case "PUT":
					this.handlePut(r);
					break;
				case "PATCH":
					this.handlePut(r);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleGet(HttpExchange r) throws IOException, InterruptedException, URISyntaxException, JSONException {
		String getUri;
		HttpRequest getRequest;
		HttpResponse<String> getResponse;
		// substring(11) to remove /apigateway from url
		String urlAccessed = r.getRequestURI().toString().substring(11);
		if (urlAccessed.startsWith("/location/")) {
			getUri = String.format("http://locationmicroservice:8000%s", urlAccessed);
			getRequest = HttpRequest.newBuilder().uri(new URI(getUri)).GET().build();
			getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(getResponse.body());
			sendResponse(r, body, getResponse.statusCode());
		} else {
			sendStatus(r, 404);
		}
	}

	public void handlePost(HttpExchange r) throws IOException {
		System.out.println("Handle post");
	}

	public void handlePut(HttpExchange r) throws IOException {
		System.out.println("Handle put");
	}

	public void handlePatch(HttpExchange r) throws IOException {
		System.out.println("Handle patch");
	}

	public void writeOutputStream(HttpExchange r, String response) throws IOException {
		OutputStream os = r.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	public void sendResponse(HttpExchange r, JSONObject obj, int statusCode) throws JSONException, IOException {
		obj.put("status", errorMap.get(statusCode));
		String response = obj.toString();
		r.sendResponseHeaders(statusCode, response.length());
		writeOutputStream(r, response);
	}

	public void sendStatus(HttpExchange r, int statusCode) throws JSONException, IOException {
		JSONObject res = new JSONObject();
		res.put("status", errorMap.get(statusCode));
		String response = res.toString();
		r.sendResponseHeaders(statusCode, response.length());
		this.writeOutputStream(r, response);
	}
}
