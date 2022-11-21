package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Confirm extends Endpoint {

    /**
     * POST /trip/confirm
     * @body driver, passenger, startTime
     * @return 200, 400
     * Adds trip info into the database after trip has been requested.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("driver") && body.has("passenger") && body.has("startTime")) {
            try {
                // TODO: check if driver and passenger exist
                ObjectId postResult = this.dao.postTrip(body.getString("driver"), body.getString("passenger"), body.getInt("startTime"));
                if (postResult != null){
                    JSONObject res = new JSONObject();
                    res.put("data", postResult);
                    this.sendResponse(r, res, 200);
                } else {
                    this.sendStatus(r, 500);
                }
            } catch (Exception e) {
                this.sendStatus(r, 500);
            }
        } else {
            this.sendStatus(r, 400);
        }
    }
}
