package ca.utoronto.utm.mcs;

import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static String convert(InputStream inputStream) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    public static JSONArray findIterableToJSONArray(FindIterable<Document> docs, List<String> fieldsToRemove) throws Exception {
        JSONArray arr = new JSONArray();
        int i = 0;
        for (Document doc : docs) {
            JSONObject res = new JSONObject(doc.toJson());
            for (String s : fieldsToRemove) {
                res.remove(s);
            }
            arr.put(i, res);
            i++;
        }
        return arr;
    }
}
