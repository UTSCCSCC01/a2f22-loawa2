package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;

public class Navigation extends Endpoint {
    
    /**
     * GET /location/navigation/:driverUid?passengerUid=:passengerUid
     * @param driverUid, passengerUid
     * @return 200, 400, 404, 500
     * Get the shortest path from a driver to passenger weighted by the
     * travel_time attribute on the ROUTE_TO relationship.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        try{
            //Retrieving driverUid and passengerUid from query params
            String driverUid = params[3].substring(0, params[3].indexOf('?'));
            String passengerUid = params[3].substring(params[3].indexOf('=') + 1);

            // Getting navigation
            Result driverLocationResult= this.dao.getUserLocationByUid(driverUid);
            Result passengerLocationResult= this.dao.getUserLocationByUid(passengerUid);
            if (driverLocationResult.hasNext() && passengerLocationResult.hasNext()){
                JSONObject res = new JSONObject();

                Record driverLocation = driverLocationResult.next();
                String driverStreet = driverLocation.get("n.street").asString();
                Record passengerLocation = passengerLocationResult.next();
                String passengerStreet = passengerLocation.get("n.street").asString();
                Result result = this.dao.getNavigation(driverStreet, passengerStreet);
                if(result.hasNext()){
                    Record driver = result.next();
                    Value nodenames = driver.get("nodeNames");
                    Value costs = driver.get("costs");
                    Value traffic_list = driver.get("traffic");
                    res.put("status", "OK");

                    JSONObject data = new JSONObject();
                    Float total_cost = costs.get(costs.size()-1).asFloat();
                    data.put("total_time", total_cost);;

                    JSONArray  route = new JSONArray ();
                    Double time_fix = 0.0;
                    for (int i = 0; i < nodenames.size(); i++) {
                        JSONObject new_route = new JSONObject();
                        String val1 = nodenames.get(i).asString();
                        Double val2 = new Double(costs.get(i).toString());
                        Boolean val3 = traffic_list.get(i).asBoolean();
                        Double actual_time = val2 - time_fix;
                        time_fix += val2;
                        new_route.put("street",val1);
                        new_route.put("time",actual_time);
                        // difference in field name
                        new_route.put("is_traffic",val3);
                        route.put(new_route);
                    }
                    data.put("route",route );
                    res.put("data", data);
                    this.sendResponse(r, res, 200);
                }
                else {
                    this.sendStatus(r, 404);
                }
            }
            else {
                this.sendStatus(r, 404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
