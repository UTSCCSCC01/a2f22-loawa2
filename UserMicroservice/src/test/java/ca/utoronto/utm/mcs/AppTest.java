package ca.utoronto.utm.mcs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {

    private HttpClient httpClient;
    @BeforeEach
    public void init(){
        httpClient = HttpClient.newHttpClient();
    }
    @Test
    public void registerPass() throws URISyntaxException, IOException, InterruptedException {
        String expectedResponse = "{\"status\":\"OK\"}";
        int expectedStatus = 200;
        String postUri = "http://localhost:8001/user/register";
        String name = generateRandomId();
        String email = generateRandomId();
        String password = generateRandomId();
        String PostBody = String.format("{\"name\":\"%s\", \"email\":\"%s\",\"password\":\"%s\" }", name, email, password);
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI(postUri))
                .POST(HttpRequest.BodyPublishers.ofString(PostBody))
                .build();

        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedStatus, postResponse.statusCode());
        assertEquals(expectedResponse, postResponse.body());
    }
    @Test
    public void registerFail() throws URISyntaxException, IOException, InterruptedException {
        String expectedResponse = "{\"status\":\"BAD REQUEST\"}";
        int expectedStatus = 400;
        String postUri = "http://localhost:8001/user/register";
        String PostBody = String.format("{\"name\":\"%s\", \"potato\":\"%s\"}", "driverUid", "yes");
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI(postUri))
                .POST(HttpRequest.BodyPublishers.ofString(PostBody))
                .build();

        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedStatus, postResponse.statusCode());
        assertEquals(expectedResponse, postResponse.body());
    }
    @Test
    public void loginPass() throws URISyntaxException, IOException, InterruptedException {
        String expectedResponse = "{\"status\":\"OK\"}";
        int expectedStatus = 200;
        String registerUri = "http://localhost:8001/user/register";
        String loginUri = "http://localhost:8001/user/login";
        String name = generateRandomId();
        String email = generateRandomId();
        String password = generateRandomId();
        String registerBody = String.format("{\"name\":\"%s\", \"email\":\"%s\",\"password\":\"%s\" }", name, email, password);
        String loginBody = String.format("{\"email\":\"%s\",\"password\":\"%s\" }", email, password);
        HttpRequest registerRequest = HttpRequest.newBuilder()
                .uri(new URI(registerUri))
                .POST(HttpRequest.BodyPublishers.ofString(registerBody))
                .build();

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI(loginUri))
                .POST(HttpRequest.BodyPublishers.ofString(loginBody))
                .build();

        httpClient.send(registerRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedStatus, postResponse.statusCode());
        assertEquals(expectedResponse, postResponse.body());
    }
    @Test
    public void loginFail() throws URISyntaxException, IOException, InterruptedException {
        String expectedResponse = "{\"status\":\"BAD REQUEST\"}";
        int expectedStatus = 400;
        String postUri = "http://localhost:8001/user/login";
        String PostBody = String.format("{\"name\":\"%s\", \"potato\":\"%s\"}", "driverUid", "yes");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI(postUri))
                .POST(HttpRequest.BodyPublishers.ofString(PostBody))
                .build();

        HttpResponse<String> postResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(expectedStatus, postResponse.statusCode());
        assertEquals(expectedResponse, postResponse.body());
    }
    private String generateRandomId(){
        int min = 10000000;
        int max = 99999999;
        int rand = (int) ((Math.random() * max - min) + min);
        return String.format("u%d", rand);
    }
}
