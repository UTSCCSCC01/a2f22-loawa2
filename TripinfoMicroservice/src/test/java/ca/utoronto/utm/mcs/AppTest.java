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
    public void tripRequestPass() throws URISyntaxException, IOException, InterruptedException, JSONException {
        String passengerUid = generateRandomId();
        String passengerPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", passengerUid, false);
        String driverUid = generateRandomId();
        String driverPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", driverUid, true);
        String userPutUri = "http://apigateway:8000/location/user";

        String tripRequestUri = "http://apigateway:8000/trip/request";
        String tripRequestBody = String.format("{\"uid\":\"%s\", \"radius\":%d}", passengerUid, 10);
        int expectedStatus = 200;

        HttpRequest passengerPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(passengerPutBody))
                .build();

        HttpRequest driverPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(driverPutBody))
                .build();

        HttpRequest tripRequestPostRequest = HttpRequest.newBuilder()
                .uri(new URI(tripRequestUri))
                .POST(HttpRequest.BodyPublishers.ofString(tripRequestBody))
                .build();

        httpClient.send(passengerPutRequest, HttpResponse.BodyHandlers.ofString());
        httpClient.send(driverPutRequest, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> postResponse = httpClient.send(tripRequestPostRequest, HttpResponse.BodyHandlers.ofString());

        // Check if our newly added driver is in the list of nearby drivers
        JSONObject body = new JSONObject(postResponse.body());
        JSONArray drivers = body.getJSONArray("data");
        boolean contains = false;
        for (int i = 0; i < drivers.length(); i++){
            if (drivers.getString(i).equals(driverUid)) {
                contains = true;
            }
        }

        assertTrue(contains);
        assertEquals(expectedStatus, postResponse.statusCode());
    }

    @Test
    public void tripRequestFail() throws URISyntaxException, IOException, InterruptedException{
        String tripRequestUri = "http://apigateway:8000/trip/request";
        String tripRequestBody = String.format("{\"radius\":%d}", 10);
        String expectedResponse = "{\"status\":\"BAD REQUEST\"}";
        int expectedStatus = 400;

        HttpRequest tripRequestPostRequest = HttpRequest.newBuilder()
                .uri(new URI(tripRequestUri))
                .POST(HttpRequest.BodyPublishers.ofString(tripRequestBody))
                .build();

        HttpResponse<String> postResponse = httpClient.send(tripRequestPostRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedStatus, postResponse.statusCode());
        assertEquals(expectedResponse, postResponse.body());
    }

    @Test
    public void tripConfirmPass() throws URISyntaxException, IOException, InterruptedException {
        String passengerUid = generateRandomId();
        String passengerPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", passengerUid, false);
        String driverUid = generateRandomId();
        String driverPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", driverUid, true);
        String userPutUri = "http://apigateway:8000/location/user";

        String tripConfirmUri = "http://apigateway:8000/trip/confirm";
        String tripConfirmBody = String.format("{\"driver\":\"%s\", \"passenger\":\"%s\", \"startTime\":%d}", driverUid, passengerUid, 100);
        int expectedStatus = 200;

        HttpRequest passengerPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(passengerPutBody))
                .build();

        HttpRequest driverPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(driverPutBody))
                .build();

        HttpRequest tripConfirmPostRequest = HttpRequest.newBuilder()
                .uri(new URI(tripConfirmUri))
                .POST(HttpRequest.BodyPublishers.ofString(tripConfirmBody))
                .build();

        httpClient.send(passengerPutRequest, HttpResponse.BodyHandlers.ofString());
        httpClient.send(driverPutRequest, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> postResponse = httpClient.send(tripConfirmPostRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedStatus, postResponse.statusCode());
    }

    @Test
    public void tripConfirmFail() throws URISyntaxException, IOException, InterruptedException{
        String tripConfirmUri = "http://apigateway:8000/trip/confirm";
        String tripConfirmBody = String.format("{\"startTime\":%d}", 100);
        String expectedResponse = "{\"status\":\"BAD REQUEST\"}";
        int expectedStatus = 400;

        HttpRequest tripConfirmPostRequest = HttpRequest.newBuilder()
                .uri(new URI(tripConfirmUri))
                .POST(HttpRequest.BodyPublishers.ofString(tripConfirmBody))
                .build();

        HttpResponse<String> postResponse = httpClient.send(tripConfirmPostRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedStatus, postResponse.statusCode());
        assertEquals(expectedResponse, postResponse.body());
    }

    @Test
    public void patchTripPass() throws URISyntaxException, IOException, InterruptedException, JSONException {
        String passengerUid = generateRandomId();
        String passengerPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", passengerUid, false);
        String driverUid = generateRandomId();
        String driverPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", driverUid, true);
        String userPutUri = "http://apigateway:8000/location/user";

        String tripConfirmUri = "http://apigateway:8000/trip/confirm";
        String tripConfirmBody = String.format("{\"driver\":\"%s\", \"passenger\":\"%s\", \"startTime\":%d}", driverUid, passengerUid, 100);

        int expectedStatus = 200;
        String expectedResponse = "{\"status\":\"OK\"}";

        HttpRequest passengerPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(passengerPutBody))
                .build();

        HttpRequest driverPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(driverPutBody))
                .build();

        HttpRequest tripConfirmPostRequest = HttpRequest.newBuilder()
                .uri(new URI(tripConfirmUri))
                .POST(HttpRequest.BodyPublishers.ofString(tripConfirmBody))
                .build();

        httpClient.send(passengerPutRequest, HttpResponse.BodyHandlers.ofString());
        httpClient.send(driverPutRequest, HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> postResponse = httpClient.send(tripConfirmPostRequest, HttpResponse.BodyHandlers.ofString());

        JSONObject body = new JSONObject(postResponse.body());
        String tripId = body.getString("data");

        String patchTripUri = String.format("http://apigateway:8000/trip/%s", tripId);
        String patchTripBody = String.format("{\n" +
                "    \"distance\": %d,\n" +
                "    \"endTime\": %d,\n" +
                "    \"timeElapsed\": \"%s\",\n" +
                "    \"discount\": %f,\n" +
                "    \"totalCost\": %f,\n" +
                "    \"driverPayout\": %f\n" +
                "}", 100, 111, "00:15:00", 0.69, 69.69, 69.0);

        HttpRequest patchTripRequest = HttpRequest.newBuilder()
                .uri(new URI(patchTripUri))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(patchTripBody))
                .build();

        HttpResponse<String> patchResponse = httpClient.send(patchTripRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedStatus, patchResponse.statusCode());
        assertEquals(expectedResponse, patchResponse.body());
    }

    @Test
    public void patchTripFail() throws URISyntaxException, IOException, InterruptedException{
        int expectedStatus = 400;
        String expectedResponse = "{\"status\":\"BAD REQUEST\"}";

        String patchTripUri = String.format("http://apigateway:8000/trip/%s", "");
        String patchTripBody = String.format("{\n" +
                "    \"distance\": %d,\n" +
                "    \"endTime\": %d,\n" +
                "    \"timeElapsed\": \"%s\",\n" +
                "    \"discount\": %f,\n" +
                "    \"totalCost\": %f,\n" +
                "    \"driverPayout\": %f\n" +
                "}", 100, 111, "00:15:00", 0.69, 69.69, 69.0);

        HttpRequest patchTripRequest = HttpRequest.newBuilder()
                .uri(new URI(patchTripUri))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(patchTripBody))
                .build();

        HttpResponse<String> patchResponse = httpClient.send(patchTripRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedStatus, patchResponse.statusCode());
        assertEquals(expectedResponse, patchResponse.body());
    }

    @Test
    public void tripsForPassengerPass() throws URISyntaxException, IOException, InterruptedException, JSONException {
        String passengerUid = generateRandomId();
        String passengerPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", passengerUid, false);
        String driverUid = generateRandomId();
        String driverPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", driverUid, true);
        String userPutUri = "http://apigateway:8000/location/user";

        String tripConfirmUri = "http://apigateway:8000/trip/confirm";
        String tripConfirmBody = String.format("{\"driver\":\"%s\", \"passenger\":\"%s\", \"startTime\":%d}", driverUid, passengerUid, 100);
        int expectedStatus = 200;

        HttpRequest passengerPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(passengerPutBody))
                .build();

        HttpRequest driverPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(driverPutBody))
                .build();

        HttpRequest tripConfirmPostRequest = HttpRequest.newBuilder()
                .uri(new URI(tripConfirmUri))
                .POST(HttpRequest.BodyPublishers.ofString(tripConfirmBody))
                .build();

        httpClient.send(passengerPutRequest, HttpResponse.BodyHandlers.ofString());
        httpClient.send(driverPutRequest, HttpResponse.BodyHandlers.ofString());
        httpClient.send(tripConfirmPostRequest, HttpResponse.BodyHandlers.ofString());

        String getUri = String.format("http://apigateway:8000/trip/passenger/%s", passengerUid);
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(getUri))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        // Check if the passenger actually has the trip with the driver
        JSONObject body = new JSONObject(getResponse.body());
        JSONObject data = body.getJSONObject("data");
        JSONArray trips = data.getJSONArray("trips");
        boolean contains = false;
        for (int i = 0; i < trips.length(); i++){
            JSONObject trip = new JSONObject(trips.getString(i));
            if (trip.getString("driver").equals(driverUid)) {
                contains = true;
            }
        }

        assertTrue(contains);
        assertEquals(expectedStatus, getResponse.statusCode());
    }

    @Test
    public void tripForPassengerFail() throws URISyntaxException, IOException, InterruptedException {
        int expectedStatus = 400;
        String expectedResponse = "{\"status\":\"BAD REQUEST\"}";

        String getUri = String.format("http://apigateway:8000/trip/passenger/%s", "");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(getUri))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedResponse, getResponse.body());
        assertEquals(expectedStatus, getResponse.statusCode());
    }

    @Test
    public void tripsForDriverPass() throws URISyntaxException, IOException, InterruptedException, JSONException {
        String passengerUid = generateRandomId();
        String passengerPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", passengerUid, false);
        String driverUid = generateRandomId();
        String driverPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", driverUid, true);
        String userPutUri = "http://apigateway:8000/location/user";

        String tripConfirmUri = "http://apigateway:8000/trip/confirm";
        String tripConfirmBody = String.format("{\"driver\":\"%s\", \"passenger\":\"%s\", \"startTime\":%d}", driverUid, passengerUid, 100);
        int expectedStatus = 200;

        HttpRequest passengerPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(passengerPutBody))
                .build();

        HttpRequest driverPutRequest = HttpRequest.newBuilder()
                .uri(new URI(userPutUri))
                .PUT(HttpRequest.BodyPublishers.ofString(driverPutBody))
                .build();

        HttpRequest tripConfirmPostRequest = HttpRequest.newBuilder()
                .uri(new URI(tripConfirmUri))
                .POST(HttpRequest.BodyPublishers.ofString(tripConfirmBody))
                .build();

        httpClient.send(passengerPutRequest, HttpResponse.BodyHandlers.ofString());
        httpClient.send(driverPutRequest, HttpResponse.BodyHandlers.ofString());
        httpClient.send(tripConfirmPostRequest, HttpResponse.BodyHandlers.ofString());

        String getUri = String.format("http://apigateway:8000/trip/driver/%s", driverUid);
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(getUri))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        // Check if the driver actually has the trip with the passenger
        JSONObject body = new JSONObject(getResponse.body());
        JSONObject data = body.getJSONObject("data");
        JSONArray trips = data.getJSONArray("trips");
        boolean contains = false;
        for (int i = 0; i < trips.length(); i++){
            JSONObject trip = new JSONObject(trips.getString(i));
            if (trip.getString("passenger").equals(passengerUid)) {
                contains = true;
            }
        }

        assertTrue(contains);
        assertEquals(expectedStatus, getResponse.statusCode());
    }

    @Test
    public void tripsForDriverFail() throws URISyntaxException, IOException, InterruptedException{
        int expectedStatus = 400;
        String expectedResponse = "{\"status\":\"BAD REQUEST\"}";

        String getUri = String.format("http://apigateway:8000/trip/driver/%s", "");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(getUri))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedResponse, getResponse.body());
        assertEquals(expectedStatus, getResponse.statusCode());
    }


//    @Test
//    public void driverTimePass() throws URISyntaxException, IOException, InterruptedException, JSONException {
//        String driverUid = generateRandomId();
//        String driverPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", driverUid, true);
//        String passengerUid = generateRandomId();
//        String passengerPutBody = String.format("{\"uid\":\"%s\", \"is_driver\":%b}", passengerUid, false);
//        String userPutUri = "http://apigateway:8000/location/user";
//
//        String roadSource = generateRandomId();
//        String roadSourcePutBody = String.format("{\"roadName\":\"%s\", \"hasTraffic\":%b}", roadSource, false);
//        String roadTarget = generateRandomId();
//        String roadTargetPutBody = String.format("{\"roadName\":\"%s\", \"hasTraffic\":%b}", roadTarget, false);
//        String roadPutUri = "http://apigateway:8000/location/road";
//
//        String setDriverLocation = String.format("http://apigateway:8000/location/%s", driverUid);
//        String driverLocationPatchBody = String.format("{\"longitude\":%f,\"latitude\":%f,\"street\":\"%s\"}", 0.0, 0.0, roadSource);
//        String setPassengerLocation = String.format("http://apigateway:8000/location/%s", passengerUid);
//        String passengerLocationPatchBody = String.format("{\"longitude\":%f,\"latitude\":%f,\"street\":\"%s\"}", 0.0, 0.0, roadTarget);
//
//        String confirmTripBody = String.format("{\"driver\":%s,\"passenger\":%s,\"startTime\":\"%d\"}", driverUid, passengerUid, 10);
//        String confirmTripUri = "http://apigateway:8000/trip/confirm";
//
//        // users
//        HttpRequest passengerPutRequest = HttpRequest.newBuilder()
//                .uri(new URI(userPutUri))
//                .PUT(HttpRequest.BodyPublishers.ofString(passengerPutBody))
//                .build();
//
//        HttpRequest driverPutRequest = HttpRequest.newBuilder()
//                .uri(new URI(userPutUri))
//                .PUT(HttpRequest.BodyPublishers.ofString(driverPutBody))
//                .build();
//        // road
//        HttpRequest roadSourcePutRequest = HttpRequest.newBuilder()
//                .uri(new URI(roadPutUri))
//                .PUT(HttpRequest.BodyPublishers.ofString(roadSourcePutBody))
//                .build();
//
//        HttpRequest roadTargetRequest = HttpRequest.newBuilder()
//                .uri(new URI(roadPutUri))
//                .PUT(HttpRequest.BodyPublishers.ofString(roadTargetPutBody))
//                .build();
//
//        // user's location
//        HttpRequest passengerPatchRequest = HttpRequest.newBuilder()
//                .uri(new URI(setPassengerLocation))
//                .method("PATCH", HttpRequest.BodyPublishers.ofString(passengerLocationPatchBody))
//                .build();
//
//        HttpRequest driverPatchRequest = HttpRequest.newBuilder()
//                .uri(new URI(setDriverLocation))
//                .method("PATCH", HttpRequest.BodyPublishers.ofString(driverLocationPatchBody))
//                .build();
//
//        HttpRequest confirmTripPostRequest = HttpRequest.newBuilder()
//                .uri(new URI(confirmTripUri))
//                .method("POST", HttpRequest.BodyPublishers.ofString(confirmTripBody))
//                .build();
//
//        httpClient.send(passengerPutRequest, HttpResponse.BodyHandlers.ofString());
//        httpClient.send(driverPutRequest, HttpResponse.BodyHandlers.ofString());
//
//        httpClient.send(roadSourcePutRequest, HttpResponse.BodyHandlers.ofString());
//        httpClient.send(roadTargetRequest, HttpResponse.BodyHandlers.ofString());
//
//        httpClient.send(passengerPatchRequest, HttpResponse.BodyHandlers.ofString());
//        httpClient.send(driverPatchRequest, HttpResponse.BodyHandlers.ofString());
//
//        HttpResponse<String> confirmTripResponse = httpClient.send(confirmTripPostRequest, HttpResponse.BodyHandlers.ofString());
//        JSONObject body = new JSONObject(confirmTripResponse.body());
//        String tripId = body.getString("data");
//
//        String driverTimeUri = String.format("http://apigateway:8000/trip/drivertime/%s", tripId);
//
//        HttpRequest getRequest = HttpRequest.newBuilder()
//                .uri(new URI(driverTimeUri))
//                .GET()
//                .build();
//
//        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

//        String expectedResponse = "{\"data\":{\"arrival_time\":10},\"status\":\"OK\"}";
//        int expectedStatus = 200;
//
//        assertEquals(expectedResponse, getResponse.body());
//        assertEquals(expectedStatus, getResponse.statusCode());

//    }

    @Test
    public void driverTimeFail() throws URISyntaxException, IOException, InterruptedException{

        String driverTimeUri = String.format("http://apigateway:8000/trip/drivertime/%s", "");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(driverTimeUri))
                .GET()
                .build();

        HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
        String expectedResponse = "{\"status\":\"BAD REQUEST\"}";
        int expectedStatus = 400;

        assertEquals(expectedResponse, getResponse.body());
        assertEquals(expectedStatus, getResponse.statusCode());
    }

    private String generateRandomId(){
        int min = 10000000;
        int max = 99999999;
        int rand = (int) ((Math.random() * max - min) + min);
        return String.format("u%d", rand);
    }
}
