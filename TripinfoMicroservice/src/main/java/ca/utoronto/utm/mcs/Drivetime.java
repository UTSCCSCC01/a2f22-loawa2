package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.mongodb.client.FindIterable;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Drivetime extends Endpoint {

    /**
     * GET /trip/driverTime/:_id
     * @param _id
     * @return 200, 400, 404, 500
     * Get time taken to get from driver to passenger on the trip with
     * the given _id. Time should be obtained from navigation endpoint
     * in location microservice.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        String trip = params[3];
        FindIterable<Document> cursor = this.dao.getTrip(trip);
        try {
            if (cursor != null) {
                JSONObject var = new JSONObject();
                List<String> fieldsToRemove = new ArrayList<>();
                //fieldsToRemove.add("passenger");
                JSONObject tripData = new JSONObject();
                JSONObject tripJSON = Utils.findIterableToJSONArray(cursor, fieldsToRemove).getJSONObject(0);
                String driver = tripJSON.getString("driver");
                String passenger = tripJSON.getString("passenger");
                HttpClient httpClient = HttpClient.newHttpClient();
                String getUri = String.format("http://apigateway:8000/location/navigation/%s?passengerUid=%s", driver, passenger);
                HttpRequest getRequest = HttpRequest.newBuilder().uri(new URI(getUri)).GET().build();
                HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
                if (getResponse.statusCode() == 200) {
                    JSONObject res = new JSONObject();
                    JSONObject bod = new JSONObject(getResponse.body());
                    JSONObject data = bod.getJSONObject("data");
                    JSONObject arrivalTime = new JSONObject();
                    arrivalTime.put("arrival_time", data.getInt("total_time"));
                    res.put("data", arrivalTime);
                    this.sendResponse(r, res, 200);
                } else {
                    this.sendStatus(r, 404);
                }
                return;
            }
            this.sendStatus(r, 404);
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
