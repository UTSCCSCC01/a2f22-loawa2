package ca.utoronto.utm.mcs;

import com.mongodb.client.FindIterable;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Passenger extends Endpoint {

    /**
     * GET /trip/passenger/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips the passenger with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException,JSONException{
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }

        // TODO: check if passenger exists
        String passenger = params[3];
        FindIterable<Document> cursor = this.dao.getPassengerTrips(passenger);
        try {
            if (cursor != null) {
                JSONObject var = new JSONObject();
                List<String> fieldsToRemove = new ArrayList<>();
                fieldsToRemove.add("passenger");
                JSONObject data = new JSONObject();
                data.put("trips", Utils.findIterableToJSONArray(cursor, fieldsToRemove));
                var.put("data", data);
                this.sendResponse(r, var, 200);
                return;
            }
            this.sendStatus(r, 404);
        } catch (Exception e) {
            this.sendStatus(r, 500);
        }
    }
}
