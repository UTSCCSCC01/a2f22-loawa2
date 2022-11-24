package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends Endpoint {

    /**
     * POST /user/login
     * @body email, password
     * @return 200, 400, 401, 404, 500
     * Login a user into the system if the given information matches the 
     * information of the user in the database.
     */
    
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("email") && body.has("password")) {
            try {
                ResultSet res = this.dao.login(body.getString("email"), body.getString("password"));
                if (res.next()){
                    this.sendStatus(r, 200);
                }
                else{
                    this.sendStatus(r, 404);
                }
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
