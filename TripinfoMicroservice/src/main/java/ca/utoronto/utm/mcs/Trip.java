package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Trip extends Endpoint {

    /**
     * PATCH /trip/:_id
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost
     * @return 200, 400, 404
     * Adds extra information to the trip with the given id when the 
     * trip is done. 
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 3 || params[2].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }

        String _id = params[2];

        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("distance") && body.has("endTime") && body.has("timeElapsed") && body.has("discount") && body.has("totalCost") && body.has("driverPayout")) {
            try {
                boolean putResult = this.dao.updateTrip(_id, body.getInt("distance"), body.getInt("endTime"), body.getString("timeElapsed"), body.getDouble("discount"), body.getDouble("totalCost"), body.getDouble("driverPayout"));
                if (putResult){
                    this.sendStatus(r, 200);
                } else {
                    this.sendStatus(r, 500);
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.sendStatus(r, 500);
            }
        } else {
            this.sendStatus(r, 400);
        }
    }
}
