package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;
 
public class AppTest {
    private HttpClient httpClient;
    @BeforeEach
    public void init(){
        httpClient = HttpClient.newHttpClient();
    }
    @Test
    public void getNearbyDriverPass() throws URISyntaxException, IOException, InterruptedException, JSONException {
        String passengerUid = generateRandomId();
        String passengerPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", passengerUid, false);
        String driverUid = generateRandomId();
        String driverPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", driverUid, true);
        String userPutUri = "http://localhost:8000/location/user";
        String getUri = String.format("http://localhost:8000/location/nearbyDriver/%s?radius=%d", passengerUid, 10);

        int expectedStatus = 200;
        String expectedData = String.format("{\"street\":\"%s\",\"latitude\":%d,\"longitude\":%d}", "", 0, 0);

        HttpRequest passengerPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(passengerPutBody))
                .build();

        HttpRequest driverPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(driverPutBody))
                .build();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(getUri))
                .GET()
                .build();

        httpClient.send(passengerPutRequest, HttpResponse.BodyHandlers.ofString());
        httpClient.send(driverPutRequest, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        // Check if our newly added driver is in the list of nearby drivers
        JSONObject body = new JSONObject(getResponse.body());
        JSONObject drivers = body.getJSONObject("data");

        assertTrue(drivers.has(driverUid));
        assertEquals(expectedData, drivers.getString(driverUid));
        assertEquals(expectedStatus, getResponse.statusCode());
    }

    @Test
    public void getNearbyDriverFail() throws URISyntaxException, IOException, InterruptedException, JSONException {
        String expectedResponse = "{\"status\":\"BAD REQUEST\"}";
        int expectedStatus = 400;
        String getUri = "http://localhost:8000/location/nearbyDriver/";

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(getUri))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedStatus, getResponse.statusCode());
        assertEquals(expectedResponse, getResponse.body());
    }

    @Test
    public void getNavigationPass() throws URISyntaxException, IOException, InterruptedException {
        // TODO: FINISH THIS TEST
        fail();
    }

    @Test
    public void getNavigationFail() throws URISyntaxException, IOException, InterruptedException {
        // TODO: FINISH THIS TEST
        fail();
    }

    private String generateRandomId(){
        int min = 10000000;
        int max = 99999999;
        int rand = (int) ((Math.random() * max - min) + min);
        return String.format("u%d", rand);
    }
}
