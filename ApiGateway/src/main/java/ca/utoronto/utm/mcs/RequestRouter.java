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
					this.handlePost(r);
					break;
				case "PUT":
					this.handlePut(r);
					break;
				case "PATCH":
					this.handlePatch(r);
					break;
				case "DELETE":
					this.handleDelete(r);
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

		String urlAccessed = r.getRequestURI().toString();

		if (urlAccessed.startsWith("/location/")) {
			getUri = String.format("http://locationmicroservice:8000%s", urlAccessed);
			getRequest = HttpRequest.newBuilder().uri(new URI(getUri)).GET().build();
			getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(getResponse.body());
			sendResponse(r, body, getResponse.statusCode());
		}
		else if (urlAccessed.startsWith("/user/")){
			getUri = String.format("http://usermicroservice:8000%s", urlAccessed);
			getRequest = HttpRequest.newBuilder().uri(new URI(getUri)).GET().build();
			getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(getResponse.body());
			sendResponse(r, body, getResponse.statusCode());
		}
		else if (urlAccessed.startsWith("/trip/")){
			getUri = String.format("http://tripinfomicroservice:8000%s", urlAccessed);
			getRequest = HttpRequest.newBuilder().uri(new URI(getUri)).GET().build();
			getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(getResponse.body());
			sendResponse(r, body, getResponse.statusCode());
		}
		else {
			sendStatus(r, 404);
		}
	}

	public void handlePost(HttpExchange r) throws IOException, InterruptedException, URISyntaxException, JSONException  {
		String postUri;
		HttpRequest postRequest;
		HttpResponse<String> postResponse;

		HttpRequest.BodyPublisher bodyRequest = HttpRequest.BodyPublishers.ofByteArray(r.getRequestBody().readAllBytes());
		String urlAccessed = r.getRequestURI().toString();

		if (urlAccessed.startsWith("/location/")) {
			postUri = String.format("http://locationmicroservice:8000%s", urlAccessed);
			postRequest = HttpRequest.newBuilder().uri(new URI(postUri)).method("POST",bodyRequest ).build();
			postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(postResponse.body());
			sendResponse(r, body, postResponse.statusCode());
		}
		else if (urlAccessed.startsWith("/user/")){
			postUri = String.format("http://usermicroservice:8000%s", urlAccessed);
			postRequest = HttpRequest.newBuilder().uri(new URI(postUri)).method("POST",bodyRequest ).build();
			postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(postResponse.body());
			sendResponse(r, body, postResponse.statusCode());
		}
		else if (urlAccessed.startsWith("/trip/")){
			postUri = String.format("http://tripinfomicroservice:8000%s", urlAccessed);
			postRequest = HttpRequest.newBuilder().uri(new URI(postUri)).method("POST",bodyRequest ).build();
			postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(postResponse.body());
			sendResponse(r, body, postResponse.statusCode());
		}
		else {
			sendStatus(r, 404);
		}
	}

	public void handlePut(HttpExchange r) throws IOException, InterruptedException, URISyntaxException, JSONException {
		String putUri;
		HttpRequest putRequest;
		HttpResponse<String> putResponse;

		HttpRequest.BodyPublisher bodyRequest = HttpRequest.BodyPublishers.ofByteArray(r.getRequestBody().readAllBytes());
		String urlAccessed = r.getRequestURI().toString();

		if (urlAccessed.startsWith("/location/")) {
			putUri = String.format("http://locationmicroservice:8000%s", urlAccessed);
			putRequest = HttpRequest.newBuilder().uri(new URI(putUri)).method("PUT",bodyRequest ).build();
			putResponse = httpClient.send(putRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(putResponse.body());
			sendResponse(r, body, putResponse.statusCode());
		}
		else if (urlAccessed.startsWith("/user/")){
			putUri = String.format("http://usermicroservice:8000%s", urlAccessed);
			putRequest = HttpRequest.newBuilder().uri(new URI(putUri)).method("PUT",bodyRequest ).build();
			putResponse = httpClient.send(putRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(putResponse.body());
			sendResponse(r, body, putResponse.statusCode());
		}
		else if (urlAccessed.startsWith("/trip/")){
			putUri = String.format("http://tripinfomicroservice:8000%s", urlAccessed);
			putRequest = HttpRequest.newBuilder().uri(new URI(putUri)).method("PUT",bodyRequest ).build();
			putResponse = httpClient.send(putRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(putResponse.body());
			sendResponse(r, body, putResponse.statusCode());
		}
		else {
			sendStatus(r, 404);
		}
	}

	public void handlePatch(HttpExchange r) throws IOException, InterruptedException, URISyntaxException, JSONException {
		String patchUri;
		HttpRequest patchRequest;
		HttpResponse<String> patchResponse;

		HttpRequest.BodyPublisher bodyRequest = HttpRequest.BodyPublishers.ofByteArray(r.getRequestBody().readAllBytes());
		String urlAccessed = r.getRequestURI().toString();

		if (urlAccessed.startsWith("/location/")) {
			patchUri = String.format("http://locationmicroservice:8000%s", urlAccessed);
			patchRequest = HttpRequest.newBuilder().uri(new URI(patchUri)).method("PATCH",bodyRequest ).build();
			patchResponse = httpClient.send(patchRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(patchResponse.body());
			sendResponse(r, body, patchResponse.statusCode());
		}
		else if (urlAccessed.startsWith("/user/")){
			patchUri = String.format("http://usermicroservice:8000%s", urlAccessed);
			patchRequest = HttpRequest.newBuilder().uri(new URI(patchUri)).method("PATCH",bodyRequest ).build();
			patchResponse = httpClient.send(patchRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(patchResponse.body());
			sendResponse(r, body, patchResponse.statusCode());
		}
		else if (urlAccessed.startsWith("/trip/")){
			patchUri = String.format("http://tripinfomicroservice:8000%s", urlAccessed);
			patchRequest = HttpRequest.newBuilder().uri(new URI(patchUri)).method("PATCH",bodyRequest ).build();
			patchResponse = httpClient.send(patchRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(patchResponse.body());
			sendResponse(r, body, patchResponse.statusCode());
		}
		else {
			sendStatus(r, 404);
		}
	}

	public void handleDelete(HttpExchange r) throws IOException, InterruptedException, URISyntaxException, JSONException {
		String deleteUri;
		HttpRequest deleteRequest;
		HttpResponse<String> deleteResponse;

		HttpRequest.BodyPublisher bodyRequest = HttpRequest.BodyPublishers.ofByteArray(r.getRequestBody().readAllBytes());
		String urlAccessed = r.getRequestURI().toString();

		if (urlAccessed.startsWith("/location/")) {
			deleteUri = String.format("http://locationmicroservice:8000%s", urlAccessed);
			deleteRequest = HttpRequest.newBuilder().uri(new URI(deleteUri)).method("DELETE",bodyRequest ).build();
			deleteResponse = httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
			JSONObject body = new JSONObject(deleteResponse.body());
			sendResponse(r, body, deleteResponse.statusCode());
		} else {
			sendStatus(r, 404);
		}
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
