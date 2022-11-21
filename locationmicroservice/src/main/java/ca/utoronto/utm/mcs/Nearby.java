package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class Nearby extends Endpoint {
    
    /**
     * GET /location/nearbyDriver/:uid?radius=:radius
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers that are within a certain radius around a user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }

        try {
            // Retrieving uid and radius from query params
            String uid = params[3].substring(0, params[3].indexOf('?'));
            int radius = Integer.parseInt(params[3].substring(params[3].indexOf('=') + 1));

            // Getting the desired user's longitude and latitude
            Result result = this.dao.getUserLocationByUid(uid);
            if (result.hasNext()) {
                JSONObject res = new JSONObject();

                Record user = result.next();
                Double longitude = user.get("n.longitude").asDouble();
                Double latitude = user.get("n.latitude").asDouble();

                JSONObject data = new JSONObject();

                // Getting the nearby drivers
                Result nearbyDriversResult = this.dao.getNearbyDrivers(uid, longitude, latitude, radius);
                int count = 0;
                while (nearbyDriversResult.hasNext()) {
                    count++;
                    Record driver = nearbyDriversResult.next();
                    String dUid = driver.get("u.uid").asString();
                    Double dLongitude = driver.get("u.longitude").asDouble();
                    Double dLatitude = driver.get("u.latitude").asDouble();
                    String dStreet = driver.get("u.street").asString();
                    JSONObject driverData = new JSONObject();
                    driverData.put("longitude", dLongitude);
                    driverData.put("latitude", dLatitude);
                    driverData.put("street", dStreet);
                    data.put(dUid, driverData);
                }

                if (count > 0) {
                    res.put("status", "OK");
                    res.put("data", data);
                    this.sendResponse(r, res, 200);
                } else {
                    this.sendStatus(r, 404);
                }

            } else {
                this.sendStatus(r, 404);
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
