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
                Result result = this.dao.getNavigation(passengerStreet, driverStreet);
                System.out.println(result);
                while(result.hasNext()){
                    System.out.println("hererererer");
                    Record driver = result.next();
                    List<Object> nodenames = driver.get("nodeNames").asList();
                    List<Object> costs = driver.get("costs").asList();
                    res.put("nodenames", nodenames);
                    res.put("costs", costs);
                }
                this.sendResponse(r, res, 200);

            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }
}
