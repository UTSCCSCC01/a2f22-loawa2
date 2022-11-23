package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Register extends Endpoint {

    /**
     * POST /user/register
     * @body name, email, password
     * @return 200, 400, 500
     * Register a user into the system using the given information.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        System.out.println("Here");
        if (body.has("name") && body.has("email") && body.has("password")) {
            try {
                this.dao.register(body.getString("name"), body.getString("email"), body.getString("password"));
            } catch (SQLException e) {
                e.printStackTrace();
                this.sendStatus(r, 500);
                return;
            }
            this.sendStatus(r, 200);
        } else {
            this.sendStatus(r, 400);
        }
    }
}
